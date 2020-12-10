package com.siva.api;

import com.siva.api.Handlers.APIHandler;
import com.siva.api.Handlers.HelloGetHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Class description
 *
 * @author : Siva Shanthini
 * created on : 10/Dec/2020
 */
public class MyHttpsServer {

    public static void main(String[] args) throws Exception {
        try {
            System.out.println("Starting server...");
            // setup the socket address
            InetSocketAddress address = new InetSocketAddress(443);

            // initialise the HTTPS server
            HttpsServer httpsServer = HttpsServer.create(address, 0);

            SSLConfiguration sslConfiguration = new SSLConfiguration();
            httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslConfiguration.getSslContext()) {
                @Override
                public void configure(HttpsParameters params) {
                    try {
                        // initialise the SSL context
                        SSLContext c = getSSLContext();
                        SSLEngine engine = c.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        // Set the SSL parameters
                        SSLParameters sslParameters = c.getSupportedSSLParameters();
                        params.setSSLParameters(sslParameters);

                    } catch (Exception ex) {
                        System.out.println("Failed to create HTTPS port");
                        System.out.println(ex.getMessage());
                    }
                }
            });

            httpsServer.createContext("/api", new APIHandler());
            httpsServer.createContext("/api/hello", new HelloGetHandler());

            httpsServer.setExecutor(null);
            httpsServer.start();
            System.out.println("Server Started, waiting to accept request.");
            System.out.println("###########################################\n\n");

        } catch (IOException ex) {
            System.out.println("Failed to create HTTPS server on port 443");
            System.out.println(ex.getMessage());
        }
    }
}
