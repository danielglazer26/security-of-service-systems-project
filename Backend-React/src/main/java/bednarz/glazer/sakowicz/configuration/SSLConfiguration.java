package bednarz.glazer.sakowicz.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.server.Ssl;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SSLConfiguration {
    private final Ssl sslProperties;

    public SSLConfiguration(ServerProperties serverProperties) {
        sslProperties = serverProperties.getSsl();
    }

    @PostConstruct
    private void configureSSL() {
        System.setProperty("https.protocols", sslProperties.getProtocol());
        System.setProperty("javax.net.ssl.trustStore", sslProperties.getTrustStore());
        System.setProperty("javax.net.ssl.trustStorePassword", sslProperties.getTrustStorePassword());
        System.setProperty("javax.net.ssl.trustStoreType", sslProperties.getTrustStoreType());
        System.setProperty("javax.net.ssl.trustStoreProvider", sslProperties.getTrustStoreProvider());
    }
}
