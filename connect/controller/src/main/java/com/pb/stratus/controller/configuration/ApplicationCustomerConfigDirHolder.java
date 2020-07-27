package com.pb.stratus.controller.configuration;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.pb.stratus.core.configuration.CustomerConfigDirHolder;

import java.io.File;

//import com.pb.stratus.core.configuration.CustomerConfigDirHolder;

import com.pb.stratus.core.exception.ConfigurationException;
/**
 * Reads the Configuration Directory for settings.
 * @Author sa021sh
 */
public class ApplicationCustomerConfigDirHolder
        implements CustomerConfigDirHolder
{
    private static final Logger logger =
            LogManager.getLogger(ApplicationCustomerConfigDirHolder.class);

    public static final String DIR_PROPERTY_NAME 
            = "stratus.customer.config.dir";

    private File customerConfigDir;
    
    public ApplicationCustomerConfigDirHolder()
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
        logger.info(" Startup Param: " + path);

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
