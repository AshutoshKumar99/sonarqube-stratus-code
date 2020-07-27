package com.pb.stratus.onpremsecurity.http;

import com.pb.stratus.core.common.application.SpringApplicationContextLocator;
import com.pb.stratus.security.core.http.RequestAuthorizer;

/**
 * Created by SU019CH on 4/9/2019.
 */
public class HttpRequestAuthorizerFactory {

    public RequestAuthorizer getJWTAuthorizer() {
        return (JWTRequestAuthorizerImpl) SpringApplicationContextLocator.getApplicationContext().getBean("jwtAuthorizer");
    }

    public RequestAuthorizer getBasicAuthorizer(String user, String password) {
        return new BasicAuthenticationAuthorizerImpl(user, password);
    }
}
