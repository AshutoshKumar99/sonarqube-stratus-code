package com.pb.stratus.controller.print.content;

import com.pb.stratus.controller.print.MapMarkerParameters;
import com.pb.stratus.core.util.ObjectUtils;
import uk.co.graphdata.utilities.contract.Contract;

import java.util.Comparator;

/**
 * Compares two markers to determine which one should be rendered above the 
 * other in a map image
 */
public class MapMarkerParametersComparator implements Comparator<MapMarkerParameters>
{

    /**
     * Compares the two given markers. If o1 should be rendered above o2, the
     * return value is positive, otherwise the return value is negative. Only
     * if the two markers are at the same location, the return value is 0.
     * 
     * @throws IllegalArgumentException if o1 and o2 are from different
     *         projection systems.
     */
    public int compare(MapMarkerParameters o1, MapMarkerParameters o2)
    {
        Contract.pre(ObjectUtils.equals(o1.getProjection(), 
                o2.getProjection()), "Only markers with the same SRS can " 
                + "be compared");
        if (o1.getMarkerY() < o2.getMarkerY())
        {
            return 1;
        }
        if (o1.getMarkerY() > o2.getMarkerY())
        {
            return -1;
        }
        if (o1.getMarkerX() < o2.getMarkerX())
        {
            return 1;
        }
        if (o1.getMarkerX() > o2.getMarkerX())
        {
            return -1;
        }
        return 0;
    }

}
