package com.pb.stratus.core.configuration;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.exception.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class SystemPropertyTenantNameHolder implements TenantNameHolder
{
    private static final Logger logger =
             Logger.getLogger(SystemPropertyTenantNameHolder.class);
    private String tenantName;
    
    public SystemPropertyTenantNameHolder()
    {
        tenantName = System.getProperty(Constants.TENANT_NAME);
        if (StringUtils.isBlank(tenantName))
        {
            logger.error("Could not load Controller properly...." + 
                    Constants.TENANT_NAME + " system property not set");
            throw new ConfigurationException("System property '" 
                    + Constants.TENANT_NAME + "' not set");
        }
    }

    public String getTenantName()
    {
        return tenantName;
    }

}
