package com.pb.stratus.controller;

import com.pb.stratus.controller.action.ControllerAction;
import com.pb.stratus.controller.action.ControllerActionFactory;
import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.configuration.TenantProfile;
import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.core.common.Constants;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

public class ControllerServletTest {

    private HttpServletRequest m_request;
    private ControllerAction m_controllerAction;

    @Before
    public void Setup()
    {
        final String pathInfo = "/controller/authentication/getinfo";
        m_request = mock(HttpServletRequest.class);
        when(m_request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME)).thenReturn("stratus");
        when(m_request.getPathInfo()).thenReturn(pathInfo);

        HttpSession mockSession = mock(HttpSession.class);
        when(m_request.getSession(any(Boolean.class))).thenReturn(mockSession);

        ServletContext mockServletContext = mock(ServletContext.class);
        when(mockSession.getServletContext()).thenReturn(mockServletContext);

        Application mockApplication = mock(Application.class);
        when(mockServletContext.getAttribute( ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME)).thenReturn(mockApplication);

        TenantProfileManager mockTenantProfileManager = mock(TenantProfileManager.class);
        when(mockApplication.getTenantProfileManager()).thenReturn(mockTenantProfileManager);

        TenantProfile tenantProfile = mock(TenantProfile.class);
        when(mockTenantProfileManager.getProfile(any(String.class))).thenReturn(tenantProfile);

        ControllerActionFactory actionFactory = mock(ControllerActionFactory.class);
        when(tenantProfile.getActionFactory()).thenReturn(actionFactory);

        m_controllerAction = mock(ControllerAction.class);
        when(tenantProfile.getActionFactory().getControllerAction(pathInfo)).thenReturn(m_controllerAction);
    }

    @Test
    public void doingItsJobTest() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        doNothing().when(m_controllerAction).execute(m_request, response);
        ControllerServlet controllerServlet = new ControllerServlet();
        controllerServlet.service(m_request, response);
    }
}
