package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.MultiPoint;
import com.mapinfo.midev.service.geometries.v1.Point;
import uk.co.graphdata.utilities.contract.Contract;

public class MultiPointStrategy extends GeoJsonStrategy
{

    @Override
    protected String getType(Object value)
    {
        return "MultiPoint";
    }

    @Override
    protected void processCoordinates(Object value, StringBuilder b)
    {
        Contract.pre(value != null, "Value required");
        Contract.pre(value instanceof MultiPoint, "Value must be a MultiPoint");
        MultiPoint mp = (MultiPoint) value;
        b.append("[");
        int i = 0;
        for (Point p : mp.getPoint())
        {
            if (i++ > 0)
            {
                b.append(", ");
            }
            processPointCoordinates(p, b);
        }
        b.append("]");
    }

}
