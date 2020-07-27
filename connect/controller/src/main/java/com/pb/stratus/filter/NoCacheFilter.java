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
 * to avoid caching the resource being sent.
 */
public class NoCacheFilter implements Filter
{

    public void destroy()
    {
        // nothing needs to be done ...
    }

    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException
    {
        HttpServletResponse httpResponse = (HttpServletResponse)response;
        httpResponse.setHeader("Cache-Control", "no-cache");
        filterChain.doFilter(request, response);
    }

    public void init(FilterConfig arg0) throws ServletException
    {
        // nothing needs to be done ...
    }
}
