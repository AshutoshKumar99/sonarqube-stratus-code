package com.pb.stratus.controller.action;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by AW001AG on 03/05/2018.
 */
public class ProxyUtils {
    private static final String PROXY_HOST = "http.proxyHost";
    private static final String PROXY_PORT = "http.proxyPort";
    private static final String PROXY_USER_NAME = "http.proxyUser";
    private static final String PROXY_PASSWORD = "http.proxyPassword";

    private static final String TIME_OUT = "timeout";

    private static final Logger logger = LogManager.getLogger(ProxyUtils.class);

    //Take it from JVM params
    private static String timeoutParam = System.getProperty(TIME_OUT);

    private static int TIMEOUT_IN_SECONDS = timeoutParam != null && !timeoutParam.isEmpty() ?
            Integer.parseInt(timeoutParam) : 100;


    private static RequestConfig requestConfig = RequestConfig.custom().
            setConnectTimeout(TIMEOUT_IN_SECONDS * 1000).
            setConnectionRequestTimeout(TIMEOUT_IN_SECONDS * 1000).
            setSocketTimeout(TIMEOUT_IN_SECONDS * 1000).build();

    private static String host = System.getProperty(PROXY_HOST);
    private static String port = System.getProperty(PROXY_PORT);
    private static String proxyUserName = System.getProperty(PROXY_USER_NAME);
    private static String proxyPassword = System.getProperty(PROXY_PASSWORD);
    private static HttpHost proxyHost = host != null && !host.isEmpty() && port != null && !port.isEmpty() ? new HttpHost(host, Integer.parseInt(port), "http") : null;

    static {
        logger.info("Proxy values passed from JVM params are Host: " + host + " Port is: "
                + port + " timeoutParam:" + timeoutParam);
    }

    private static DefaultProxyRoutePlanner routePlanner =
            host != null && !host.isEmpty() && port != null && !port.isEmpty() ?
                    new DefaultProxyRoutePlanner(proxyHost) : null;

    public static CloseableHttpClient getHttpClient() {
        return getHttpClient(null, null);
    }

    public static CloseableHttpClient getHttpClient(String userName, String password) {

        CloseableHttpClient httpClient;
        HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultRequestConfig(requestConfig);

        CredentialsProvider credentialsProvider = null;
        if (userName != null && !userName.isEmpty()) {
            credentialsProvider = new
                    BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(userName, password));
        }

        if (host != null && !host.isEmpty() && port != null && !port.isEmpty()) {
            logger.info("Returning HttpClient with proxy");
            httpClientBuilder = httpClientBuilder.setRoutePlanner(routePlanner);
        }

        if (null != proxyUserName && !proxyUserName.isEmpty() && null != proxyPassword && !proxyPassword.isEmpty()) {
            if (null == credentialsProvider) {
                credentialsProvider = new BasicCredentialsProvider();
            }
            credentialsProvider.setCredentials(new AuthScope(proxyHost.getHostName(), proxyHost.getPort()), new UsernamePasswordCredentials(proxyUserName, proxyPassword));
        }
        if (credentialsProvider != null) {
            httpClientBuilder = httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
        }
        httpClient = httpClientBuilder.build();

        return httpClient;
    }
}
