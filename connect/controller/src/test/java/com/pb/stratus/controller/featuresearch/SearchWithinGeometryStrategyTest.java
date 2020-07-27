package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.SearchIntersectsRequest;
import com.mapinfo.midev.service.feature.v1.SearchWithinPolygonRequest;
import com.mapinfo.midev.service.feature.v1.SearchWithinPolygonResponse;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.geometries.v1.MultiPolygon;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchWithinGeometryParams;
import com.pb.stratus.controller.service.SearchWithinGeometryParams.SpatialOperation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SearchWithinGeometryStrategyTest
{
    
    private FeatureServiceInterface mockFeatureWebService;
    
    private SearchWithinGeometryStrategy strategy;
    
    private SearchWithinGeometryParams mockParams;
    
    private FeatureSearchResultConverter mockConverter;

    @Before
    public void setUp()
    {
        mockFeatureWebService = mock(FeatureServiceInterface.class);
        mockConverter = mock(FeatureSearchResultConverter.class);
        FeatureSearchResultConverterFactory mockFactory 
                = mock(FeatureSearchResultConverterFactory.class);
        when(mockFactory.createConverter()).thenReturn(mockConverter);
        strategy = new SearchWithinGeometryStrategy(mockFeatureWebService, 
                mockFactory, 1234);
        mockParams = new SearchWithinGeometryParams();
        MultiPolygon mockGeometry = mock(MultiPolygon.class);
        mockParams.setGeometry(mockGeometry);
        mockParams.setTable("someTable");
    }
    
//    @Test
//    public void shouldRequestExpectedPolygon() throws Exception
//    {
//        MultiPolygon mockPolygon = mock(MultiPolygon.class);
//        mockParams.setMultiPolygon(mockPolygon);
//        SearchWithinPolygonRequest request = performSearchAndCaptureRequest();
//        assertEquals(mockPolygon, request.getMultiPolygon());
//    }
//    
    
    @Test
    public void shouldReturnConvertedResult() throws Exception
    {
        mockParams.setSpatialOperation(SpatialOperation.ENTIRELYWITHIN);
        SearchWithinPolygonResponse mockResponse = mock(
                SearchWithinPolygonResponse.class);
        when(mockFeatureWebService.searchWithinPolygon(
                any(SearchWithinPolygonRequest.class))).thenReturn(
                        mockResponse);
        FeatureSearchResult mockResult = mock(FeatureSearchResult.class);
        when(mockConverter.convert("table1", mockResponse, mockParams
                )).thenReturn(mockResult);
        mockParams.setTable("table1");
        FeatureSearchResult actualResult = strategy.search(mockParams);
        assertEquals(mockResult, actualResult);
    }
    
    @Test
    public void shouldCallSearchWithinPolygonIfSpatialOperationIsEntirelyWithin() 
            throws Exception
    {
        mockParams.setSpatialOperation(SpatialOperation.ENTIRELYWITHIN);
        SearchWithinPolygonRequest request 
                = performSearchWithinPolygonAndCaptureRequest();
        assertEquals(mockParams.getGeometry(), request.getMultiPolygon());
        assertEquals(mockParams.getTable(), "someTable");
    }
    
    @Test
    public void shouldCallSearchIntersectsIfSpatialOperationIsIntersects()
            throws Exception
    {
        mockParams.setSpatialOperation(SpatialOperation.INTERSECTS);
        SearchIntersectsRequest request 
                = performSearchIntersectsAndCaptureRequest();
        assertEquals(mockParams.getGeometry(), request.getGeometry());
        assertEquals(mockParams.getTable(), "someTable");
    }

    private SearchWithinPolygonRequest performSearchWithinPolygonAndCaptureRequest() 
            throws Exception
    {
        strategy.search(mockParams);
        ArgumentCaptor<SearchWithinPolygonRequest> arg 
                = ArgumentCaptor.forClass(SearchWithinPolygonRequest.class);
        verify(mockFeatureWebService).searchWithinPolygon(arg.capture());
        return arg.getValue();
    }

    private SearchIntersectsRequest performSearchIntersectsAndCaptureRequest() 
            throws Exception
    {
        strategy.search(mockParams);
        ArgumentCaptor<SearchIntersectsRequest> arg 
                = ArgumentCaptor.forClass(SearchIntersectsRequest.class);
        verify(mockFeatureWebService).searchIntersects(arg.capture());
        return arg.getValue();
    }
}
