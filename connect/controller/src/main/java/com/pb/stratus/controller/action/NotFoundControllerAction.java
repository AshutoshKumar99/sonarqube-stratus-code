package com.pb.stratus.controller.action;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class unconditionally sends an HTTP error code 404 to the client 
 */
public class NotFoundControllerAction extends BaseControllerAction 
{

    public void execute(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException 
    {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

}
