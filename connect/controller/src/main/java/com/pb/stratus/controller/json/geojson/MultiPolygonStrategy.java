package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.MultiPolygon;
import com.mapinfo.midev.service.geometries.v1.Polygon;

public class MultiPolygonStrategy extends GeoJsonStrategy
{

    @Override
    protected String getType(Object value)
    {
        return "MultiPolygon";
    }

    @Override
    protected void processCoordinates(Object value, StringBuilder b)
    {
        MultiPolygon multiPolygon = (MultiPolygon) value;
        int i = 0;
        b.append("[");
        for (Polygon poly : multiPolygon.getPolygon())
        {
            if (i++ > 0)
            {
                b.append(", ");
            }
            processPolygonCoordinates(poly, b);
        }
        b.append("]");
    }

}
