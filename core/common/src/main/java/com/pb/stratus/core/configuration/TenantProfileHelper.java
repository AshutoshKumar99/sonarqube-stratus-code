/**
 * TenantProfileHelper
 *
 * This class is a stopgap, to avoid the cyclic dependencies between security
 * and controller modules. It should be removed when security layer can access
 * Tenant specific attributes without a need for dependency on controller module.
 *
 * User: GU003DU
 * Date: 7/3/14
 * Time: 5:08 PM
 */

package com.pb.stratus.core.configuration;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class TenantProfileHelper {
    private static final String TENANT_PROFILE_MGR_CLASS = "com.pb.stratus.controller.configuration.TenantProfileManagerImpl";
    private static final String GET_SESSION_TIMEOUT_METHOD = "getSessionTimeout";
    private static Method getSessionTimeoutMethod = null;
    private static Logger logger = Logger.getLogger(TenantProfileHelper.class);
    private static final int DEFAULT_SESSION_TIMEOUT = 30;

    static {
        try {
            Class clazz = Class.forName(TENANT_PROFILE_MGR_CLASS);
            getSessionTimeoutMethod = clazz.getMethod(GET_SESSION_TIMEOUT_METHOD, HttpServletRequest.class);
        } catch (Throwable e) {
            logger.error(e);
        }
    }

    public static int getSessionTimeout(HttpServletRequest request) {
        int sessionTimeout = DEFAULT_SESSION_TIMEOUT;
        try {
            sessionTimeout = (Integer) getSessionTimeoutMethod.invoke(null, request);
        } catch (Throwable e) {
            logger.error(e);
        }
        return sessionTimeout;
    }
}
