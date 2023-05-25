package bednarz.glazer.sakowicz.configuration;

import bednarz.glazer.sakowicz.userinfo.UserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    @Value("${sso.authorization.url}")
    private String authorizationUrl;
    private final RestTemplate restTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        RequestEntity<Void> requestEntity = RequestEntity
                .get(authorizationUrl)
                .header(HttpHeaders.COOKIE, request.getHeader("Cookie"))
                .build();

        ResponseEntity<UserInfo> userInfoResponseEntity;

        try {
            userInfoResponseEntity = restTemplate.exchange(requestEntity, UserInfo.class);
        } catch (HttpClientErrorException exception) {
            response.setStatus(exception.getStatusCode().value());
            return;
        }

        UserInfo userInfo = userInfoResponseEntity.getBody();

        var authorities = List.of(new SimpleGrantedAuthority(Objects.requireNonNull(userInfo).role().name()));
        var authentication = new UsernamePasswordAuthenticationToken(userInfo, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
