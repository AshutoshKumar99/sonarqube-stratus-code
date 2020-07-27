package com.pb.stratus.controller.exception;

import com.pb.stratus.controller.IdentifierResolveFailureException;
import com.pb.stratus.core.exception.DefaultExceptionConverter;
import uk.co.graphdata.utilities.contract.Contract;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
public class IdentifierResolveFailuerExceptionConverter extends DefaultExceptionConverter
{
    public static final String IDENTIFIER_NAME_HEADER_NAME = "IdentifierName";

    public IdentifierResolveFailuerExceptionConverter(int errorCode)
    {
        super(errorCode);
    }

    @Override
    public void sendError(Exception x, HttpServletResponse response) throws IOException
    {
        Contract.pre(x instanceof IdentifierResolveFailureException, "Exception must be an instance of "
                + IdentifierResolveFailureException.class.getName());
        IdentifierResolveFailureException upx = (IdentifierResolveFailureException) x;
        response.setHeader(IDENTIFIER_NAME_HEADER_NAME, upx.getIdentifierName());
        super.sendError(x, response);
    }
}


