package com.pb.stratus.controller;

import com.pb.stratus.controller.action.ControllerAction;
import com.pb.stratus.controller.configuration.TenantProfile;
import com.pb.stratus.controller.configuration.TenantProfileManagerImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ControllerServlet extends HttpServlet {

    private static final long serialVersionUID = -4560607137658830579L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        TenantProfile tenantProfile = TenantProfileManagerImpl.getRequestProfile(request);
        String pathInfo = request.getPathInfo();
        ControllerAction action = tenantProfile.getActionFactory()
                .getControllerAction(pathInfo);
        action.execute(request, response);
    }
}

