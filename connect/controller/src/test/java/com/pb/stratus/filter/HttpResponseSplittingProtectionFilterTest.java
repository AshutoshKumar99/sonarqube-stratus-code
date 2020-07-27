package com.pb.stratus.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class HttpResponseSplittingProtectionFilterTest
{
    
    @Test
    public void shouldStripCrlfsFromResponseHeaders() throws Exception
    {
        HttpResponseSplittingProtectionFilter filter 
                = new HttpResponseSplittingProtectionFilter();
        FilterChain mockChain = new FilterChain()
        {
            public void doFilter(ServletRequest req, ServletResponse resp)
                    throws IOException, ServletException
            {
                HttpServletResponse httpResponse = (HttpServletResponse) resp;
                httpResponse.setHeader("header1", "a\r\nb");
                httpResponse.addHeader("header2", "c\r\n\r\nd");
            }
        };
        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest(), mockResponse, mockChain);
        assertEquals("a b", mockResponse.getHeader("header1"));
        assertEquals("c  d", mockResponse.getHeader("header2"));
    }

}
