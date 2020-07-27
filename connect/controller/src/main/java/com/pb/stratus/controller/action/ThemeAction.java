package com.pb.stratus.controller.action;

import com.pb.stratus.controller.util.BrandUtil;
import com.pb.stratus.core.configuration.TenantSpecificFileSystemResourceResolver;
import net.sf.json.JSON;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Action class to retrieve a tenant theme resource using the given tenant resource resolver
 */
public class ThemeAction extends BaseControllerAction
{
    private final TenantSpecificFileSystemResourceResolver resourceResolver;
    private final Map<String, String> mimeTypes = new HashMap<String, String>();

    /**
     * Class constructor
     *
     * @param  resourceResolver the pre-initialised tenant resource resolver
     */
    public
    ThemeAction(TenantSpecificFileSystemResourceResolver resourceResolver)
    {
    this.resourceResolver = resourceResolver;

        mimeTypes.clear();
        mimeTypes.put(".gif", "image/gif");
        mimeTypes.put(".png", "image/png");
        mimeTypes.put(".jpg", "image/jpeg");
        mimeTypes.put(".jpeg", "image/jpeg");
        mimeTypes.put(".css", "text/css");
        mimeTypes.put(".js", "text/javascript");
        mimeTypes.put(".ts", "text/x.typescript");
        mimeTypes.put(".htm", "text/html");
        mimeTypes.put(".html", "text/html");
        mimeTypes.put(".ttf", "text/html");
        mimeTypes.put(".woff2", "font/woff2");
        mimeTypes.put(".woff", "font/woff");
        mimeTypes.put(".swf", "application/x-shockwave-flash");
        mimeTypes.put(".pdf", "application/pdf");
        mimeTypes.put(".avi", "video/avi");
        mimeTypes.put(".mp2", "video/mpeg");
        mimeTypes.put(".mp3", "audio/mpeg3");
        mimeTypes.put(".mpeg", "video/mpeg");
        mimeTypes.put(".mpg", "video/mpeg");
        mimeTypes.put(".tif", "image/tiff");
        mimeTypes.put(".tiff", "image/tiff");
        mimeTypes.put(".xml", "application/xml");
        //CONN-24390 Allowing more mime types to be accessed from Theme Folder
        mimeTypes.put(".3gp", "video/3gpp");
        mimeTypes.put(".3g2", "video/3gpp2");
        mimeTypes.put(".kml", "application/vnd.google-earth.kml+xml");
        mimeTypes.put(".kmz", "application/vnd.google-earth.kmz");
        mimeTypes.put(".svg", "image/svg+xml");
        mimeTypes.put(".wmv", "video/x-ms-wmv");
        mimeTypes.put(".mp4a", "audio/mp4");
        mimeTypes.put(".mp4", "video/mp4");
        mimeTypes.put(".mp4Audio", "application/mp4");
        mimeTypes.put(".weba", "audio/webm");
        mimeTypes.put(".webm", "video/webm");
        mimeTypes.put(".oga", "audio/ogg");
        mimeTypes.put(".ogv", "video/ogg");
        mimeTypes.put(".ogx", "application/ogg");
        mimeTypes.put(".json", "application/json");
        mimeTypes.put(".qt", "video/quicktime");
        mimeTypes.put(".mov", "video/quicktime");

    }

    /**
     * Handles the http request
     *
     * @param  request  HTTP request
     * @param  response HTTP response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String fileName = request.getPathInfo();
        String mimeType = getMimeTypeFromFileExtension(fileName);

        if (mimeType == null)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }else
        {
            OutputStream os = response.getOutputStream();
            response.setContentType(mimeType);
            if("brandstyle".equals(request.getParameter(BrandUtil.TYPE_PARAM))){
                fileName = getBrandCssFile(request, fileName);
            }
            InputStream is = resourceResolver.getResourceAsStream(fileName);

            if (is == null)
            {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
            else if (is != null && mimeType.equalsIgnoreCase(mimeTypes.get(".xml")))
            {
                OutputStreamWriter osw = null;
                try{
                    JSON objJson = new XMLSerializer().readFromStream(is);
                    //write JSON data to OutputStream
                    response.setContentType("text/json-comment-filtered");
                    response.setCharacterEncoding("UTF-8");
                    osw = new OutputStreamWriter(response.getOutputStream());
                    osw.write("/*");
                    objJson.write(osw);
                    osw.write("*/");
                }finally {
                    if (osw != null) {
                        osw.flush();
                        osw.close();
                    }
                }
            }
            else
            {
                IOUtils.copy(is, os);
                is.close();
                os.flush();
            }
        }
    }

    /**
     * Returns the mime type string for a given filename
     *
     * @param  fileName the filename with extension
     *
     * @return mime type string or null if the file extension unrecognised
     */
    private String getMimeTypeFromFileExtension(String fileName)
    {
        String mimeType = null;

        int idx = fileName.lastIndexOf('.');
        if (idx > 0)
        {
            String ext = fileName.substring(idx);
            mimeType = mimeTypes.get(ext.toLowerCase());
        }

        return mimeType;
    }

    /*
    * Returns the brand style path for a request.
    * checks brand parameter then checks mapcfg parameter to get brand from mapconfig.
    * */

    private String getBrandCssFile(HttpServletRequest request, String fileName){
        String mapConfig = "defaultmap";
        String brand = request.getParameter(BrandUtil.BRAND_PARAM);
        String mapcfgParam = request.getParameter(BrandUtil.MAPCFG_PARAM);
        if(brand != null && brand != ""){
            return BrandUtil.getBrandStylePath(brand);
        }
        if(mapcfgParam != null && mapcfgParam != ""){
            mapConfig = mapcfgParam;
        }
        brand = BrandUtil.getBrand(resourceResolver, mapConfig);
        return BrandUtil.getBrandStylePath(brand);
    }
}
