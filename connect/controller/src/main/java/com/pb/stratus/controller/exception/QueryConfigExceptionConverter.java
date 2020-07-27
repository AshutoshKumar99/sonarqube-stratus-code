package com.pb.stratus.controller.exception;

import com.pb.stratus.core.exception.DefaultExceptionConverter;
import org.apache.commons.lang.StringUtils;
import uk.co.graphdata.utilities.contract.Contract;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class QueryConfigExceptionConverter extends DefaultExceptionConverter
{
   

    private static final String ERROR_TYPE = "ErrorType";
    private static final String PARAM = "Param";
    
    public QueryConfigExceptionConverter(int errorCode)
    {
        super(errorCode);
    }

    @Override
    public void sendError(Exception x, HttpServletResponse response)
        throws IOException
    {
        Contract.pre(x instanceof QueryConfigException,
            "Exception must be an instance of "
                + QueryConfigException.class.getName());
        QueryConfigException ex = (QueryConfigException) x;

        String key =ex.getKey();
        String value = ex.getValue();
        if(!StringUtils.isBlank(key)){

        response.setHeader(ERROR_TYPE, key);

        }
        
        if(!StringUtils.isBlank(key) && !StringUtils.isBlank(value))
        {
            response.setHeader(PARAM, value);  
        }
        super.sendError(x, response);
    }
}
