package bednarz.glazer.sakowicz.sso.system.connection.settings.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.*;

import static bednarz.glazer.sakowicz.sso.system.ConstStorage.*;


@Component
public class CookieManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieManager.class);
    private static final String EMPTY_VALUE = "";
    public static final long FIVE_MINUTES_IN_SECONDS = 5 * 60;
    private final FrontendServerProperties properties;
    @Value("${security.app.cookie.expired.time.ms}")
    private long expirationTime;
    @Value("${security.app.cookie.name}")
    private List<String> cookieName;
    @Value("${security.app.cookie.secret}")
    private String applicationSecret;

    @Autowired
    public CookieManager(FrontendServerProperties frontendServerProperties) {
        properties = frontendServerProperties;
    }

    public Optional<String> getJwtFromCookies(HttpServletRequest request, String cookieName) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (Objects.isNull(cookie)) {
            return Optional.empty();
        } else {
            return Optional.of(cookie.getValue());
        }
    }

    public Optional<Map<String, String>> getServer(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        String finalIpAddress = ipAddress;

        return properties.getConfiguration()
                .values()
                .stream()
                .filter(stringStringMap -> stringStringMap.get(ADDRESS_PROPERTIES).contains(finalIpAddress))
                .findFirst();
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

    public ResponseCookie generateOTPCookie(String login) {
        return generateResponseCookie(
                cookieName.get(OTP_COOKIE_NUMBER),
                applicationSecret,
                login,
                AUTHORIZATION_OTP_ENDPOINT,
                FIVE_MINUTES_IN_SECONDS
        );
    }

    private ResponseCookie generateResponseCookie(String cookieName, String secret, String login, String path,
                                                  long tokenValidityInSeconds) {
        String jwtToken = createJsonWebToken(login, secret);
        return ResponseCookie
                .from(cookieName, jwtToken)
                .path(path)
                .maxAge(tokenValidityInSeconds)
                .httpOnly(true)
                .build();
    }

    public ResponseCookie generateEmptyCookie() {
        return ResponseCookie
                .from(cookieName.get(EMPTY_COOKIE_ID), EMPTY_VALUE)
                .path(AUTHENTICATED_ENDPOINT)
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
            LOGGER.error("Incorrect token algorithm: ", e);
        } catch (SignatureVerificationException e) {
            LOGGER.error("Invalid token signature: ", e);
        } catch (TokenExpiredException e) {
            LOGGER.error("Token is expired: ", e);
        } catch (MissingClaimException e) {
            LOGGER.error("Token is missing claim: ", e);
        } catch (IncorrectClaimException e) {
            LOGGER.error("Token has incorrect claim: ", e);
        } catch (JWTVerificationException e) {
            LOGGER.debug("Invalid token", e);
        }
        return Optional.empty();
    }
}
