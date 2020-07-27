package com.pb.stratus.controller.print;

import com.pb.stratus.core.util.ObjectUtils;

import java.awt.*;

/**
 * The resolution is a property of an image that expresses the ratio of pixels
 * per distance unit.
 */
public class Resolution
{
    private static final double INCH_IN_CM = 2.54;
    
    private int pixelsPerInch;
    
    public Resolution(int pixelsPerInch)
    {
        this.pixelsPerInch = pixelsPerInch;
    }
    
    public int getPixelsPerInch()
    {
        return pixelsPerInch;
    }
    
    public Dimension calculatePixelDimensions(String width, String height) 
            throws IllegalArgumentException
    {
        checkUnitIsCm(width);
        checkUnitIsCm(height);
        return new Dimension(convertToPixels(width),
                convertToPixels(height));
    }

    private int convertToPixels(String distance)
    {
        try
        {
            double value = Double.parseDouble(
                    distance.substring(0, distance.length() - 2));
            double pixelValue = value * pixelsPerInch / INCH_IN_CM;
            return (int) Math.round(pixelValue);
        }
        catch (NumberFormatException nfx)
        {
            throw new IllegalArgumentException(String.format("Cannot parse " 
                    + "distance expression: %s", distance));
        }
    }

    private void checkUnitIsCm(String distance)
    {
        if (!distance.endsWith("cm"))
        {
            throw new IllegalArgumentException("The unit in the distance " 
                    + "expression '" + distance + "' is not supported");
        }
    }
    
    public double calculatePixelWidthInCm(int pixels)
    {
        return ((double) pixels) / pixelsPerInch * INCH_IN_CM;
    }
    
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null)
        {
            return false;
        }
        if (this.getClass() != o.getClass())
        {
            return false;
        }
        Resolution that = (Resolution) o;
        return this.pixelsPerInch == that.pixelsPerInch;
    }
    
    public int hashCode()
    {
        return ObjectUtils.hash(ObjectUtils.SEED, pixelsPerInch);
    }
    

}
