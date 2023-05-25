package bednarz.glazer.sakowicz.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${sso.logout.url}")
    private String logoutUrl;
    private final RestTemplate restTemplate;

    public ResponseEntity<Void> logout(HttpServletRequest request) {
        RequestEntity<Void> requestEntity = RequestEntity
                .get(logoutUrl)
                .header(HttpHeaders.COOKIE, request.getHeader("Cookie"))
                .build();

        return restTemplate.exchange(requestEntity, Void.class);
    }
}
