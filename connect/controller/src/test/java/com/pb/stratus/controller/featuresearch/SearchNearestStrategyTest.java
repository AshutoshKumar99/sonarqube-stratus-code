package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.SearchNearestRequest;
import com.mapinfo.midev.service.feature.v1.SearchNearestResponse;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchNearestParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SearchNearestStrategyTest
{
    
    private FeatureServiceInterface mockFeatureWebService;
    
    private SearchNearestParams mockParams;
    
    private SearchNearestStrategy strategy;

    private FeatureSearchResultConverter mockConverter;
    
    @Before
    public void setUp()
    {
        mockFeatureWebService = mock(FeatureServiceInterface.class);
        mockConverter = mock(FeatureSearchResultConverter.class);
        FeatureSearchResultConverterFactory mockFactory 
                = mock(FeatureSearchResultConverterFactory.class);
        when(mockFactory.createConverter()).thenReturn(mockConverter);
        strategy = new SearchNearestStrategy(mockFeatureWebService, 
                mockFactory, 1234);
        mockParams = new SearchNearestParams();
    }
    
    @Test
    public void shouldRequestExpectedCenterPoint() throws Exception
    {
        Point mockPoint = mock(Point.class);
        mockParams.setPoint(mockPoint);
        SearchNearestRequest request 
                = performSearchAndCaptureWebServiceRequest();
        assertEquals(mockPoint, request.getGeometry());
    }
    
    @Test
    public void shouldRequestExpectedDistance() throws Exception
    {
        mockParams.setDistanceUnit(DistanceUnit.CHAIN);
        mockParams.setDistanceValue(4321);
        SearchNearestRequest request 
                = performSearchAndCaptureWebServiceRequest();
        assertEquals(DistanceUnit.CHAIN, request.getMaxDistance().getUom());
        assertEquals(4321, request.getMaxDistance().getValue(), 0d);
        
    }
    
    @Test
    public void shouldRequestExpectedDistanceAttributeName() throws Exception
    {
        SearchNearestRequest request 
                = performSearchAndCaptureWebServiceRequest();
        assertEquals(mockParams.getReturnedDistanceAttributeName(), 
                request.getReturnedDistanceAttributeName());
    }
    
    @Test
    public void shouldRequestExpectedReturnedDistanceUnit() throws Exception
    {
        mockParams.setReturnedDistanceUnit(DistanceUnit.LINK);
        SearchNearestRequest request 
                = performSearchAndCaptureWebServiceRequest();
        assertEquals(DistanceUnit.LINK, request.getReturnedDistanceUnit());
    }
    
    @Test
    public void shouldRequestExpectedMaxResults() throws Exception
    {
        mockParams.setMaxResults(12);
        SearchNearestRequest request = performSearchAndCaptureWebServiceRequest();
        assertEquals(12, request.getMaxNumberOfCandidates());
    }
    
    @Test
    public void shouldReturnConvertedResult() throws Exception
    {
        SearchNearestResponse mockResponse = mock(
                SearchNearestResponse.class);
        when(mockFeatureWebService.searchNearest(
                any(SearchNearestRequest.class))).thenReturn(
                        mockResponse);
        FeatureSearchResult mockResult = mock(FeatureSearchResult.class);
        when(mockConverter.convert("table1", mockResponse, mockParams))
                .thenReturn(mockResult);
        mockParams.setTable("table1");
        FeatureSearchResult actualResult = strategy.search(mockParams);
        assertEquals(mockResult, actualResult);
    }
    
    private SearchNearestRequest performSearchAndCaptureWebServiceRequest()
            throws Exception
    {
        
        strategy.search(mockParams);
        ArgumentCaptor<SearchNearestRequest> arg 
                = ArgumentCaptor.forClass(SearchNearestRequest.class);
        verify(mockFeatureWebService).searchNearest(arg.capture());
        return arg.getValue();
    }

}
