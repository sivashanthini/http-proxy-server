package com.siva.api;

import com.siva.api.dto.ClientInfo;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

/**
 * Class description
 *
 * @author : Siva Shanthini
 * created on : 10/Dec/2020
 */
public class Utils {

    private static boolean rateLimit(ClientInfo clientInfo, Date now) {
        System.out.println("client info from inMemory : " + clientInfo);
        if (now.after(clientInfo.getTtl())) {
            clientInfo.clearRequestCount();
            clientInfo.setRequestTime(now);
            return true;
        }
        if (now.before(clientInfo.getTtl()) && clientInfo.getRequestCount() > Constants.RATE_LIMIT) {
            return false;
        } else {
            return true;
        }
    }

    public static Map<String, List<String>> splitQuery(String query) {
        if (query == null || "".equals(query)) {
            return Collections.emptyMap();
        }

        return Pattern.compile("&").splitAsStream(query)
                .map(s -> Arrays.copyOf(s.split("="), 2))
                .collect(groupingBy(s -> decode(s[0]), mapping(s -> decode(s[1]), toList())));

    }

    public static String decode(final String encoded) {
        try {
            return encoded == null ? null : URLDecoder.decode(encoded, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 is a required encoding", e);
        }
    }

    public static ClientInfo updateClientInfo(String clientId, Date now, Set<ClientInfo> clientInfos) {
        ClientInfo clientInfo = clientInfos.stream()
                .filter(c -> c.getClientID().equalsIgnoreCase(clientId))
                .peek(f -> f.updateClientInfo(now))
                .findFirst().orElse(new ClientInfo(clientId, new Date()));

        clientInfos.add(clientInfo);
        return clientInfo;
    }

    public static ClientInfo findClientAfterApplyingRateLimit(HttpExchange exchange) throws IOException {
        Date now = new Date();
        List<String> clientId = exchange.getRequestHeaders().get("ClientID");
        System.out.println("requested ClientID : " + clientId.get(0));
        ClientInfo clientInfo = Utils.updateClientInfo(clientId.get(0), now, InMemory.getInstance().clientInfos);

        if (!Utils.rateLimit(clientInfo, now)) {
            // HTTP 429 Too Many Requests response status code
            exchange.sendResponseHeaders(429, -1);
        }
        return clientInfo;
    }
}
