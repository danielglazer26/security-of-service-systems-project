package bednarz.glazer.sakowicz.backendapp1.configuration;

import bednarz.glazer.sakowicz.backendapp1.requests.BodyRequestType;
import bednarz.glazer.sakowicz.backendapp1.requests.HeaderRequestType;
import bednarz.glazer.sakowicz.backendapp1.requests.RequestFactory;
import bednarz.glazer.sakowicz.backendapp1.userinfo.UserInfo;
import bednarz.glazer.sakowicz.backendapp1.userinfo.UserInfoRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.singletonList;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final RequestFactory requestFactory;

    private final RestTemplate restTemplate;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        RequestEntity<Void> verifyRequest =
                requestFactory.buildGetRequest(HeaderRequestType.AUTHORIZATION, request).build();

        ResponseEntity<Long> verifyResponse;

        try {
            verifyResponse = restTemplate.exchange(verifyRequest, Long.class);
        } catch (HttpClientErrorException exception) {
            response.setStatus(exception.getStatusCode().value());
            return;
        }

        var cookiesToSet = verifyResponse.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookiesToSet != null) {
            cookiesToSet.forEach(cookie -> response.addHeader(HttpHeaders.SET_COOKIE, cookie));
        }

        RequestEntity<UserInfoRequest> userInfoRequest = requestFactory.buildPostRequest(BodyRequestType.USER_INFO, request)
                .body(new UserInfoRequest(singletonList(verifyResponse.getBody())));

        ResponseEntity<UserInfo[]> userInfoResponseEntity;

        try {
            userInfoResponseEntity = restTemplate.exchange(userInfoRequest, UserInfo[].class);
        } catch (HttpClientErrorException exception) {
            response.setStatus(exception.getStatusCode().value());
            return;
        }

        UserInfo userInfo = Objects.requireNonNull(userInfoResponseEntity.getBody())[0];

        var authorities = List.of(new SimpleGrantedAuthority(Objects.requireNonNull(userInfo).role().name()));
        var authentication = new UsernamePasswordAuthenticationToken(userInfo, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
