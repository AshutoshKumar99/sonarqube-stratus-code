package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.SearchIntersectsRequest;
import com.mapinfo.midev.service.feature.v1.SearchIntersectsResponse;
import com.mapinfo.midev.service.feature.v1.SearchResponse;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.featurecollection.v1.Feature;
import com.mapinfo.midev.service.featurecollection.v1.FeatureCollectionMetadata;
import com.mapinfo.midev.service.featurecollection.v1.FeatureList;
import com.mapinfo.midev.service.geometries.v1.Envelope;
import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.geometries.v1.Pos;
import com.pb.stratus.controller.info.FeatureCollection;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchAtPointParams;
import com.pb.stratus.controller.service.SearchParams;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SearchAtPointStrategyTest
{
    
    private SearchAtPointStrategy searchAtPointStrat;
    
    private FeatureServiceInterface mockFeatureWebService;

    private SearchIntersectsResponse mockResponse;

    private FeatureSearchResultConverter mockConverter;

    private SearchAtPointParams mockParams;
    
    private FeatureSearchResult mockResult;

    private FeatureSearchResultConverterFactory mockFactory;

    private SearchParams params = new SearchParams();
    
    @Before
    public void setUp() throws Exception
    {
        mockFeatureWebService = mock(FeatureServiceInterface.class);
        mockConverter = mock(FeatureSearchResultConverter.class);
        mockFactory = mock(FeatureSearchResultConverterFactory.class);
        when(mockFactory.createConverter()).thenReturn(mockConverter);
        mockResult = createMockResult("table1");
        when(mockConverter.convert(any(String.class), 
                any(SearchResponse.class), any(SearchParams.class))).thenReturn
                (mockResult);
        searchAtPointStrat = new SearchAtPointStrategy(mockFeatureWebService, 
                mockFactory, 1234);
        mockResponse = createMockResponse(1);
        when(mockFeatureWebService.searchIntersects(
                any(SearchIntersectsRequest.class))).thenReturn(mockResponse);
        setUpMockParams();
    }
    
    private void setUpMockParams()
    {
        mockParams = new SearchAtPointParams();
        mockParams.addTable("table1", 1);
        mockParams.addQuerySQL("table1","NA");
        Pos p = new Pos();
        p.setX(1);
        p.setY(2);
        Point point = new Point();
        point.setPos(p);
        mockParams.setPoint(point);
    }
    
    @Test
    public void shouldCallWebserviceForEachTable() throws Exception
    {
        mockParams.addTable("table2", 1);
        mockParams.addQuerySQL("table2","NA");
        List<SearchIntersectsRequest> requests 
                = callSearchAndCaptureWebServiceRequests();
        assertEquals(2, requests.size());
    }
    
    @Test
    public void shouldUseConverterToConvertResponses() throws Exception
    {
        FeatureSearchResult actualResult = searchAtPointStrat.search(
                mockParams);
        verify(mockConverter).convert("table1", mockResponse, params);
		//verify(mockConverter).convert("table1", mockResponse, null);
        assertEquals(1, actualResult.getFeatureCollections().size());
        assertEquals(mockResult.getFeatureCollections().get("table1"), 
                actualResult.getFeatureCollections().get("table1"));
    }
    
    @Test
    public void shouldJoinMultipeResultsIntoOne()
    {
        mockParams.addTable("table2", 1);
        mockParams.addQuerySQL("table2","NA");
        FeatureSearchResult mockResult1 = createMockResult("table1");
        FeatureSearchResult mockResult2 = createMockResult("table2");
        when(mockConverter.convert(any(String.class), 
                any(SearchResponse.class), any(SearchParams.class))).thenReturn(
                mockResult1, mockResult2);
        FeatureSearchResult actualResult = searchAtPointStrat.search(
                mockParams);
        assertEquals(2, actualResult.getFeatureCollections().size());
        assertEquals(mockResult1.getFeatureCollections().get("table1"), 
                actualResult.getFeatureCollections().get("table1"));
        assertEquals(mockResult2.getFeatureCollections().get("table2"), 
                actualResult.getFeatureCollections().get("table2"));
    }
    
    private FeatureSearchResult createMockResult(String tableName)
    {
        FeatureSearchResult result = new FeatureSearchResult();
        result.addFeatureCollection(tableName, mock(FeatureCollection.class));
        return result;
    }
    
    @Test
    public void shouldRequestBufferAroundPointWithSpecifiedWidth() 
            throws Exception
    {
        Point point = new Point();
        Pos pos = new Pos();
        pos.setX(1234);
        pos.setY(5678);
        point.setPos(pos);
        point.setSrsName("someSrs");
        mockParams.setPoint(point);
        mockParams.addTable("table1", 12);
        mockParams.addQuerySQL("table1","NA");
        List<SearchIntersectsRequest> reqs 
                = callSearchAndCaptureWebServiceRequests();
        SearchIntersectsRequest req = reqs.get(0);
        assertExpectedEnvelope(req.getGeometry());
    }
    
    private void assertExpectedEnvelope(Geometry actualGeom)
    {
        assertTrue(actualGeom instanceof Envelope);
        Envelope actual = (Envelope) actualGeom;
        Envelope expected = createExpectedEnvelope();
        assertEquals(expected.getPos().get(0).getX(), 
                actual.getPos().get(0).getX(), 0d);
        assertEquals(expected.getPos().get(0).getY(), 
                actual.getPos().get(0).getY(), 0d);
        assertEquals(expected.getPos().get(1).getX(), 
                actual.getPos().get(1).getX(), 0d);
        assertEquals(expected.getPos().get(1).getY(), 
                actual.getPos().get(1).getY(), 0d);
        assertEquals(expected.getSrsName(), actual.getSrsName());
    }
    
    private Envelope createExpectedEnvelope()
    {
        Envelope envelope = new Envelope();
        Pos ll = new Pos();
        ll.setX(1222);
        ll.setY(5666);
        Pos tr = new Pos();
        tr.setX(1246);
        tr.setY(5690);
        envelope.getPos().add(ll);
        envelope.getPos().add(tr);
        envelope.setSrsName("someSrs");
        return envelope;
    }
    
    @Test
    public void shouldNotExceedMaxResultsOnMultipleWebServiceCalls() 
            throws Exception
    {
        searchAtPointStrat = new SearchAtPointStrategy(mockFeatureWebService, 
                mockFactory, 9);
        SearchIntersectsResponse mockResponse1 = createMockResponse(3);
        SearchIntersectsResponse mockResponse2 = createMockResponse(4);
        SearchIntersectsResponse mockResponse3 = createMockResponse(2);
        SearchIntersectsResponse mockResponse4 = createMockResponse(1);
        when(mockFeatureWebService.searchIntersects(
                any(SearchIntersectsRequest.class))).thenReturn(mockResponse1, 
                        mockResponse2, mockResponse3, mockResponse4);
        mockParams.addTable("table2", 1);
        mockParams.addQuerySQL("table2","NA");
        mockParams.addTable("table3", 1);
        mockParams.addQuerySQL("table3","NA");
        List<SearchIntersectsRequest> requests 
                = callSearchAndCaptureWebServiceRequests();
		assertEquals(3, requests.size());
        assertEquals(9, (int) requests.get(0).getPageLength());
        assertEquals(9, (int) requests.get(1).getPageLength());
        assertEquals(9, (int) requests.get(2).getPageLength());
    }
    
    private SearchIntersectsResponse createMockResponse(int numFeatures)
    {
        SearchIntersectsResponse response = new SearchIntersectsResponse();
        com.mapinfo.midev.service.featurecollection.v1.FeatureCollection featureCollection 
                = new com.mapinfo.midev.service.featurecollection.v1.FeatureCollection();
        response.setFeatureCollection(featureCollection);
        FeatureCollectionMetadata metadata = new FeatureCollectionMetadata();
        featureCollection.setFeatureCollectionMetadata(metadata);
        metadata.setCount((long) numFeatures);
        FeatureList featureList = new FeatureList();
        featureCollection.setFeatureList(featureList);
        for (int i = 0; i < numFeatures; i++)
        {
            featureList.getFeature().add(mock(Feature.class));
        }
        return response;
    }
    
    private List<SearchIntersectsRequest> 
        callSearchAndCaptureWebServiceRequests() throws Exception
    {
        searchAtPointStrat.search(mockParams);
        ArgumentCaptor<SearchIntersectsRequest> arg = ArgumentCaptor.forClass(
                SearchIntersectsRequest.class);
        verify(mockFeatureWebService, times(mockParams.getTables().size()))
                .searchIntersects(arg.capture());
        return arg.getAllValues();
    }

}
