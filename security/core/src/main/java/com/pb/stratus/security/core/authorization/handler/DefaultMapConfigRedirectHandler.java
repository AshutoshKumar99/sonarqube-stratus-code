package com.pb.stratus.security.core.authorization.handler;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.TenantConfiguration;
import com.pb.stratus.security.core.connect.identity.RequestBasisAccessConfigurationResolver;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceException;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * @author vi001ty This class responsible for redirection to first lexicographic
 *         mapconfig if user is not authorize for defaultmap config. Anonymous
 *         user will be redirected to login if not authorised to view any
 *         mapconfig.
 */
public class DefaultMapConfigRedirectHandler
{
    private static final Logger logger =
            Logger.getLogger(DefaultMapConfigRedirectHandler.class);
    private RequestBasisAccessConfigurationResolver accessResolver = null;
    private AuthorizationUtils authorizationUtils;

    public void setAccessResolver(
            RequestBasisAccessConfigurationResolver accessResolver)
    {
        this.accessResolver = accessResolver;
    }

    public void setAuthorizationUtils(AuthorizationUtils authorizationUtils) {
        this.authorizationUtils = authorizationUtils;
    }

    public void handle(HttpServletRequest request, HttpServletResponse response)
    {
        Set<ResourceAuthorizationConfig> configs = null;
        try
        {
            configs =
                    authorizationUtils.getAuthorizeConfigs(request, ResourceType.MAP_CONFIG);
            if (configs != null && !configs.isEmpty())
            {
                if (!authorizationUtils.configsContains(AuthorizationUtils.DEFAULT_MAPCFG, configs))
                {
                    logger
                            .debug("User not authorised to view defaultmap config.");
                    String config =
                            authorizationUtils.getFirstLexicographicEntry(configs);
                    String tenant =
                            (String) request
                                    .getAttribute(Constants.TENANT_ATTRIBUTE_NAME);
                    String url =
                            request.getContextPath() + "/" + tenant + "/?" +
                                    AuthorizationUtils.REQUEST_PARAM_MAPCFG +
                                    "=" + config;
                    response.sendRedirect(url);
                }
            }
            else
            {
                if (configs != null)
                {
                    // CONN-13397
                    // if accesstype is public or both ,user logged in as
                    // anonymoususer and user don't have public assess on any
                    // map config.
                    if (accessResolver.isAnonymousLoginAllowed() &&
                            authorizationUtils.isAnonymousUser()) {
                        logger.debug("Anonymous user not authorised to view any map config.");
                        logger.debug("Redirecting to login page.");
                        String ssoURL = TenantConfiguration.getTenantConfiguration(request).getSsoStartUrl();
                        response.sendRedirect(ssoURL);
                    }
                }
            }

        }
        catch (ResourceException e)
        {
            logger.error(e.getMessage());
        }
        catch (IOException e)
        {
            logger.error(e.getMessage());
        }
    }
}
