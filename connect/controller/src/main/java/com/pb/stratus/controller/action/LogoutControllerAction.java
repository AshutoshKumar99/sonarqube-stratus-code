package com.pb.stratus.controller.action;

import com.pb.stratus.core.common.Constants;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: sh003bh
 * Date: 10/30/11
 * Time: 2:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogoutControllerAction extends BaseControllerAction {
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tenantName = (String)request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME);
        String url = request.getContextPath() +  "/" + tenantName;
        request.setAttribute("sign.in.url", url);
        RequestDispatcher rd = request.getRequestDispatcher("/signIn.jsp");
        rd.forward(request, response);

    }
}
