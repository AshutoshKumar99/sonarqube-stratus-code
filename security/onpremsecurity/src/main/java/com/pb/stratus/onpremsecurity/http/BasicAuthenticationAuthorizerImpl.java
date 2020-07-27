package com.pb.stratus.onpremsecurity.http;

import com.pb.stratus.security.core.http.RequestAuthorizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.util.Base64;
import org.springframework.http.client.ClientHttpRequest;

/**
 * Provides Basic Authentication Authorizer
 */
public class BasicAuthenticationAuthorizerImpl implements RequestAuthorizer {

    private static Log log = LogFactory.getLog(BasicAuthenticationAuthorizerImpl.class);

    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_SCHEME = "Basic";

    private String userName;
    private String password;

    public BasicAuthenticationAuthorizerImpl(String userName, String password){
        this.userName = userName;
        this.password = password;
    }

    @Override
    public boolean isAuthorized(ClientHttpRequest request) {
        String authValue = request.getHeaders().getFirst(AUTH_HEADER);
        boolean authorized = (authValue != null) && (authValue.startsWith(AUTH_SCHEME + " "));

        if (log.isDebugEnabled()) {
            String authmsg = authorized ? "Request is authorized." : "Request is not authorized.";
            log.debug("isAuthorized(): " + authmsg + "  Authorization header value: " + authValue);
        }

        return authorized;
    }

    @Override
    public boolean authorize(ClientHttpRequest request) {
        boolean debugLogging = log.isDebugEnabled();
        if (userName != null && password != null) {
            log.debug("Adding authorization header with anonymous credentials.");
            addBasicAuthenticationHeader(request, userName, password);
            return true;
        }

        if (debugLogging) {
            log.debug("authorize(): cannot authorize the request.");
        }
        return false;
    }

    private void addBasicAuthenticationHeader(ClientHttpRequest request, String username, String password) {
        String usernamePasswordPair = username + ":" + password;
        String headerValue = AUTH_SCHEME + " " + Base64.encode(usernamePasswordPair.getBytes());
        request.getHeaders().set(AUTH_HEADER, headerValue);
    }

}
