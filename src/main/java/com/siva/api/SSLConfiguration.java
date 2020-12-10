package com.siva.api;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Class description
 *
 * @author : Siva Shanthini
 * created on : 10/Dec/2020
 */
public class SSLConfiguration {
    SSLContext sslContext = null;

    SSLConfiguration() {
        try{
            init();
        } catch (IOException | KeyManagementException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException | CertificateException ex) {
            System.out.println("Failed to create HTTPS server on port 443");
            System.out.println(ex.getMessage());
        }
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    private void init() throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, KeyManagementException, UnrecoverableKeyException {
         sslContext = SSLContext.getInstance("TLS");

        // initialise the keystore
        char[] password = "password".toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12");

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("keystore.jks");
 
        ks.load(inputStream, password);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
    }
}
