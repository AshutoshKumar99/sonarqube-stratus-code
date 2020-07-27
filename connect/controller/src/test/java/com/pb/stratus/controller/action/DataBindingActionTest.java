package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.feature.v1.DescribeTableResponse;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDataType;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinitionList;
import com.mapinfo.midev.service.featurecollection.v1.ScalarAttributeDefinition;
import com.mapinfo.midev.service.table.v1.TableMetadata;
import com.pb.stratus.controller.QueryType;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchByQueryParams;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ar009sh
 * Date: 14/1/15
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataBindingActionTest extends FeatureSearchActionTestBase{

    String srsName;
    MockHttpServletRequest request;
    DataBindingAction action;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        srsName = "epsg:27700";
        request = getRequest();
    }

    @Test
    public void testCreateObjectWithNonNumericColumn() throws Exception
    {
        request.addParameter("bindTable", "bindTable");
        request.addParameter("bindColumn", "bindColumn");
        request.addParameter("bindValue", "['first','second','third']");
        request.addParameter("sourceSrs", srsName);

        FeatureService mockFeatureService = createMock(FeatureService.class);

        DescribeTableResponse mockResponse = createMockResponseWithGivenDataType(AttributeDataType.STRING);
        expect(mockFeatureService.describeTable("bindTable")).andReturn(mockResponse);

        FeatureSearchResult expectedResult = createDummyResult();
        Capture<SearchByQueryParams> captureParams = new Capture<SearchByQueryParams>();
        expect(mockFeatureService.searchByQuery(capture(captureParams)))
                .andReturn(expectedResult);
        replay(mockFeatureService);
        action = new DataBindingAction(mockFeatureService);
        Object actualResult = action.createObject(request);

        SearchByQueryParams actualParams = captureParams.getValue();

        assertEquals("bindTable", actualParams.getTableName());
        assertEquals("bindColumn", actualParams.getColumnName());
        assertEquals("'first','second','third'", actualParams.getParams());
        assertEquals(QueryType.DataBindingQuery,actualParams.getQueryType());

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testCreateObjectWithNumericColumn() throws Exception
    {
        request.addParameter("bindTable", "bindTable");
        request.addParameter("bindColumn", "bindColumn");
        request.addParameter("bindValue", "['0','1','2']");
        request.addParameter("sourceSrs", srsName);

        FeatureService mockFeatureService = createMock(FeatureService.class);

        DescribeTableResponse mockResponse = createMockResponseWithGivenDataType(AttributeDataType.INT);
        expect(mockFeatureService.describeTable("bindTable")).andReturn(mockResponse);

        FeatureSearchResult expectedResult = createDummyResult();
        Capture<SearchByQueryParams> captureParams = new Capture<SearchByQueryParams>();
        expect(mockFeatureService.searchByQuery(capture(captureParams)))
                .andReturn(expectedResult);
        replay(mockFeatureService);
        action = new DataBindingAction(mockFeatureService);
        Object actualResult = action.createObject(request);

        SearchByQueryParams actualParams = captureParams.getValue();

        assertEquals("bindTable", actualParams.getTableName());
        assertEquals("bindColumn", actualParams.getColumnName());
        assertEquals("0,1,2", actualParams.getParams());
        assertEquals(QueryType.DataBindingQuery,actualParams.getQueryType());

        assertEquals(expectedResult, actualResult);
    }

    private DescribeTableResponse createMockResponseWithGivenDataType(AttributeDataType dataType) {
        DescribeTableResponse response = new DescribeTableResponse();
        TableMetadata metadata = new TableMetadata();
        response.setTableMetadata(metadata);
        AttributeDefinitionList attList = new AttributeDefinitionList();
        metadata.setAttributeDefinitionList(attList);

        ScalarAttributeDefinition stringAttrDefinition = new ScalarAttributeDefinition();
        stringAttrDefinition.setDataType(dataType);
        stringAttrDefinition.setName("bindColumn");

        attList.getAttributeDefinition().add(stringAttrDefinition);
        return response;
    }
}
