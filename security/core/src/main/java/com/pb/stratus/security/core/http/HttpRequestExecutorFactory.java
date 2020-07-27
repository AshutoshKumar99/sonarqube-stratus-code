package com.pb.stratus.security.core.http;

import com.pb.stratus.core.configuration.ControllerConfiguration;

/**
 * This interface is used in TileServiceAction for creating a request executor
 */
public interface HttpRequestExecutorFactory {
    IHttpRequestExecutor create(ControllerConfiguration config);
    IHttpRequestExecutor create(String userName, String password);
    IHttpRequestExecutor create();
}
