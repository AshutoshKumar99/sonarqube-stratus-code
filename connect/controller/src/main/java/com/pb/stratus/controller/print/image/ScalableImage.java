package com.pb.stratus.controller.print.image;

import uk.co.graphdata.utilities.contract.Contract;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * A ScalableImage is an image that is supposed to be scaled down in case it
 * increases a given maximum side length, maintaining the aspect ratio. 
 * Independent of the size of the image, it is guaranteed that neither the 
 * height, nor the width of the image returned from {@link #getScaledImage()} 
 * exceed the maxim side length.
 */
public class ScalableImage
{
    
    private BufferedImage image;
    
    private int maxSideLength = -1;

    public ScalableImage(BufferedImage img)
    {
        this.image = img;
    }

    /**
     * @return an image whose side lengths don't exceed the previously set 
     *         maxSideLength. If no maxSideLength was set, the original image
     *         will be returned.
     */
    public BufferedImage getScaledImage()
    {
        if (!needsScaling())
        {
            return image;
        }
        Dimension newSize = calculateScaledDimension();
        return scaleTo(newSize);
    }

    private boolean needsScaling()
    {
        if (maxSideLength < 0)
        {
            return false;
        }
        return image.getWidth() > maxSideLength 
                || image.getHeight() > maxSideLength;
    }

    private Dimension calculateScaledDimension()
    {
        int width = image.getWidth();
        int height = image.getHeight();
        double scaleFactor = ((double) maxSideLength) 
                / Math.max(width, height);
        return new Dimension((int) (width * scaleFactor), 
                (int) (height * scaleFactor));
    }

    /**
     * Sets the maximum value which must not be exceeded by either the height 
     * or the width of the resulting image.
     * 
     * @param maxSideLength a positive integer
     */
    public void setMaxSideLength(int maxSideLength)
    {
        Contract.pre(maxSideLength > 0, "maxSideLength must be positive");
        this.maxSideLength = maxSideLength;
    }
    
    private BufferedImage scaleTo(Dimension newSize)
    {
        Image newImage = image.getScaledInstance(newSize.width, newSize.height, 
                Image.SCALE_SMOOTH);
        BufferedImage scaledImage = new BufferedImage(
            newSize.width, newSize.height, image.getType());
        Graphics2D graphics2D = scaledImage.createGraphics();
        graphics2D.drawImage(newImage, 0, 0, null);
        graphics2D.dispose();
        return scaledImage;
    }

}
