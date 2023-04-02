package bednarz.glazer.sakowicz.sso.system.connection.settings.jwt;


import bednarz.glazer.sakowicz.sso.system.database.services.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static bednarz.glazer.sakowicz.sso.system.ConstStorage.COOKIE_NAME_PROPERTIES;
import static bednarz.glazer.sakowicz.sso.system.ConstStorage.JWT_SECRET_PROPERTIES;


@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final CookieManager cookieManager;
    private final MyUserDetailsService userDetailsService;

    @Autowired
    public JwtRequestFilter(CookieManager cookieManager, MyUserDetailsService userDetailsService) {
        this.cookieManager = cookieManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        cookieManager.getLoginFromOTPCookies(request).ifPresentOrElse(
                (login) -> validateUsername(request, login),
                () -> validateDataFromAnotherApplication(request)
        );
        filterChain.doFilter(request, response);
    }

    private void validateDataFromAnotherApplication(HttpServletRequest request) {
        cookieManager.getServer(request).ifPresent(serverData -> {
            String cookieName = serverData.get(COOKIE_NAME_PROPERTIES);
            cookieManager.getJwtFromCookies(request, cookieName)
                    .flatMap(jwtToken -> cookieManager.decodeJwtToken(jwtToken, serverData.get(JWT_SECRET_PROPERTIES)))
                    .ifPresent(login -> validateUsername(request, login));
        });
    }

    private void validateUsername(HttpServletRequest request, String username) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, userDetails.getPassword(), userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UsernameNotFoundException e) {
            log.info("This username doesn't exist: {}", e.getMessage());
        }
    }
}
