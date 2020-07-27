package com.pb.stratus.controller.json;

import com.mapinfo.midev.service.featurecollection.v1.*;
import com.pb.stratus.controller.info.DescribeTableResult;
import com.pb.stratus.controller.legend.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by ra007gi on 1/13/2016.
 */
public class DescribeTableResultStrategyTest {

    private DescribeTableResultStrategy strategy;
    DescribeTableResult  mockResult;

    @Before
    public void setUp()

    {
        strategy = new DescribeTableResultStrategy();
        mockResult = new DescribeTableResult();

        List<AttributeDefinition> attributeList = new ArrayList<>();
        AttributeDefinition attributeDefinition1 = new ScalarAttributeDefinition() ;
        attributeDefinition1.setDataType(AttributeDataType.STRING);
        attributeDefinition1.setReadOnly(false);
        attributeDefinition1.setName("Description");
        attributeList.add(attributeDefinition1);
        AttributeDefinition attributeDefinition2 = new ScalarAttributeDefinition() ;
        attributeDefinition2.setDataType(AttributeDataType.STRING);
        attributeDefinition2.setReadOnly(false);
        attributeDefinition2.setName("Col_Name");
        attributeList.add(attributeDefinition2);

        KeyDefinition keyDefinition = new KeyDefinition();
        keyDefinition.setKeyType(KeyType.EXPLICIT);

        mockResult.addAttributionDefinitionList(attributeList);
        mockResult.setKeyDefinition(keyDefinition);
        mockResult.setSupportsInsert(false);
    }

    @Test
    public void shouldGenerateExpectedJson()
    {
        String expectedResult = "{\"supportsInsert\": \"false\"" +
                ", \"keyDefinition\": "
                + "{\"name\":\"\",\"type\":\"Explicit\"}" +
                ", \"definitionList\": ["
                + "{\"name\":\"Description\",\"type\":\"STRING\",\"readOnly\":\"false\"}, "
                + "{\"name\":\"Col_Name\",\"type\":\"STRING\",\"readOnly\":\"false\"}]}";

        StringBuilder b = new StringBuilder();
        strategy.processValue(mockResult, b);
        assertEquals(expectedResult, b.toString());
    }

}
