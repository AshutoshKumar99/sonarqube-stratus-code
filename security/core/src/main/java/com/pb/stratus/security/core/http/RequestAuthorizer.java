package com.pb.stratus.security.core.http;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 4/2/14
 * Time: 6:23 PM
 * To change this template use File | Settings | File Templates.
 */

import org.springframework.http.client.ClientHttpRequest;

/**
 * A strategy object for applying authorization info to a request before it is sent.
 */
public interface RequestAuthorizer {

    /**
     * @param request The request
     * @return true if the request is authorized according to the criteria of
     * this authorizer.  This does not imply that the authorization is valid;
     * just that the protocol seems to be met.
     */
    boolean isAuthorized(ClientHttpRequest request);

    /**
     * Attempts to authorize the request.  May fail if actual processing
     * is required to create an authorization header.  Returning true
     * means that the authorization info was provided; it does not ensure
     * that the authorization will be accepted by the service.
     * @param request The request
     * @return true if authorization succeeded
     */
    boolean authorize(ClientHttpRequest request);

}
