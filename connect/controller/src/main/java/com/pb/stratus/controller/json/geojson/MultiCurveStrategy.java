package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.Curve;
import com.mapinfo.midev.service.geometries.v1.MultiCurve;
import uk.co.graphdata.utilities.contract.Contract;

public class MultiCurveStrategy extends GeoJsonStrategy
{

    @Override
    protected String getType(Object value)
    {

        return "MultiLineString";
    }

    @Override
    protected void processCoordinates(Object value, StringBuilder b)
    {
        Contract.pre(value != null, "Value required");
        Contract.pre(value instanceof MultiCurve, "Value must be a MultiCurve");
        MultiCurve curves = (MultiCurve) value;
        b.append("[");
        int i = 0;
        for (Curve curve : curves.getCurve())
        {
            if (i++ > 0)
            {
                b.append(", ");
            }
            processCurveCoordinates(curve, b);
        }
        b.append("]");
    }

}