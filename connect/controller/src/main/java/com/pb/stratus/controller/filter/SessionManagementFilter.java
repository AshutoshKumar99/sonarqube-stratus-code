package com.pb.stratus.controller.filter;


import com.pb.stratus.controller.strategy.SessionValidationStrategy;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * SessionManagementFilter
 * User: GU003DU
 * Date: 12/20/13
 * Time: 11:09 AM
 */
public class SessionManagementFilter extends GenericFilterBean implements InitializingBean {

    private static final Logger logger = LogManager.getLogger(SessionManagementFilter.class);

    private SessionValidationStrategy sessionValidationStrategy;

    public SessionManagementFilter(SessionValidationStrategy sessionValidationStrategy) {
        this.sessionValidationStrategy = sessionValidationStrategy;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession currentSession = request.getSession(false);
        if (currentSession != null) {
            if (sessionValidationStrategy.invalidateSession(request)) {
                currentSession.invalidate();
                // create a new session
                request.getSession(true);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
