package com.pb.stratus.security.core.connect.identity.filter;


import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.*;

public class SloCallbackFilterTest
{
    private SloCallbackFilter sloFilter;
    private HttpServletRequest mRequest = mock(HttpServletRequest.class);
    private HttpServletResponse mResponse = mock(HttpServletResponse.class);
    private String m_tenant = "customerstratustenant1_noida";
        
    @Before
    public void setUp() throws Exception
    {
         sloFilter = new SloCallbackFilter();
         sloFilter.setSloResumeBaseUri("http://slohost:8080");
         HttpSession m_session = mock(HttpSession.class);
         when(mRequest.getSession()).thenReturn(m_session);

         ServletContext m_servletContext = mock(ServletContext.class);
         when(m_session.getServletContext()).thenReturn(m_servletContext);
    }
    
    @Test
    public void testDoFilter() throws IOException, ServletException{
       
        FilterChain mFilterChain = mock(FilterChain.class);
        when(mRequest.getParameter("resume")).thenReturn("/resume");
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.isAuthenticated()).thenReturn(true);
        SecurityContext mockSecurityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(mockSecurityContext);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        
        sloFilter.doFilter(mRequest,mResponse,mFilterChain);
        verify(mockAuthentication).setAuthenticated(false);
        verify(mockSecurityContext).setAuthentication(null);
        verify(mResponse).sendRedirect("http://slohost:8080/resume");

    }

}
