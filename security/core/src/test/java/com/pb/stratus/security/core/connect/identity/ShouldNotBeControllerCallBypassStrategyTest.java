package com.pb.stratus.security.core.connect.identity;


import com.pb.stratus.security.core.authentication.ShouldNotBeControllerCallBypassStrategy;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShouldNotBeControllerCallBypassStrategyTest {
    private ShouldNotBeControllerCallBypassStrategy shouldNotBeControllerCallBypassStrategy;

    @Before
    public void setUp() throws Exception {
        shouldNotBeControllerCallBypassStrategy = new ShouldNotBeControllerCallBypassStrategy();
        List<String> controllerUrls = new ArrayList<String>();
        controllerUrls.add("/controller/**");
        shouldNotBeControllerCallBypassStrategy.setControllerUrls(controllerUrls);
        // called by Spring framework.
        shouldNotBeControllerCallBypassStrategy.afterPropertiesSet();
    }

    @Test
    public void checkUrlListHasBeenSet() {
        try {
            shouldNotBeControllerCallBypassStrategy.afterPropertiesSet();
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void checkWhetherTheUrlsAreCompiled() throws Exception {
        Assert.assertTrue(shouldNotBeControllerCallBypassStrategy.
                getRestrictedUrls().size() == 1);
        String expectedCompiledUrl = shouldNotBeControllerCallBypassStrategy.
                getRestrictedUrls().get(0);
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        Assert.assertTrue(antPathMatcher.match("/controller*//**//**" ,expectedCompiledUrl));
    }

    @Test
    public void shouldReturnFalseIfControllerRequestIsMade() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/controller/images/someimage");
        Assert.assertFalse(shouldNotBeControllerCallBypassStrategy.
                shouldBypassAuthentication(req));
    }

    @Test
    public void shouldReturnTrueIfControllerRequestIsNotMade() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/images/someimage");
        Assert.assertTrue(shouldNotBeControllerCallBypassStrategy.
                shouldBypassAuthentication(req));
    }
}
