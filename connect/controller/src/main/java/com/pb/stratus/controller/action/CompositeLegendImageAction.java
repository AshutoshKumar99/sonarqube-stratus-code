package com.pb.stratus.controller.action;

import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.legend.SpriteImageService;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Writes a sprite image containing all icons of a LegendData object to the 
 * response   
 */
public class CompositeLegendImageAction extends BaseControllerAction
{
    private SpriteImageService spriteImageService;
    
    public CompositeLegendImageAction(SpriteImageService spriteImageService)
    {
        this.spriteImageService = spriteImageService;
    }
    
    public void execute(HttpServletRequest request,
            HttpServletResponse response) throws ServletException,
            IOException
    {
        makeImageExpireAfterOneMinute(response);
        String[] overlayNames = getOverlayNames(request);
        BufferedImage image = getSpriteImage(overlayNames);
        writeImageToResponse(image, response);
    }

    private void writeImageToResponse(BufferedImage image,
            HttpServletResponse response) throws IOException
    {
        response.setContentType("image/png");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        byte[] imageData = bos.toByteArray();
        response.setContentLength(imageData.length);
        response.getOutputStream().write(imageData);
    }

    private BufferedImage getSpriteImage(String... overlayNames)
    {
        return spriteImageService.getImage(overlayNames);
    }

    private String[] getOverlayNames(HttpServletRequest request)
    {
        String overlayNames = request.getParameter("overlays");
        if (overlayNames == null)
        {
            throw new IllegalRequestException();
        }
        return overlayNames.split("\\s*,\\s*");
    }

    private void makeImageExpireAfterOneMinute(HttpServletResponse response)
    {
        response.setHeader("Cache-Control", "max-age=60");
    }

}
