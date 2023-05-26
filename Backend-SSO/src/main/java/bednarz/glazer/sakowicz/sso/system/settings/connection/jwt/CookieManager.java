package bednarz.glazer.sakowicz.sso.system.settings.connection.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.*;

import static bednarz.glazer.sakowicz.sso.system.ConstStorage.*;


@Component
@Slf4j
public class CookieManager {
    private static final int EMPTY_COOKIE_ID = 0;
    private static final String EMPTY_VALUE = "";
    private static final long FIVE_MINUTES_IN_SECONDS = 5 * 60;
    private static final int OTP_COOKIE_NUMBER = 1;
    private final FrontendServerProperties properties;
    @Value("${security.app.cookie.expired.time.ms}")
    private long expirationTime;
    @Value("${security.app.cookie.name}")
    private List<String> cookieName;
    @Value("${security.app.cookie.secret}")
    private String applicationSecret;
    @Value("${security.app.cookie.domain}")
    private String domain;

    @Autowired
    public CookieManager(FrontendServerProperties frontendServerProperties) {
        properties = frontendServerProperties;
    }

    public Optional<String> getLoginFromOTPCookies(HttpServletRequest request) {
        return getJwtFromCookies(request, cookieName.get(OTP_COOKIE_NUMBER))
                .flatMap(jwtToken -> decodeJwtToken(jwtToken, applicationSecret));
    }

    public Optional<Map<String, String>> getServer(HttpServletRequest request) {
        //TODO
        /*String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        String finalIpAddress = ipAddress;

        return properties.getConfiguration()
                .values()
                .stream()
                .filter(stringStringMap -> stringStringMap.get(ADDRESS_PROPERTIES).contains(finalIpAddress))
                .findFirst();*/
        return properties.getConfiguration().values().stream().findFirst();
    }

    public Optional<String> getJwtFromCookies(HttpServletRequest request, String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        return Objects.isNull(cookie) ? Optional.empty() : Optional.of(cookie.getValue());
    }

    public ResponseCookie generateCookie(HttpServletRequest request, String login) {
        Optional<Map<String, String>> server = getServer(request);

        if (server.isPresent()) {
            Map<String, String> serverConfiguration = server.get();
            return generateResponseCookie(
                    serverConfiguration.get(COOKIE_NAME_PROPERTIES),
                    serverConfiguration.get(JWT_SECRET_PROPERTIES),
                    login,
                    AUTHENTICATED_ENDPOINT,
                    expirationTime / 1000
            );
        } else {
            return generateEmptyCookie();
        }
    }

    public ResponseCookie[] generateDeleteAuthCookies() {
        var serversConfigurations = properties.getConfiguration().values();
        return serversConfigurations.stream()
                .map(serverConfiguration -> {
                    String cookieName = serverConfiguration.get(COOKIE_NAME_PROPERTIES);
                    return generateEmptyCookie(cookieName, AUTHENTICATED_ENDPOINT);
                })
                .toArray(ResponseCookie[]::new);
    }

    public ResponseCookie generateOTPCookie(String login) {
        return generateResponseCookie(
                cookieName.get(OTP_COOKIE_NUMBER),
                applicationSecret,
                login,
                AUTHORIZATION_OTP_ENDPOINT,
                FIVE_MINUTES_IN_SECONDS
        );
    }

    public ResponseCookie generateDeleteOTPCookie() {
        return generateEmptyCookie(cookieName.get(OTP_COOKIE_NUMBER), AUTHORIZATION_OTP_ENDPOINT);
    }

    private ResponseCookie generateResponseCookie(
            String cookieName, String secret, String login, String path, long tokenValidityInSeconds
    ) {
        String jwtToken = createJsonWebToken(login, secret);
        return ResponseCookie
                .from(cookieName, jwtToken)
                .domain(domain)
                .path(path)
                .secure(true)
                .maxAge(tokenValidityInSeconds)
                .httpOnly(true)
                .build();
    }

    public ResponseCookie generateEmptyCookie() {
        return generateEmptyCookie(cookieName.get(EMPTY_COOKIE_ID), AUTHENTICATED_ENDPOINT);
    }

    public ResponseCookie generateEmptyCookie(String name, String path) {
        return ResponseCookie
                .from(name, EMPTY_VALUE)
                .path(path)
                .maxAge(0)
                .build();
    }

    private String createJsonWebToken(String login, String secret) {
        return JWT.create()
                .withSubject(login)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC512(secret));
    }

    public Optional<String> decodeJwtToken(String jwtToken, String secret) {
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC512(secret)).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(jwtToken);

            return Optional.ofNullable(decodedJWT.getSubject());
        } catch (AlgorithmMismatchException e) {
            log.error("Incorrect token algorithm: ", e);
        } catch (SignatureVerificationException e) {
            log.error("Invalid token signature: ", e);
        } catch (TokenExpiredException e) {
            log.error("Token is expired: ", e);
        } catch (MissingClaimException e) {
            log.error("Token is missing claim: ", e);
        } catch (IncorrectClaimException e) {
            log.error("Token has incorrect claim: ", e);
        } catch (JWTVerificationException e) {
            log.debug("Invalid token", e);
        }
        return Optional.empty();
    }
}
