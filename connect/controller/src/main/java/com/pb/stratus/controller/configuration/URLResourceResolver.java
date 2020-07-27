package com.pb.stratus.controller.configuration;

import uk.co.graphdata.utilities.resource.ResourceResolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * An implementation to resolve resource files from URL
 */
public class URLResourceResolver implements ResourceResolver
{

    private URLBuilder urlBuilder;

    public URLResourceResolver(URLBuilder urlBuilder)
    {
        this.urlBuilder = urlBuilder;
    }

    public long getLastModified(String path)
    {
        URLConnection urlConn = null;
        try
        {
            urlConn = getResourceConnection(path);
        }
        catch (IOException e)
        {
            return -1;
        }
        return urlConn.getLastModified();
    }

    public URL getResource(String path)
    {
        URL resourceURL = null;
        try
        {
            resourceURL = urlBuilder.buildFullURL(path);
        }
        catch (IOException muex)
        {
            return null;
        }
        return resourceURL;
    }

    public InputStream getResourceAsStream(String path)
    {
        InputStream is = null;
        try
        {
            URLConnection urlConn = getResourceConnection(path);
            is = urlConn.getInputStream();
        }
        catch (IOException e)
        {
            return null;
        }
        return is;

    }

    public URLConnection getResourceConnection(String path) throws IOException
    {
        URL u = getResource(path);
        if (u == null)
        {
            throw new IOException("Resource doesn't exist.");
        }
        return u.openConnection();
    }

}
