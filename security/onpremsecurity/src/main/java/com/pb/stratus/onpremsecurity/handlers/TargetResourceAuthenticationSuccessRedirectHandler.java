package com.pb.stratus.onpremsecurity.handlers;

import com.pb.stratus.onpremsecurity.util.HttpSessionRequestCache;
import com.pb.stratus.security.core.authentication.RequestCache;
import com.pb.stratus.security.core.session.SessionTimeoutStrategy;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

/**
 * After a successful Authentication, redirects the user to a location saved before  authentication commence.
 */
public class TargetResourceAuthenticationSuccessRedirectHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();
    private SessionTimeoutStrategy sessionTimeoutStrategy;

    private static final Logger logger = Logger.getLogger(TargetResourceAuthenticationSuccessRedirectHandler.class);

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetResourceUriEncoded = requestCache.getRequest(request);

        //gu003du:CONN-18257- set session timeout when corresponding strategy is configured.
        if (sessionTimeoutStrategy != null) {
            sessionTimeoutStrategy.setMaxInactiveInterval(request);
        }
        String redirect = request.getParameter("redirect");
        if(redirect != null && redirect.equalsIgnoreCase("false")){
            response.setHeader("login_status","success");
            PrintWriter out = response.getWriter();
            out.print("success");
        }else {
            // redirect to requested Url
            if (StringUtils.isNotBlank(targetResourceUriEncoded)) {
                response.sendRedirect(targetResourceUriEncoded);
                logger.info("Redirecting to saved request after successful authentication.");
            }
        }
    }

    public void setRequestCache(HttpSessionRequestCache requestCache) {
        this.requestCache = requestCache;
    }

    public SessionTimeoutStrategy getSessionTimeoutStrategy() {
        return sessionTimeoutStrategy;
    }

    public void setSessionTimeoutStrategy(SessionTimeoutStrategy sessionTimeoutStrategy) {
        this.sessionTimeoutStrategy = sessionTimeoutStrategy;
    }
}
