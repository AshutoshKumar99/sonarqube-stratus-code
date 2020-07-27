package com.pb.stratus.web.filter;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.ConfigProperties;
import com.pb.stratus.core.configuration.ConnectConfigProperties;
import com.pb.stratus.core.service.UsageStatisticsLoggingService;
import com.pb.stratus.web.service.CaptureUsageStatisticsImpl;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: su019ch
 * Date: 6/2/14
 * Time: 4:21 PM
 */
public class UsageStatisticsLoggingFilterTest {

    public static final String DIR_PROPERTY_NAME = "stratus.customer.config.dir";

    private UsageStatisticsLoggingService usageStatisticsLoggingService;
    private ConfigProperties configProperties;

    @Before
    public void setUp() throws Exception
    {
        usageStatisticsLoggingService = mock(UsageStatisticsLoggingService.class);
        configProperties = mock(ConnectConfigProperties.class);
    }

    @Test
    public void testDoFilter() throws Exception
    {
        MockHttpServletRequest mockHttpRequest = new MockHttpServletRequest();
        HttpServletResponse mockHttpResponse = mock(HttpServletResponse.class);

        UsageStatisticsLoggingFilter loggingFilter = new UsageStatisticsLoggingFilter();

        assertNull(ThreadContext.get("sessionId"));
        assertNull(ThreadContext.get("user-agent"));
        assertNull(ThreadContext.get("tenant"));
        assertNull(ThreadContext.get("user"));
        assertNull(ThreadContext.get("requestURL"));

        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpRequest.setSession(mockHttpSession);

        URL url = this.getClass().getResource("");
        String urlpath = url.getPath();
        System.setProperty(DIR_PROPERTY_NAME, urlpath);

        SecurityContext securityContext = new SecurityContextImpl();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("CurrentUser");
        when(authentication.isAuthenticated()).thenReturn(true);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockHttpRequest.addHeader("user-agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");
        mockHttpRequest.addHeader("referer", "http://localhost:8010/connect/controller/featureSearchByGeometry");
        mockHttpRequest.setAttribute(Constants.TENANT_ATTRIBUTE_NAME, "analyst");
        mockHttpRequest.setRequestURI("http://localhost:8010/connect/controller/featureSearchByGeometry");
        mockHttpRequest.setContextPath("/connect");
        when(mockHttpResponse.getHeader("Content-Length")).thenReturn("50");

        Logger logger = mock(Logger.class);
        loggingFilter.setConfig(configProperties);
        when(usageStatisticsLoggingService.configureLogger(configProperties)).thenReturn(logger);

        loggingFilter.setUsageStatistics(new CaptureUsageStatisticsImpl());
        loggingFilter.setUsageStatisticsLoggingService(usageStatisticsLoggingService);
        when(configProperties.isCaptureUsageDataEnabled()).thenReturn(true);

        loggingFilter.doFilter(mockHttpRequest, mockHttpResponse, getFilterChain());

        assertNull(ThreadContext.get("sessionId"));
        assertNull(ThreadContext.get("user-agent"));
        assertNull(ThreadContext.get("tenant"));
        assertNull(ThreadContext.get("user"));
        assertNull(ThreadContext.get("requestURL"));
    }

    private FilterChain getFilterChain()
    {
        class DummyFilterChain implements FilterChain
        {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse)
                    throws IOException, ServletException
            {
                assertNotNull(ThreadContext.get("sessionId"));
                assertNotNull(ThreadContext.get("user-agent"));
                assertNotNull(ThreadContext.get("tenant"));
                assertNotNull(ThreadContext.get("user"));
                assertNotNull(ThreadContext.get("requestURL"));
            }
        }
        return new DummyFilterChain();
    }
}
