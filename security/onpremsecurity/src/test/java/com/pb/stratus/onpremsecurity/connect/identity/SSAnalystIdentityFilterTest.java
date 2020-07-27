package com.pb.stratus.onpremsecurity.connect.identity;

import com.pb.stratus.security.core.connect.identity.AnonymousTenantAuthenticationBypassStrategy;
import com.pb.stratus.security.core.authentication.StaticResourceAuthenticationBypassStrategy;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;

public class SSAnalystIdentityFilterTest {

    SSAnalystIdentityFilter target;
    StaticResourceAuthenticationBypassStrategy mockStaticResourceAuthenticationBypassStrategy;
    AnonymousTenantAuthenticationBypassStrategy mockAnonymousTenantAuthenticationBypassStrategy;
    HttpServletRequest mockRequest = mock(HttpServletRequest.class);
    HttpServletResponse mockResponse = mock(HttpServletResponse.class);
    private Authentication mockAuthentication;

    @Before
    public void setup(){
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
        target = new SSAnalystIdentityFilter();
        mockAuthentication = mock(Authentication.class);
        mockAnonymousTenantAuthenticationBypassStrategy =  mock(AnonymousTenantAuthenticationBypassStrategy.class);
        mockStaticResourceAuthenticationBypassStrategy = mock(StaticResourceAuthenticationBypassStrategy.class);
        target.setAnonymousTenantAuthenticationBypassStrategy(mockAnonymousTenantAuthenticationBypassStrategy);
        target.setStaticResourceAuthenticationBypassStrategy(mockStaticResourceAuthenticationBypassStrategy);

    }

    @Test
    public void shouldBypassAuthForStaticResources() throws IOException, ServletException {

        FilterChain mockChain = mock(FilterChain.class);
        when(mockStaticResourceAuthenticationBypassStrategy.shouldBypassAuthentication(mockRequest)).thenReturn(true);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        target.doFilter(mockRequest, mockResponse, mockChain);
        verify(mockChain).doFilter(mockRequest,mockResponse);


    }

    @Test
    public void shouldBypassAuthForAnonymousTenant() throws IOException, ServletException {

        FilterChain mockChain = mock(FilterChain.class);
        when(mockStaticResourceAuthenticationBypassStrategy.shouldBypassAuthentication(mockRequest)).thenReturn(false);
        when(mockAnonymousTenantAuthenticationBypassStrategy.shouldBypassAuthentication(mockRequest)).thenReturn(true);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        target.doFilter(mockRequest, mockResponse, mockChain);
        verify(mockChain).doFilter(mockRequest,mockResponse);


    }

    @Test(expected = AuthenticationException.class)
    public void throwExceptionIfNoAuthenticationfound() throws IOException, ServletException {
        FilterChain mockChain = mock(FilterChain.class);
        when(mockStaticResourceAuthenticationBypassStrategy.shouldBypassAuthentication(mockRequest)).thenReturn(false);
        target.doFilter(mockRequest, mockResponse, mockChain);
    }

    @Test
    public void testSecuredTenantWithAuthenticatedUser() throws IOException, ServletException {
        FilterChain mockChain = mock(FilterChain.class);
        when(mockStaticResourceAuthenticationBypassStrategy.shouldBypassAuthentication(mockRequest)).thenReturn(false);
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        SecurityContextHolder.getContext().setAuthentication(mockAuthentication);
        target.doFilter(mockRequest, mockResponse, mockChain);
        verify(mockChain).doFilter(mockRequest, mockResponse);
    }

}
