package com.pb.stratus.controller.filter;

import com.pb.stratus.controller.util.MockSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CharacterEncodingFilterTest
{

    CharacterEncodingFilter encodingFilter;
    private MockSupport mockSupport;
    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;
    private MockFilterConfig filterConfig;

    @Before
    public void setUp() throws Exception
    {
        mockSupport = new MockSupport();
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        encodingFilter = new CharacterEncodingFilter();
        filterConfig = new MockFilterConfig();
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testInitIgnoreTrue() throws Exception
    {
        filterConfig.addInitParameter("encoding", "UTF-8");
        filterConfig.addInitParameter("ignore", "true");
        encodingFilter.init(filterConfig);
        assertEquals(filterConfig.getInitParameter("encoding"),
                encodingFilter.encoding);
        assertEquals(new Boolean(filterConfig.getInitParameter("ignore"))
                .booleanValue(), encodingFilter.ignore);
    }

    @Test
    public void testInitIgnoreFalse() throws Exception
    {
        filterConfig.addInitParameter("encoding", "UTF-8");
        filterConfig.addInitParameter("ignore", "false");
        encodingFilter.init(filterConfig);
        assertEquals(filterConfig.getInitParameter("encoding"),
                encodingFilter.encoding);
        assertEquals(new Boolean(filterConfig.getInitParameter("ignore"))
                .booleanValue(), encodingFilter.ignore);
    }

    @Test
    public void testInitIgnoreNull() throws Exception
    {
        filterConfig.addInitParameter("encoding", "UTF-8");
        encodingFilter.init(filterConfig);
        assertEquals(filterConfig.getInitParameter("encoding"),
                encodingFilter.encoding);
        assertEquals(true, encodingFilter.ignore);
    }
    
    @Test
    public void testInitIgnoreYes() throws Exception
    {
        filterConfig.addInitParameter("encoding", "UTF-8");
        filterConfig.addInitParameter("ignore", "Yes");
        encodingFilter.init(filterConfig);
        assertEquals(filterConfig.getInitParameter("encoding"),
                encodingFilter.encoding);
        assertEquals(true, encodingFilter.ignore);
    }

    @Test
    public void testInitIgnoreEmpty() throws Exception
    {
        filterConfig.addInitParameter("encoding", "UTF-8");
        filterConfig.addInitParameter("ignore", "");
        encodingFilter.init(filterConfig);
        assertEquals(filterConfig.getInitParameter("encoding"),
                encodingFilter.encoding);
        assertEquals(false, encodingFilter.ignore);
    }
    @Test
    public void testDoFilterEncodingNullIgnoreFalse() throws Exception
    {
        encodingFilter.encoding = "UTF-8";
        encodingFilter.ignore = false;
        mockRequest.setCharacterEncoding(null);
        encodingFilter.doFilter(mockRequest, mockResponse, createMockFilterChain());
        assertEquals(encodingFilter.encoding, mockRequest
                .getCharacterEncoding());
    }

    @Test
    public void testDoFilterIgnoreTrueEncodingNull() throws Exception
    {
        encodingFilter.encoding = "UTF-8";
        encodingFilter.ignore = true;
        mockRequest.setCharacterEncoding(null);
        encodingFilter.doFilter(mockRequest, mockResponse, createMockFilterChain());
        assertEquals(encodingFilter.encoding, mockRequest
                .getCharacterEncoding());
    }

    @Test
    public void testDoFilterIgnoreFalseEncodingDiff() throws Exception
    {
        encodingFilter.encoding = "UTF-8";
        encodingFilter.ignore = false;
        mockRequest.setCharacterEncoding("ISO");
        encodingFilter.doFilter(mockRequest, null, createMockFilterChain());
        assertTrue(!encodingFilter.encoding.equals(mockRequest
                .getCharacterEncoding()));
    }

    @Test
    public void testDoFilterIgnoreTrueEncodingDiff() throws Exception
    {
        encodingFilter.encoding = "UTF-8";
        encodingFilter.ignore = true;
        mockRequest.setCharacterEncoding("ISO");
        encodingFilter.doFilter(mockRequest, mockResponse, createMockFilterChain());
        assertEquals(encodingFilter.encoding, mockRequest
                .getCharacterEncoding());
    }

    @Test
    public void testDoFilterEncodingNull() throws Exception
    {
        encodingFilter.encoding = null;
        encodingFilter.ignore = true;
        mockRequest.setCharacterEncoding("ISO");
        encodingFilter.doFilter(mockRequest, mockResponse, createMockFilterChain());
        assertEquals("ISO", mockRequest.getCharacterEncoding());
    }

    private FilterChain createMockFilterChain() throws Exception
    {
        FilterChain chain = mockSupport.createMock(FilterChain.class);
        chain.doFilter((ServletRequest) anyObject(),
                (ServletResponse) anyObject());
        expectLastCall();
        replay(chain);
        return chain;
    }

}
