/**
 * Created with IntelliJ IDEA.
 * User: ar009sh
 * Date: 3/13/14
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */

package com.pb.stratus.onpremsecurity.interceptors;

import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.apache.cxf.message.Message;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

public class ConnectAuthenticationOutInterceptor extends AbstractAuthenticationOutInterceptor implements InitializingBean {

    private static final Logger logger = Logger.getLogger(ConnectAuthenticationOutInterceptor.class);

    private AuthorizationUtils authorizationUtils;

    public ConnectAuthenticationOutInterceptor() {
    }

    @Override
    public void populateAuthenticationInfo(Message message) {
        String username, password;
        if (isAnonymousUser()) {
            username = requestBasisAccessConfigurationResolver.getAnonymousUserName();
            password = requestBasisAccessConfigurationResolver.getAnonymousPassword();
            addBasicAuthenticationHeader(message, username, password);
        } else {
            addBasicAuthenticationHeader(message);
        }
    }

    private boolean isAnonymousUser() {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return authorizationUtils.isAnonymousUser();
        } else {
            return false;
        }
    }

    public ConnectAuthenticationOutInterceptor(String phase) {
        super(phase);
    }

    public void setAuthorizationUtils(AuthorizationUtils authorizationUtils) {
        this.authorizationUtils = authorizationUtils;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(authorizationUtils, "authorizationUtils must be set");
        Assert.notNull(requestBasisAccessConfigurationResolver, "requestBasisAccessConfigurationResolver must be set");
    }
}