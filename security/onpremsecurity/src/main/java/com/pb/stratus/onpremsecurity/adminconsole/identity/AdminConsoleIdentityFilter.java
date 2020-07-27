package com.pb.stratus.onpremsecurity.adminconsole.identity;

import com.pb.stratus.security.core.authentication.StaticResourceAuthenticationBypassStrategy;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: AL021CH
 * Date: 3/13/14
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminConsoleIdentityFilter extends GenericFilterBean {
    private static final Logger logger = Logger.getLogger(AdminConsoleIdentityFilter.class);
    private StaticResourceAuthenticationBypassStrategy staticResourceAuthenticationBypassStrategy;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {
        SecurityContext securityContext = null;
        if(authenticationNotRequired((HttpServletRequest) req)){
            filterChain.doFilter(req,res);
            return;
        }

        if(SecurityContextHolder.getContext() != null)
        {
            securityContext = SecurityContextHolder.getContext();
        }

        if(securityContext != null && securityContext.getAuthentication() == null)
        {
            // this could be due to :

            //1. ItemSelectable a first filter chain pass .first time authentication is null in this filter
            //2. A timeout scenario.
            throw new AuthenticationCredentialsNotFoundException("No credentials found.");
        }

        filterChain.doFilter(req, res);
    }
    @Override
    public void afterPropertiesSet() {
        Assert.notNull(staticResourceAuthenticationBypassStrategy, "staticResourceAuthenticationBypassStrategy must be specified");
    }

    private boolean authenticationNotRequired(HttpServletRequest req) {
        if(staticResourceAuthenticationBypassStrategy.shouldBypassAuthentication(req)){
            return true;
        }
        return false;
    }

    public void setStaticResourceAuthenticationBypassStrategy(StaticResourceAuthenticationBypassStrategy staticResourceAuthenticationBypassStrategy) {
        this.staticResourceAuthenticationBypassStrategy = staticResourceAuthenticationBypassStrategy;
    }
}
