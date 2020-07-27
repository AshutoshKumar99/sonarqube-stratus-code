package com.pb.stratus.controller.action;

import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.configuration.TenantProfile;
import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.controller.exception.QueryConfigException;
import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.FileSystemConfigReader;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorization;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import net.sf.json.JSON;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.*;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryDataLoadActionTest extends ControllerActionTestBase
{

    private QueryDataLoadAction action = null;
    private FileSystemConfigReader mockConfigReader = null;
    
    private final String TABLE_NAME = "planningApplications";
    private final String TENANT_NAME = "tenant1";
    private final String QUERY_NAME = "SampleQueryData";
    private final String QUERY1 = "SampleQueryData";
    private final String QUERY2 = "query2";
    private final String QUERY3 = "query3";
    private HttpServletRequest m_request;
    private HttpSession  m_session;
    private ServletContext m_context;
    private Application m_application;
    private TenantProfileManager m_tenantProfileManager;
    private TenantProfile m_tenantProfile;
    private ResourceAuthorization m_resourceAuthorization;
    private SecurityContext m_securityContext;
    private Authentication m_authentication;
    private AuthorizationUtils m_authorizationUtils;

    @Before
    public void setUpAction() throws Exception
    {
        mockConfigReader = new FileSystemConfigReader(new File(
                "src\\test\\resources\\com\\pb\\stratus\\controller\\action")
                .getAbsolutePath(), "stratus");

        action = new QueryDataLoadAction(mockConfigReader);
        
        // object mocking
        m_request = mock(HttpServletRequest.class);
        m_session = mock(HttpSession.class);
        m_context = mock(ServletContext.class);
        m_application = mock(Application.class);
        m_tenantProfileManager = mock(TenantProfileManager.class);
        m_tenantProfile = mock(TenantProfile.class);
        m_resourceAuthorization = mock(ResourceAuthorization.class);
        m_securityContext = mock(SecurityContext.class);
        m_authentication = mock(Authentication.class);

        SecurityContextHolder.setContext(m_securityContext);
        
        // method mocking
        when(m_request.getParameter("tableName")).thenReturn(TABLE_NAME);
        when(m_request.getParameter("queryName")).thenReturn(QUERY_NAME);
        when(m_request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME)).thenReturn(TENANT_NAME);
        
        when(m_request.getSession(any(Boolean.class))).thenReturn(m_session);
        when(m_session.getServletContext()).thenReturn(m_context);
        when(m_context.getAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME)).thenReturn(m_application);
        when(m_application.getTenantProfileManager()).thenReturn(m_tenantProfileManager);
        when(m_tenantProfileManager.getProfile(TENANT_NAME)).thenReturn(m_tenantProfile);
        when(m_tenantProfile.getResourceAuthorization(ResourceType.QUERY_CONFIG)).thenReturn(m_resourceAuthorization);
        when(m_securityContext.getAuthentication()).thenReturn(m_authentication);
    }

    @Test (expected = QueryConfigException.class)
    public void checkRequestParameters() throws Exception
    {
        when(m_request.getParameter("tableName")).thenReturn("");
        when(m_request.getParameter("queryName")).thenReturn("");
        action.createObject(m_request);
	}
}
