package com.pb.stratus.controller.util;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import uk.co.graphdata.utilities.resource.ResourceResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class ResourceServlet extends HttpServlet
{
    private static final long serialVersionUID = -5038659835024629751L;
    
    private static final Logger logger = 
        LogManager.getLogger(ResourceServlet.class);
    
    private ResourceResolver res;
    
    private Map<String, String> mimeTypes = new HashMap<String, String>();
    
    private final String CONTEXT_PATH_JS_PATH = "/dojo/contextpath.js";
    
    private final String CONTEXT_PATH_JS = "dojo.provide(\"dojo.contextpath\");" 
        + "contextPath = \"%s\";";
    
    @Override
    public void init(ServletConfig sc) throws ServletException
    {
        res = new ServletCtxThenClassPathResolver(sc.getServletContext(), 
                "/WEB-INF");
        mimeTypes.clear();
        mimeTypes.put(".gif", "image/gif");
        mimeTypes.put(".png", "image/png");
        mimeTypes.put(".jpg", "image/jpeg");
        mimeTypes.put(".txt", "text/plain");
        mimeTypes.put(".htc", "text/x-component");
        mimeTypes.put(".css", "text/css");
        mimeTypes.put(".js", "text/javascript");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException
    {
        if (CONTEXT_PATH_JS_PATH.equals(req.getPathInfo()))
        {
            OutputStream os = resp.getOutputStream();
            resp.setContentType("text/javascript");
            resp.setCharacterEncoding("UTF-8");
            byte[] b = String.format(CONTEXT_PATH_JS, req.getContextPath())
                    .getBytes("UTF-8");
            resp.setContentLength(b.length);
            os.write(b);
            os.flush();
            return;
        }
        URLConnection con;
        try
        {
            con = getResourceConnection(req);
        }
        catch (ResourceNotFoundException rnfx)
        {
            logger.error(rnfx.getMessage());
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String ct = con.getContentType();
        
        //FIXME does this cover all cases? probably not.
        if (ct == null || ct.equals("content/unknown"))
        {
            String name = con.getURL().toString();
            int idx = name.lastIndexOf('.');
            if (idx < 0)
            {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String ext = name.substring(idx);
            ct = mimeTypes.get(ext.toLowerCase());
            if (ct == null)
            {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }
        else if (!mimeTypes.containsValue(ct))
        {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        //FIXME Encoding?
        InputStream is = con.getInputStream();
        OutputStream os = resp.getOutputStream();
        resp.setContentType(ct);
        IOUtils.copy(is, os);
        is.close();
        os.flush();
    }

    @Override
    protected long getLastModified(HttpServletRequest req)
    {
        try
        {
            URLConnection con = getResourceConnection(req);
            return con.getLastModified();
        }
        catch (Exception x)
        {
			//@TODO: Maybe neaten this 'magic string' somehow
            logger.error("[getLastModified] "+x.getMessage());
            return -1;
        }
    }
    
    protected URLConnection getResourceConnection(HttpServletRequest req) 
            throws ResourceNotFoundException, IOException
    {
        String resource = req.getPathInfo();
        if (resource == null)
        {
            throw new ResourceNotFoundException(resource);
        }
        // remove leading slash
        resource = resource.substring(1);
        URLConnection con = res.getResourceConnection(resource);
        if (con == null)
        {
            throw new ResourceNotFoundException(resource);
        }
        return con;
    }

}
