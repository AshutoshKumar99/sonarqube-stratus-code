package com.pb.stratus.core.exception;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Instances of this class are responsible for transforming an exception into an HTTP error code and send it to the
 * client through a given servlet response. Optionally, other steps will be taken, like adding additional information in
 * response headers or the response body.
 * 
 * @author Volker Leidl
 */
public interface HttpExceptionConverter
{

    /**
     * Sends <code>x</code> to the given servlet response.
     * 
     * @param x the exception to send to the client
     * @param resp the response object to send the exception to
     * @throws IOException if the response couldn't be written to
     */
    public void sendError(Exception x, HttpServletResponse resp) throws IOException;

}
