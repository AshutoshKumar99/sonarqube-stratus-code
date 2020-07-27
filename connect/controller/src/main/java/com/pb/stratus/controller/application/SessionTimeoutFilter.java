package com.pb.stratus.controller.application;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * When the session destroyed (application/web), at the first request of client, this filter will
 * be fired and a dialog is shown to the user stating that the session has timed out.
 */
public class SessionTimeoutFilter implements Filter {

    /**
     * 
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
        // empty
    }

    /**
     * In this method we check whether the request is ajax and session is valid
     * then we send an error in response else continue the filter chain
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException 
    {
        if ((request instanceof HttpServletRequest)
                && (response instanceof HttpServletResponse)) 
        {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            if ("XMLHttpRequest".equals(httpServletRequest.getHeader("X-Requested-With"))
                    && isSessionInvalid(httpServletRequest)) 
            {
                httpServletResponse.sendError(467, "sessiontimeout");
            }
            else
            {
                filterChain.doFilter(request, response);
            }
        }
    }

    /**
     * This method will return true if the session is valid else return false
     * @param httpServletRequest
     * @return sessionInValid
     */
    private boolean isSessionInvalid(HttpServletRequest httpServletRequest) {
        boolean sessionInValid = (httpServletRequest.getRequestedSessionId() != null)
                && !httpServletRequest.isRequestedSessionIdValid();
        return sessionInValid;
    }

    public void destroy()
    {
        // empty
    }

}