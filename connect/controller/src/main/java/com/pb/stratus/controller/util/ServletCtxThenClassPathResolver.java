package com.pb.stratus.controller.util;

import uk.co.graphdata.utilities.resource.BaseResourceResolver;
import uk.co.graphdata.utilities.resource.ClassPathResourceResolver;
import uk.co.graphdata.utilities.resource.ResourceResolver;
import uk.co.graphdata.utilities.resource.ServletContextResourceResolver;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ServletCtxThenClassPathResolver extends BaseResourceResolver
{
    
    private ResourceResolver r1;
    
    private ResourceResolver r2;
    
    public ServletCtxThenClassPathResolver(ServletContext ctx)
    {
        this(ctx, null);
    }

    public ServletCtxThenClassPathResolver(ServletContext ctx, String prefix)
    {
        r1 = new ServletContextResourceResolver(ctx, prefix);
        r2 = new ClassPathResourceResolver();
    }

    public InputStream getResourceAsStream(String name)
    {
        InputStream is = r1.getResourceAsStream(name);
        if (is != null)
        {
            return is;
        }
        return r2.getResourceAsStream(name);
    }

    public URL getResource(String name)
    {
        URL url = r1.getResource(name);
        if (url != null)
        {
            return url;
        }
        return r2.getResource(name);
    }

    public URLConnection getResourceConnection(String name) throws IOException
    {
        URLConnection uc = r1.getResourceConnection(name);
        if (uc != null)
        {
            return uc;
        }
        return r2.getResourceConnection(name);
    }

    public ServletContextResourceResolver getServletContextResourceResolver()
    {
        return (ServletContextResourceResolver)r1;
    }
}
