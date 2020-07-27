package com.pb.stratus.controller.exception;

import com.pb.stratus.controller.UnknownPropertyException;
import com.pb.stratus.core.exception.DefaultExceptionConverter;
import uk.co.graphdata.utilities.contract.Contract;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UnknownPropertyExceptionConverter extends DefaultExceptionConverter
{

    public static final String PROPERTY_NAME_HEADER_NAME = "PropertyName";

    public UnknownPropertyExceptionConverter(int errorCode)
    {
        super(errorCode);
    }

    @Override
    public void sendError(Exception x, HttpServletResponse response) throws IOException
    {
        Contract.pre(x instanceof UnknownPropertyException, "Exception must be an instance of "
                + UnknownPropertyException.class.getName());
        UnknownPropertyException upx = (UnknownPropertyException) x;
        response.setHeader(PROPERTY_NAME_HEADER_NAME, upx.getPropertyName());
        super.sendError(x, response);
    }

}
