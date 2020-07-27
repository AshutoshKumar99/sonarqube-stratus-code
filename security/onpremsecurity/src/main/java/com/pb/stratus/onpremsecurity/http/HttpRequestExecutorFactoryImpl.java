package com.pb.stratus.onpremsecurity.http;

import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.http.HttpRequestExecutorFactory;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import com.pb.stratus.security.core.http.RequestAuthorizer;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 3/4/14
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequestExecutorFactoryImpl implements HttpRequestExecutorFactory {

    private static final Logger logger = Logger.getLogger(HttpRequestExecutorFactory.class);

    public void setRequestAuthorizer(RequestAuthorizer authorizer) {
        this.requestAuthorizer = authorizer;
    }

    private RequestAuthorizer requestAuthorizer;

    @Override
    public IHttpRequestExecutor create(ControllerConfiguration config) {
        IHttpRequestExecutor executor = new HttpRequestExecutorImpl();
        ((HttpRequestExecutorImpl) executor).setAuthorizer(requestAuthorizer);

        if (logger.isDebugEnabled()) {
            logger.debug("HttpRequestExecutor created with authorize: " +
                    requestAuthorizer.getClass());
        }

        return executor;
    }

    @Override
    public IHttpRequestExecutor create(String userName, String password) {
        IHttpRequestExecutor executor = new HttpRequestExecutorImpl();
        ((HttpRequestExecutorImpl) executor).setAuthorizer(requestAuthorizer);
        if (logger.isDebugEnabled()) {
            logger.debug("HttpRequestExecutor created with authorize: " +
                    requestAuthorizer.getClass());
        }

        return executor;
    }

    @Override
    public IHttpRequestExecutor create() {
        HttpRequestExecutorImpl executor = new HttpRequestExecutorImpl();
        executor.setAuthorizer(requestAuthorizer);
        if (logger.isDebugEnabled()) {
            logger.debug("HttpRequestExecutor created with authorize: " +
                    requestAuthorizer.getClass());
        }

        return executor;
    }
}
