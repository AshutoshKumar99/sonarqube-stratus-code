package com.pb.stratus.controller.filter;

import com.pb.stratus.core.common.Constants;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.ThreadContext;

import javax.servlet.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * This filter will set tenantname and Server Hostname to log4j MDC @see {@link org.apache.log4j.MDC#put(String, Object)}
 *
 */
public class StratusLoggingFilter implements Filter
{

    private final String TENANT_NAME_KEY = "tenant";
    private final String HOST_NAME_KEY = "host";
    private FilterConfig filterConfig;
    
    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy()
    {
        

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        String tenantName = (String)request.getAttribute(Constants.TENANT_ATTRIBUTE_NAME);
        String hostName = getHostName();
        if(!StringUtils.isEmpty(tenantName))
        {
            ThreadContext.put(TENANT_NAME_KEY, tenantName);
        }
        ThreadContext.put(HOST_NAME_KEY, hostName);
        try
        {
            chain.doFilter(request, response);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally 
        {
            ThreadContext.remove(TENANT_NAME_KEY);
            ThreadContext.remove(HOST_NAME_KEY);
        }
        
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
        this.filterConfig = filterConfig;

    }
    
    private String getHostName()
    {
        String hostName = "";

        try
        {
            InetAddress address = InetAddress.getLocalHost();
            if (null != address)
            {
                hostName = address.getHostName();
            }
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            return "Unknown Host";
        }

        return hostName;
    }

}
