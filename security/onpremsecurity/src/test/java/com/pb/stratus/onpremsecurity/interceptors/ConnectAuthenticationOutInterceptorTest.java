
/**
 * Unit Test cases for ConnectAuthenticationOutInterceptor.
 * User: vi001ty
 * Date: 4/2/14
 * Time: 3:36 PM
 */


package com.pb.stratus.onpremsecurity.interceptors;

import com.pb.stratus.onpremsecurity.token.SpectrumToken;
import com.pb.stratus.onpremsecurity.util.JWTAuthorizationHandler;
import com.pb.stratus.security.core.connect.identity.RequestBasisAccessConfigurationResolver;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import org.apache.cxf.message.Message;
import org.apache.ws.security.util.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConnectAuthenticationOutInterceptorTest {
    private ConnectAuthenticationOutInterceptor target;
    private Message mockMessage;
    Map<String, List<String>> mockHeaders;
    private Authentication mockAuth;
    private AuthorizationUtils mockAuthorizationUtils;
    private RequestBasisAccessConfigurationResolver mockRequestBasisAccessConfigurationResolver;
    private HttpServletRequest request;
    private static final String CXF_ENDPOINT_PARAM = "org.apache.cxf.message.Message.ENDPOINT_ADDRESS";
    private static final String AM_ENDPOINT_NAME = "TokenManagerService";
    private ServletRequestAttributes requestAttributes;
    private HttpSession session;
    private JWTAuthorizationHandler mockHandler;
    private SpectrumToken spectrumToken = new SpectrumToken();

    @Before
    public void setup() {
        target = new ConnectAuthenticationOutInterceptor();
        mockHeaders = new HashMap();
        mockMessage = mock(Message.class);
        mockAuthorizationUtils = mock(AuthorizationUtils.class);
        mockAuth = mock(Authentication.class);
        mockHandler = mock(JWTAuthorizationHandler.class);
        mockRequestBasisAccessConfigurationResolver = mock(RequestBasisAccessConfigurationResolver.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        request.setParameter("username", "pPublic");
        request.setParameter("password", "PPassword1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        target.setAuthorizationUtils(mockAuthorizationUtils);
        target.setRequestBasisAccessConfigurationResolver(mockRequestBasisAccessConfigurationResolver);
        when(mockMessage.get(Message.PROTOCOL_HEADERS)).thenReturn(mockHeaders);
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        spectrumToken.setSession("123");
        spectrumToken.setAccessToken("abc");
        spectrumToken.setUserId("admin");
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
        Mockito.when(mockAuth.getPrincipal()).thenReturn("admin");
        Mockito.when(mockAuth.getCredentials()).thenReturn("admin");

    }

    @After
    public void teardown() {
        RequestContextHolder.resetRequestAttributes();
        SecurityContextHolder.clearContext();
    }

    /**
     * It should add a basic authentication header to the message if current request is for tokenmanager endpoint.
     */
    @Test
    public void testForTokenRequest() {
        when(mockMessage.get(CXF_ENDPOINT_PARAM)).thenReturn(AM_ENDPOINT_NAME);
        when(mockMessage.get("java.lang.reflect.Method")).thenReturn("TokenManagerService.getAccessExpiringToken");
        target.setUsernameParameter("username");
        target.setPasswordParameter("password");

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("username", "pPublic");
        mockRequest.setParameter("password", "PPassword1");
        ServletRequestAttributes mockAttrib = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(mockAttrib);

        String expectedValue = new String("Basic " + Base64.encode(("pPublic" + ":" + "PPassword1").getBytes()));
        SecurityContextHolder.getContext().setAuthentication(null);
        target.handleMessage(mockMessage);
        assertEquals(mockHeaders.get("Authorization").get(0), expectedValue);
    }

    /**
     * It should use credentials from security context for renewing the security token.     *
     */
    @Test
    public void testForTokenRenewalRequest() {
        when(mockMessage.get(CXF_ENDPOINT_PARAM)).thenReturn(AM_ENDPOINT_NAME);
        when(mockMessage.get("java.lang.reflect.Method")).thenReturn("TokenManagerService.getAccessExpiringToken");
        target.setUsernameParameter("username");
        target.setPasswordParameter("password");
        when(mockAuthorizationUtils.isAnonymousUser()).thenReturn(false);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        ServletRequestAttributes mockAttrib = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(mockAttrib);
        Authentication authn =  mock(Authentication.class);
        when (authn.getPrincipal()).thenReturn("pPublic");
        when (authn.getCredentials()).thenReturn("PPassword1");
        SecurityContextHolder.getContext().setAuthentication(authn);

        String expectedValue = new String("Basic " + Base64.encode(("pPublic" + ":" + "PPassword1").getBytes()));

        target.handleMessage(mockMessage);
        assertEquals(mockHeaders.get("Authorization").get(0), expectedValue);
    }


    /**
     * It should add Bearer token if the request is not for tokenmanager service endpoint .
     * @throws Exception
     */
    @Test
    public void testBearerAuthenticationRequest() throws Exception {

        String expectedValue = new String("Bearer abc");
        Map<String, List<String>> jwtHeaders = new HashMap();
        jwtHeaders.put("Authorization", Collections.singletonList(expectedValue));
        when(mockHandler.getJWTHeaders(false)).thenReturn(jwtHeaders);
        when(mockAuthorizationUtils.isAnonymousUser()).thenReturn(false);
        when(mockMessage.get(CXF_ENDPOINT_PARAM)).thenReturn("NOT_AM");
        target.setJwtAuthorizationHandler(mockHandler);
        target.handleMessage(mockMessage);
        assertEquals(mockHeaders.get("Authorization").get(0), expectedValue);
    }

}
