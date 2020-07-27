package com.pb.stratus.controller.action;

import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.configuration.TenantProfile;
import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.core.common.Constants;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorization;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QueryListActionTest
{

    private QueryListAction target =null;
    private final String TABLE_NAME = "table1";
    private final String TENANT_NAME = "tenant1";
    private final String QUERY1 = "query1";
    private final String QUERY2 = "query2";
    private final String QUERY3 = "query3";
    private final String NO_FILTER = "No Filter";
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
    private static final String CUSTOM_FILTER = "Custom Filter";
  

    @Before
    public void setUpAction() throws Exception
    {
        m_authorizationUtils = mock(AuthorizationUtils.class);
        target = new QueryListAction(m_authorizationUtils);
        
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
        when(m_request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME)).thenReturn(TENANT_NAME);
        
        when(m_request.getSession(any(Boolean.class))).thenReturn(m_session);
        when(m_session.getServletContext()).thenReturn(m_context);
        when(m_context.getAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME)).thenReturn(m_application);
        when(m_application.getTenantProfileManager()).thenReturn(m_tenantProfileManager);
        when(m_tenantProfileManager.getProfile(TENANT_NAME)).thenReturn(m_tenantProfile);
        when(m_tenantProfile.getResourceAuthorization(ResourceType.QUERY_CONFIG)).thenReturn(m_resourceAuthorization);
        when(m_securityContext.getAuthentication()).thenReturn(m_authentication);
        
        
       
    }

    private Set<ResourceAuthorizationConfig> createConfigs(List<GrantedAuthority> grantedAuthorities)
    {
        Set<ResourceAuthorizationConfig> configs = new HashSet<ResourceAuthorizationConfig>();
        configs.add(new ResourceAuthorizationConfig(QUERY1, grantedAuthorities));
        configs.add(new ResourceAuthorizationConfig(QUERY2, grantedAuthorities));
        configs.add(new ResourceAuthorizationConfig(QUERY3, grantedAuthorities));
        return configs;
    }

    private void addAdminAuthority(Collection<GrantedAuthority> grantedAuthorities)
    {
        grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_Administrators"));
    }
        

    @Test
    public void emptyConfigListTest()throws Exception{
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        addAdminAuthority(grantedAuthorities);
        Set<ResourceAuthorizationConfig> authorizedConfigs = new HashSet<>();
        when(m_resourceAuthorization.getAuthorizationConfigs(grantedAuthorities,TABLE_NAME)).thenReturn(authorizedConfigs);
        
        when(m_authorizationUtils.getAuthorizeConfigs(
                m_request, ResourceType.QUERY_CONFIG, m_request.getParameter("tableName")))
                .thenReturn(new HashSet());
        QueryListAction.QueryListResponse response = (QueryListAction.QueryListResponse)target.createObject(m_request);
        
        assertTrue(response.getQueryList().contains(NO_FILTER));
        assertTrue(response.getQueryList().contains(CUSTOM_FILTER));
        assertEquals(2, response.getQueryList().size());
        
    }
    
    @Test
    public void configListTest()throws Exception{
        
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        addAdminAuthority(grantedAuthorities);
        Set<ResourceAuthorizationConfig> authorizedConfigs = createConfigs(grantedAuthorities);
        when(m_resourceAuthorization.getAuthorizationConfigs(grantedAuthorities,TABLE_NAME)).thenReturn(authorizedConfigs);
        
        when(m_authorizationUtils.getAuthorizeConfigs(
                m_request, ResourceType.QUERY_CONFIG, m_request.getParameter("tableName")))
                .thenReturn(authorizedConfigs);

        QueryListAction.QueryListResponse response = (QueryListAction.QueryListResponse)target.createObject(m_request);
        assertNotNull(response);
        assertTrue(response.getQueryList().contains(QUERY1));
        assertTrue(response.getQueryList().contains(QUERY2));
        assertTrue(response.getQueryList().contains(QUERY3));
        assertTrue(response.getQueryList().contains(NO_FILTER));
        assertTrue(response.getQueryList().contains(CUSTOM_FILTER));
        
    }

}
