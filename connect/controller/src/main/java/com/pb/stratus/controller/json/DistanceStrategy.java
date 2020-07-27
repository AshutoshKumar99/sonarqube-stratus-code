package com.pb.stratus.controller.json;

import com.mapinfo.midev.service.units.v1.Distance;
import com.mapinfo.midev.service.units.v1.DistanceUnit;


/**
 * Serialises feature attribute objects into JSON strings.
 */
public class DistanceStrategy implements TypedConverterStrategy
{
    public void processValue(Object value, StringBuilder b)
    {
        Distance holder = (Distance) value;
        b.append("{");
        b.append("\"value\":");
        b.append("\"");
        b.append(holder.getValue());
        b.append("\"");
        b.append(",");
        b.append("\"unit\":");
        b.append("\"");
        DistanceUnit unit = holder.getUom();
        b.append(unit.value());
        b.append("\"");
        b.append("}");
    }
}
