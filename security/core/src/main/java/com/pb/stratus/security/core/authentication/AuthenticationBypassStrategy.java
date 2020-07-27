package com.pb.stratus.security.core.authentication;

import javax.servlet.ServletRequest;

/**
 * A strategy interface for the authentication filters to decide whether the
 * current request should be bypassed from the authentication being done by the
 * filter. The order in which the strategies appear in the filter is important!
 */
public interface AuthenticationBypassStrategy {
    boolean shouldBypassAuthentication(ServletRequest req);
}
