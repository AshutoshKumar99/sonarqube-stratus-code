package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.DescribeTableRequest;
import com.mapinfo.midev.service.feature.v1.DescribeTableResponse;
import com.mapinfo.midev.service.feature.v1.SearchBySQLRequest;
import com.mapinfo.midev.service.feature.v1.SearchBySQLResponse;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDataType;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinitionList;
import com.mapinfo.midev.service.featurecollection.v1.GeometryAttributeDefinition;
import com.mapinfo.midev.service.table.v1.TableMetadata;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchByExpressionParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class SearchByExpressionStrategyTest
{
    
    private FeatureServiceInterface mockFeatureWebService;
    
    private SearchByExpressionStrategy searchStrat;

    private FeatureSearchResultConverter mockConverter;
    
    private SearchByExpressionParams mockParams;
    
    @Before
    public void setUp()
    {
        mockFeatureWebService = mock(FeatureServiceInterface.class);
        mockConverter = mock(FeatureSearchResultConverter.class);
        FeatureSearchResultConverterFactory mockFactory 
                = mock(FeatureSearchResultConverterFactory.class);
        when(mockFactory.createConverter()).thenReturn(mockConverter);
        searchStrat = new SearchByExpressionStrategy(mockFeatureWebService, 
                mockFactory, 1234);
        mockParams = new SearchByExpressionParams();
        mockParams.setTable("someTable");
    }
    
    @Test
    public void shouldAddAllAttributesToExpression() throws Exception
    {
        mockParams.getAttributes().add("attribute1");
        mockParams.getAttributes().add("attribute2");
        assertExpressionMatches("select \"attribute1\", \"attribute2\" from .*");
    }
    

    @Test
    public void shouldUseAsteriskIfNoAttributes() throws Exception
    {
        String pattern = Pattern.quote("select * from");
        assertExpressionMatches(pattern + " .*");
    }
    
    @Test
    public void shouldUseCorrectTableInExpression() throws Exception
    {
        assertExpressionMatches("select .* from \"someTable\"\\s?.*");
    }
    
    @Test
    public void shouldAppendCorrectWhereClauseToExpression() throws Exception
    {
        mockParams.setExpression("attribute1 like 'value1%'");
        assertExpressionMatches(".* where attribute1 like 'value1%'");
    }
    
    
    @Test
    public void shouldIncludeGeometryColumnIfRequested() throws Exception
    {
        mockParams.getAttributes().add("attribute1");
        mockParams.setIncludeGeometry(true);
        setUpMockDescibeTableCall();
        assertExpressionMatches("select \"attribute1\", \"someName\" from .*");
    }
    
    @Test
    public void shouldNotIncludeGeometryColumnIfAllAttributesAreRequested() 
            throws Exception
    {
        mockParams.setIncludeGeometry(true);
        setUpMockDescibeTableCall();
        String pattern = Pattern.quote("select * from");
        assertExpressionMatches(pattern + " .*");
    }
    
    private void assertExpressionMatches(String pattern) throws Exception
    {
        searchStrat.search(mockParams);
        ArgumentCaptor<SearchBySQLRequest> arg = ArgumentCaptor.forClass(
                SearchBySQLRequest.class);
        verify(mockFeatureWebService).searchBySQL(arg.capture());
        SearchBySQLRequest request = arg.getValue();
        String expression = request.getSQL();
        if (!expression.matches(pattern))
        {
            fail(expression + " doesn't match '" + pattern + "'");
        }
    }
    
    private void setUpMockDescibeTableCall() throws Exception
    {
        DescribeTableResponse response = new DescribeTableResponse();
        TableMetadata metadata = new TableMetadata();
        response.setTableMetadata(metadata);
        AttributeDefinitionList attList = new AttributeDefinitionList();
        metadata.setAttributeDefinitionList(attList);
        GeometryAttributeDefinition attDef = new GeometryAttributeDefinition();
        attDef.setDataType(AttributeDataType.GEOMETRY);
        attDef.setName("someName");
        attList.getAttributeDefinition().add(attDef);
        when(mockFeatureWebService.describeTable(
                any(DescribeTableRequest.class))).thenReturn(response);
    }
    
    @Test
    public void shouldReturnConvertedResult() throws Exception
    {
        SearchBySQLResponse mockResponse = mock(SearchBySQLResponse.class);
        when(mockFeatureWebService.searchBySQL(any(SearchBySQLRequest.class)))
                .thenReturn(mockResponse);
        FeatureSearchResult mockResult = mock(FeatureSearchResult.class);
        when(mockConverter.convert("someTable", mockResponse, null))
                .thenReturn(mockResult);
        FeatureSearchResult actualResult = searchStrat.search(mockParams);
        assertEquals(mockResult, actualResult);
    }

}
