package com.pb.stratus.controller.json.geojson;


import com.mapinfo.midev.service.geometries.v1.LineString;
import com.pb.stratus.core.common.Preconditions;

public class LineStringStrategy extends GeoJsonStrategy{
    @Override
    protected String getType(Object value) {
        return "LineString";
    }

    @Override
    protected void processCoordinates(Object value, StringBuilder b) {
        Preconditions.checkNotNull(value, "value cannot be null");
        if(!(value instanceof LineString))
        {
            throw new IllegalArgumentException(
                    "value should be instanceof LineString");

        }
        processLineStringCoordinates((LineString)value, b);
    }
}
