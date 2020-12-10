package com.siva.api.Handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.siva.api.HTTPMethods;
import com.siva.api.Utils;
import com.siva.api.dto.ClientInfo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Class description
 *
 * @author : Siva Shanthini
 * created on : 10/Dec/2020
 */
public class APIHandler implements HttpHandler {


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        ClientInfo clientInfo = Utils.findClientAfterApplyingRateLimit(exchange);
        if (HTTPMethods.GET.name().equals(exchange.getRequestMethod())) {

            Map<String, List<String>> params = Utils.splitQuery(exchange.getRequestURI().getRawQuery());
            String noNameText = "Anonymous";
            String name = params.getOrDefault("name", List.of(noNameText)).stream().findFirst().orElse(noNameText);
            String respText = String.format("Hello %s! You last logged in at : %s", noNameText,
                    clientInfo.getRequestTime());
            exchange.sendResponseHeaders(200, respText.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
        } else {
            exchange.sendResponseHeaders(405, -1);// 405 Method Not Allowed
        }

        exchange.close();
    }
}
