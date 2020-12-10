package com.siva.api;

import java.util.HashSet;
import java.util.Set;

import com.siva.api.dto.ClientInfo;

/**
 * Class description
 *
 * @author : Siva Shanthini
 * created on : 10/Dec/2020
 */
public class InMemory {
    public Set<ClientInfo> clientInfos = new HashSet<>();

    private static InMemory inMemory;
    private static final Object lock = new Object();
    public static InMemory getInstance() {
        if(null == inMemory) {
            synchronized (lock) {
                if(null == inMemory) {
                    inMemory = new InMemory();
                }
            }
        }
        return inMemory;
    }
}
