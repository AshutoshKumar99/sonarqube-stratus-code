package com.pb.stratus.security.core.http;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.List;

/**
 * Executes HTTP requests via a list of in- and out-interceptors.
 *
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 3/3/14
 * Time: 12:57 PM
 */

public interface IHttpRequestExecutor {
    /**
     * Executes the request.  This method also retries the request if
     * it fails, and the interceptors indicate that it can be retried with
     * different results (e.g. renewing a security token)
     * @param request The request
     * @return The response
     * @throws java.io.IOException Thrown if there is a communication problem.
     */
    ClientHttpResponse executeRequest(ClientHttpRequest request) throws IOException;
}
