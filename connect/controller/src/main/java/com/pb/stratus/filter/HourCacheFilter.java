package com.pb.stratus.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * This Servlet Filter sets an HTTP header 
 * to cache the resource being sent for one hour.
 */
public class HourCacheFilter implements Filter
{

    public void destroy()
    {
        // nothing needs to be done ...
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        httpResponse.setHeader("Cache-Control", "max-age=3600");
        filterChain.doFilter(request, response);
    }

    public void init(FilterConfig arg0) throws ServletException
    {
        // nothing needs to be done ...
    }
}
