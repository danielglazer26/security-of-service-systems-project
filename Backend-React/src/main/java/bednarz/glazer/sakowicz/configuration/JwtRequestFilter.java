package bednarz.glazer.sakowicz.configuration;

import bednarz.glazer.sakowicz.userinfo.UserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Value("${authorization.url}")
    private String authorizationUrl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        //TODO to remove
        HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);

        RequestEntity<Void> requestEntity = RequestEntity
                .get(authorizationUrl)
                .header(HttpHeaders.COOKIE, request.getHeader("Cookie"))
                .build();

        ResponseEntity<UserInfo> userInfoResponseEntity;

        try {
            userInfoResponseEntity = new RestTemplate().exchange(requestEntity, UserInfo.class);
        } catch (HttpClientErrorException exception) {
            response.setStatus(exception.getStatusCode().value());
            return;
        }

        UserInfo userInfo = userInfoResponseEntity.getBody();

        var authorities = List.of(new SimpleGrantedAuthority(userInfo.role().name()));
        var authentication = new UsernamePasswordAuthenticationToken(userInfo, null, authorities);
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
