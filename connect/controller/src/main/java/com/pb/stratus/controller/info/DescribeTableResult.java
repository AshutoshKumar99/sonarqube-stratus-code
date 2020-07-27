package com.pb.stratus.controller.info;

import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinition;
import com.mapinfo.midev.service.featurecollection.v1.KeyDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This object represents the GeoJson structure of the describe table result
 * object.
 */
public class DescribeTableResult
{
    private List<AttributeDefinition> definitionList = new
            ArrayList<AttributeDefinition>();

    private KeyDefinition keyDefinition;

    private Boolean supportsInsert = false;

    public List<AttributeDefinition> getDefinitionList()
    {
        return Collections.unmodifiableList(definitionList);
    }

    public void addAttributionDefinitionList(List<AttributeDefinition>
            attributeDefinitions)
    {
        definitionList.addAll(attributeDefinitions);
    }

    public KeyDefinition getKeyDefinition() {
        return keyDefinition;
    }

    public void setKeyDefinition(KeyDefinition keyDefinition) {
        this.keyDefinition = keyDefinition;
    }

    public void setSupportsInsert(boolean supportsInsert) {
        this.supportsInsert = supportsInsert;
    }

    public Boolean isSupportsInsert() {
        return supportsInsert;
    }
}
