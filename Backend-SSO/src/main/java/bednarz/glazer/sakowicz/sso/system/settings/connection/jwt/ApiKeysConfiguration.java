package bednarz.glazer.sakowicz.sso.system.settings.connection.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties("app")
@Getter
@Setter
public class ApiKeysConfiguration {

    public static String APP_NAME = "app-name";
    public static String CREDENTIAL = "credential";

    Map<String, Map<String, String>> clients;
}

