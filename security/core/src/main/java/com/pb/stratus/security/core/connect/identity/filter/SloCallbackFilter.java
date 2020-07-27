package com.pb.stratus.security.core.connect.identity.filter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: sh003bh
 * Date: 10/31/11
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class SloCallbackFilter extends GenericFilterBean implements InitializingBean {

    private String sloResumeBaseUri;

    public void setSloResumeBaseUri(String baseUri) {
        sloResumeBaseUri = baseUri;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            authentication.setAuthenticated(false);
            securityContext.setAuthentication(null);
        }

        String resumePath = request.getParameter("resume");
        ((HttpServletResponse) response).sendRedirect(sloResumeBaseUri + resumePath);
    }
}
