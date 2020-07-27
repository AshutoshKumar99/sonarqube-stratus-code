package com.pb.stratus.controller.action;

import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.marker.MarkerRepository;
import com.pb.stratus.controller.marker.MarkerType;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import uk.co.graphdata.utilities.resource.ResourceResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Action class to get marker image from tenant theme context.
 */
public class MarkerAction extends BaseControllerAction
{
    
    private static final String NAME_PARAM = "name";
    
    private static final String TYPE_PARAM = "type";
    
    private MarkerRepository markerRepo;

    public static final String MARKER_ICON_FOLDER = 
            "images/catalog/markericon";

    public static final String MARKER_SHADOW_FOLDER = 
            "images/catalog/markershadow";

    private ResourceResolver tenantThemeResourceResolver;

    private static final Logger logger = LogManager.getLogger(MarkerAction.class.getName());

    public MarkerAction(MarkerRepository markerRepo)

    {
        this.markerRepo = markerRepo;
    }

    public void execute(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String fileName = request.getParameter(NAME_PARAM);
        String mimeType = getMimeType(fileName);
        response.setContentType(mimeType);
        OutputStream os = response.getOutputStream();
        MarkerType markerType = getMarkerType(request);
        InputStream is = markerRepo.getMarkerImage(fileName, markerType);
        try
        {
            IOUtils.copy(is, os);
        }
        catch (Exception e)
        {
            logger.error("Client Abort :"+e.getMessage(), e);
        }
        finally
        {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    private String getMimeType(String fileName)
    {
        if (fileName.endsWith(".png"))
        {
            return "image/png";
        }
        if (fileName.endsWith(".jpg"))
        {
            return "image/jpeg";
        }
        if (fileName.endsWith(".gif"))
        {
            return "image/gif";
        }
        return null;
    }
    
    private MarkerType getMarkerType(HttpServletRequest request)
    {
        String type = request.getParameter(TYPE_PARAM);
        try
        {
            return MarkerType.valueOf(type.toUpperCase());
        }
        catch (IllegalArgumentException iax)
        {
            throw new IllegalRequestException("Unsupported marker type '" 
                    + type + "'");
        }
    }
}
