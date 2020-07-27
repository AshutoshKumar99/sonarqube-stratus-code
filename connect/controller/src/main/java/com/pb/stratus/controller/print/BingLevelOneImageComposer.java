package com.pb.stratus.controller.print;

import uk.co.graphdata.utilities.contract.Contract;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Given a desired mage size, this class calculates the imagesize to be 
 * requested from Bing with the offset into the current image 
 * 
 */

public class BingLevelOneImageComposer
{
    private static final int WORLD_PIXELS = 512;
    
    public Dimension getImageSizeForBingRequest(Dimension desiredSize)
    {
        Contract.pre(desiredSize != null, "imageCanvas is required");
        
        int width = desiredSize.width;
        int height = desiredSize.height;
        
        if (width > WORLD_PIXELS)
        {
            width = WORLD_PIXELS;
        }
        
        if (height > WORLD_PIXELS)
        {
            height = WORLD_PIXELS;
        }
        
        return new Dimension(width, height);
    }
    
    public Point2D getOffsetInImage(Dimension desiredSize, 
            Dimension levelOneImageSize)
    {
        int offsetX = 
            Math.round((desiredSize.width - levelOneImageSize.width) / 2); 
        int offsetY = 
            Math.round((desiredSize.height - levelOneImageSize.height) / 2); 
        return new Point2D.Double(offsetX, offsetY);
    }
}
