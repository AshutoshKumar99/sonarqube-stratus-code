package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.Envelope;
import uk.co.graphdata.utilities.contract.Contract;

public class EnvelopeStrategy extends GeoJsonStrategy
{

    @Override
    protected String getType(Object value)
    {
        return "Envelope";
    }

    @Override
    protected void processCoordinates(Object value, StringBuilder b)
    {
        Contract.pre(value != null, "Value required");
        Contract.pre(value instanceof Envelope, "Value must be a Polygon");
        processEnvelopeCoordinates((Envelope) value, b);
    }

}
