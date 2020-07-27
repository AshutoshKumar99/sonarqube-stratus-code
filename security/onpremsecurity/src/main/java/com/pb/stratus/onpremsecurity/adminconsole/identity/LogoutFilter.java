package com.pb.stratus.onpremsecurity.adminconsole.identity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by GU003DU on 13-Nov-18.
 */
public class LogoutFilter extends GenericFilterBean {

    private final List<LogoutHandler> handlers;
    private RequestMatcher logoutRequestMatcher;

    public LogoutFilter(LogoutHandler... handlers) {
        this.handlers = Arrays.asList(handlers);
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (requiresLogout(request, response)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (logger.isDebugEnabled()) {
                logger.debug("Logging out user '" + auth + "' and transferring to logout destination");
            }

            for (LogoutHandler handler : handlers) {
                handler.logout(request, response, auth);
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Allow subclasses to modify when a logout should take place.
     *
     * @param request  the request
     * @param response the response
     * @return <code>true</code> if logout should occur, <code>false</code> otherwise
     */
    protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
        return logoutRequestMatcher.matches(request);
    }

    public void setLogoutRequestMatcher(RequestMatcher logoutRequestMatcher) {
        Assert.notNull(logoutRequestMatcher, "logoutRequestMatcher cannot be null");
        this.logoutRequestMatcher = logoutRequestMatcher;
    }
}
