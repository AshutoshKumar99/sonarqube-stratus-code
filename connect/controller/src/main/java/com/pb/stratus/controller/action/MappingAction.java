package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.mapping.v1.RenderMapResponse;
import com.mapinfo.midev.service.mapping.v1.Rendering;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.service.MappingService;
import com.pb.stratus.controller.service.RenderMapParams;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Vinh
 * 
 */
public class MappingAction extends BaseControllerAction
{

    private static final Logger logger = LogManager.getLogger(MappingAction.class);
    private static final String SRS_PARAM_NAME = "srs";
    private static final String X_PARAM_NAME = "x";
    private static final String Y_PARAM_NAME = "y";
    private static final String ZOOM_PARAM_NAME = "zoom";
    private static final String WIDTH_PARAM_NAME = "width";
    private static final String HEIGHT_PARAM_NAME = "height";
    private static final String OUTPUT_PARAM_NAME = "output";
    private static final String NAMED_LAYERS = "layers";
    private static final String RENDERING = "rendering";

    private MappingService mappingService;
    
    public MappingAction(MappingService mappingService)
    {
        this.mappingService = mappingService;
    }

    private MappingService getMappingService()
    {
        return mappingService;
    }
    

    public void execute(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            String output = request.getParameter(OUTPUT_PARAM_NAME);
            byte[] image = null;
            int imageHeight = 542;
            int imageWidth = 542;
            // Log the Query String
            try
            {
                logger.debug(request.getQueryString());

                String layers = request.getParameter(NAMED_LAYERS);
                if(layers != null && !layers.isEmpty())
                {
                    RenderMapResponse renderResponse = getLayerMap(request, true);
                    image = renderResponse.getMapImage().getImage();
                    response.setContentType(output);
                }else
                {
                    try{
                        imageHeight = Integer.parseInt(request.getParameter(HEIGHT_PARAM_NAME));
                        imageWidth =  Integer.parseInt(request.getParameter(WIDTH_PARAM_NAME));
                    }catch(NumberFormatException ex){}
                    image = getTransparentImage(imageWidth, imageHeight);
                    response.setContentType("image/png");
                }
                response.setContentType(output);
            }
            catch (ServiceException exception)
            {
                if (output.equals("image/gif"))
                {
                    response.setContentType("image/gif");
                    InputStream inputStream = getClass()
                            .getResourceAsStream("/transparent.gif");

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
                            1024);
                    byte[] bytes = new byte[512];

                    int readBytes;
                    while ((readBytes = inputStream.read(bytes)) > 0)
                    {
                        outputStream.write(bytes, 0, readBytes);
                    }
                    image = outputStream.toByteArray();

                    inputStream.close();
                    outputStream.close();
                }
                else
                {
                    throw exception;
                }
            }

            String cacheControl = request.getParameter("cacheAge");
            if (cacheControl != null) {
                response.setHeader("Cache-Control", "max-age="+cacheControl);
            } else {
                response.setHeader("Cache-Control", "max-age=3600");
            }
            response.setContentLength(image.length);
            response.getOutputStream().write(image);
            response.flushBuffer();
        }
        catch (ServiceException ex)
        {
            logger.debug(ex);
            throw new IllegalRequestException(ex);
        }
    }


    private RenderMapResponse getLayerMap(HttpServletRequest request, boolean returnImage)
            throws ServiceException, UnsupportedEncodingException
    {
        RenderMapParams params = new RenderMapParams();

        List<String> layersList = createLayerList(request.getParameter(NAMED_LAYERS));

        params.setMapLayers(layersList);
        params.setSrs(request.getParameter(SRS_PARAM_NAME));
        params.setHeight(request.getParameter(HEIGHT_PARAM_NAME));
        params.setWidth(request.getParameter(WIDTH_PARAM_NAME));
        params.setXPos(request.getParameter(X_PARAM_NAME));
        params.setYPos(request.getParameter(Y_PARAM_NAME));
        params.setZoom(request.getParameter(ZOOM_PARAM_NAME));
        params.setImageMimeType(request.getParameter(OUTPUT_PARAM_NAME));
        params.setReturnImage(returnImage);
        params.setRendering(Rendering.QUALITY);


        return getMappingService().getLayerMap(params);
    }

    private List<String> createLayerList(String namedLayer) throws UnsupportedEncodingException
    {
        String[] layers = namedLayer.split(",");
        List<String> layersAsList = new ArrayList<String>();
        if (layers != null && layers.length > 0)
        {
            for (String layer : layers)
            {
                layersAsList.add(URLDecoder.decode(layer, "UTF-8"));
            }
        }
        return layersAsList;
    }

    private byte[] getTransparentImage(int width, int height) throws IOException
    {
        BufferedImage blank = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(blank, "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return imageInByte;
    }

}
