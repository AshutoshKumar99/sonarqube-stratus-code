package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.*;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDataType;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinitionList;
import com.mapinfo.midev.service.featurecollection.v1.GeometryAttributeDefinition;
import com.mapinfo.midev.service.featurecollection.v1.ScalarAttributeDefinition;
import com.mapinfo.midev.service.table.v1.NamedTable;
import com.mapinfo.midev.service.table.v1.TableMetadata;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchParams;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class BaseSearchStrategyTest
{

    private BaseSearchStrategy strategy;

    private FeatureServiceInterface mockFeatureWebService;

    private SearchParams params;

    private int maxResults;

    private FeatureSearchResultConverterFactory mockConverterFactory;

    private FeatureSearchResultConverter mockConverter;

    @Before
    public void setUp()
    {
        mockFeatureWebService = mock(FeatureServiceInterface.class);
        maxResults = 1234;
        mockConverterFactory = mock(FeatureSearchResultConverterFactory.class);
        mockConverter = mock(FeatureSearchResultConverter.class);
        when(mockConverterFactory.createConverter()).thenReturn(mockConverter);
        strategy =
                new BaseSearchStrategy(mockFeatureWebService,
                        mockConverterFactory, maxResults)
                {
                    public FeatureSearchResult
                            search(SearchParams searchParams)
                    {
                        return null;
                    }
                };
        params = new SearchParams();
    }

    @Test
    public void shouldPopulateAttributes() throws Exception
    {
        params.getAttributes().add("attr1");
        params.getAttributes().add("attr2");

        DescribeTableResponse mockResponse = createMockResponse();
        when(
                mockFeatureWebService
                        .describeTable(any(DescribeTableRequest.class)))
                .thenReturn(mockResponse);
        SpatialSearchRequest request = new SearchIntersectsRequest();
        strategy.popuplateBaseValues("someTable", request, params);
        assertEquals("attr1", request.getAttributeList().getAttributeName()
                .get(0));
        assertEquals("attr2", request.getAttributeList().getAttributeName()
                .get(1));
        assertTrue(request.getAttributeList().getAttributeName()
                .contains("MI_KEY"));
    }

    //It made no sense to have this method as we are not retrieving geometry now.
//    @Test
//    public void shouldAddGeometryAttributeIfRequested() throws Exception
//    {
//        params.setIncludeGeometry(true);
//        params.getAttributes().add("someAttribute");
//        DescribeTableResponse mockResponse = createMockResponse();
//        when(
//                mockFeatureWebService
//                        .describeTable(any(DescribeTableRequest.class)))
//                .thenReturn(mockResponse);
//        SpatialSearchRequest request = new SearchIntersectsRequest();
//        strategy.popuplateBaseValues("someTable", request, params);
//        assertTrue(request.getAttributeList().getAttributeName()
//                .contains("someName"));
//    }

    @Test
    public void shouldNotAddGeometryAttributeIfAllAttributesRequested()
    {
        params.setIncludeGeometry(true);
        SpatialSearchRequest request = new SearchIntersectsRequest();
        strategy.popuplateBaseValues("someTable", request, params);
        verifyNoMoreInteractions(mockFeatureWebService);
        // CONN-13488 Don't set empty element. 
        assertNull(request.getAttributeList());
    }

    private DescribeTableResponse createMockResponse()
    {
        DescribeTableResponse response = new DescribeTableResponse();
        TableMetadata metadata = new TableMetadata();
        response.setTableMetadata(metadata);
        AttributeDefinitionList attList = new AttributeDefinitionList();
        metadata.setAttributeDefinitionList(attList);
        GeometryAttributeDefinition attDef = new GeometryAttributeDefinition();
        attDef.setDataType(AttributeDataType.GEOMETRY);
        attDef.setName("someName");
        ScalarAttributeDefinition miPrinxAttrDefinition = new ScalarAttributeDefinition();
        miPrinxAttrDefinition.setDataType(AttributeDataType.DOUBLE);
        miPrinxAttrDefinition.setName("MI_PRINX");

        attList.getAttributeDefinition().add(miPrinxAttrDefinition);
        attList.getAttributeDefinition().add(attDef);
        return response;
    }

    @Test
    public void shouldPopulatePageAndNumResults()
    {
        params.setPageLength(25);
        params.setPageNumber(2);
        SpatialSearchRequest request = new SearchIntersectsRequest();
        strategy.popuplateBaseValues("someTable", request, params);
        assertEquals(2, request.getPageNumber());
        assertEquals(25, request.getPageLength().intValue());
    }

    @Test
    public void shouldPopulateDefaultPageAndNumResultsIfParamsAreNegative()
    {
        params.setPageLength(-25);
        params.setPageNumber(-2);
        SpatialSearchRequest request = new SearchIntersectsRequest();
        strategy.popuplateBaseValues("someTable", request, params);
        assertEquals(1, request.getPageNumber());
        assertEquals(maxResults, request.getPageLength().intValue());
    }

    @Test
    public void shouldPopulateOrderByParams()
    {
        List<String> orderByList = new ArrayList<String>();
        orderByList.add("abc");
        params.setOrderByList(orderByList);
        params.setOrderByDirection("DESC");
        SpatialSearchRequest request = new SearchIntersectsRequest();
        strategy.popuplateBaseValues("someTable", request, params);
        assertEquals("abc", request.getOrderByList().getOrderBy().get(0)
                .getAttributeName());
        assertEquals(OrderByDirection.DESCENDING, request.getOrderByList()
                .getOrderBy().get(0).getOrderByDirection());
    }

    @Test
    public void shouldConvertResponse()
    {
        SearchResponse mockResponse = mock(SearchResponse.class);
        FeatureSearchResult mockResult = mock(FeatureSearchResult.class);
        when(mockConverter.convert("table1", mockResponse, null)).thenReturn(
                mockResult);
        FeatureSearchResult actualResult =
                strategy.convertWebServiceResponse("table1", mockResponse);
        assertEquals(mockResult, actualResult);
    }

    @Test
    public void shouldPopulateTableName()
    {
        SpatialSearchRequest request = new SearchIntersectsRequest();
        strategy.popuplateBaseValues("someTable", request, params);
        assertEquals("someTable", ((NamedTable) request.getTable()).getName());
    }
}
