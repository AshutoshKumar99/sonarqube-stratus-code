package com.pb.stratus.onpremsecurity.adminconsole.identity;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.mockito.Mockito.verify;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

public class AdminAnalystAuthenticationEntryPointTest {

    @Test
    public void testCommence() throws Exception
    {
        AnalystSsoRedirectHelper mockHelper = mock(AnalystSsoRedirectHelper.class);

        AdminAnalystAuthenticationEntryPoint entryPoint = new AdminAnalystAuthenticationEntryPoint();
        entryPoint.setRedirectHelper(mockHelper);

        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(request, response, new BadCredentialsException("Bad Credentials Exception"));
        verify(mockHelper).sendSsoRedirect(request, response);
    }

}