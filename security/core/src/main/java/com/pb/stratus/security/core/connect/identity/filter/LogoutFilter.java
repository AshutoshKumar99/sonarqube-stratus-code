package com.pb.stratus.security.core.connect.identity.filter;

import com.pb.stratus.core.configuration.TenantConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: sh003bh
 * Date: 10/30/11
 * Time: 1:49 PM
 */
public class LogoutFilter extends GenericFilterBean implements InitializingBean {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
			// this will log out the user and re-request the original resource (essentially, returning them to this filter)
			HttpServletResponse httpServletResponse = (HttpServletResponse)response;
            String sloStartUrl = TenantConfiguration.getTenantConfiguration((HttpServletRequest)request).getSloStartUrl();
            httpServletResponse.sendRedirect(sloStartUrl);
        } else {
			// NOTE: this allows unauthenticated requests to the url of this filter
			chain.doFilter(request, response);
		}
    }
}
