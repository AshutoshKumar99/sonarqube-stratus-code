package com.pb.stratus.onpremsecurity.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/24/14
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnalystLogoutSuccessHandlerTest {

    private AnalystLogoutSuccessHandler target;
    private HttpServletRequest m_request;
    private HttpServletResponse m_response;

    private String logoutSuccessPage = "successfullySignout.jsp";


    @Before
    public void setup(){
        target = new AnalystLogoutSuccessHandler();
        target.setLogoutSuccessPage(logoutSuccessPage);
        m_request = mock(HttpServletRequest.class);
        m_response = mock(HttpServletResponse.class);

    }

    @Test
    public void testLogoutSuccessUrl() throws IOException, ServletException {
        target.onLogoutSuccess(m_request, m_response, mock(Authentication.class));
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(m_response).sendRedirect(argument.capture());
        assertEquals("successfullySignout.jsp", argument.getValue());
    }

    @Test
    public void testLogoutSuccessUrlWithLocaleParam() throws IOException, ServletException {
        when(m_request.getParameter("lang")).thenReturn("fr");
        target.onLogoutSuccess(m_request, m_response, mock(Authentication.class));
        ArgumentCaptor<String> argument = ArgumentCaptor.forClass(String.class);
        verify(m_response).sendRedirect(argument.capture());
        assertEquals("successfullySignout.jsp?lang=fr", argument.getValue());
    }

    @Test
    public void testLogoutSuccessHandlerWithRedirectFalse() throws IOException, ServletException {
        when(m_request.getParameter("redirect")).thenReturn("false");
        PrintWriter mockWriter = mock(PrintWriter.class);
        when(m_response.getWriter()).thenReturn(mockWriter);

        target.onLogoutSuccess(m_request, m_response, mock(Authentication.class));
        verify(m_response, never()).sendRedirect(anyString());
        verify(m_response).setHeader("logout_status", "success");
    }
}
