package com.pb.stratus.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * A Servlet filter that strips CRLF pairs from response headers to protect 
 * from potential HTTP response splitting attacks.  
 */
public class HttpResponseSplittingProtectionFilter implements Filter
{

    public void init(FilterConfig cfg) throws ServletException
    {
    }

    public void destroy()
    {
    }

    public void doFilter(ServletRequest req, ServletResponse resp,
            FilterChain chain) throws IOException, ServletException
    {
        if (resp instanceof HttpServletResponse)
        {
            chain.doFilter(req, 
                    new CrlfStrippingWrapper((HttpServletResponse) resp)); 
        }
        else
        {
            chain.doFilter(req, resp);
        }
    }
    
    private class CrlfStrippingWrapper extends HttpServletResponseWrapper
    {

        public CrlfStrippingWrapper(HttpServletResponse response)
        {
            super(response);
        }

        @Override
        public void addHeader(String name, String value)
        {
            super.addHeader(name, getStrippedValue(value));
        }

        @Override
        public void setHeader(String name, String value)
        {
            super.setHeader(name, getStrippedValue(value));
        }
        
        private String getStrippedValue(String value)
        {
            return value.replace("\r\n", " ");
        }

        
        
    }

}
