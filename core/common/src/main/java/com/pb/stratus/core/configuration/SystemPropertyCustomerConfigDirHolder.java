package com.pb.stratus.core.configuration;

import com.pb.stratus.core.exception.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;

public class SystemPropertyCustomerConfigDirHolder 
        implements CustomerConfigDirHolder
{

    public static final String DIR_PROPERTY_NAME 
            = "stratus.customer.config.dir";
    private static final Logger logger =
             Logger.getLogger(SystemPropertyCustomerConfigDirHolder.class);
    private File customerConfigDir;
    
    public SystemPropertyCustomerConfigDirHolder()
    {
        String path = System.getProperty(DIR_PROPERTY_NAME);
        if (StringUtils.isBlank(path))
        {
            logger.error("Could not load Controller properly...." + 
            		DIR_PROPERTY_NAME + " system property not set");
            throw new ConfigurationException("System property '" 
                    + DIR_PROPERTY_NAME + "' has not been set");
        }
        customerConfigDir = new File(path);
        if (!customerConfigDir.isDirectory())
        {
            throw new ConfigurationException("Customer configuration " 
                    + "directory '" + path + "' doesn't exist or is not " 
                    + "a directory");
        }
    }

    public File getCustomerConfigDir()
    {
        return customerConfigDir;
    }

}
