package com.pb.stratus.core.exception;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultExceptionConverter implements HttpExceptionConverter
{

    public static final String STRATUS_ERROR_HEADER_NAME = "StratusErrorMessage";

    private int errorCode;

    public DefaultExceptionConverter()
    {
        this.errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    public DefaultExceptionConverter(int errorCode)
    {
        this.errorCode = errorCode;
    }

    /**
     * Sends the error code of this class to the given response with {@link HttpServletResponse#sendError(int, String)}.
     * The message of <code>x</code> (or the value of <code>x.toString()</code> if <code>getMessage()</code> returns a
     * null or blank value) is set as an HTTP response header with the name {@link #STRATUS_ERROR_HEADER_NAME}. The same
     * message is also passed into {@link HttpServletResponse#sendError(int, String)}.
     * 
     * @param x the exception to send to the client
     * @param response the HTTP servlet response to send the error to.
     * @throws IOException if the response could not be written to.
     */
    public void sendError(Exception x, HttpServletResponse response) throws IOException
    {
        String message = x.getMessage();
        if (StringUtils.isBlank(message))
        {
            message = x.toString();
        }
        // VHL This sets t.getMessage() into the resultant error page
        response.setHeader(STRATUS_ERROR_HEADER_NAME, message);
        // VHL This allows dojo.xhr to retrieve this information from the header
        response.sendError(errorCode, message);
    }

}
