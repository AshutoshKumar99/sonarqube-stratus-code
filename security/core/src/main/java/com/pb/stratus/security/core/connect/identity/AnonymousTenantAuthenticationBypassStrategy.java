package com.pb.stratus.security.core.connect.identity;


import com.pb.stratus.security.core.authentication.AuthenticationBypassStrategy;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import javax.servlet.ServletRequest;

import static com.pb.stratus.core.common.Constants.TENANT_ATTRIBUTE_NAME;

/**
 * Strategy to decide whether the tenant has anonymous access enabled or not.
 */
public class AnonymousTenantAuthenticationBypassStrategy implements
        AuthenticationBypassStrategy, InitializingBean{

    // injected
    private RequestBasisAccessConfigurationResolver
            requestBasisAccessConfigurationResolver;

    /**
     * The class expects that the request will have a tenant name in it. If tenant
     * name is not found then an exception is thrown.
     * @param req
     * @return  boolean true if tenant has anonymous access enable | false if the
     *          tenant has only secured access.
     */
    @Override
    public boolean shouldBypassAuthentication(ServletRequest req) {
        if(req.getAttribute(TENANT_ATTRIBUTE_NAME) == null) {
            throw new IllegalStateException("Tenant name cannot be null");
        }
        return requestBasisAccessConfigurationResolver.isAnonymousLoginAllowed();
    }

    public RequestBasisAccessConfigurationResolver getRequestBasisAccessConfigurationResolver() {
        return requestBasisAccessConfigurationResolver;
    }

    public void setRequestBasisAccessConfigurationResolver(
            RequestBasisAccessConfigurationResolver requestBasisAccessConfigurationResolver) {
        this.requestBasisAccessConfigurationResolver = requestBasisAccessConfigurationResolver;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(requestBasisAccessConfigurationResolver);
    }
}
