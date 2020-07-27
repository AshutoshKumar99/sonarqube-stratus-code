package com.pb.stratus.onpremsecurity.http;

import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import com.pb.stratus.security.core.http.RequestAuthorizer;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 3/4/14
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequestExecutorImpl implements IHttpRequestExecutor {

    private static final Logger logger = Logger.getLogger(HttpRequestExecutorImpl.class);

    private RequestAuthorizer authorizer;

    public RequestAuthorizer getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(RequestAuthorizer authorizer) {
        this.authorizer = authorizer;
    }

    @Override
    public ClientHttpResponse executeRequest(ClientHttpRequest request) throws IOException {
        boolean debugEnabled = logger.isDebugEnabled();
        if (debugEnabled) {
            logger.debug(String.format("execute(): Executing request: method=%s; URL=%s, Headers=%s",
                    request.getMethod(),
                    request.getURI().toString(),
                    request.getHeaders().toString()
            ));
        }

        if (authorizer != null && !authorizer.isAuthorized(request)) {
            if (debugEnabled) {
                logger.debug("execute(): Authorizing using RequestAuthorizer: " + authorizer.toString());
            }
            authorizer.authorize(request);
        }
        else {
            if (debugEnabled) {
                if (authorizer == null)
                    logger.debug("execute(): Found no acceptable RequestAuthorizer -- not authorizing the request.");
                else
                    logger.debug("execute(): The request is already authorized according to RequestAuthorizer: " + authorizer.toString());
            }
        }

        if (debugEnabled) {
            logger.debug("execute(): Sending the request to the server.");
        }
        ClientHttpResponse response = request.execute();

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            if (debugEnabled) {
                logger.debug("execute(): Received 401 - Unauthorized response from server.");
            }

        }

        if (debugEnabled) {
            logger.debug("execute(): Received response: status=" + response.getStatusCode());
        }

        return response;
    }

}
