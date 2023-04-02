package bednarz.glazer.sakowicz.sso.system.connection.settings.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties("frontend")
@Getter
@Setter
public class FrontendServerProperties {

    Map<String, Map<String, String>> configuration;

}
