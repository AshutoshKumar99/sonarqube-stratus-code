package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.LegacyText;
import uk.co.graphdata.utilities.contract.Contract;

;

public class LegacyTextStrategy extends GeoJsonStrategy
{

    @Override
    protected String getType(Object value)
    {
        return "LegacyText";
    }

    @Override
    protected void processCoordinates(Object value, StringBuilder b)
    {
        Contract.pre(value != null, "Value required");
        Contract.pre(value instanceof LegacyText, "Value must be a point");
        processLegacyTextCoordinates((LegacyText) value, b);
    }

}
