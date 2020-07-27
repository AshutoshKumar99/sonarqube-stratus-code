package com.pb.stratus.security.core.connect.identity;


import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletRequest;

import static com.pb.stratus.core.common.Constants.TENANT_ATTRIBUTE_NAME;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AnonymousTenantAuthenticationBypassStrategyTest {

    private AnonymousTenantAuthenticationBypassStrategy
            anonymousTenantAuthenticationBypassStrategy;
    private RequestBasisAccessConfigurationResolver
            mockRequestBasisAccessConfigurationResolver;

    @Before
    public void setUp() throws Exception {
        anonymousTenantAuthenticationBypassStrategy =
                new AnonymousTenantAuthenticationBypassStrategy();
        mockRequestBasisAccessConfigurationResolver =
                mock(RequestBasisAccessConfigurationResolver.class);
        anonymousTenantAuthenticationBypassStrategy.
                setRequestBasisAccessConfigurationResolver(
                        mockRequestBasisAccessConfigurationResolver);
        // called by spring
        anonymousTenantAuthenticationBypassStrategy.afterPropertiesSet();
    }

    @Test
    public void RequestBasisAccessConfigurationResolverShouldNotBeNull() {
        assertTrue(anonymousTenantAuthenticationBypassStrategy.
                getRequestBasisAccessConfigurationResolver() != null);
    }
    
    @Test (expected = IllegalStateException.class)
    public void shouldThrowExceptionIfTenantNameNotPresentInRequest() {
        ServletRequest req = mock(ServletRequest.class);
        when(req.getAttribute(TENANT_ATTRIBUTE_NAME)).thenReturn(null);
        anonymousTenantAuthenticationBypassStrategy.shouldBypassAuthentication(req);
    }
    
    @Test
    public void shouldReturnTrueIfTenantIsAnonymous() {
        ServletRequest req = mock(ServletRequest.class);
        when(req.getAttribute(TENANT_ATTRIBUTE_NAME)).thenReturn("some-tenant");
        when(mockRequestBasisAccessConfigurationResolver.isAnonymousLoginAllowed()).
                thenReturn(true);
        assertTrue(anonymousTenantAuthenticationBypassStrategy.
                shouldBypassAuthentication(req));
    }

    @Test
    public void shouldReturnFalseIfTenantNotAnonymous() {
        ServletRequest req = mock(ServletRequest.class);
        when(req.getAttribute(TENANT_ATTRIBUTE_NAME)).thenReturn("some-tenant");
        when(mockRequestBasisAccessConfigurationResolver.isAnonymousLoginAllowed()).
                thenReturn(false);
        assertFalse(anonymousTenantAuthenticationBypassStrategy.
                shouldBypassAuthentication(req));
    }

}
