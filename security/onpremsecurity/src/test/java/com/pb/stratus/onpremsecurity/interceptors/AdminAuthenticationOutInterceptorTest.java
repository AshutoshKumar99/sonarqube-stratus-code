package com.pb.stratus.onpremsecurity.interceptors;

import com.pb.stratus.onpremsecurity.token.SpectrumToken;
import com.pb.stratus.onpremsecurity.util.JWTAuthorizationHandler;
import org.apache.cxf.message.Message;
import org.apache.ws.security.util.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 4/2/14
 * Time: 3:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminAuthenticationOutInterceptorTest {

    private AdminAuthenticationOutInterceptor target;
    private Message mockMessage;
    Map<String, List<String>> mockHeaders;
    private Authentication mockAuth = mock(Authentication.class);
    private JWTAuthorizationHandler mockHandler =
            mock(JWTAuthorizationHandler.class);
    private SpectrumToken spectrumToken = new SpectrumToken();
    private static final String CXF_ENDPOINT_PARAM = "org.apache.cxf.message.Message.ENDPOINT_ADDRESS";
    private static final String TM_ENDPOINT_NAME = "TokenManagerService";

    @Before
    public void setup() {
        target = new AdminAuthenticationOutInterceptor();
        mockHeaders = new HashMap<String, List<String>>();
        mockMessage = mock(Message.class);
        when(mockMessage.get(Message.PROTOCOL_HEADERS)).thenReturn(mockHeaders);

        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
        Mockito.when(mockAuth.getPrincipal()).thenReturn("admin");
        Mockito.when(mockAuth.getCredentials()).thenReturn("admin");
    }

    @After
    public void teardown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void testGetTokenRequest() {
        when(mockMessage.get(CXF_ENDPOINT_PARAM)).thenReturn(TM_ENDPOINT_NAME);
        when(mockMessage.get("java.lang.reflect.Method")).thenReturn("TokenManagerService.getAccessExpiringToken");
        target.setUsernameParameter("username");
        target.setPasswordParameter("password");

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("username", "pPublic");
        mockRequest.setParameter("password", "PPassword1");
        ServletRequestAttributes mockAttrib = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(mockAttrib);

        String expectedValue = new String("Basic " + Base64.encode(("pPublic" + ":" + "PPassword1").getBytes()));
        target.handleMessage(mockMessage);
        assertEquals(((List) mockHeaders.get("Authorization")).get(0), expectedValue);
    }

    @Test
    public void testTokenRenewRequest() throws Exception {

        String expectedValue = new String("Basic " + Base64.encode(("adminuser" + ":" + "adminpasscode").getBytes()));
        when(mockMessage.get(CXF_ENDPOINT_PARAM)).thenReturn(TM_ENDPOINT_NAME);
        when(mockMessage.get("java.lang.reflect.Method")).thenReturn("TokenManagerService.getAccessExpiringToken");
        ServletRequestAttributes mockAttrib = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(mockAttrib);
        SecurityContext securityContext = new SecurityContextImpl();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("adminuser");
        when(authentication.getCredentials()).thenReturn("adminpasscode");
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        target.handleMessage(mockMessage);
        assertEquals(((List) mockHeaders.get("Authorization")).get(0), expectedValue);
    }

    @Test
    public void testJWTHeaders() throws Exception {
        String expectedAuthHeader = "Bearer tokenString";
        String expectedCookieHeader = "SESSION=sessionString";
        when(mockMessage.get(CXF_ENDPOINT_PARAM)).thenReturn("END_POINT");
        when(mockMessage.get("java.lang.reflect.Method")).thenReturn("service.x");
        ServletRequestAttributes mockAttrib = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(mockAttrib);
        spectrumToken.setAccessToken("tokenString");
        Map<String, List<String>> jwtHeaders = new HashMap();
        jwtHeaders.put("Authorization", Collections.singletonList(expectedAuthHeader));
        when(mockHandler.getJWTHeaders(false)).thenReturn(jwtHeaders);

        target.setJwtAuthorizationHandler(mockHandler);
        target.handleMessage(mockMessage);
        verify(mockHandler).getJWTHeaders(false);
        assertEquals(expectedAuthHeader, ((List) mockHeaders.get("Authorization")).get(0));

    }

}
