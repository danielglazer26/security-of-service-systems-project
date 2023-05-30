package bednarz.glazer.sakowicz.ssolib.configuration;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.server.Ssl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Configuration
public class SSLConfiguration {
    private final Ssl sslProperties;

    public SSLConfiguration(ServerProperties serverProperties) {
        sslProperties = serverProperties.getSsl();
    }

    @Bean
    protected SSLContext sslContext() {
        SSLContext sslContext;
        try(var keyStoreStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(sslProperties.getKeyStore().split(":")[1]);
            var trustStoreStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(sslProperties.getTrustStore().split(":")[1])) {

            sslContext = SSLContext.getInstance("SSL");

            KeyStore keyStore = KeyStore.getInstance(sslProperties.getKeyStoreType());
            char[] keyPassword = sslProperties.getKeyStorePassword().toCharArray();
            keyStore.load(keyStoreStream, keyPassword);
            KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            keyFactory.init(keyStore, keyPassword);

            KeyStore trustStore = KeyStore.getInstance(sslProperties.getTrustStoreType());
            char[] trustPassword = sslProperties.getTrustStorePassword().toCharArray();
            trustStore.load(trustStoreStream, trustPassword);
            TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustFactory.init(trustStore);
            TrustManager[] trustManagers = trustFactory.getTrustManagers();

            KeyManager[] keyManagers = keyFactory.getKeyManagers();
            sslContext.init(keyManagers, trustManagers, null);
            SSLContext.setDefault(sslContext);
        } catch (IOException | UnrecoverableKeyException | CertificateException | KeyStoreException |
                 NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }

        return sslContext;
    }
}
