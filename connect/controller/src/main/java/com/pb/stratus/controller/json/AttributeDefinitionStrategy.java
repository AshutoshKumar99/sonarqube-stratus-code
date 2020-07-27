package com.pb.stratus.controller.json;

import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinition;


/**
 * Serialises feature attribute objects into JSON strings.
 */
public class AttributeDefinitionStrategy implements TypedConverterStrategy
{
    public void processValue(Object value, StringBuilder b)
    {
        AttributeDefinition holder = (AttributeDefinition) value;
        b.append("{");
        b.append("\"name\":");
        b.append("\"");
        b.append(holder.getName());
        b.append("\"");
        b.append(",");
        b.append("\"type\":");
        b.append("\"");
        b.append(holder.getDataType());
        b.append("\"");
        b.append(",");
        b.append("\"readOnly\":");
        b.append("\"");
        b.append(holder.isReadOnly());
        b.append("\"");
        b.append("}");
    }
}
