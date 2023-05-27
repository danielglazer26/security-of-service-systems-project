package bednarz.glazer.sakowicz.backendapp2.requests;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

@Component
public class RequestFactory {

    public static final String API_KEY = "api-key";
    public static final String API_CREDENTIAL = "api-credential";

    @Value("${sso.api-key}")
    private String apiKey;

    @Value("${sso.api-credential}")
    private String apiCredential;

    @Value("${sso.url.authorization}")
    private String authorizationUrl;

    @Value("${sso.url.userinfo}")
    private String userInfoUrl;

    @Value("${sso.url.logout}")
    private String logoutUrl;

    public RequestEntity.BodyBuilder buildPostRequest(BodyRequestType bodyRequestType, HttpServletRequest request) {
        return switch (bodyRequestType) {
            case USER_INFO -> RequestEntity
                    .post(userInfoUrl)
                    .header(HttpHeaders.COOKIE, request.getHeader(HttpHeaders.COOKIE))
                    .header(API_KEY, apiKey)
                    .header(API_CREDENTIAL, apiCredential);
        };
    }

    public RequestEntity.HeadersBuilder<?> buildGetRequest(HeaderRequestType headerRequestType, HttpServletRequest request) {
        return switch (headerRequestType) {
            case AUTHORIZATION -> RequestEntity
                    .get(authorizationUrl)
                    .header(HttpHeaders.COOKIE, request.getHeader(HttpHeaders.COOKIE));
            case LOGOUT -> RequestEntity
                    .get(logoutUrl)
                    .header(HttpHeaders.COOKIE, request.getHeader(HttpHeaders.COOKIE));
        };
    }

}
