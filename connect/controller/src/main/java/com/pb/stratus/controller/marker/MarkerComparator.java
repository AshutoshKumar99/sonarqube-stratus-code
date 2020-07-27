package com.pb.stratus.controller.marker;

import com.pb.stratus.controller.print.Marker;

import java.util.Comparator;

/**
 * Orders markers from North-West to South-East. If a collection of markers is
 * rendered into an image in the order defined by this comparator, they appear
 * to overlap in an intuitive way.  
 */
public class MarkerComparator implements Comparator<Marker>
{
    public int compare(Marker o1, Marker o2)
    {
        double relY = o2.getLocation().getY() - o1.getLocation().getY();
        if (relY != 0)
        {
            return (int) Math.signum(relY);
        }
        return (int) Math.signum(o2.getLocation().getX() 
                - o1.getLocation().getX()); 
    }
}
