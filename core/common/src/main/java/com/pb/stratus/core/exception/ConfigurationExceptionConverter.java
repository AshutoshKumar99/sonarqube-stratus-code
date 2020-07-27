package com.pb.stratus.core.exception;

import uk.co.graphdata.utilities.contract.Contract;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author vi001ty
 *
 */
public class ConfigurationExceptionConverter extends DefaultExceptionConverter
{
    public static final String CONFIGURATION_ERROR_HEADER_MESSAGE = "Tenant Configuration not found.";

    public ConfigurationExceptionConverter(int errorCode)
    {
        super(errorCode);
    }
    
    /* (non-Javadoc)
     * @see com.pb.stratus.core.exception.DefaultExceptionConverter#sendError(java.lang.Exception, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void sendError(Exception x, HttpServletResponse response)
            throws IOException
    {
        Contract.pre(x instanceof ConfigurationException,
                "Exception must be an instance of " +
                        ConfigurationException.class.getName());
        x = new ConfigurationException(CONFIGURATION_ERROR_HEADER_MESSAGE);
        super.sendError(x, response);
    }
    
    
}
