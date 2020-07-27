/*****************************************************************************
 *       Copyright © 2012, Pitney Bowes Software Inc.
 *       All  rights reserved.
 *       Confidential Property of Pitney Bowes Software Inc.
 *
 * $Author: $
 * $Revision: $
 * $LastChangedDate: $
 *
 * $HeadURL: $
 *****************************************************************************/
package com.pb.stratus.core.component;

import org.junit.Test;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.*;

public class HttpStatusCodeFilterTest {
    @Test
    public void testInit() throws Exception {
        // just verify no exceptions
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", "400", "500", "400,401,402", "500,501,888"));
    }

    @Test(expected = ServletException.class)
    public void testInit_missingTriggerHeader() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig(null, "status", null, null, "401,402", "501,888"));
    }

    @Test(expected = ServletException.class)
    public void testInit_missingStatusCodeHeader() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", null, null, null, "401,402", "501,888"));
    }

    @Test(expected = ServletException.class)
    public void testInit_missingClientCodes() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, null, "501,888"));
    }

    @Test(expected = ServletException.class)
    public void testInit_missingServerCodes() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "401,402", null));
    }

    @Test(expected = ServletException.class)
    public void testInit_emptyTriggerHeader() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("", "status", null, null, "401,402", "501,888"));
    }

    @Test(expected = ServletException.class)
    public void testInit_emptyStatusCodeHeader() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "", null, null, "401,402", "501,888"));
    }

    @Test(expected = ServletException.class)
    public void testInit_emptyClientCodes() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "", "501,888"));
    }

    @Test(expected = ServletException.class)
    public void testInit_emptyServerCodes() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "401,402", ""));
    }

    @Test(expected = ServletException.class)
    public void testInit_noClientCodes() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, ",", "501,888"));
    }

    @Test(expected = ServletException.class)
    public void testInit_noServerCodes() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "401,402", ","));
    }

    @Test(expected = ServletException.class)
    public void testInit_invalidClientCodes() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "401,abc,402", "501,888"));
    }

    @Test(expected = ServletException.class)
    public void testInit_invalidServerCodes() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "401", "xyz"));
    }

    @Test(expected = ServletException.class)
    public void testDoFilter_clientErrorOutsideAcceptableRange1() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "399,400", "501"));
    }

    @Test(expected = ServletException.class)
    public void testDoFilter_clientErrorOutsideAcceptableRange2() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "400,500", "501"));
    }

    @Test(expected = ServletException.class)
    public void testDoFilter_serverErrorOutsideAcceptableRange1() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "400", "499,500"));
    }

    @Test(expected = ServletException.class)
    public void testDoFilter_serverErrorOutsideAcceptableRange2() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "400", "500,1000"));
    }

    @Test(expected = ServletException.class)
    public void testDoFilter_invalidClientConvertTo1() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", "", null, "400", "500"));
    }

    @Test(expected = ServletException.class)
    public void testDoFilter_invalidClientConvertTo2() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", "abc", null, "400", "500"));
    }

    @Test(expected = ServletException.class)
    public void testDoFilter_clientConvertToOutsideAcceptableRange1() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", "399", null, "400", "500"));
    }

    @Test(expected = ServletException.class)
    public void testDoFilter_clientConvertToOutsideAcceptableRange2() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", "500", null, "400", "500"));
    }

    @Test(expected = ServletException.class)
    public void testDoFilter_invalidServerConvertTo1() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, "", "400", "500"));
    }

    @Test(expected = ServletException.class)
    public void testDoFilter_invalidServerConvertTo2() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, "abc", "400", "500"));
    }

    @Test(expected = ServletException.class)
    public void testDoFilter_serverConvertToOutsideAcceptableRange1() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, "499", "400", "500"));
    }

    @Test(expected = ServletException.class)
    public void testDoFilter_serverConvertToOutsideAcceptableRange2() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, "1000", "400", "500"));
    }

    @Test
    public void testDoFilter_triggerHeaderNotSet() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "401", "501"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(444);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn(null);

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).sendError(444);
    }

    @Test
    public void testDoFilter_triggerHeaderSetButEmpty() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "401", "501"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(444);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setIntHeader("status", 444);
        verify(mockResponse).sendError(400);
    }

    @Test
    public void testDoFilter_clientError_sendError() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "444,455,466", "555,566,577"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(401);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setIntHeader("status", 401);
        verify(mockResponse).sendError(400);
    }

    @Test
    public void testDoFilter_clientError_sendErrorWithMessage() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "444,455,466", "555,566,577"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(401, "error 401 occurred");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setIntHeader("status", 401);
        verify(mockResponse).sendError(400, "error 401 occurred");
    }

    @Test
    public void testDoFilter_clientError_setStatus() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "444,455,466", "555,566,577"));

        FilterChain mockFilterChain = buildSetStatusFilterChain(401);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setIntHeader("status", 401);
        verify(mockResponse).setStatus(400);
    }

    @Test
    public void testDoFilter_clientError_setStatusWithMessage() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "444,455,466", "555,566,577"));

        FilterChain mockFilterChain = buildSetStatusFilterChain(401, "error 401 occurred");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setIntHeader("status", 401);
        verify(mockResponse).sendError(400, "error 401 occurred");
    }

    @Test
    public void testDoFilter_clientError_inList() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "444,455,466", "555,566,577"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(444);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).sendError(444);
        verify(mockResponse, never()).setIntHeader(eq("status"), anyInt());
    }

    @Test
    public void testDoFilter_serverError_sendError() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "444,455,466", "555,566,577"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(501);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setIntHeader("status", 501);
        verify(mockResponse).sendError(500);
    }

    @Test
    public void testDoFilter_serverError_sendErrorWithMessage() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "444,455,466", "555,566,577"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(501, "error 501 occurred");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setIntHeader("status", 501);
        verify(mockResponse).sendError(500, "error 501 occurred");
    }

    @Test
    public void testDoFilter_serverError_setStatus() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "444,455,466", "555,566,577"));

        FilterChain mockFilterChain = buildSetStatusFilterChain(501);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setIntHeader("status", 501);
        verify(mockResponse).setStatus(500);
    }

    @Test
    public void testDoFilter_serverError_setStatusWithMessage() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "444,455,466", "555,566,577"));

        FilterChain mockFilterChain = buildSetStatusFilterChain(501, "error 501 occurred");

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setIntHeader("status", 501);
        verify(mockResponse).sendError(500, "error 501 occurred");
    }

    @Test
    public void testDoFilter_serverError_inList() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "444,455,466", "555,566,577"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(555);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).sendError(555);
        verify(mockResponse, never()).setIntHeader(eq("status"), anyInt());
    }

    @Test
    public void testDoFilter_clientError_customError() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", "444", null, "400", "500"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(401);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setIntHeader("status", 401);
        verify(mockResponse).sendError(444);
    }

    @Test
    public void testDoFilter_serverError_customError() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, "555", "400", "500"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(501);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setIntHeader("status", 501);
        verify(mockResponse).sendError(555);
    }

    @Test
    public void testDoFilter_errorLessThan400() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "400", "500"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(333);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).sendError(333);
    }

    @Test
    public void testDoFilter_serverErrorAbove599() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "400", "500,666"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(612);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).setIntHeader("status", 612);
        verify(mockResponse).sendError(500);
    }

    @Test
    public void testDoFilter_serverErrorAbove599_inList() throws Exception {
        HttpStatusCodeFilter filter = new HttpStatusCodeFilter();
        filter.init(buildFilterConfig("trigger", "status", null, null, "400", "500,666"));

        FilterChain mockFilterChain = buildSendErrorFilterChain(666);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("trigger")).thenReturn("anyValueIsFineHere");

        HttpServletResponse mockResponse = mock(HttpServletResponse.class);

        filter.doFilter(mockRequest, mockResponse, mockFilterChain);

        verify(mockResponse).sendError(666);
        verify(mockResponse, never()).setIntHeader(eq("status"), anyInt());
    }

    private FilterConfig buildFilterConfig(final String triggerHeader, final String statusCodeHeader,
                                           final String convertClientErrorCodesTo, final String convertServerErrorCodesTo,
                                           final String clientErrorCodes, final String serverErrorCodes) {
        return new FilterConfig() {
            private final Map<String, String> initParams;

            {
                initParams = new HashMap<String, String>();
                if (triggerHeader != null) initParams.put("triggerHeader", triggerHeader);
                if (statusCodeHeader != null) initParams.put("statusCodeHeader", statusCodeHeader);
                if (convertClientErrorCodesTo != null) initParams.put("convertClientErrorCodesTo", convertClientErrorCodesTo);
                if (convertServerErrorCodesTo != null) initParams.put("convertServerErrorCodesTo", convertServerErrorCodesTo);
                if (clientErrorCodes != null) initParams.put("acceptableClientErrorCodes", clientErrorCodes);
                if (clientErrorCodes != null) initParams.put("acceptableServerErrorCodes", serverErrorCodes);
            }

            @Override
            public String getFilterName() {
                return "HttpStatusCodeFilter";
            }

            @Override
            public ServletContext getServletContext() {
                throw new UnsupportedOperationException("Not needed for testing");
            }

            @Override
            public String getInitParameter(String s) {
                return initParams.get(s);
            }

            @Override
            public Enumeration getInitParameterNames() {
                return Collections.enumeration(initParams.keySet());
            }
        };
    }

    private FilterChain buildSendErrorFilterChain(final int sc)
    {
        return new FilterChain(){
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
                ((HttpServletResponse)servletResponse).sendError(sc);
            }
        };
    }

    private FilterChain buildSendErrorFilterChain(final int sc, final String message)
    {
        return new FilterChain(){
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
                ((HttpServletResponse)servletResponse).sendError(sc, message);
            }
        };
    }

    private FilterChain buildSetStatusFilterChain(final int sc)
    {
        return new FilterChain(){
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
                ((HttpServletResponse)servletResponse).setStatus(sc);
            }
        };
    }

    private FilterChain buildSetStatusFilterChain(final int sc, final String message)
    {
        return new FilterChain(){
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
                ((HttpServletResponse)servletResponse).sendError(sc, message);
            }
        };
    }
}
