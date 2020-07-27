package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.BoundParameter;
import com.mapinfo.midev.service.feature.v1.DescribeTableResponse;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDataType;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinitionList;
import com.mapinfo.midev.service.featurecollection.v1.ScalarAttributeDefinition;
import com.mapinfo.midev.service.table.v1.TableMetadata;
import com.pb.stratus.controller.QueryType;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.service.SearchByQueryParams;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;

/**
 * Created by vi012gu on 7/15/2015.
 */
public class ApplicationLinkingQueryTest {

    private SearchByQueryParams mockParams;
    private String TABLE_NAME = "Test_Table";
    private String COLUMN_NAME = "Test_Column";
    private String MI_FUNCTION_NAME = "MI_AggregateEnvelope";
    private String MI_FUNCTION_COLUMN = "Obj";
    private final String SOURCE_SRS_PARAM = "EPSG:3857";
    private QueryMetadata mockQueryMetadata;
    private List<BoundParameter> boundParams = new ArrayList<>();
    private ApplicationLinkingQuery applicationLinkingQuery;

    @Before
    public void setUp() throws ServiceException {
        mockParams = new SearchByQueryParams();
        mockParams.setTableName(TABLE_NAME);
        mockParams.setColumnName(COLUMN_NAME);
        mockParams.setQueryType(QueryType.ApplicationLinkingQuery);
        applicationLinkingQuery = new ApplicationLinkingQuery();
    }

    @Test
    public void testCreateSQLQueryForStringColumn () throws Exception {

        mockParams.setParams("'PK8000050','PK8000051','PK8000052','PK8000053','PK8000054','PK8000055'");
        FeatureService mockFeatureService = createMock(FeatureService.class);
        DescribeTableResponse mockResponse = createMockResponseWithGivenDataType(AttributeDataType.STRING);
        expect(mockFeatureService.describeTable(TABLE_NAME)).andReturn(mockResponse);

        String expectedResult = "SELECT " +
                "MI_AggregateEnvelope(\"Obj\") " +
                "AS MI_AggregateEnvelope_Obj " +
                "FROM \"Test_Table\" " +
                "WHERE Test_Column IN ('PK8000050','PK8000051','PK8000052','PK8000053','PK8000054','PK8000055')";

        List<String> functionColumnsList = Arrays.asList(MI_FUNCTION_COLUMN);
        mockParams.setFunctionAttributes(getSelectClauseHavingFunction(functionColumnsList));
        String query = applicationLinkingQuery.createSQLQuery(mockQueryMetadata, mockParams, true, boundParams, null);
        assert(expectedResult.equals(query));
    }

    @Test
    public void testCreateSQLQueryForNumericColumn () throws Exception {
        mockParams.setParams("8000050,8000051,8000052,8000053,8000054,8000055");
        FeatureService mockFeatureService = createMock(FeatureService.class);
        DescribeTableResponse mockResponse = createMockResponseWithGivenDataType(AttributeDataType.INT);
        expect(mockFeatureService.describeTable(TABLE_NAME)).andReturn(mockResponse);

        String expectedResult = "SELECT " +
                "MI_AggregateEnvelope(\"Obj\") " +
                "AS MI_AggregateEnvelope_Obj " +
                "FROM \"Test_Table\" " +
                "WHERE Test_Column IN (8000050,8000051,8000052,8000053,8000054,8000055)";

        List<String> functionColumnsList = Arrays.asList(MI_FUNCTION_COLUMN);
        mockParams.setFunctionAttributes(getSelectClauseHavingFunction(functionColumnsList));
        String query = applicationLinkingQuery.createSQLQuery(mockQueryMetadata, mockParams, true, boundParams, null);
        assert(expectedResult.equals(query));
    }

    private Map<String, List<String>> getSelectClauseHavingFunction (List<String> functionColumnsList) {
        Map<String, List<String>> functionAttributesMap = new LinkedHashMap<>();
        List<String> functionList = Arrays.asList(MI_FUNCTION_NAME);
        for (String functionColumn : functionColumnsList) {
            functionAttributesMap.put(functionColumn, functionList);
        }
        return functionAttributesMap;
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
