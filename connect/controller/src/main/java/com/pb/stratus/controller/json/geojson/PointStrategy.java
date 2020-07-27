package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.Point;
import uk.co.graphdata.utilities.contract.Contract;

public class PointStrategy extends GeoJsonStrategy
{

    @Override
    protected String getType(Object value)
    {
        return "Point";
    }

    @Override
    protected void processCoordinates(Object value, StringBuilder b)
    {
        Contract.pre(value != null, "Value required");
        Contract.pre(value instanceof Point, "Value must be a point");
        processPointCoordinates((Point) value, b);
    }

}
