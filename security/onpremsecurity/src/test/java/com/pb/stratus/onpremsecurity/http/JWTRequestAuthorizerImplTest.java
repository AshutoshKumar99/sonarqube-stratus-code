package com.pb.stratus.onpremsecurity.http;

import com.pb.stratus.onpremsecurity.util.JWTAuthorizationHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by VI001TY on 3/28/2016.
 */
public class JWTRequestAuthorizerImplTest {

    private JWTRequestAuthorizerImpl target;
    private ClientHttpRequest clientRequest;
    private JWTAuthorizationHandler mockHandler;
    private HttpHeaders mockHeader;
    private static final String AUTH_HEADER = "Authorization";
    public static final String SPECTRUM_TOKEN = "SpectrumToken";

    @Before
    public void setup(){
        target = new JWTRequestAuthorizerImpl();
        clientRequest = mock(ClientHttpRequest.class);

        mockHeader = mock(HttpHeaders.class);
        mockHandler = mock(JWTAuthorizationHandler.class);
        when(clientRequest.getHeaders()).thenReturn(mockHeader);
    }

    @Test
    public void testIsAuthorized(){
        when(mockHeader.getFirst(AUTH_HEADER)).thenReturn("Bearer ABCd");
        assertTrue(target.isAuthorized(clientRequest));
        when(mockHeader.getFirst(AUTH_HEADER)).thenReturn(null);
        assertFalse(target.isAuthorized(clientRequest));
        when(mockHeader.getFirst(AUTH_HEADER)).thenReturn("Basic xyz");
        assertFalse(target.isAuthorized(clientRequest));
    }


    @Test
    public void testAuthorize(){
        String expectedValue = "Bearer abcd.1234";
        Map jwtHeaders =new HashMap<String, List<?>>();
        jwtHeaders.put("Authorization", Collections.singletonList(expectedValue));
        when(mockHandler.getJWTHeaders(false)).thenReturn(jwtHeaders);
        target.setJwtAuthorizationHandler(mockHandler);
        target.authorize(clientRequest);
        ArgumentCaptor<Map> map = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> value = ArgumentCaptor.forClass(String.class);
        verify(mockHeader).putAll(map.capture());
        assertEquals(((List)map.getValue().get("Authorization")).get(0), expectedValue);
    }
}
