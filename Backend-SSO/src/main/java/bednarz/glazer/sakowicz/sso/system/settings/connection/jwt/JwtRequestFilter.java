package bednarz.glazer.sakowicz.sso.system.settings.connection.jwt;


import bednarz.glazer.sakowicz.sso.system.database.services.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static bednarz.glazer.sakowicz.sso.system.ConstStorage.COOKIE_NAME_PROPERTIES;
import static bednarz.glazer.sakowicz.sso.system.ConstStorage.JWT_SECRET_PROPERTIES;


@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final CookieManager cookieManager;
    private final MyUserDetailsService userDetailsService;

    public JwtRequestFilter(CookieManager cookieManager, MyUserDetailsService userDetailsService) {
        this.cookieManager = cookieManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Optional<String> login = cookieManager.getLoginFromOTPCookies(request);

        if (login.isPresent()) {
            validateUsername(request, response, filterChain, login.get());
        } else {
            validateDataFromAnotherApplication(request, response, filterChain);
        }
    }

    private void validateDataFromAnotherApplication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Optional<Map<String, String>> serverDataOpt = cookieManager.getServer(request);

        if (serverDataOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Map<String, String> serverData = serverDataOpt.get();
        String cookieName = serverData.get(COOKIE_NAME_PROPERTIES);
        Optional<String> login = cookieManager.getJwtFromCookies(request, cookieName)
                .flatMap(jwtToken -> cookieManager.decodeJwtToken(jwtToken, serverData.get(JWT_SECRET_PROPERTIES)));

        if (login.isPresent()) {
            validateUsername(request, response, filterChain, login.get());
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void validateUsername(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String username)
            throws ServletException, IOException {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, userDetails.getPassword(), userDetails.getAuthorities()
                    );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (UsernameNotFoundException e) {
            log.info("This username doesn't exist: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
