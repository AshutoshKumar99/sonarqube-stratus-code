package com.pb.stratus.onpremsecurity.http;

import com.pb.stratus.onpremsecurity.util.JWTAuthorizationHandler;
import com.pb.stratus.security.core.http.RequestAuthorizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.client.ClientHttpRequest;

import java.util.List;
import java.util.Map;

/**
 * Created by VI001TY on 3/28/2016.
 */
public class JWTRequestAuthorizerImpl implements RequestAuthorizer {

    private static Log log = LogFactory.getLog(JWTRequestAuthorizerImpl.class);
    private static final String AUTH_HEADER = "Authorization";
    private static final String AUTH_SCHEME = "Bearer";
    private JWTAuthorizationHandler jwtAuthorizationHandler;

    public void setJwtAuthorizationHandler(JWTAuthorizationHandler handler) {
        jwtAuthorizationHandler = handler;
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
        Map<String, List<String>> headers = jwtAuthorizationHandler.getJWTHeaders(false);
        request.getHeaders().putAll(headers);
        return true;
    }
}
