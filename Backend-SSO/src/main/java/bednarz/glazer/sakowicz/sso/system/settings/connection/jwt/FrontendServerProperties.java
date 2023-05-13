package bednarz.glazer.sakowicz.sso.system.settings.connection.jwt;

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
