package com.pb.stratus.onpremsecurity.connect.identity;

import com.pb.stratus.security.core.connect.identity.AnonymousTenantAuthenticationBypassStrategy;
import com.pb.stratus.security.core.authentication.StaticResourceAuthenticationBypassStrategy;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: vi001ty
 * Date: 5/1/14
 * Time: 11:47 AM
 * <p/>
 * This Filter will handle anonymous access on analyst if authentication-required is configured on analyst.
 */

public class SSAnalystIdentityFilter extends GenericFilterBean {

    private static final Logger logger = Logger.getLogger(SSAnalystIdentityFilter.class);
    private StaticResourceAuthenticationBypassStrategy staticResourceAuthenticationBypassStrategy;
    private AnonymousTenantAuthenticationBypassStrategy anonymousTenantAuthenticationBypassStrategy;

    /**
     * @param req
     * @param res
     * @param chain
     * @throws java.io.IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        Authentication currentAuthentication = null;
        if(authenticationNotRequired(request)){
            chain.doFilter(req,res);
            return;
        }
        if(SecurityContextHolder.getContext() != null)
           currentAuthentication = SecurityContextHolder.getContext().getAuthentication();

        if(currentAuthentication == null || !currentAuthentication.isAuthenticated()){
            if(currentAuthentication == null)
                logger.debug("Authentication credentials not found.");
            else
                logger.debug("Current user not authenticated.");

            throw new AuthenticationCredentialsNotFoundException("No credentials found.");
        }
        chain.doFilter(req,res);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(staticResourceAuthenticationBypassStrategy, "staticResourceAuthenticationBypassStrategy must be specified");
        Assert.notNull(anonymousTenantAuthenticationBypassStrategy, "anonymousTenantAuthenticationBypassStrategy must be specified");
    }

    private boolean authenticationNotRequired(HttpServletRequest req) {
        if(staticResourceAuthenticationBypassStrategy.shouldBypassAuthentication(req)){
            return true;
        }else
            return anonymousTenantAuthenticationBypassStrategy.shouldBypassAuthentication(req);
    }

    @Override
    public void destroy() {}

    public void setStaticResourceAuthenticationBypassStrategy(StaticResourceAuthenticationBypassStrategy staticResourceAuthenticationBypassStrategy) {
        this.staticResourceAuthenticationBypassStrategy = staticResourceAuthenticationBypassStrategy;
    }

    public void setAnonymousTenantAuthenticationBypassStrategy(AnonymousTenantAuthenticationBypassStrategy anonymousTenantAuthenticationBypassStrategy) {
        this.anonymousTenantAuthenticationBypassStrategy = anonymousTenantAuthenticationBypassStrategy;
    }

}
