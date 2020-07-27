package com.pb.stratus.controller.util;

import com.pb.stratus.security.core.http.RequestAuthorizer;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * Created by SU019CH on 4/9/2019.
 * This class is responsible for executing a given URL along with provided authorizer.
 */
public class RestUrlExecutorImpl implements RestUrlExecutor {
    private static Logger logger = LogManager.getLogger(RestUrlExecutorImpl.class);
    private ClientHttpRequestFactory clientHttpRequestFactory;
    private RequestAuthorizer jwtRequestAuthorizer;

    public RestUrlExecutorImpl() {
        clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
    }

    public RestUrlExecutorImpl(RequestAuthorizer requestAuthorizer) {
        clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        this.jwtRequestAuthorizer = requestAuthorizer;
    }

    public ClientHttpResponse executeGet(String url, RequestAuthorizer requestAuthorizer)
            throws IOException, URISyntaxException {
        ClientHttpRequest restRequest = clientHttpRequestFactory.createRequest(new URI(url), HttpMethod.GET);
        requestAuthorizer.authorize(restRequest);
        return restRequest.execute();
    }

    public String post(String payload, String url) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("URL: " + url + " and post payload: " + payload);
        }
        URI uri = new URI(url);
        ClientHttpRequest restRequest = clientHttpRequestFactory.createRequest(uri, HttpMethod.POST);
        OutputStream os = restRequest.getBody();
        InputStream is = IOUtils.toInputStream(payload, StandardCharsets.UTF_8.name());
        try {
            IOUtils.copy(is, os);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
        MediaType contentTypeHeader = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType
                .APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
        restRequest.getHeaders().setContentType(contentTypeHeader);

        jwtRequestAuthorizer.authorize(restRequest);
        ClientHttpResponse response = restRequest.execute();

        // handle  response
        if (response.getStatusCode() == HttpStatus.OK) {
            try (InputStream inputStream = response.getBody()) {
                return IOUtils.toString(inputStream);
            }
        } else {
            throw new Exception("Failed to execute request for URL " + url + " Status Code: " +
                    response.getStatusCode() + " reason: " + response.getStatusText());
        }
    }

    public String get(String url) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("URL: " + url);
        }
        URI uri = new URI(url);
        ClientHttpRequest restRequest = clientHttpRequestFactory.createRequest(uri, HttpMethod.GET);
        MediaType contentTypeHeader = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType
                .APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
        restRequest.getHeaders().setContentType(contentTypeHeader);

        jwtRequestAuthorizer.authorize(restRequest);
        ClientHttpResponse response = restRequest.execute();

        // handle  response
        if (response.getStatusCode() == HttpStatus.OK) {
            try (InputStream inputStream = response.getBody()) {
                return IOUtils.toString(inputStream);
            }
        } else {
            throw new Exception("Failed to execute request for URL " + url + " Status Code: " +
                    response.getStatusCode() + " reason: " + response.getStatusText());
        }
    }
}
