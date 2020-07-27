package com.pb.stratus.controller.json;

import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinition;
import com.mapinfo.midev.service.featurecollection.v1.KeyDefinition;
import com.pb.stratus.controller.info.DescribeTableResult;
import com.pb.stratus.controller.legend.*;
import org.apache.commons.lang.StringEscapeUtils;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Serialises DescribeTableResult objects into JSON strings.
 */
public class DescribeTableResultStrategy extends OwnedConverterStrategy
{

    public void processValue(Object value, StringBuilder b)
    {
        DescribeTableResult holder = (DescribeTableResult) value;
        List<AttributeDefinition> defnList = holder.getDefinitionList();
        b.append("{\"supportsInsert\": \"");
        b.append(escape(String.valueOf(holder.isSupportsInsert())));
        b.append("\", \"keyDefinition\": ");
        KeyDefinition keyDefinition = holder.getKeyDefinition();
        processKeyDefinition(keyDefinition, b);
        b.append(", \"definitionList\": [");
        processAttributeList(defnList,b);
        b.append("]}");
    }

    private void processKeyDefinition(KeyDefinition keyDefinition, StringBuilder b) {
        KeyDefinitionStrategy keyDefinitionStrategy = new KeyDefinitionStrategy();
        keyDefinitionStrategy.processValue(keyDefinition, b);
    }

    private void processAttributeList(List<AttributeDefinition> defnList, StringBuilder b)
    {
        AttributeDefinitionStrategy attributeDefinitionStrategy = new AttributeDefinitionStrategy();
        int idx = 0;
        for(AttributeDefinition attributeDefinition: defnList){
            if (idx++ > 0)
            {
                b.append(", ");
            }
            attributeDefinitionStrategy.processValue(attributeDefinition,b);
        }
    }
    
    private String escape(String value)
    {
        return StringEscapeUtils.escapeJavaScript(value);
    }
}
