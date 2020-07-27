package com.pb.stratus.controller.exception;

import com.pb.stratus.core.exception.DefaultExceptionConverter;
import org.apache.commons.lang.StringUtils;
import uk.co.graphdata.utilities.contract.Contract;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 
 *
 */
public class CatalogConfigExceptionConverter extends DefaultExceptionConverter
{
   
    private static final String ERROR_TYPE = "ErrorType";
    private static final String PARAM = "Param";
    
    public CatalogConfigExceptionConverter(int errorCode)
    {
        super(errorCode);
    }

    @Override
    public void sendError(Exception ex, HttpServletResponse response)
        throws IOException
    {
        Contract.pre(ex instanceof CatalogConfigException,
            "Exception must be an instance of "
                + CatalogConfigException.class.getName());
        
        CatalogConfigException catalogConfigException = (CatalogConfigException) ex;

        String key = catalogConfigException.getKey();
        String value = catalogConfigException.getValue();
        if(!StringUtils.isBlank(key))
        {
            response.setHeader(ERROR_TYPE, key);
        }
        
        if(!StringUtils.isBlank(key) && !StringUtils.isBlank(value))
        {
            response.setHeader(PARAM, value);  
        }
        super.sendError(ex, response);
    }
}
