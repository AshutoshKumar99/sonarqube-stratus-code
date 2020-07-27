package com.pb.stratus.controller.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A proxy relays a request to another destination and sends the response
 * from that destination back to the original caller 
 */
public interface Proxy 
{
    
    /**
     * Proxies the given request to <code>targetUrl</code> and sends the 
     * content identified by that URL back through the given response
     * 
     * @param targetUrl the target URL to read the content from
     * @param request the original request
     * @param response the response to send the content back to
     */
    public void proxy(URL targetUrl, HttpServletRequest request, 
            HttpServletResponse response) throws IOException, MalformedURLException;

    /**
     * Overloaded
     * Requests the content of a resource from a target URL and sends it back
     * to the caller. 
     * 
     * @param targetUrl the URL of the target resource
     * @param is the resource content to be sent back to caller.
     */
    public InputStream proxy(URL targetUrl) throws IOException, 
            MalformedURLException;

}
