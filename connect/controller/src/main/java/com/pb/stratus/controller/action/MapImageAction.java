package com.pb.stratus.controller.action;

import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.compositor.MapCompositor;
import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.Marker;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * This action renders a map image with the watermark and copyright 
 */
public class MapImageAction extends BaseControllerAction
{
    
    public static final String PIXEL_WIDTH_PARAM = "pixelWidth";
    
    public static final String OUTPUT_PARAM = "output";
    
    public static final int MAX_IMAGE_DIM = 1680;
    public static final int MIN_IMAGE_DIM = 160;
    public static final String DEFAULT_UNIT = "METER";
    
    
    private MapCompositor mapCompositor;
    
    public MapImageAction(MapCompositor mapCompositor)
    {
        this.mapCompositor = mapCompositor;
    }

    public void execute(HttpServletRequest request, 
            HttpServletResponse response) throws ServletException, IOException
    {
        MapImageRequestParameters params = new MapImageRequestParameters(
                request);
        BoundingBox bb = params.getBoundingBox();
        Dimension imageSize = calculateImageSize(params.getBoundingBox(), 
                request);
        assertImageSizeSupported(imageSize);
        List<Marker> emptyMarkerList = Collections.emptyList();
        List<Annotation> emptyAnnotations = Collections.emptyList();
        String unit = request.getParameter(DISPLAY_UNIT_PARAM);

        /**
         * Pass the default unit of display
         */
        unit = (unit != null)?unit: DEFAULT_UNIT;

        BufferedImage image = mapCompositor.renderMap(bb, imageSize, 
                params.getMapConfigName(), params.getLayers(), emptyMarkerList,
                emptyAnnotations, null, null,null, params.getZoomLevel(), unit);

        String imageType = getImageType(request);
        response.setContentType("image/" + imageType);
        BufferedImage outputImage = createOutputBufferedImage(imageType, image);
        ImageIO.write(outputImage, imageType, response.getOutputStream());
    }

    private BufferedImage createOutputBufferedImage(String imageType,
        BufferedImage image)
    {
        BufferedImage outputBufferImage;

        if ("png".equals(imageType))
        {
            outputBufferImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        }
        else
        {
            outputBufferImage = new BufferedImage(image.getWidth(),
                image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        }

        Graphics2D g = outputBufferImage.createGraphics();

        try
        {
            // For the jpg and gif, add a white background to the canvas as this
            // will replace the blackground due to transparency issues
            if ("jpg".equals(imageType) || "jpeg".equals(imageType)
                || "gif".equals(imageType))
            {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, image.getWidth(), image.getHeight());
                g.setComposite(AlphaComposite.SrcOver);
            }

        g.drawImage(image, null, 0, 0);

            return outputBufferImage;
            
    }
        finally
        {
            g.dispose();
        }
    }
    
    private void assertImageSizeSupported(Dimension size)
    {
        if ((size.width > MAX_IMAGE_DIM) || 
            (size.height > MAX_IMAGE_DIM))
        {
            throw new IllegalRequestException("Requested image size exceeds " +
            		"the maximum supported size of " + MAX_IMAGE_DIM + " X " +
            		MAX_IMAGE_DIM);
        }
        
        if ((size.width < MIN_IMAGE_DIM) || 
            (size.height < MIN_IMAGE_DIM))
        {
            throw new IllegalRequestException("Requested image size is smaller " +
                    "than the minimum supported size of " + MIN_IMAGE_DIM + " X " +
                    MIN_IMAGE_DIM);
        }
    }
    
    private String getImageType(HttpServletRequest request)
    {
        String imageType = request.getParameter(OUTPUT_PARAM);
        if ("png".equals(imageType) || "jpg".equals(imageType) 
                || "jpeg".equals(imageType) || "gif".equals(imageType))
        {
            return imageType;
        }
        else
        {
            throw new IllegalRequestException("Image type '" + imageType 
                    + "' not supported");
        }
    }
    
    private Dimension calculateImageSize(BoundingBox bb, 
            HttpServletRequest request)
    {
        int width = Integer.parseInt(request.getParameter(PIXEL_WIDTH_PARAM));
        double height = width / bb.getWidth() * bb.getHeight();
        return new Dimension(width, (int) Math.round(height));
    }
}
