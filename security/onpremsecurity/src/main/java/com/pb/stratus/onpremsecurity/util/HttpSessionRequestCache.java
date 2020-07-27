package com.pb.stratus.onpremsecurity.util;

import com.pb.stratus.security.core.authentication.RequestCache;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/20/14
 * Time: 7:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class HttpSessionRequestCache implements RequestCache {

    private boolean createSessionAllowed = true;
    private static final Logger logger= Logger.getLogger(HttpSessionRequestCache.class);

    private String SAVED_REQUEST_URL_KEY = "SECURITY_SAVED_REQUEST_URL_KEY";
    @Override
    public void saveRequest(HttpServletRequest request, String requestUrl) {
        if (createSessionAllowed || request.getSession() != null) {
            request.getSession().setAttribute(SAVED_REQUEST_URL_KEY, requestUrl);
            logger.info("Original request saved to be redirected back after authentication");
        }
    }

    @Override
    public String getRequest(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String)session.getAttribute(SAVED_REQUEST_URL_KEY);
        }
        return null;
    }

    public void setCreateSessionAllowed(boolean createSessionAllowed) {
        this.createSessionAllowed = createSessionAllowed;
    }
}
