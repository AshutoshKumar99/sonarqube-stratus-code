package com.pb.stratus.security.core.connect.identity;

import org.junit.Test;
import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class ParameterPassThruRedirectStrategyTest {

    @Test
    public void passThruTest() throws Exception {

        RedirectStrategy redirectStrategy = mock(RedirectStrategy.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String url = "/test";

        ParameterPassThruRedirectStrategy parameterPassThruRedirectStrategy = mock(ParameterPassThruRedirectStrategy.class);
        parameterPassThruRedirectStrategy.setRedirectStrategy(redirectStrategy);
        doNothing().when(parameterPassThruRedirectStrategy).sendRedirect(request, response, url);
    }
}