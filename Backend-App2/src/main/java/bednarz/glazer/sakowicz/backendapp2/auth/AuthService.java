package bednarz.glazer.sakowicz.backendapp2.auth;

import bednarz.glazer.sakowicz.ssolib.requests.HeaderRequestType;
import bednarz.glazer.sakowicz.ssolib.requests.RequestFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RequestFactory requestFactory;
    private final RestTemplate restTemplate;

    public ResponseEntity<Void> logout(HttpServletRequest request) {
        RequestEntity<Void> requestEntity = requestFactory.buildGetRequest(HeaderRequestType.LOGOUT, request).build();
        return restTemplate.exchange(requestEntity, Void.class);
    }
}
