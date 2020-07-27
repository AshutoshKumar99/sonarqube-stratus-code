package com.pb.stratus.security.core.adminconsole.authorization;


import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockServletContext

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.mockito.Mockito.*

public class SessionInvalidationFilterTests
{
    @Test
    public void testDoFilter_differentTenant()
    {
        Filter filter = new SessionInvalidationFilter();
        ServletContext context = new MockServletContext();
        context.setContextPath("/contextPath");
        filter.setServletContext(context);

        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("tenant", "foo");
        request.setAttribute("originalRequestUri", "/originalPath");
        request.setQueryString("foo=bar");
        request.getSession().setAttribute("tenant", "bar")

        HttpServletResponse mockResponse = mock(HttpServletResponse);
        FilterChain mockFilterChain = mock(FilterChain);

        filter.doFilter(request, mockResponse, mockFilterChain);

        verify(mockResponse).sendRedirect("/contextPath/originalPath?foo=bar");
        verify(mockFilterChain, never()).doFilter(any(), any())
    }

    @Test
    public void testDoFilter_sameTenant()
    {
        Filter filter = new SessionInvalidationFilter();
        ServletContext context = new MockServletContext();
        context.setContextPath("/contextPath");
        filter.setServletContext(context);

        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("tenant", "foo");
        request.setAttribute("originalRequestUri", "/originalPath");
        request.setQueryString("foo=bar");
        request.getSession().setAttribute("tenant", "foo")

        HttpServletResponse mockResponse = mock(HttpServletResponse);
        FilterChain mockFilterChain = mock(FilterChain);

        filter.doFilter(request, mockResponse, mockFilterChain);

        verify(mockResponse, never()).sendRedirect(any());
        verify(mockFilterChain).doFilter(request, mockResponse);
    }

    @Test
    public void testDoFilter_sameTenant_differentCase()
    {
        Filter filter = new SessionInvalidationFilter();
        ServletContext context = new MockServletContext();
        context.setContextPath("/contextPath");
        filter.setServletContext(context);

        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("tenant", "foo");
        request.setAttribute("originalRequestUri", "/originalPath");
        request.setQueryString("foo=bar");
        request.getSession().setAttribute("tenant", "fOO")

        HttpServletResponse mockResponse = mock(HttpServletResponse);
        FilterChain mockFilterChain = mock(FilterChain);

        filter.doFilter(request, mockResponse, mockFilterChain);

        verify(mockResponse, never()).sendRedirect(any());
        verify(mockFilterChain).doFilter(request, mockResponse);
    }
}
