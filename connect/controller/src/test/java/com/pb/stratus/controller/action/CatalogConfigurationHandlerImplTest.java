package com.pb.stratus.controller.action;

import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.configuration.TenantProfile;
import com.pb.stratus.controller.configuration.TenantProfileManager;
import com.pb.stratus.controller.exception.CatalogConfigException;
import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.ConfigFileType;
import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorization;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import com.pb.stratus.security.core.util.AuthorizationUtils;
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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CatalogConfigurationHandlerImplTest {
	
	private HttpServletRequest mRequest;
	private ConfigFileType CONFIG_TYPE = ConfigFileType.CATALOG;
	private CatalogConfigurationHandlerImpl catalogImpl;
	private ConfigReader mockConfigReader;
    private Authentication authentication;
    private SecurityContext m_context;
    private HttpSession  m_session;
    private ServletContext m_servletContext;
    private Application m_application;
    private TenantProfileManager m_tenantProfileManager;
    private TenantProfile m_tenantProfile;
    private ResourceAuthorization m_resourceAuthorization;
    private AuthorizationUtils m_authorizationUtils;
    
    private static final String USER_ROLE = "Users";
    private static final String ADMIN_ROLE = "Administrators";
    private static final String TENANT_NAME = "tenant1";
    private static final String CONFIG1 = "config1";
    private static final String CONFIG2 = "config2";

	
	@Before
    public void setUpAction() throws Exception
    {
		mRequest = mock(HttpServletRequest.class);
		mockConfigReader = mock(ConfigReader.class);
		
	    m_context = mock(SecurityContext.class);
	    m_session = mock(HttpSession.class);
	    m_servletContext = mock(ServletContext.class);
	    m_application = mock(Application.class);
	    m_tenantProfileManager = mock(TenantProfileManager.class);
	    m_tenantProfile = mock(TenantProfile.class);
	    m_resourceAuthorization = mock(ResourceAuthorization.class);
        m_authorizationUtils = mock(AuthorizationUtils.class);
	    SecurityContextHolder.setContext(m_context);
	    
	    authentication = mock(Authentication.class);
        when(mRequest.getAttribute(Constants.TENANT_ATTRIBUTE_NAME)).thenReturn(TENANT_NAME);
        when(m_context.getAuthentication()).thenReturn(authentication);
        Collection<GrantedAuthority> userAuthorities = new ArrayList<GrantedAuthority>();


        //when(authentication.getAuthorities()).thenReturn(userAuthorities);
        Mockito.doReturn(userAuthorities).when(authentication).getAuthorities();
        
        when(mRequest.getSession(any(Boolean.class))).thenReturn(m_session);
        when(m_session.getServletContext()).thenReturn(m_servletContext);
        when(m_servletContext.getAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME)).thenReturn(m_application);
        when(m_application.getTenantProfileManager()).thenReturn(m_tenantProfileManager);
        when(m_tenantProfileManager.getProfile(TENANT_NAME)).thenReturn(m_tenantProfile);
        when(m_tenantProfile.getResourceAuthorization(ResourceType.FMN_CONFIG)).thenReturn(m_resourceAuthorization);
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_"+ADMIN_ROLE));
        grantedAuthorities.add(new GrantedAuthorityImpl("ROLE_"+USER_ROLE));
        Set<ResourceAuthorizationConfig> expectedConfigs = new HashSet<ResourceAuthorizationConfig>();
        expectedConfigs.add(new ResourceAuthorizationConfig(CONFIG1, grantedAuthorities));
        expectedConfigs.add(new ResourceAuthorizationConfig(CONFIG2, grantedAuthorities));
        when(m_resourceAuthorization.getAuthorizationConfigs(grantedAuthorities, null)).thenReturn(expectedConfigs);
        
        catalogImpl = new CatalogConfigurationHandlerImpl();
		
    }

	/**
	 * When the queryString is empty
	 * @throws Exception
	 */
    @Test
    public void handleAuthorizationEmptyQueryStringTest() throws Exception {
    	
        when(mRequest.getQueryString()).thenReturn("");
        when(m_authorizationUtils.isConfigurationAuthorized(mRequest, CONFIG1, CONFIG_TYPE)).thenReturn(true);

        Boolean isAuthorized = catalogImpl.handleAuthorization(mRequest, CONFIG1, CONFIG_TYPE,
                mockConfigReader, m_authorizationUtils);
        assertTrue(isAuthorized.equals(Boolean.TRUE));
    }
    
    /**
	 * When the queryString is present but the table name is not present in the configuration file
	 * In this case it will throw the exception
	 * @throws Exception
	 */
    @Test (expected=CatalogConfigException.class)
    public void handleAuthorizationQueryStringExceptionTest() throws Exception {
    	
        when(mRequest.getQueryString()).thenReturn("showNearest=ConservationAreas1");
        String configFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><find-nearest-config><categories><category><layers><layer><name>ConservationAreas</name></layer></layers></category></categories></find-nearest-config>";
        InputStream is = new ByteArrayInputStream(configFile.getBytes("UTF-8"));
        when(mockConfigReader.getConfigFile(any(String.class), 
                any(ConfigFileType.class))).thenReturn(is);

        when(m_authorizationUtils.isConfigurationAuthorized(mRequest, CONFIG1, CONFIG_TYPE)).thenReturn(true);
        catalogImpl.handleAuthorization(mRequest, CONFIG1, CONFIG_TYPE, mockConfigReader, m_authorizationUtils);
    }
    
    /**
	 * When the queryString is present but the table name is not present in the configuration file
	 * @throws Exception
	 */
    @Test
    public void handleAuthorizationQueryStringTest() throws Exception {
    	
        when(mRequest.getQueryString()).thenReturn("showNearest=ConservationAreas");
        String configFile = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><find-nearest-config><categories><category><layers><layer><name>ConservationAreas</name></layer></layers></category></categories></find-nearest-config>";
        InputStream is = new ByteArrayInputStream(configFile.getBytes("UTF-8"));
        when(mockConfigReader.getConfigFile(any(String.class), 
                any(ConfigFileType.class))).thenReturn(is);

        when(m_authorizationUtils.isConfigurationAuthorized(mRequest, CONFIG1, CONFIG_TYPE)).thenReturn(true);

        Boolean isAuthorized = catalogImpl.handleAuthorization(mRequest, CONFIG1, CONFIG_TYPE,
                mockConfigReader, m_authorizationUtils);
        assertTrue(isAuthorized.equals(Boolean.TRUE));
    }

}
