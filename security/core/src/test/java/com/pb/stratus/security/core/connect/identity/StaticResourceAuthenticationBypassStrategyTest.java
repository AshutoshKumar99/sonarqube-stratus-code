package com.pb.stratus.security.core.connect.identity;


import com.pb.stratus.security.core.authentication.ShouldNotBeControllerCallBypassStrategy;
import com.pb.stratus.security.core.authentication.StaticResourceAuthenticationBypassStrategy;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StaticResourceAuthenticationBypassStrategyTest {
    private StaticResourceAuthenticationBypassStrategy
            staticResourceAuthenticationBypassStrategy;
    private ShouldNotBeControllerCallBypassStrategy
            mockShouldNotBeControllerCallBypassStrategy;

    @Before
    public void setUp() throws Exception {
        staticResourceAuthenticationBypassStrategy = new
                StaticResourceAuthenticationBypassStrategy();
        List<String> urls = new ArrayList<String>();
        urls.add("/**/favicon.ico");
        mockShouldNotBeControllerCallBypassStrategy =
                mock(ShouldNotBeControllerCallBypassStrategy.class);
        staticResourceAuthenticationBypassStrategy.setUrlPatterns(urls);
        staticResourceAuthenticationBypassStrategy.
                setShouldNotBeControllerCallBypassStrategy(
                        mockShouldNotBeControllerCallBypassStrategy);
        // called by spring
        staticResourceAuthenticationBypassStrategy.afterPropertiesSet();

    }

    @Test
    public void checkProperTiesHaveBeenSet() {
        assertTrue(staticResourceAuthenticationBypassStrategy.
                getUrlPatterns().size() == 1);
        assertNotNull(staticResourceAuthenticationBypassStrategy.
                getShouldNotBeControllerCallBypassStrategy());
    }

    @Test
    public void checkUrlsHaveBeenCompiled() {
        String actualCompiledUrl = staticResourceAuthenticationBypassStrategy.
                getStaticResourceUrls().get(0);
        PathMatcher urlMatcher  = new AntPathMatcher();
        assertTrue(urlMatcher.match("/**/favicon.ico",actualCompiledUrl));
    }

    @Test
    public void shouldReturnTrueIfStaticResourceRequested() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/connect/images/favicon.ico");
        when(mockShouldNotBeControllerCallBypassStrategy.
                shouldBypassAuthentication(req)).thenReturn(true);
        assertTrue(staticResourceAuthenticationBypassStrategy.
                shouldBypassAuthentication(req));
    }

    @Test
    public void shouldReturnFalseIfResourcePatternIsNotInList() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/connect/some-random-request-not-in-list");
        when(mockShouldNotBeControllerCallBypassStrategy.
                shouldBypassAuthentication(req)).thenReturn(true);
        assertFalse(staticResourceAuthenticationBypassStrategy.
                shouldBypassAuthentication(req));
    }

    @Test
    public void shouldReturnFalseIfResourceRequestedViaController() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/connect/controller/images/favicon.ico");
        when(mockShouldNotBeControllerCallBypassStrategy.
                shouldBypassAuthentication(req)).thenReturn(false);
        assertFalse(staticResourceAuthenticationBypassStrategy.
                shouldBypassAuthentication(req));

    }
}
