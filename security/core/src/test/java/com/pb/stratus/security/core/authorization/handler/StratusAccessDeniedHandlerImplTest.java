package com.pb.stratus.security.core.authorization.handler;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.core.configuration.TenantConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TenantConfiguration.class)
public class StratusAccessDeniedHandlerImplTest
{

    private static final String TENANT_NAME = "tenant";
    private HttpServletRequest m_request;
    private HttpServletResponse m_response;
    private StratusAccessDeniedHandlerImpl target;
    private DefaultMapConfigRedirectHandler m_mapConfigRedirectHandler;
    private RequestDispatcher m_dispatcher;
    private String ERROR_PAGE = "/errorpage.jsp";
    private String MAPCFG_BING = "bing";
    private Authentication m_authentication;
    private SecurityContext m_securityContext;
    private MockServletContext m_context;
    private MockHttpSession m_session;
    private ControllerConfiguration m_configuration;
    private AuthorizationUtils m_authorizationUtils;
    private String M_SSO_URL = "http://context/mocksso";

    @Before
    public void setUp() throws Exception
    {
        m_request = mock(HttpServletRequest.class);
        m_response = mock(HttpServletResponse.class);
        m_context = new MockServletContext();
        m_session = new MockHttpSession(m_context);
        when(m_request.getSession()).thenReturn(m_session);
        when(m_request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME))
                .thenReturn(TENANT_NAME);
        m_dispatcher = mock(RequestDispatcher.class);
        m_mapConfigRedirectHandler =  mock(DefaultMapConfigRedirectHandler.class);
        m_authorizationUtils = mock(AuthorizationUtils.class);
        target = new StratusAccessDeniedHandlerImpl();
        target.setMapconfigRedirecthandler(m_mapConfigRedirectHandler);
        target.setAuthorizationUtils(m_authorizationUtils);
        target.setErrorPage(ERROR_PAGE);
        m_configuration = mock(ControllerConfiguration.class);
        PowerMockito.mockStatic(TenantConfiguration.class);
        when(TenantConfiguration.getTenantConfiguration(m_request)).thenReturn(m_configuration);
    }

    private void authenticateUser(boolean anonymous)
    {
        if (anonymous)
        {
            List<GrantedAuthority> userAuthority =
                    new ArrayList<GrantedAuthority>();
            userAuthority.add(new GrantedAuthorityImpl("ROLE_ANONYMOUS"));
            m_authentication =
                    new AnonymousAuthenticationToken("Stratus", "Guest",
                            userAuthority);
        }
        else
        {
            m_authentication = mock(Authentication.class);
        }
        m_securityContext = mock(SecurityContext.class);
        when(m_securityContext.getAuthentication())
                .thenReturn(m_authentication);
        SecurityContextHolder.setContext(m_securityContext);
    }

    @Test
    public void defaultRedirectTest() throws Exception
    {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");
        when(m_request.getParameter(AuthorizationUtils.REQUEST_PARAM_MAPCFG))
                .thenReturn(null);
        when(m_request.getPathInfo()).thenReturn(null);
        when(m_request.getRequestDispatcher(anyString())).thenReturn(m_dispatcher);
        authenticateUser(true);

        target.handle(m_request, m_response, ex);
        verify(m_dispatcher).forward(m_request, m_response);
        verify(m_mapConfigRedirectHandler).handle(m_request, m_response);
    }

    // user will be rediredted to login page if mapcfg param is in request.
    @Test
    public void loginPageRedirectTest() throws Exception
    {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");
        when(m_request.getParameter(AuthorizationUtils.REQUEST_PARAM_MAPCFG))
                .thenReturn(MAPCFG_BING);
        when(m_request.getPathInfo()).thenReturn(null);
        when(m_configuration.getSsoStartUrl()).thenReturn(M_SSO_URL);
        when(m_authorizationUtils.isAnonymousUser()).thenReturn(true);
        when(m_request.getRequestDispatcher(anyString())).thenReturn(m_dispatcher);
        authenticateUser(true);
            target.handle(m_request, m_response, ex);
        verifyZeroInteractions(m_mapConfigRedirectHandler);
        verify(m_response).sendRedirect(M_SSO_URL);
    }

    @Test
    public void errorPageRedirectTest() throws Exception
    {
        AccessDeniedException ex = new AccessDeniedException("Access Denied");
        when(m_request.getParameter(AuthorizationUtils.REQUEST_PARAM_MAPCFG))
                .thenReturn(MAPCFG_BING);
        when(m_request.getPathInfo()).thenReturn(null);
        when(m_request.getRequestDispatcher(ERROR_PAGE)).thenReturn(m_dispatcher);
        when(m_authorizationUtils.isAnonymousUser()).thenReturn(false);
        when(m_request.getRequestDispatcher(anyString())).thenReturn(m_dispatcher);
        authenticateUser(false);
        target.handle(m_request, m_response, ex);
        verifyZeroInteractions(m_mapConfigRedirectHandler);
        verify(m_dispatcher).forward(m_request, m_response);
    }
}
