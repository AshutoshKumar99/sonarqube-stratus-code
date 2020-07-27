package com.pb.stratus.controller.exception;

import com.pb.stratus.controller.UnknownTableException;
import com.pb.stratus.core.exception.DefaultExceptionConverter;
import uk.co.graphdata.utilities.contract.Contract;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UnknownTableExceptionConverter extends DefaultExceptionConverter
{
    public static final String TABLE_NAME_HEADER_NAME = "TableName";

    public UnknownTableExceptionConverter(int errorCode)
    {
        super(errorCode);
    }

    @Override
    public void sendError(Exception x, HttpServletResponse response)
            throws IOException
    {
        Contract.pre(x instanceof UnknownTableException,
                "Exception must be an instance of "
                        + UnknownTableException.class.getName());
        UnknownTableException utx = (UnknownTableException) x;
        response.setHeader(TABLE_NAME_HEADER_NAME, utx.getTableName());
        super.sendError(x, response);
    }
}
