package com.pb.stratus.onpremsecurity.connect.identity;

import com.pb.stratus.onpremsecurity.handlers.TargetResourceAuthenticationSuccessRedirectHandler;
import com.pb.stratus.onpremsecurity.util.HttpSessionRequestCache;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TargetResourceAuthenticationSuccessRedirectHandlerTest {
    TargetResourceAuthenticationSuccessRedirectHandler handler;

    @Before
    public void setup() {
        handler = new TargetResourceAuthenticationSuccessRedirectHandler();
    }

    /**
     * Tests that the proper InnerTargetResource redirect is sent 
     * @throws Exception if thrown
     */
    @Test
    public void testInnerTargetResourceWithQueryParams() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        HttpSessionRequestCache mockRequestCache = Mockito.mock(HttpSessionRequestCache.class);
        handler.setRequestCache(mockRequestCache);
        Mockito.when(mockRequestCache.getRequest(request)).thenReturn("https%3A%2F%2Fstratus-dpdev.nw.pb.com%3A433%2Fconnect%2Fcustomerstratustenant1_noida%2F%3Fmapcfg%3DMapConfig%26locale%3Den%26x%3D0%26y%3D2000000%26zoom%3D11%26base%3DBing%2520Hybrid");

        Authentication authentication = Mockito.mock(Authentication.class);
        handler.onAuthenticationSuccess(request, response, authentication);
        Mockito.verify(response).sendRedirect("https%3A%2F%2Fstratus-dpdev.nw.pb.com%3A433%2Fconnect%2Fcustomerstratustenant1_noida%2F%3Fmapcfg%3DMapConfig%26locale%3Den%26x%3D0%26y%3D2000000%26zoom%3D11%26base%3DBing%2520Hybrid");
    }
}
