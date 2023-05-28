package bednarz.glazer.sakowicz.sso.system.settings.connection.jwt;


import bednarz.glazer.sakowicz.sso.system.database.services.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;


@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final CookieManager cookieManager;
    private final MyUserDetailsService userDetailsService;

    public JwtRequestFilter(CookieManager cookieManager, MyUserDetailsService userDetailsService) {
        this.cookieManager = cookieManager;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/key/api");
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        Optional<String> login = cookieManager.getLoginFromOTPCookie(request);

        if (login.isPresent()) {
            validateUsername(request, response, filterChain, login.get());
        } else {
            validateDataFromAnotherApplication(request, response, filterChain);
        }
    }

    private void validateDataFromAnotherApplication(HttpServletRequest request, HttpServletResponse response,
                                                    FilterChain filterChain) throws ServletException, IOException {
        Optional<String> login = cookieManager.getLoginFromAuthCookie(request);

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
