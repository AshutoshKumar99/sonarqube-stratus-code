
package com.pb.stratus.controller.print;

import com.pb.stratus.core.util.ObjectUtils;
import uk.co.graphdata.utilities.contract.Contract;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This class contains all the parameters for the map markers and it
 * contains populate method to read the parameters from the request object
 *
 */
public class MapMarkerParameters
{
    private BufferedImage markerImage;

    private BufferedImage markerShadowImage;
    
    private String projectionSrs;
    
    private double xCoordinate;
    
    private double yCoordinate;
    
    private Point anchorPoint;

    private Point shadowAnchorPoint;
    
    public MapMarkerParameters()
    {
    }
    
    public MapMarkerParameters(double x, double y, String srs)
    {
        this.projectionSrs = srs;
        this.xCoordinate = x;
        this.yCoordinate = y;
    }
    
    public MapMarkerParameters(BufferedImage markerImage, String srs, double x,
            double y)
    {
        this(x, y, srs);
        this.markerImage = markerImage;
    }

    public void setMarker(BufferedImage marker)
    {
        this.markerImage = marker;
    }

    public BufferedImage getMarker()
    {
        return this.markerImage;
    }

    public void setProjection(String projection)
    {
        this.projectionSrs = projection;
    }

    public String getProjection()
    {
        return this.projectionSrs;
    }

    public void setMarkerX(double markerX)
    {
        this.xCoordinate = markerX;
    }

    public double getMarkerX()
    {
        return this.xCoordinate;
    }

    public void setMarkerY(double markerY)
    {
        this.yCoordinate = markerY;
    }

    public double getMarkerY()
    {
        return this.yCoordinate;
    }

    public Point getAnchorPoint()
    {
        Contract.pre(markerImage != null, "Marker image has to be set before " 
                + "getAnchorPoint can be called");
        if (anchorPoint == null)
        {
            anchorPoint = getDefaultAnchorPoint(markerImage);
        }
        return anchorPoint;
    }

    public void setAnchorPoint(Point anchorPoint)
    {
        this.anchorPoint = anchorPoint;
    }

    public Point getShadowAnchorPoint()
    {
        Contract.pre(markerShadowImage != null, "Marker Shadow image has to be set before "
                + "getShadowAnchorPoint can be called");
        if (shadowAnchorPoint == null)
        {
            shadowAnchorPoint = getDefaultAnchorPoint(markerImage);
        }
        return shadowAnchorPoint;
    }

    public void setShadowAnchorPoint(Point shadowAnchorPoint)
    {
        this.shadowAnchorPoint = shadowAnchorPoint;
    }
    
    public boolean equals(Object o)
    {
        MapMarkerParameters that = ObjectUtils.castToOrReturnNull(
                MapMarkerParameters.class, o);
        if (that == null)
        {
            return false;
        }
        if (!ObjectUtils.equals(this.anchorPoint, that.anchorPoint))
        {
            return false;
        }
        if (!ObjectUtils.equals(this.markerImage, that.markerImage))
        {
            return false;
        }
        if (!ObjectUtils.equals(this.projectionSrs, that.projectionSrs))
        {
            return false;
        }
        if (!ObjectUtils.equals(this.xCoordinate, that.xCoordinate))
        {
            return false;
        }
        if (!ObjectUtils.equals(this.yCoordinate, that.yCoordinate))
        {
            return false;
        }
        return true;
    }
    
    public int hashCode()
    {
        int hc = ObjectUtils.SEED;
        hc = ObjectUtils.hash(hc, anchorPoint);
        hc = ObjectUtils.hash(hc, markerImage);
        hc = ObjectUtils.hash(hc, projectionSrs);
        hc = ObjectUtils.hash(hc, xCoordinate);
        hc = ObjectUtils.hash(hc, yCoordinate);
        return hc;
    }
    
    private Point getDefaultAnchorPoint(BufferedImage image)
    {
        return new Point((int) (image.getWidth() / 2d), 
                image.getHeight() - 1);
    }

    /**
     * @return the markerShadowImage
     */
    public BufferedImage getMarkerShadowImage()
    {
        return markerShadowImage;
    }

    /**
     * @param markerShadowImage the markerShadowImage to set
     */
    public void setMarkerShadowImage(BufferedImage markerShadowImage)
    {
        this.markerShadowImage = markerShadowImage;
    }
}
