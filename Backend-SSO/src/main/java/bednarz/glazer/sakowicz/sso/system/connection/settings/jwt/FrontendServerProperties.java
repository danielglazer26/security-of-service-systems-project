package bednarz.glazer.sakowicz.sso.system.connection.settings.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties("frontend")
public class FrontendServerProperties {

    Map<String, Map<String, String>> configuration;

    public Map<String, Map<String, String>> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Map<String, String>> configuration) {
        this.configuration = configuration;
    }
}
