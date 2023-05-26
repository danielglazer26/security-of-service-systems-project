package bednarz.glazer.sakowicz.backendapp2.configuration;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.server.Ssl;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.nio.file.Paths;

@Configuration
public class SSLConfiguration {
    private final Ssl sslProperties;
    private final ResourceLoader resourceLoader;

    public SSLConfiguration(ServerProperties serverProperties, ResourceLoader resourceLoader) {
        sslProperties = serverProperties.getSsl();
        this.resourceLoader = resourceLoader;
    }

    @SneakyThrows
    @PostConstruct
    private void configureSSL() {
        System.setProperty("https.protocols", sslProperties.getProtocol());
        System.setProperty("javax.net.ssl.trustStore", Paths.get(resourceLoader.getResource(sslProperties.getTrustStore()).getURI()).toString());
        System.setProperty("javax.net.ssl.trustStorePassword", sslProperties.getTrustStorePassword());
        System.setProperty("javax.net.ssl.trustStoreType", sslProperties.getTrustStoreType());
        System.setProperty("javax.net.ssl.trustStoreProvider", sslProperties.getTrustStoreProvider());
    }
}
