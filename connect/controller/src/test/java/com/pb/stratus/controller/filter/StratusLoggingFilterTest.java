package com.pb.stratus.controller.filter;

import org.apache.logging.log4j.ThreadContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * StratusLoggingFilterTest to verify tenantname from incoming request and hostname added to 
 * Mapped Diagnostic Context of log4j
 */

public class StratusLoggingFilterTest
{
    private final String TENANT_NAME_KEY = "tenant";
    private final String HOST_NAME_KEY = "host";

    @Before
    public void setUp() throws Exception
    {
    }

    @Test
    public void testDoFilter()throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        StratusLoggingFilter filter = new StratusLoggingFilter();
        assertNull(ThreadContext.get(TENANT_NAME_KEY));
        assertNull(ThreadContext.get(HOST_NAME_KEY));
        request.setAttribute("tenant", "test");
        filter.doFilter(request, response, getFilterChain());
        assertNull(ThreadContext.get(TENANT_NAME_KEY));
        assertNull(ThreadContext.get(HOST_NAME_KEY));
        
    }
    
    private FilterChain getFilterChain(){
        class DummyFilterChian implements FilterChain{

            @Override
            public void doFilter(ServletRequest request,
                    ServletResponse response) throws IOException,
                    ServletException
            {
                assertNotNull(ThreadContext.get(TENANT_NAME_KEY));
                assertEquals("test", ThreadContext.get(TENANT_NAME_KEY));
                assertNotNull(ThreadContext.get(HOST_NAME_KEY));
                
            }}
        return new DummyFilterChian();
    }

}
