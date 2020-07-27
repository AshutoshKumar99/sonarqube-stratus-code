package com.pb.stratus.controller.action;


import com.mapinfo.midev.service.feature.v1.DescribeTableResponse;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinition;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinitionList;
import com.mapinfo.midev.service.table.v1.TableMetadata;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.DescribeTableResult;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.easymock.EasyMock.*;

public class DescribeTableActionTest extends
        FeatureSearchActionTestBase
{
    private String tableName;
    private String locale;
    private MockHttpServletRequest request;
    private DescribeTableAction action;
    private FeatureService mockFeatureService;


    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        request = getRequest();
        mockFeatureService = createMock(FeatureService.class);
    }

    @Test
    public void shouldThrowExceptionIfTableNameNotPresent() throws Exception
    {
        request.addParameter("tableName", "");
        action = new DescribeTableAction(mockFeatureService);
        try
        {
            action.createObject(request);
            fail("should throw runtime exception");
        }
        catch(RuntimeException rte)
        {
            // expected
        }
    }

    @Test
    public void testCreateObject() throws Exception
    {
        request.addParameter("tableName", "table1");
        request.addParameter("locale", "en-US");
        DescribeTableResponse describeTableResult = createDummyResponse();
        Capture<String> capture = new Capture<String>();
        expect(mockFeatureService.describeTable(capture(capture))).andReturn(
                describeTableResult);

        replay(mockFeatureService);
        action = new DescribeTableAction(mockFeatureService);
        Object actualResult = action.createObject(request);
        String actualParams = capture.getValue();
        assertEquals("table1", actualParams);
    }

    @Test
    public void conversionOfResult() throws Exception
    {
        request.addParameter("tableName", "table1");
        request.addParameter("locale", "en-US");
        DescribeTableResponse describeTableResult = createDummyResponse();
        Capture<String> capture = new Capture<String>();
        expect(mockFeatureService.describeTable(capture(capture))).andReturn(
                describeTableResult);

        replay(mockFeatureService);
        action = new DescribeTableAction(mockFeatureService);
        Object result = action.createObject(request);
        DescribeTableResult actualResult = (DescribeTableResult)result;
        List<AttributeDefinition> attributeDefinitions = actualResult
            .getDefinitionList();
        assertEquals(1, attributeDefinitions.size());
        assertEquals("attr", attributeDefinitions.get(0).getName());
        assertTrue(actualResult.isSupportsInsert());
    }

    private DescribeTableResponse createDummyResponse()
    {
        DescribeTableResponse describeTableResponse =  new
                DescribeTableResponse();
        TableMetadata tableMetadata = new TableMetadata();
        com.mapinfo.midev.service.featurecollection.v1.AttributeDefinitionList
                attributeDefinitionList = new AttributeDefinitionList();
        List<AttributeDefinition> attributeDefinitions =
            attributeDefinitionList.getAttributeDefinition();
        AttributeDefinition attributeDefinition = new AttributeDefinition()
        {
            @Override
            public String getName()
            {
                return "attr";
            }
        };
        attributeDefinitions.add(attributeDefinition);
        tableMetadata.setAttributeDefinitionList(attributeDefinitionList);
        tableMetadata.setSupportsInsert(true);
        describeTableResponse.setTableMetadata(tableMetadata);
        return describeTableResponse;
    }
}
