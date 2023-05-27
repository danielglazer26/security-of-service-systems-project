package bednarz.glazer.sakowicz.sso.system.settings.connection.jwt;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import java.util.HashMap;
import java.util.Map;


public class ApiKeyRequestFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final ApiKeysConfiguration apiKeysConfiguration;

    public ApiKeyRequestFilter(ApiKeysConfiguration apiKeysConfiguration) {
        this.apiKeysConfiguration = apiKeysConfiguration;

        this.setAuthenticationManager(authentication -> {
            Map<String, String> principal = (Map<String, String>) authentication.getPrincipal();

            if (principal.isEmpty()) {
                throw new BadCredentialsException("The API key was not found or not the expected value.");
            }
            if (!isCredentialCorrect(authentication, principal)) {
                throw new BadCredentialsException("The API credential was not found or it is incorrect.");
            }

            authentication.setAuthenticated(true);
            return authentication;
        });
    }

    private static boolean isCredentialCorrect(Authentication authentication, Map<String, String> principal) {
        return principal.get(ApiKeysConfiguration.CREDENTIAL).equals(authentication.getCredentials());
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return apiKeysConfiguration.getClients().getOrDefault(request.getHeader("api-key"), new HashMap<>());
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return request.getHeader("api-credential");
    }
}
