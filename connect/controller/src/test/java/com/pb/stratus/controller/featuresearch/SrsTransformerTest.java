package com.pb.stratus.controller.featuresearch;

import com.pb.stratus.controller.geometry.GeometryService;
import com.pb.stratus.controller.info.Feature;
import com.pb.stratus.controller.info.FeatureCollection;
import com.pb.stratus.controller.info.FeatureSearchResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class SrsTransformerTest
{
    
    private GeometryService mockGeometryService;
    private SrsTransformer transformer;
    
    @Before
    public void setUp()
    {
        mockGeometryService = mock(GeometryService.class);
        transformer = new SrsTransformer(mockGeometryService);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void shouldTransformFeaturesFromAllTables()
    {
        FeatureCollection mockFeatureCollection 
                = new FeatureCollection(createFeatures(6));
        when(mockGeometryService.transformFeatureCollection(
                any(List.class), any(String.class))).thenReturn(
                        mockFeatureCollection);
        FeatureSearchResult result = new FeatureSearchResult();
        List<Feature> features1 = createFeatures(4);
        FeatureCollection fc = new FeatureCollection(features1);
        result.addFeatureCollection("table1", fc);
        List<Feature> features2 = createFeatures(2);
        fc = new FeatureCollection(features2);
        result.addFeatureCollection("table2", fc);
        transformer.transform(result, "targetSrs");
        ArgumentCaptor<List> arg = ArgumentCaptor.forClass(List.class);
        verify(mockGeometryService).transformFeatureCollection(arg.capture(), 
                eq("targetSrs"));
        List actualFeatures = arg.getValue();
        assertEquals(6, actualFeatures.size());
        assertTrue(actualFeatures.containsAll(features1));
        assertTrue(actualFeatures.containsAll(features2));
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnFeaturesInCorrectFeatureCollections()
    {
        List<Feature> features1 = createFeatures(1);
        List<Feature> features2 = createFeatures(2);
        List<Feature> features3 = createFeatures(3);
        when(mockGeometryService.transformFeatureCollection(any(List.class), 
                any(String.class))).thenAnswer(
                            new Answer<FeatureCollection>() 
        {
            public FeatureCollection answer(InvocationOnMock invocation) 
            {
                
                return new FeatureCollection(
                        (List) invocation.getArguments()[0]);
            }
        });
        FeatureSearchResult result = new FeatureSearchResult();
        result.addFeatureCollection("table1", 
                new FeatureCollection(features1));
        result.addFeatureCollection("table2", 
                new FeatureCollection(features2));
        result.addFeatureCollection("table3", 
                new FeatureCollection(features3));
        FeatureSearchResult actualResult = transformer.transform(result, 
                "targetSrs");
        assertEquals(3, actualResult.getFeatureCollections().size());
        assertEquals(features1, actualResult.getFeatureCollections().get(
                "table1").getFeatures());
        assertEquals(features2, actualResult.getFeatureCollections().get(
                "table2").getFeatures());
        assertEquals(features3, actualResult.getFeatureCollections().get(
                "table3").getFeatures());
    }

    @Test
    public void
    shouldIncludeTotalCountIfPresentInUntransformedFeatureSearchResult()
    {
        FeatureCollection mockFeatureCollection
                = new FeatureCollection(createFeatures(6));
        when(mockGeometryService.transformFeatureCollection(
                any(List.class), any(String.class))).thenReturn(
                        mockFeatureCollection);
        FeatureSearchResult result = new FeatureSearchResult();
        List<Feature> features1 = createFeatures(4);
        FeatureCollection fc = new FeatureCollection(features1);
        fc.setTotalCount(100L);
        result.addFeatureCollection("table1", fc);
        FeatureSearchResult actualResult = transformer.transform(result,
                "targetSrs");
        long actualTotalCount  = actualResult.getFeatureCollections().get
            ("table1").getTotalCount();
        assertEquals(100L, actualTotalCount);
    }
    
    private List<Feature> createFeatures(int size)
    {
        List<Feature> features = new LinkedList<Feature>();
        for (int i = 0; i < size; i++)
        {
            features.add(mock(Feature.class));
        }
        return features;
    }

}
