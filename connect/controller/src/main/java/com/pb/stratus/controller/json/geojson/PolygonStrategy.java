package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.Polygon;
import uk.co.graphdata.utilities.contract.Contract;

public class PolygonStrategy extends GeoJsonStrategy
{

    @Override
    protected String getType(Object value)
    {
        return "Polygon";
    }

    @Override
    protected void processCoordinates(Object value, StringBuilder b)
    {
        Contract.pre(value != null, "Value required");
        Contract.pre(value instanceof Polygon, "Value must be a Polygon");
        processPolygonCoordinates((Polygon) value, b);
    }

}
