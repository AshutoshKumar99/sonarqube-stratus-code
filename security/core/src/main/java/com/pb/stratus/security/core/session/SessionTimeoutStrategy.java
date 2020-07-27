package com.pb.stratus.security.core.session;

import javax.servlet.http.HttpServletRequest;

/**
 * SessionTimeoutStrategy
 * User: GU003DU
 * Date: 7/9/14
 * Time: 3:20 PM
 */
public interface SessionTimeoutStrategy {

    void setMaxInactiveInterval(HttpServletRequest request);
}
