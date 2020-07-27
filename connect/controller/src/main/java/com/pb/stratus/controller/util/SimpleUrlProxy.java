package com.pb.stratus.controller.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * A simple URL proxy requests a resource from a constructed target URL and
 * sends its content back to the original caller. The target URL is constructed
 * by taking the URL passed into 
 * {@link #proxy(URL, HttpServletRequest, HttpServletResponse)} as a base
 * and appending the path info and the query string from the given request.
 * The encoding and content type of the response will be set to the same values
 * as given by the target resource.
 */
public class SimpleUrlProxy implements Proxy {


    private static final Logger logger = LogManager.getLogger(SimpleUrlProxy.class.getName());

    /**
     * Requests the content of a resource from a target URL and sends it back
     * to the caller through the given servlet response. See the class comment
     * for more details on how the target URL is constructed.
     * 
     * @param baseUrl the base URL of the target resource
     * @param request the request of the caller
     * @param response the response used to send the resource content back
     *        to the caller
     */
    public void proxy(URL baseUrl, HttpServletRequest request,
            HttpServletResponse response) throws IOException, 
            MalformedURLException
    {
        URL targetUrl = constructTargetUrl(baseUrl, request);
        URLConnection conn = targetUrl.openConnection();
        conn.connect();
        String contentType = conn.getContentType();
        if (contentType != null)
        {
            response.setContentType(contentType);
        }
        String encoding = conn.getContentEncoding();
        if (encoding != null)
        {
            response.setCharacterEncoding(encoding);
        }
        OutputStream os = response.getOutputStream();
        InputStream is = conn.getInputStream();
        try
        {
            IOUtils.copy(is, os);
        }
        catch(Exception e)
        {
            StackTraceElement[] trace = e.getStackTrace();
            logger.error("proxy failed :" + trace[0].toString());
        }
        finally
        {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }
    
    /**
     * Overloaded
     * Requests the content of a resource from a target URL and sends it back
     * to the caller. 
     * 
     * @param targetUrl the URL of the target resource
     * @return InputStream the resource content to be sent back to caller.
     */
    public InputStream proxy(URL targetUrl) throws IOException, 
            MalformedURLException
    {
        URLConnection conn = targetUrl.openConnection();
        conn.connect(); 
        return conn.getInputStream();
    }
    
    private URL constructTargetUrl(URL baseUrl, HttpServletRequest request)
            throws MalformedURLException
    {
        StringBuilder url = new StringBuilder(baseUrl.toString());
        String pathInfo = request.getPathInfo();
        if (!StringUtils.isBlank(pathInfo))
        {
            url.append(pathInfo);
        }
        String queryString = request.getQueryString();
        if (!StringUtils.isBlank(queryString))
        {
            url.append("?");
            url.append(queryString);
        }
        return new URL(url.toString());
    }

}
