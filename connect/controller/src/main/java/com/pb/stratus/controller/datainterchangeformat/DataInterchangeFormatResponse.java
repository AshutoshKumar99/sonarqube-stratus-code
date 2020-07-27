package com.pb.stratus.controller.datainterchangeformat;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Response sent to the client can be in various formats like Json, Csv ,
 * xml etc.
 * This is the general Interface for such responses. All such DataInterchange
 * formats must implement this interface.
 */
public interface DataInterchangeFormatResponse
{
    /**
     * Given the response object and an arbitrary object various
     * implementations must convert the Object into respective format the
     * implementation is responsible for.
     * @param response HttpServletResponse
     * @param results Object that needs to be converted to a particular format.
     * @throws IOException
     */
    public void send(HttpServletResponse response, Object results)
            throws IOException;

    /**
     * Support for adding cookie to the response.
     * @param response
     * @param cookies
     */
    public void addCookieToResponse(HttpServletResponse response,
            Cookie...cookies);
}
