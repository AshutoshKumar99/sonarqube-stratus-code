/**
 * This is an interface for a factory which creates Apache CloseableHttpClient
 * User: GU003DU
 * Date: 4/18/13
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
package com.pb.stratus.controller.httpclient;

import org.apache.http.impl.client.CloseableHttpClient;


public interface HttpClientFactory {
    /**
     * Creates a HttpClient for making Http requests.
     *
     * @param service service url as added in map config.
     * @param secure  a boolean value indicating whether service is secured or not.
     * @param request Actual request Url.
     * @return A HttpClient.
     * @throws Exception
     */
    CloseableHttpClient getHttpClient(String service, boolean secure, StringBuilder request) throws Exception;
}
