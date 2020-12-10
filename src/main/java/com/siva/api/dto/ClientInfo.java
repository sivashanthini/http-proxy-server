package com.siva.api.dto;

import com.siva.api.Constants;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class description
 *
 * @author : Siva Shanthini
 * created on : 10/Dec/2020
 */
public class ClientInfo {
    private String clientID;
    private Date requestTime;
    private Date ttl;
    private AtomicInteger requestCount = new AtomicInteger();


    public ClientInfo(String clientID, Date requestTime) {
        this.clientID = clientID;
        this.requestTime = requestTime;
        setTtl();
        this.requestCount.incrementAndGet();
    }

    public void updateClientInfo(Date now) {
        this.requestTime = requestTime;
        setTtl();
        if (this.requestCount.get() <= Constants.RATE_LIMIT)
            this.requestCount.incrementAndGet();
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public Date getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Date requestTime) {
        this.requestTime = requestTime;
    }

    public Date getTtl() {
        return ttl;
    }

    private void setTtl(Date ttl) {
        this.ttl = ttl;
    }
    private void setTtl() {
        this.setTtl(new Date(this.getRequestTime().getTime() + Constants.EXPIRED_TIME_IN_SEC * 1000L));
    }
    public int getRequestCount() {
        return this.requestCount.get();
    }

    public void clearRequestCount() {
        this.requestCount.compareAndSet(Constants.RATE_LIMIT+1,1);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ClientInfo{");
        sb.append("clientID='").append(clientID).append('\'');
        sb.append(", requestTime=").append(requestTime);
        sb.append(", ttl=").append(ttl);
        sb.append(", requestCount=").append(requestCount);
        sb.append('}');
        return sb.toString();
    }
}
