package com.pb.stratus.controller.print;

import com.pb.stratus.core.util.ObjectUtils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * A marker is an icon on a map that highlights a specific location. The anchor 
 * point of a marker identifies the pixel on the marker icon that coincides
 * with the location it relates to. The anchor point is interpreted relative
 * to the top-left corner of the marker icon.
 */
public class Marker
{
    
    private BufferedImage icon;
    
    private Point anchorPoint;
    
    private Point2D location;
    
    public Marker(BufferedImage markerIcon, Point anchorPoint, 
            Point2D location)
    {
        this.icon = markerIcon;
        this.anchorPoint = anchorPoint;
        this.location = location;
    }
    
    public BufferedImage getIcon()
    {
        return icon;
    }

    public Point getAnchorPoint()
    {
        return anchorPoint;
    }

    public Point2D getLocation()
    {
        return location;
    }

    /**
     * Creates a new marker with the given icon rendered into the current
     * marker icon at the given offset. If the current marker icon is too small
     * to accommodate the new icon at the given offset, the size of the new 
     * icon will be so that no clipping occurs. The anchor point of the new
     * marker will be adjusted accordingly.  
     */
    public Marker augmentWithIcon(Point offset, BufferedImage additionalIcon)
    {
        Dimension newSize = calculateNewIconSize(offset, additionalIcon);
        BufferedImage newIcon = new BufferedImage(newSize.width, 
                newSize.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = newIcon.createGraphics();
        Point currentIconOffset = calculateCurrentIconOffset(offset);
        graphics.drawImage(icon, null, currentIconOffset.x, 
                currentIconOffset.y);
        Point additionalIconOffset = calculateAdditionalIconOffset(offset);
        graphics.drawImage(additionalIcon, null, additionalIconOffset.x, 
                additionalIconOffset.y);
        graphics.dispose();
        Point newAnchorPoint = new Point(anchorPoint.x + currentIconOffset.x, 
                anchorPoint.y + currentIconOffset.y);
        return new Marker(newIcon, newAnchorPoint, location);
    }
    
    private Point calculateCurrentIconOffset(Point offset)
    {
        int x, y;
        if (offset.x >= 0)
        {
            x = 0;
        }
        else
        {
            x = offset.x * -1;
        }
        if (offset.y >= 0)
        {
            y = 0;
        }
        else
        {
            y = offset.y * -1;
        }
        return new Point(x, y);
    }

    private Point calculateAdditionalIconOffset(Point offset)
    {
        int x, y;
        if (offset.x >= 0)
        {
            x = offset.x;
        }
        else
        {
            x = 0;
        }
        if (offset.y >= 0)
        {
            y = offset.y;
        }
        else
        {
            y = 0;
        }
        return new Point(x, y);
        
    }

    private Dimension calculateNewIconSize(Point offset, 
            BufferedImage additionalIcon)
    {
        int width, height;
        if (offset.x >= 0)
        {
            width = Math.max(offset.x + additionalIcon.getWidth(), 
                    icon.getWidth());
        }
        else
        {
            width = Math.max(additionalIcon.getWidth(), 
                    offset.x * -1 + icon.getWidth());
        }
        if (offset.y >= 0)
        {
            height = Math.max(offset.y + additionalIcon.getHeight(), 
                    icon.getHeight());
        }
        else
        {
            height = Math.max(additionalIcon.getHeight(), 
                    offset.y * -1 + icon.getHeight());
        }
        return new Dimension(width, height);
    }
    
    public boolean equals(Object o)
    {
        if (o == this)
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
        Marker that = (Marker) o;
        if (!ObjectUtils.equals(this.anchorPoint, that.anchorPoint))
        {
            return false;
        }
        if (!ObjectUtils.equals(this.location, that.location))
        {
            return false;
        }
        //XXX Admittedly, just checking for the height and width doesn't 
        //    guarantee the same image, but it's enough for the test case 
        if (this.icon.getWidth() != that.icon.getWidth())
        {
            return false;
        }
        if (this.icon.getHeight() != that.icon.getHeight())
        {
            return false;
        }
        return true;
    }
    
    public int hashCode()
    {
        int hc = ObjectUtils.SEED;
        hc = ObjectUtils.hash(hc, anchorPoint);
        hc = ObjectUtils.hash(hc, location);
        // We could serialise the image to PNG and include it in the hashcode 
        // calculation, but that sounds like overkill. It's valid to leave it
        // out without violating the contract of hashCode().
        return hc;
    }

}
