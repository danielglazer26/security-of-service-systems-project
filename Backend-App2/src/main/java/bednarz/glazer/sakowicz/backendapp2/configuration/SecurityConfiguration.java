package bednarz.glazer.sakowicz.backendapp2.configuration;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    @Value("${app.frontend.url}")
    private String frontendUrl;
    private final JwtRequestFilter jwtRequestFilter;
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.GET, "/api/user/info", "/api/text", "/api/text/review")
                .hasAnyAuthority("USER", "ADMIN", "MODERATOR")
                .requestMatchers(HttpMethod.POST, "/api/text")
                .hasAnyAuthority("USER", "ADMIN", "MODERATOR")
                .requestMatchers(HttpMethod.DELETE, "/api/text")
                .hasAnyAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/text/review")
                .hasAnyAuthority("ADMIN", "MODERATOR")
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(frontendUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowCredentials(true);
            }
        };
    }
}
