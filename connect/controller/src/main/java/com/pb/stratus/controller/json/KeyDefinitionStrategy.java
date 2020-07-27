package com.pb.stratus.controller.json;

import com.mapinfo.midev.service.featurecollection.v1.KeyDefinition;

import java.util.List;

/**
 * Created by VI012GU on 2/23/2016.
 */
public class KeyDefinitionStrategy implements TypedConverterStrategy {
    @Override
    public void processValue(Object value, StringBuilder b) {
        KeyDefinition holder = (KeyDefinition) value;
        b.append("{");
        b.append("\"name\":");
        b.append("\"");
        List<String> attributeRefList = holder.getAttributeRef();
        if (null != attributeRefList && attributeRefList.size() > 0) {
            b.append(holder.getAttributeRef().get(0));
        }
        b.append("\"");
        b.append(",");
        b.append("\"type\":");
        b.append("\"");
        if (null != holder.getKeyType()) {
            b.append(holder.getKeyType().value());
        }
        b.append("\"");
        b.append("}");
    }
}
