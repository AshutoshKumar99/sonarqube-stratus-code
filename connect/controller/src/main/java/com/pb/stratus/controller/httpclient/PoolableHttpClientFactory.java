package com.pb.stratus.controller.httpclient;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Created by gu003du on 12/24/2015.
 */
public class PoolableHttpClientFactory {
    HttpClientBuilder builder;

    public PoolableHttpClientFactory() {
        builder = HttpClientBuilder.create();
    }

    public CloseableHttpClient getClient() {
        return builder.build();
    }
}
