package com.pb.stratus.onpremsecurity.http;

import com.pb.stratus.security.core.http.HttpRequestExecutorFactory;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import org.apache.log4j.Logger;

/**
 * Created by NI010GO on 3/7/2016.
 */
public class HttpRequestExecutorRoutingFactory {
    private static final Logger logger = Logger.getLogger(HttpRequestExecutorFactory.class);

    public IHttpRequestExecutor create(String user, String password) {
        IHttpRequestExecutor executor = new HttpRequestExecutorImpl();
        BasicAuthenticationAuthorizerImpl authorizer =
                new BasicAuthenticationAuthorizerImpl(user, password);
        ((HttpRequestExecutorImpl)executor).setAuthorizer(authorizer);

        logger.debug("HttpRequestExecutor created with BasicAuthenticationAuthorizer.");

        return executor;
    }
}
