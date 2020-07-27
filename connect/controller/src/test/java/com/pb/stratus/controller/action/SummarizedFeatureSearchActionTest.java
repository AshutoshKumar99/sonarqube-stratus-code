package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.feature.v1.DescribeTableResponse;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDataType;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinitionList;
import com.mapinfo.midev.service.featurecollection.v1.GeometryAttributeDefinition;
import com.mapinfo.midev.service.featurecollection.v1.ScalarAttributeDefinition;
import com.mapinfo.midev.service.table.v1.TableMetadata;
import com.pb.stratus.controller.featuresearch.Query;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.json.geojson.GeoJsonParser;
import com.pb.stratus.controller.json.geojson.GeometryTestUtil;
import com.pb.stratus.controller.service.SearchByQueryParams;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static com.pb.stratus.controller.service.SearchWithinGeometryParams.SpatialOperation;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 9/2/14
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class SummarizedFeatureSearchActionTest extends FeatureSearchActionTestBase {

    String srsName;
    GeoJsonParser parser;
    MockHttpServletRequest request;
    SummarizedFeatureSearchAction action;
    private String TABLE_NAME = "testTable";
    private String SPATIAL_OPEARTION = "ENTIRELYWITHIN";
    private String polygon = "{\"type\":\"MultiPolygon\",\"coordinates\":[[[[100,100],[100,200],[200,200],[200,100],[100,100]]]]}";
    private FeatureService mockFeatureService;
    private FeatureSearchResult expectedResult;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        srsName = "epsg:27700";
        parser = new GeoJsonParser(srsName);
        request = getRequest();
        request.addParameter("tableName", TABLE_NAME);
        request.addParameter("spatialOperation", SPATIAL_OPEARTION);
        request.addParameter("includeGeometry", "true");
        request.addParameter("sourceSrs", srsName);
        request.addParameter("targetSrs", "someTargetSrs");
        request.addParameter("polygon", polygon);
        mockFeatureService = createMock(FeatureService.class);
        expectedResult = createDummyResult();
    }

    @Test
    public void testCreateObjectHavingNoNumericColumn() throws Exception {

        DescribeTableResponse mockResponse = createMockResponseHavingNoNumericColumn();
        expect(mockFeatureService.describeTable(TABLE_NAME)).andReturn(mockResponse);

        Capture<SearchByQueryParams> searchByQueryParamsCapture = new Capture<>();
        expect(mockFeatureService.searchByQuery(capture(searchByQueryParamsCapture)))
                .andReturn(expectedResult);
        replay(mockFeatureService);

        action = new SummarizedFeatureSearchAction(mockFeatureService);
        Object actualResult = action.createObject(request);

        SearchByQueryParams actualParams = searchByQueryParamsCapture.getValue();
        actualParams.setGeometry(parser.parseGeometry(polygon));

        assertEquals("testTable", actualParams.getTableName());
        assertTrue(actualParams.isIncludeGeometry());
        assertEquals(SpatialOperation.ENTIRELYWITHIN, actualParams.getSpatialOperation());
        GeometryTestUtil.assertGeometry(parser.parseGeometry(polygon),
                actualParams.getGeometry());
        assertEquals(srsName, actualParams.getSourceSrs());
        assertEquals("someTargetSrs", actualParams.getTargetSrs());
        assertEquals(0, actualParams.getFunctionAttributes().size());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testCreateObjectHavingNumericColumns() throws Exception {

        DescribeTableResponse mockResponse = createMockResponseHavingNumericColumns();
        expect(mockFeatureService.describeTable(TABLE_NAME)).andReturn(mockResponse);

        Capture<SearchByQueryParams> searchByQueryParamsCapture = new Capture<>();
        expect(mockFeatureService.searchByQuery(capture(searchByQueryParamsCapture)))
                .andReturn(expectedResult);
        replay(mockFeatureService);

        action = new SummarizedFeatureSearchAction(mockFeatureService);
        Object actualResult = action.createObject(request);

        SearchByQueryParams actualParams = searchByQueryParamsCapture.getValue();
        actualParams.setGeometry(parser.parseGeometry(polygon));

        assertEquals("testTable", actualParams.getTableName());
        assertTrue(actualParams.isIncludeGeometry());
        assertEquals(SpatialOperation.ENTIRELYWITHIN, actualParams.getSpatialOperation());
        GeometryTestUtil.assertGeometry(parser.parseGeometry(polygon),
                actualParams.getGeometry());
        assertEquals(srsName, actualParams.getSourceSrs());
        assertEquals("someTargetSrs", actualParams.getTargetSrs());
        assertEquals(2, actualParams.getFunctionAttributes().size());
        assertEquals(expectedResult, actualResult);
    }

    private DescribeTableResponse createMockResponseHavingNoNumericColumn() {
        DescribeTableResponse response = new DescribeTableResponse();
        TableMetadata metadata = new TableMetadata();
        response.setTableMetadata(metadata);
        AttributeDefinitionList attList = new AttributeDefinitionList();
        metadata.setAttributeDefinitionList(attList);
        GeometryAttributeDefinition attDef = new GeometryAttributeDefinition();
        attDef.setDataType(AttributeDataType.GEOMETRY);
        attDef.setName("someName");

        ScalarAttributeDefinition stringAttrDefinition = new ScalarAttributeDefinition();
        stringAttrDefinition.setDataType(AttributeDataType.STRING);
        stringAttrDefinition.setName("SOME_STRING_ATTR");

        attList.getAttributeDefinition().add(stringAttrDefinition);
        attList.getAttributeDefinition().add(attDef);
        return response;
    }

    private DescribeTableResponse createMockResponseHavingNumericColumns() {
        DescribeTableResponse response = new DescribeTableResponse();
        TableMetadata metadata = new TableMetadata();
        response.setTableMetadata(metadata);
        AttributeDefinitionList attList = new AttributeDefinitionList();
        metadata.setAttributeDefinitionList(attList);
        GeometryAttributeDefinition attDef = new GeometryAttributeDefinition();
        attDef.setDataType(AttributeDataType.GEOMETRY);
        attDef.setName("someName");

        ScalarAttributeDefinition stringAttrDefinition = new ScalarAttributeDefinition();
        stringAttrDefinition.setDataType(AttributeDataType.STRING);
        stringAttrDefinition.setName("SOME_STRING_ATTR");

        ScalarAttributeDefinition doubleAttrDefinition = new ScalarAttributeDefinition();
        doubleAttrDefinition.setDataType(AttributeDataType.DOUBLE);
        doubleAttrDefinition.setName("SOME_DOUBLE_ATTR");

        ScalarAttributeDefinition intAttrDefinition = new ScalarAttributeDefinition();
        intAttrDefinition.setDataType(AttributeDataType.INT);
        intAttrDefinition.setName("SOME_INTEGER_ATTR");

        attList.getAttributeDefinition().add(attDef);
        attList.getAttributeDefinition().add(stringAttrDefinition);
        attList.getAttributeDefinition().add(doubleAttrDefinition);
        attList.getAttributeDefinition().add(intAttrDefinition);

        return response;
    }

}
