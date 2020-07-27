package com.pb.stratus.onpremsecurity.http;

import org.apache.ws.security.util.Base64;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: ma050si
 * Date: 4/3/14
 * Time: 3:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class BasicAuthenticationRequestAuthorizerImplTest {
    private static final String AUTH_HEADER = "Authorization";
    ClientHttpRequest clientRequest;
    BasicAuthenticationRequestAuthorizerImpl target;
    HttpHeaders mockHeader;
    private Authentication mockAuth ;

    @Before
    public void setup(){
        clientRequest = mock(ClientHttpRequest.class);
        mockHeader = mock(HttpHeaders.class);
        target = new BasicAuthenticationRequestAuthorizerImpl();
    }

    @Test
    public void testIsAuthorizedWithBasicAuthScheme() {
        when(clientRequest.getHeaders()).thenReturn(mockHeader);
        when(mockHeader.getFirst(AUTH_HEADER)).thenReturn("Basic sad");
        boolean authorized = target.isAuthorized(clientRequest);
        assertTrue(authorized);
    }

    @Test
    public void testIsAuthorizedWithNonBasicAuthScheme() {
        when(clientRequest.getHeaders()).thenReturn(mockHeader);
        when(mockHeader.getFirst(AUTH_HEADER)).thenReturn("NonBasic");
        boolean authorized = target.isAuthorized(clientRequest);
        assertFalse(authorized);
    }

    @Test
    public void authorizeAndAddBasicAuthenticationHeaderWithAuthenticatedAnonymousUser() {
        mockAuth = mock(AnonymousAuthenticationToken.class);
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
        target.setAnonymousUsername("AnalyticGuestUser");
        target.setAnonymousPassword("Password1");
        when(clientRequest.getHeaders()).thenReturn(mockHeader);
        boolean response = target.authorize(clientRequest);
        assertTrue(response);

    }

    @Test
    public void failAuthorization() {
        mockAuth = mock(Authentication.class);
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
        boolean response = target.authorize(clientRequest);
        assertFalse(response);
    }

    @Test
    public void authorizeAndAddBasicAuthenticationHeaderWithAuthenticatedUser() {
        mockAuth = new UsernamePasswordAuthenticationToken("admin", "admin");
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
        when(clientRequest.getHeaders()).thenReturn(mockHeader);
        boolean response = target.authorize(clientRequest);
        assertTrue(response);
    }

    @Test
    public void testHeaderParameter() {
        String expectedValue = "Basic " + Base64.encode(("admin" + ":" + "admin").getBytes());
        mockAuth = new UsernamePasswordAuthenticationToken("admin", "admin");
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
        when(clientRequest.getHeaders()).thenReturn(mockHeader);
        boolean response = target.authorize(clientRequest);
        ArgumentCaptor<String> header = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> value = ArgumentCaptor.forClass(String.class);
        verify(mockHeader).set(header.capture(), value.capture());
        assertEquals(value.getValue(), expectedValue);
        assertEquals(header.getValue(),"Authorization");
    }
}
