package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.Curve;
import uk.co.graphdata.utilities.contract.Contract;

public class CurveStrategy extends GeoJsonStrategy
{

    @Override
    protected String getType(Object value)
    {

        return "LineString";
    }

    @Override
    protected void processCoordinates(Object value, StringBuilder b)
    {
        Contract.pre(value != null, "Value required");
        Contract.pre(value instanceof Curve, "Value must be a Curve");
        processCurveCoordinates((Curve) value, b);
    }

}