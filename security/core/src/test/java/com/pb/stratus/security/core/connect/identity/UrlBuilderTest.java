package com.pb.stratus.security.core.connect.identity;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UrlBuilderTest {

    @Test
    public void baseUrlWithoutAnyParametersTest() throws Exception {
        String baseUrl = "/";
        String tenantName = "london";
        String parameterName = "mapconfig";
        String parameterValue = "test";
        String expectedValue = "/london/?mapconfig=test";
        UrlBuilder urlBuilder = new UrlBuilder(tenantName,baseUrl);
        urlBuilder.addQueryParameter(parameterName, parameterValue);
        assertEquals(expectedValue, urlBuilder.getUrl());
    }

    @Test
    public void baseUrlWithParametersTest() throws Exception {
        String baseUrl = "http://localhost:8080/?param1=value1";
        String parameterName = "mapconfig";
        String tenantName = "london";
        String parameterValue = "test";
        String expectedValue = "http://localhost:8080/london/?param1=value1&mapconfig=test";
        UrlBuilder urlBuilder = new UrlBuilder(tenantName,baseUrl);
        urlBuilder.addQueryParameter(parameterName, parameterValue);
        assertEquals(expectedValue, urlBuilder.getUrl());
    }


}
