package com.pb.stratus.core.configuration;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 1/27/14
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class TenantConfiguration {

    private static final String TENANT_PROFILE_MGR_CLASS = "com.pb.stratus.controller.configuration.TenantProfileManagerImpl";
    private static final String TENANT_GET_CONFIG_METHOD = "getTenantConfiguration";
    private static Method getTenantConfigurationMethod = null;
    private static Logger logger = Logger.getLogger(TenantConfiguration.class);
    private CustomerConfigDirHolder configDirHolder;
    private TenantNameHolder tenantNameHolder;

    static {
        try {
            Class clazz = Class.forName(TENANT_PROFILE_MGR_CLASS);
            getTenantConfigurationMethod = clazz.getMethod(TENANT_GET_CONFIG_METHOD, HttpServletRequest.class);
        } catch (Throwable e) {
            logger.error(e);
        }
    }

    public static ControllerConfiguration getTenantConfiguration(HttpServletRequest request) {
        ControllerConfiguration configuration = null;
        try {
            configuration = (ControllerConfiguration) getTenantConfigurationMethod.invoke(null, request);
        } catch (InvocationTargetException e) {
            logger.error(e.getTargetException());
        } catch (Throwable e) {
            logger.error(e);
        }
        return configuration;
    }

    public TenantNameHolder getTenantNameHolder() {
        return tenantNameHolder;
    }

    public void setTenantNameHolder(TenantNameHolder tenantNameHolder) {
        this.tenantNameHolder = tenantNameHolder;
    }

    public TenantConfiguration(CustomerConfigDirHolder configDirHolder) {
        this.configDirHolder = configDirHolder;
    }

    public CustomerConfigDirHolder getConfigDirHolder() {
        return configDirHolder;
    }
}
