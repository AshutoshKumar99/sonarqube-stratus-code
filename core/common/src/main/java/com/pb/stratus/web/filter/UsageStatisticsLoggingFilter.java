package com.pb.stratus.web.filter;

import com.pb.stratus.core.configuration.ConfigProperties;
import com.pb.stratus.core.configuration.ConnectConfigProperties;
import com.pb.stratus.core.service.UsageStatisticsLoggingService;
import com.pb.stratus.core.service.UsageStatisticsLoggingServiceImpl;
import com.pb.stratus.web.service.CaptureUsageStatistics;
import com.pb.stratus.web.service.CaptureUsageStatisticsImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: su019ch
 * Date: 5/26/14
 * Time: 3:22 PM
 */
public class UsageStatisticsLoggingFilter implements Filter {

    private Logger usageStatisticsLogger = null;
    private CaptureUsageStatistics usageStatistics;
    private UsageStatisticsLoggingService usageStatisticsLoggingService;
    private boolean appenderAttached = false;
    private ConfigProperties configProperties = null;

    private static final Logger logger = LogManager.getLogger(UsageStatisticsLoggingFilter.class);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        usageStatisticsLoggingService = new UsageStatisticsLoggingServiceImpl();
        usageStatistics = new CaptureUsageStatisticsImpl();
        //Can be configured on basis of init param.
        configProperties = new ConnectConfigProperties();
    }

    /**
     * Only for unit tests
     *
     * @param config
     */
    public void setConfig(ConfigProperties config) {
        this.configProperties = config;
    }

    /**
     * Only for unit tests
     *
     * @param usageStatistics
     */
    public void setUsageStatistics(CaptureUsageStatistics usageStatistics) {
        this.usageStatistics = usageStatistics;
    }

    /**
     * Only for unit tests
     *
     * @param loggingService
     */
    public void setUsageStatisticsLoggingService(UsageStatisticsLoggingService loggingService) {
        this.usageStatisticsLoggingService = loggingService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!excludeThisFilter((HttpServletRequest) request) && configProperties.isCaptureUsageDataEnabled()) {
            HttpServletRequest httpRequest = ((HttpServletRequest) request);

            usageStatistics.captureApplicationName(httpRequest);
            usageStatistics.captureHostIP(httpRequest);
            usageStatistics.captureInternalHostIP(httpRequest);
            usageStatistics.captureUserAgent(httpRequest);
            usageStatistics.captureTenant(httpRequest);
            usageStatistics.captureOperationName(httpRequest);
            usageStatistics.captureMapConfig(httpRequest);
            usageStatistics.captureParameters(httpRequest);
            usageStatistics.captureUserId();
            usageStatistics.captureSessionId(httpRequest);
            usageStatistics.captureBaseUrl(httpRequest);

            long startTime = System.currentTimeMillis();

            try {
                chain.doFilter(request, response);
            } finally {
                usageStatistics.captureResponseTime(startTime);

                if (response instanceof HttpServletResponse) {
                    HttpServletResponse httpResponse = (HttpServletResponse) response;

                    usageStatistics.captureResponseStatusCode(httpResponse);
                    usageStatistics.captureResponseSize(httpResponse);
                }

                try {
                    getLogger().info("");
                } catch (Exception ex) {
                    logger.warn("Error while logging Usage Statistics:" + ex.getMessage());
                }
                ThreadContext.clearAll();
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * Need this method to get logger because we can get Logger when we have real HttpRequest.
     * can not get logger in @method init() as executed at context startup.
     *
     * @return
     */
    private Logger getLogger() {

        if (!appenderAttached) {
            usageStatisticsLogger = usageStatisticsLoggingService.configureLogger(configProperties);
            appenderAttached = true;
        }
        return usageStatisticsLogger;
    }

    /**
     * checks if the request url is a theme url
     * This is required as we don't need to capture these url statistics
     *
     * @param request HttpServletRequest
     * @return true|false
     */
    private boolean excludeThisFilter(HttpServletRequest request) {
        String requestUrl = request.getRequestURL().toString();
        if (requestUrl.contains("/controller/theme/") ||
                requestUrl.contains("/controller/logout")) {
            return true;
        }

        return false;
    }

    @Override
    public void destroy() {
    }
}
