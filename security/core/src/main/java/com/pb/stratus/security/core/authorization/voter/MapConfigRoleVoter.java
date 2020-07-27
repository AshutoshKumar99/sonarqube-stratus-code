package com.pb.stratus.security.core.authorization.voter;

import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceException;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Set;

public class MapConfigRoleVoter<S> implements AccessDecisionVoter<S>, InitializingBean {

    private String publicRole;
    private static final Logger logger = Logger.getLogger(MapConfigRoleVoter.class.getName());


    private AuthorizationUtils authorizationUtils;

    public void setPublicRole(String publicRole) {
        this.publicRole = publicRole;
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, S object,  Collection<ConfigAttribute> attributes) {
        HttpServletRequest request =
                ((FilterInvocation) object).getHttpRequest();

        // Abstain from voting for request other than home page.
        if ((attributes.size() == 1 &&
                attributes.contains(new SecurityConfig(publicRole))) ||
                request.getPathInfo() != null) {
            return ACCESS_ABSTAIN;
        }

        int result = ACCESS_DENIED;
        String mapcfgParam = request.getParameter(AuthorizationUtils.REQUEST_PARAM_MAPCFG);
        Set<ResourceAuthorizationConfig> configs =
                null;
        try {
            configs = getAuthorizationUtils().getAuthorizeConfigs(request, ResourceType.MAP_CONFIG);
        } catch (ResourceException e) {
            logger.error(e.getMessage());
        }
        //No authorization to user on any map config.
        if (configs.size() == 0) {
            return result;
        }

        if (mapcfgParam == null || mapcfgParam.equals("")) {
            if (getAuthorizationUtils().configsContains(AuthorizationUtils.DEFAULT_MAPCFG, configs)) {
                result = ACCESS_GRANTED;
            }

        } else {
            if (getAuthorizationUtils().configsContains(mapcfgParam, configs)) {
                result = ACCESS_GRANTED;
            }
        }
        return result;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    public AuthorizationUtils getAuthorizationUtils() {
        return authorizationUtils;
    }

    public void setAuthorizationUtils(AuthorizationUtils authorizationUtils) {
        this.authorizationUtils = authorizationUtils;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(authorizationUtils, "authorizationUtils needs to be configured.");
    }
}
