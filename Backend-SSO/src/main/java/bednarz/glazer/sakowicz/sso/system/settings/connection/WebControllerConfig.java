package bednarz.glazer.sakowicz.sso.system.settings.connection;

import bednarz.glazer.sakowicz.sso.system.settings.connection.jwt.CookieManager;
import bednarz.glazer.sakowicz.sso.system.settings.connection.jwt.FrontendServerProperties;
import bednarz.glazer.sakowicz.sso.system.settings.connection.jwt.JwtRequestFilter;
import bednarz.glazer.sakowicz.sso.system.database.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static bednarz.glazer.sakowicz.sso.system.ConstStorage.ADDRESS_PROPERTIES;

@Configuration
@EnableMethodSecurity()
@EnableWebSecurity
public class WebControllerConfig {
    private final JwtRequestFilter jwtRequestFilter;
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public WebControllerConfig(CookieManager cookieManager, MyUserDetailsService myUserDetailsService) {
        this.jwtRequestFilter = new JwtRequestFilter(cookieManager, myUserDetailsService);
        this.myUserDetailsService = myUserDetailsService;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .csrf().disable()
                .authorizeHttpRequests()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/api/auth/register", "/api/auth/login");
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(myUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(FrontendServerProperties frontendServerProperties) {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(getAllAddressesFromProperties(frontendServerProperties).toArray(new String[0]))
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowCredentials(true);
            }
        };
    }

    private static List<String> getAllAddressesFromProperties(FrontendServerProperties frontendServerProperties) {
        return frontendServerProperties.getConfiguration()
                .values()
                .stream()
                .map(stringStringMap -> stringStringMap.get(ADDRESS_PROPERTIES))
                .toList();
    }
}
