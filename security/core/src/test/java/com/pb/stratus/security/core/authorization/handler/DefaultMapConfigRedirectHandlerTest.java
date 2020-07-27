package com.pb.stratus.security.core.authorization.handler;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.core.configuration.TenantConfiguration;
import com.pb.stratus.security.core.connect.identity.RequestBasisAccessConfigurationResolver;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorization;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationConfig;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorizationFactory;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyZeroInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TenantConfiguration.class)
public class DefaultMapConfigRedirectHandlerTest
{

    private DefaultMapConfigRedirectHandler target;
    private MockHttpServletRequest m_request;
    private HttpServletResponse m_response;
    private MockServletContext m_context;
    private MockHttpSession m_session;
    private ResourceAuthorization m_resourceAuthorization;
    private ResourceAuthorizationFactory m_resourceAuthorizationFactory;
    private SecurityContext m_securityContext;
    private Authentication m_authentication;
    private RequestBasisAccessConfigurationResolver m_accessResolver;
    private ControllerConfiguration m_configuration;
    private AuthorizationUtils m_authorizationUtils;
    private String TENANT_NAME = "tenant1";
    private String ADMIN_ROLE = "ROLE_Administrators";
    private String USER_ROLE = "ROLE_Users";
    private String PUBLIC_ROLE = "ROLE_Public";
    private String CONFIG1 = "zoom";
    private String CONFIG2 = "alpha";
    private String CONFIG3 = "beta";
    private String CONTEXT_NAME = "connect";
    private String ANONYMOUS_ROLE = "ROLE_ANONYMOUS";
    private String M_SSO_URL = "http://context/mocksso";

    @Before
    public void setUp() throws Exception
    {
        m_request = new MockHttpServletRequest();
        m_response = mock(HttpServletResponse.class);
        m_context = new MockServletContext();
        m_session = new MockHttpSession(m_context);
        m_request.setSession(m_session);
        m_request.setAttribute(Constants.TENANT_ATTRIBUTE_NAME, TENANT_NAME);
        m_resourceAuthorization = mock(ResourceAuthorization.class);
        m_resourceAuthorizationFactory = mock(ResourceAuthorizationFactory.class);
        m_securityContext = mock(SecurityContext.class);
        m_authentication = mock(Authentication.class);
        m_accessResolver = mock(RequestBasisAccessConfigurationResolver.class);
        m_configuration = mock(ControllerConfiguration.class);
        m_authorizationUtils = mock(AuthorizationUtils.class);
        target = new DefaultMapConfigRedirectHandler();
        target.setAccessResolver(m_accessResolver);
        target.setAuthorizationUtils(m_authorizationUtils);
        SecurityContextHolder.setContext(m_securityContext);
    }

    @Test
    public void redirectionTest() throws Exception {
        m_request.setContextPath(CONTEXT_NAME);

        when(m_securityContext.getAuthentication()).thenReturn(m_authentication);

        Collection<GrantedAuthority> userAuthority = new ArrayList<GrantedAuthority>();
        userAuthority.add(new GrantedAuthorityImpl(ADMIN_ROLE + "_" + TENANT_NAME));
        userAuthority.add(new GrantedAuthorityImpl(USER_ROLE + "_" + TENANT_NAME));

        //when(m_authentication.getAuthorities()).thenReturn(userAuthority);
        Mockito.doReturn(userAuthority).when(m_authentication).getAuthorities();

        Set<ResourceAuthorizationConfig> configs = new HashSet<ResourceAuthorizationConfig>();
        configs.add(new ResourceAuthorizationConfig(CONFIG1, getGrantedAuthorityForConfig(USER_ROLE)));
        configs.add(new ResourceAuthorizationConfig(CONFIG2, getGrantedAuthorityForConfig(USER_ROLE)));
        configs.add(new ResourceAuthorizationConfig(CONFIG3, getGrantedAuthorityForConfig(PUBLIC_ROLE)));

        when(m_authorizationUtils.getAuthorizeConfigs(m_request, ResourceType.MAP_CONFIG)).thenReturn(configs);
        when(m_authorizationUtils.getFirstLexicographicEntry(configs)).thenReturn(CONFIG2);

        when(
                m_resourceAuthorization.getAuthorizationConfigs(
                        getGrantedAuthorityForConfig(USER_ROLE), null))
                .thenReturn(configs);

        target.handle(m_request, m_response);
        verify(m_response).sendRedirect("connect/tenant1/?mapcfg=alpha");
    }

    @Test
    public void noConfigAuthorized_SecuredTenantTest() throws Exception
    {
        anonymous();
        when(m_securityContext.getAuthentication())
                .thenReturn(m_authentication);
        when(m_accessResolver.isAnonymousLoginAllowed()).thenReturn(false);
        Collection<GrantedAuthority> userAuthority =
                new ArrayList<GrantedAuthority>();
        userAuthority.add(new GrantedAuthorityImpl(PUBLIC_ROLE + "_" +
                TENANT_NAME));
        //when(m_authentication.getAuthorities()).thenReturn(userAuthority);
        Mockito.doReturn(userAuthority).when(m_authentication).getAuthorities();
        Set<ResourceAuthorizationConfig> configs =
                new HashSet<ResourceAuthorizationConfig>();
        configs.add(new ResourceAuthorizationConfig(CONFIG1, getGrantedAuthorityForConfig(USER_ROLE)));
        configs.add(new ResourceAuthorizationConfig(CONFIG2, getGrantedAuthorityForConfig(USER_ROLE)));
        configs.add(new ResourceAuthorizationConfig(CONFIG3, getGrantedAuthorityForConfig(USER_ROLE)));
        Set<ResourceAuthorizationConfig> authorizedConfigSet = new HashSet<ResourceAuthorizationConfig>();

        when(m_authorizationUtils.getAuthorizeConfigs(m_request, ResourceType.MAP_CONFIG)).thenReturn(authorizedConfigSet);
        when(m_authorizationUtils.isAnonymousUser()).thenReturn(true);
        when(
                m_resourceAuthorization.getAuthorizationConfigs(
                        getGrantedAuthorityForConfig(USER_ROLE), null))
                .thenReturn(configs);
        target.handle(m_request, m_response);
        verifyZeroInteractions(m_response);
    }

    @Test
    public void noConfigAuthorized_publicTenantTest() throws Exception
    {
        anonymous();
        List<GrantedAuthority> userAuthority =
                new ArrayList<GrantedAuthority>();
        userAuthority.add(new GrantedAuthorityImpl(ANONYMOUS_ROLE));
        //when(m_authentication.getAuthorities()).thenReturn(userAuthority);
        Mockito.doReturn(userAuthority).when(m_authentication).getAuthorities();
        m_authentication =
                new AnonymousAuthenticationToken("Stratus", "Guest",
                        userAuthority);

        when(m_securityContext.getAuthentication())
                .thenReturn(m_authentication);
        when(m_accessResolver.isAnonymousLoginAllowed()).thenReturn(true);
        when(m_configuration.getSsoStartUrl()).thenReturn(M_SSO_URL);
        Set<ResourceAuthorizationConfig> configs =
                new HashSet<ResourceAuthorizationConfig>();
        configs.add(new ResourceAuthorizationConfig(CONFIG1, getGrantedAuthorityForConfig(USER_ROLE)));
        configs.add(new ResourceAuthorizationConfig(CONFIG2, getGrantedAuthorityForConfig(USER_ROLE)));
        configs.add(new ResourceAuthorizationConfig(CONFIG3, getGrantedAuthorityForConfig(USER_ROLE)));
        Set<ResourceAuthorizationConfig> authorizedConfigSet = new HashSet<ResourceAuthorizationConfig>();

        when(m_authorizationUtils.getAuthorizeConfigs(
                m_request, ResourceType.MAP_CONFIG))
                .thenReturn(authorizedConfigSet);
        when(m_authorizationUtils.isAnonymousUser()).thenReturn(true);

        when(
                m_resourceAuthorization.getAuthorizationConfigs(
                        getGrantedAuthorityForConfig(USER_ROLE), null))
                .thenReturn(configs);
        target.handle(m_request, m_response);
        verify(m_response).sendRedirect(M_SSO_URL);
    }

    private void anonymous()
    {
        m_request.setContextPath(CONTEXT_NAME);
        PowerMockito.mockStatic(TenantConfiguration.class);
        when(TenantConfiguration.getTenantConfiguration(m_request)).thenReturn(m_configuration);
    }

    private List<GrantedAuthority> getGrantedAuthorityForConfig(String role)
    {
        List<GrantedAuthority> grantedAuthorities = null;
        if (!StringUtils.isBlank(role))
        {
            grantedAuthorities = new ArrayList<GrantedAuthority>();
            if (role.equals(ADMIN_ROLE))
            {
                grantedAuthorities.add(new GrantedAuthorityImpl(ADMIN_ROLE));
            }
            else if (role.equals(USER_ROLE))
            {
                grantedAuthorities.add(new GrantedAuthorityImpl(ADMIN_ROLE));
                grantedAuthorities.add(new GrantedAuthorityImpl(USER_ROLE));
            }
            else if (role.equals(PUBLIC_ROLE))
            {
                grantedAuthorities.add(new GrantedAuthorityImpl(ADMIN_ROLE));
                grantedAuthorities.add(new GrantedAuthorityImpl(USER_ROLE));
                grantedAuthorities.add(new GrantedAuthorityImpl(PUBLIC_ROLE));
            }
        }
        return grantedAuthorities;
    }

}
