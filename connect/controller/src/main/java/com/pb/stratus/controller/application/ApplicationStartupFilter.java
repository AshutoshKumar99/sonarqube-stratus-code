package com.pb.stratus.controller.application;

import javax.servlet.*;
import java.io.IOException;

/*
 * Application Filter class to run with tomcat first starts and with any
 * request to connect as this class enables that the application class is
 * intialised after initialisation of the container with the first request
 * to connect
 */
public class ApplicationStartupFilter implements Filter
{
    public static final String APPLICATION_ATTRIBUTE_NAME
            = "com.pb.stratus.controller.application";

    private ServletContext appScope;

    private Application application;

    public void init(FilterConfig fc) throws ServletException
    {
        this.appScope = fc.getServletContext();
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain fc) throws IOException, ServletException
    {
        initApplicationIfNecessary();
        fc.doFilter(request, response);
    }

    /*
     * Create the application object on the first request to connect
     */
    private synchronized void initApplicationIfNecessary()
    {
        if (application != null)
        {
            return;
        }
        application = new ServletApplication(appScope);
        application.start();
        appScope.setAttribute(APPLICATION_ATTRIBUTE_NAME, application);
    }

    public void destroy()
    {
        appScope.removeAttribute(APPLICATION_ATTRIBUTE_NAME);
    }
}
