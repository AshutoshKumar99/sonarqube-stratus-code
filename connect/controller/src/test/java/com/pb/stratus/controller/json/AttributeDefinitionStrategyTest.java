package com.pb.stratus.controller.json;

import com.mapinfo.midev.service.featurecollection.v1.AttributeDataType;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinition;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class AttributeDefinitionStrategyTest
{
    private AttributeDefinitionStrategy strat;

    @Before
    public void setUp()
    {
        strat = new AttributeDefinitionStrategy();
    }

    @Test
    public void testConversion()
    {
        StringBuilder b = new StringBuilder();
        AttributeDefinition attributeDefinition = new AttributeDefinition(){
            @Override
            public String getName()
            {
                return "dummyName";
            }
            @Override
            public AttributeDataType getDataType()
            {
                return AttributeDataType.BOOLEAN;
            }
            @Override
            public Boolean isReadOnly() {
                return false;
            }
        };
        strat.processValue(attributeDefinition, b);
        String expectedResult = "{\"name\":\"dummyName\"," +
                "\"type\":\"BOOLEAN\"," +
                "\"readOnly\":\"false\"}";
        assertEquals(expectedResult, b.toString());
    }
}
