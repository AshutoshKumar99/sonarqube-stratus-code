package com.pb.stratus.controller.util;

import com.pb.stratus.security.core.http.RequestAuthorizer;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by SU019CH on 4/9/2019.
 * Provides methods to get response data in form of JSONObject as well as in ClientHttpResponse if provided with
 * a url and valid requestAuthorizer.
 */
public interface RestUrlExecutor {

    public ClientHttpResponse executeGet(String url, RequestAuthorizer requestAuthorizer) throws IOException,
            URISyntaxException;

    public String post(String payload, String url) throws Exception;

    public String get(String url) throws Exception;
}
