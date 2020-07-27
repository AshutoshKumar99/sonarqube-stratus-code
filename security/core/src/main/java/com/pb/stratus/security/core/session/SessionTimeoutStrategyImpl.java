/**
 * SessionTimeoutStrategyImpl
 * Sets maximum allowed value of session inactive interval as per tenant configuration.
 * User: GU003DU
 * Date: 7/9/14
 * Time: 3:21 PM
 */

package com.pb.stratus.security.core.session;

import com.pb.stratus.core.configuration.TenantProfileHelper;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionTimeoutStrategyImpl implements SessionTimeoutStrategy {

    private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    /**
     *  Set maximum session inactive interval for a logged-in user  as per tenant
     *  configuration setting.
     * @param request
     */
    @Override
    public void setMaxInactiveInterval(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (session != null && authentication != null && !authenticationTrustResolver.isAnonymous(authentication)) {
            session.setMaxInactiveInterval(TenantProfileHelper.getSessionTimeout(request));
        }
    }
}
