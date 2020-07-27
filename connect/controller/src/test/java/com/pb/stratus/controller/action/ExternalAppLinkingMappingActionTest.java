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
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

public class ExternalAppLinkingMappingActionTest extends FeatureSearchActionTestBase {

    private static final String BIND_TABLE = "bindTable";
    private static final String BIND_COLUMN = "bindColumn";
    private static final String BIND_VALUE = "bindValue";
    private static final String SOURCE_SRS = "sourceSrs";
    private static final String MI_FUNCTION = "miFunction";
    private static final String MI_FUNCTION_COLUMN = "miFunctionColumn";

    private static final String TABLE_NAME = "Test_Table";
    private static final  String COLUMN_NAME = "Test_Column";
    private static final  String MI_FUNCTION_NAME = "MI_Function";
    private static final  String MI_FUNCTION_COLUMN_NAME = "GeometryColumn";
    private static final String SOURCE_SRS_PARAM = "EPSG:3857";

    String srsName;
    MockHttpServletRequest request;
    ExternalAppLinkingMappingAction action;
    Map<String, List<String>> functionAttributesMap;

    public void setUp() throws Exception {
        super.setUp();
        functionAttributesMap = new LinkedHashMap<>();
        List<String> functionList = Arrays.asList(MI_FUNCTION_NAME);
        functionAttributesMap.put(MI_FUNCTION_COLUMN_NAME, functionList);
        request = getRequest();
    }

    @Test
    public void testCreateObjectWithNonNumericColumn() throws Exception {
        request.addParameter(BIND_TABLE, TABLE_NAME);
        request.addParameter(BIND_COLUMN, COLUMN_NAME);
        request.addParameter(BIND_VALUE, "['first','second','third']");
        request.addParameter(SOURCE_SRS, SOURCE_SRS_PARAM);
        request.addParameter(MI_FUNCTION, MI_FUNCTION_NAME);
        request.addParameter(MI_FUNCTION_COLUMN, MI_FUNCTION_COLUMN_NAME);

        FeatureService mockFeatureService = createMock(FeatureService.class);

        DescribeTableResponse mockResponse = createMockResponseWithGivenDataType(AttributeDataType.STRING);
        expect(mockFeatureService.describeTable(TABLE_NAME)).andReturn(mockResponse);

        FeatureSearchResult expectedResult = createDummyResult();
        Capture<SearchByQueryParams> captureParams = new Capture<SearchByQueryParams>();
        expect(mockFeatureService.searchByQuery(capture(captureParams)))
                .andReturn(expectedResult);
        replay(mockFeatureService);
        action = new ExternalAppLinkingMappingAction(mockFeatureService);
        Object actualResult = action.createObject(request);

        SearchByQueryParams actualParams = captureParams.getValue();

        assertEquals(TABLE_NAME, actualParams.getTableName());
        assertEquals(COLUMN_NAME, actualParams.getColumnName());
        assertEquals("'first','second','third'", actualParams.getParams());
        assertEquals(functionAttributesMap, actualParams.getFunctionAttributes());
        assertEquals(QueryType.ApplicationLinkingQuery, actualParams.getQueryType());
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testCreateObjectWithNumericColumn() throws Exception {
        request.addParameter(BIND_TABLE, TABLE_NAME);
        request.addParameter(BIND_COLUMN, COLUMN_NAME);
        request.addParameter(BIND_VALUE, "['8000050','8000051','8000052','8000053','8000054','8000055']");
        request.addParameter(SOURCE_SRS, SOURCE_SRS_PARAM);

        FeatureService mockFeatureService = createMock(FeatureService.class);

        DescribeTableResponse mockResponse = createMockResponseWithGivenDataType(AttributeDataType.INT);
        expect(mockFeatureService.describeTable(TABLE_NAME)).andReturn(mockResponse);

        FeatureSearchResult expectedResult = createDummyResult();
        Capture<SearchByQueryParams> captureParams = new Capture<SearchByQueryParams>();
        expect(mockFeatureService.searchByQuery(capture(captureParams)))
                .andReturn(expectedResult);
        replay(mockFeatureService);
        action = new ExternalAppLinkingMappingAction(mockFeatureService);
        Object actualResult = action.createObject(request);

        SearchByQueryParams actualParams = captureParams.getValue();

        assertEquals(TABLE_NAME, actualParams.getTableName());
        assertEquals(COLUMN_NAME, actualParams.getColumnName());
        assertEquals("8000050,8000051,8000052,8000053,8000054,8000055", actualParams.getParams());
        assertEquals(QueryType.ApplicationLinkingQuery, actualParams.getQueryType());
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
        stringAttrDefinition.setName(COLUMN_NAME);

        attList.getAttributeDefinition().add(stringAttrDefinition);
        return response;
    }
}