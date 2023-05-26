package bednarz.glazer.sakowicz.sso.system.settings.connection.jwt;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;


public class ApiKeyRequestFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final String principalRequestHeader;

    public ApiKeyRequestFilter(String principalRequestHeader) {
        this.principalRequestHeader = principalRequestHeader;
        this.setAuthenticationManager(authentication -> {
            String principal = (String) authentication.getPrincipal();
            //TODO - dodać do propertiesów
            if (!"value".equals(principal)) {
                throw new BadCredentialsException("The API key was not found or not the expected value.");
            }
            authentication.setAuthenticated(true);
            return authentication;
        });
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(principalRequestHeader);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
}
