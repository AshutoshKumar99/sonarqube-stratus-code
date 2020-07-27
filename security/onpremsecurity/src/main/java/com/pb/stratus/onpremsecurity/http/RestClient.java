package com.pb.stratus.onpremsecurity.http;

import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;

import java.net.URI;

/**
 * Created by GU003DU on 15-Nov-18.
 */
public class RestClient {
    // Executes a given  ClientHttpRequest
    private IHttpRequestExecutor httpRequestExecutor;
    private ClientHttpRequestFactory clientHttpRequestFactory;

    public RestClient(IHttpRequestExecutor httpRequestExecutor, ClientHttpRequestFactory clientHttpRequestFactory) {
        this.httpRequestExecutor = httpRequestExecutor;
        this.clientHttpRequestFactory = clientHttpRequestFactory;
    }

    public ClientHttpResponse executeGetRequest(URI uri) throws Exception {
        return httpRequestExecutor.executeRequest(clientHttpRequestFactory.createRequest(uri, HttpMethod.GET));
    }
}
