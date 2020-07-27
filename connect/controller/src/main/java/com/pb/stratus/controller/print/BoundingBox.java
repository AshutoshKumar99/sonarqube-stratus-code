package com.pb.stratus.controller.print;

import com.pb.stratus.core.util.ObjectUtils;
import uk.co.graphdata.utilities.contract.Contract;

import java.awt.geom.Point2D;

/**
 * A bounding box is a rectangular area expressed in coordinate units of a 
 * given spatial reference system. Note, that, depending on the SRS, the 
 * bounding box might not represent a rectangular area on the surface of the 
 * earth.
 */
public class BoundingBox
{
    private double north;
    
    private double south;
    
    private double west;
    
    private double east;
    
    private String srs;
    
    public BoundingBox(double north, double south, double west, double east, 
            String srs)
    {
        Contract.pre(north > south, "North bound has to be greater " 
                + "than South bound");
        Contract.pre(east > west, "East bound has to be greater " 
                + "than West bound");
        this.north = north;
        this.south = south;
        this.west = west;
        this.east = east;
        this.srs = srs;
    }

    public double getNorth()
    {
        return north;
    }

    public double getSouth()
    {
        return south;
    }

    public double getWest()
    {
        return west;
    }

    public double getEast()
    {
        return east;
    }
    
    public String getSrs()
    {
        return srs;
    }

    public double getWidth()
    {
        return Math.abs(west - east);
    }

    public double getHeight()
    {
        return Math.abs(north - south);
    }
    
    public Point2D getCenter()
    {
        return new Point2D.Double(west + (this.getWidth() / 2), 
                south + (this.getHeight() / 2));
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
        if (o.getClass() != this.getClass())
        {
            return false;
        }
        BoundingBox that = (BoundingBox) o;
        if (this.north != that.north)
        {
            return false;
        }
        if (this.south != that.south)
        {
            return false;
        }
        if (this.west != that.west)
        {
            return false;
        }
        if (this.west != that.west)
        {
            return false;
        }
        if (!ObjectUtils.equals(this.srs, that.srs))
        {
            return false;
        }
        return true;
    }
    
    public int hashCode()
    {
        int hc = ObjectUtils.SEED;
        hc = ObjectUtils.hash(hc, north);
        hc = ObjectUtils.hash(hc, south);
        hc = ObjectUtils.hash(hc, west);
        hc = ObjectUtils.hash(hc, east);
        hc = ObjectUtils.hash(hc, srs);
        return hc;
    }

    public boolean contains(BoundingBox other)
    {
        if (other.west < this.west)
        {
            return false;
        }
        if (other.east > this.east)
        {
            return false;
        }
        if (other.north > this.north)
        {
            return false;
        }
        if (other.south < this.south)
        {
            return false;
        }
        return true;
    }
    
    /**
     * Determine whether the target bounds intersects this bounds.  Bounds are
     *     considered intersecting if any of their edges intersect or if one
     *     bounds contains the other.
     * 
     * @param bounds BoundingBox The target bounds.
     * @param inclusive boolean Treat coincident borders as intersecting.  Default
     *     is true.  If false, bounds that do not overlap but only touch at the
     *     border will not be considered as intersecting.
     *
     * @return boolean The passed-in bounds object intersects this bounds.
     */
    public boolean intersects(BoundingBox bounds, boolean inclusive) {
      
        boolean intersects = false;
        boolean  mightTouch = (
            this.west == bounds.east ||
            this.east == bounds.west ||
            this.north == bounds.south ||
            this.south == bounds.north
        );
        
        // if the two bounds only touch at an edge, and inclusive is false,
        // then the bounds don't *really* intersect.
        if (inclusive || !mightTouch) {
            // otherwise, if one of the boundaries even partially contains another,
            // inclusive of the edges, then they do intersect.
            boolean inBottom = (
                ((bounds.south >= this.south) && (bounds.south <= this.north)) ||
                ((this.south >= bounds.south) && (this.south <= bounds.north))
            );
            boolean inTop = (
                ((bounds.north >= this.south) && (bounds.north <= this.north)) ||
                ((this.north > bounds.south) && (this.north < bounds.north))
            );
            boolean inLeft = (
                ((bounds.west >= this.west) && (bounds.west <= this.east)) ||
                ((this.west >= bounds.west) && (this.west <= bounds.east))
            );
            boolean inRight = (
                ((bounds.east >= this.west) && (bounds.east <= this.east)) ||
                ((this.east >= bounds.west) && (this.east <= bounds.east))
            );
            intersects = ((inBottom || inTop) && (inLeft || inRight));
}
        return intersects;
    }
    
}
