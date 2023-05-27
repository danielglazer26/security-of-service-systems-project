package bednarz.glazer.sakowicz.sso.system.settings.connection.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.*;

@Component
@Slf4j
public class CookieManager {
    private static final String EMPTY_VALUE = "";
    private static final long ZERO_SECONDS = 0L;

    @Value("${security.cookie.otp.name}")
    private String otpCookieName;
    @Value("${security.cookie.otp.secret}")
    private String otpSecret;
    @Value("${security.cookie.otp.path}")
    private String otpCookiePath;
    @Value("${security.cookie.otp.expiration-time.seconds}")
    private long otpCookieExpirationTime;

    @Value("${security.cookie.auth.name}")
    private String authCookieName;
    @Value("${security.cookie.auth.secret}")
    private String authSecret;
    @Value("${security.cookie.auth.path}")
    private String authCookiePath;
    @Value("${security.cookie.auth.expiration-time.seconds}")
    private long authCookieExpirationTime;

    @Value("${security.cookie.domain}")
    private String domain;

    public Optional<String> getLoginFromOTPCookie(HttpServletRequest request) {
        return getCookieFromRequest(otpCookieName, request)
                .flatMap(jwtToken -> decodeJwtToken(jwtToken, otpSecret));
    }

    private Optional<String> getCookieFromRequest(String cookieName, HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        return Objects.isNull(cookie) ? Optional.empty() : Optional.of(cookie.getValue());
    }

    public Optional<String> getLoginFromAuthCookie(HttpServletRequest request) {
        return getCookieFromRequest(authCookieName, request)
                .flatMap(jwtToken -> decodeJwtToken(jwtToken, authSecret));
    }

    public ResponseCookie generateCookie(String login) {
        return generateResponseCookie(
                authCookieName,
                authSecret,
                login,
                authCookiePath,
                authCookieExpirationTime
        );
    }

    public ResponseCookie generateDeleteAuthCookie() {
        return generateEmptyCookie(authCookieName, authCookiePath);
    }

    public ResponseCookie generateOTPCookie(String login) {
        return generateResponseCookie(otpCookieName, otpSecret, login, otpCookiePath, otpCookieExpirationTime);
    }

    public ResponseCookie generateDeleteOTPCookie() {
        return generateEmptyCookie(otpCookieName, otpCookiePath);
    }

    private ResponseCookie generateResponseCookie(String cookieName, String secret, String login, String path,
                                                  long tokenValidityInSeconds) {

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

    private ResponseCookie generateEmptyCookie(String name, String path) {
        return ResponseCookie
                .from(name, EMPTY_VALUE)
                .path(path)
                .maxAge(ZERO_SECONDS)
                .build();
    }

    private String createJsonWebToken(String login, String secret) {
        return JWT.create()
                .withSubject(login)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC512(secret));
    }

    private Optional<String> decodeJwtToken(String jwtToken, String secret) {
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
