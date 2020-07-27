package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.SearchResponse;
import com.mapinfo.midev.service.feature.v1.SpatialSearchResponse;
import com.mapinfo.midev.service.featurecollection.v1.*;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FeatureSearchResultConverterTest
{
    
    private FeatureSearchResultConverter converter;
    
    @Before
    public void setUp() throws Exception
    {
        converter = new FeatureSearchResultConverter();
    }
    
    @After
    public void tearDown() throws Exception
    {
    }
    
    @Test
    public void shouldIncorporateTableName()
    {
        SearchResponse response = createMockResponse(new StringValue());
        FeatureSearchResult result = converter.convert("someTable",
                response, null);
        assertNotNull(result.getFeatureCollections().get("someTable"));
    }
    
    @Test
    public void shouldConvertAllFeatures()
    {
        StringValue value = new StringValue();
        SearchResponse response = createMockResponse(value);
        Feature f = new Feature();
        f.getAttributeValue().add(value);
        response.getFeatureCollection().getFeatureList().getFeature().add(f);
        FeatureSearchResult result = converter.convert("someTable",
                response, null);
        assertEquals(2, result.getFeatureCollections().get("someTable")
                .getFeatures().size());
    }
    
    @Test
    public void shouldConvertNullValuesToEmptyStrings()
    {
        // any AttributeValue is fine as long as we don't assign a value to it
        FeatureSearchResult result = convertWithValue(new DateValue());
        assertEquals("", getConvertedAttributeValue(result));
    }
    
    @Test
    public void shouldConvertBooleanValuesToTrueOrFalseStrings()
    {
        BooleanValue value = new BooleanValue();
        value.setValue(true);
        FeatureSearchResult result = convertWithValue(value);
        assertEquals("true", getConvertedAttributeValue(result));
    }
    
    @Test
    public void shouldConvertBinaryValueToBase64String()
    {
        BinaryValue value = new BinaryValue();
        value.setValue(new byte[] {1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89});
        
        FeatureSearchResult result = convertWithValue(value);
        assertEquals("AQECAwUIDRUiN1k=", getConvertedAttributeValue(result));
    }
    
    private FeatureSearchResult convertWithValue(AttributeValue value)
    {
        return converter.convert("someTable", createMockResponse(value), null);
    }
    
    @Test
    public void shouldEquipFeaturesWithStratusId()
    {
        StringValue value = new StringValue();
        value.setValue("someValue");
        SearchResponse mockResponse = createMockResponse(value);
        List<Feature> features = mockResponse.getFeatureCollection()
                .getFeatureList().getFeature();
        Feature feature = new Feature();
        feature.getAttributeValue().add(value);
        features.add(feature);
        FeatureSearchResult result = converter.convert("someTable", 
                mockResponse, null);
        assertEquals("someTable-1", result.getFeatureCollections().get(
                "someTable").getFeatures().get(0).getProperties().get(
                        "stratusid"));
        assertEquals("someTable-2", result.getFeatureCollections().get(
                "someTable").getFeatures().get(1).getProperties().get(
                        "stratusid"));
    }

    @Test
    public void shouldIncludeTotalCountForSpatialResponse()
    {
        SearchParams searchParams = new SearchParams();
        searchParams.setReturnTotalCount(true);
        StringValue value = new StringValue();
        value.setValue("someValue");
        SpatialSearchResponse spatialSearchResponse =
                createSpatialMockResponse(value);
        FeatureSearchResult featureSearchResult = converter.convert
                ("someValue", spatialSearchResponse, searchParams);
        long totalCount  = featureSearchResult.getFeatureCollections().get
                ("someValue").getTotalCount();
        assertEquals(100L, totalCount);
    }

    @Test
    public void shouldNotIncludeTotalCountForNonSpatialResponse()
    {
        StringValue value = new StringValue();
        value.setValue("someValue");
        SearchResponse mockResponse = createMockResponse(value);
        // setTotalCount is true but response is non-spatial
        SearchParams searchParams = new SearchParams();
        searchParams.setReturnTotalCount(true);
        FeatureSearchResult featureSearchResult = converter.convert
                ("someValue", mockResponse, searchParams);
        long totalCount  = featureSearchResult.getFeatureCollections().get
                ("someValue").getTotalCount();
        assertEquals(0L, totalCount);
    }

    private SearchResponse createMockResponse(AttributeValue value)
    {
        SearchResponse response = new SearchResponse()
        {
        };
        return createMockResponse(value, response);
    }

    private SpatialSearchResponse createSpatialMockResponse(AttributeValue value)
    {
        SpatialSearchResponse spatialSearchResponse = new
                SpatialSearchResponse(){};
        spatialSearchResponse =  (SpatialSearchResponse)createMockResponse(value,
                spatialSearchResponse);
        spatialSearchResponse.setTotalCount(100L);
        return spatialSearchResponse;
    }

    private SearchResponse createMockResponse(AttributeValue value,
            SearchResponse response)
    {
        FeatureCollection col = new FeatureCollection();
        response.setFeatureCollection(col);
        FeatureCollectionMetadata metadata = new FeatureCollectionMetadata();
        col.setFeatureCollectionMetadata(metadata);
        metadata.setCount(1L);
        AttributeDefinitionList dl = new AttributeDefinitionList();
        metadata.setAttributeDefinitionList(dl);
        AttributeDefinition attDef = new ScalarAttributeDefinition();
        attDef.setName("testAttribute");
        attDef.setDataType(AttributeDataType.STRING);
        dl.getAttributeDefinition().add(attDef);
        FeatureList fl = new FeatureList();
        col.setFeatureList(fl);
        Feature f = new Feature();
        f.getAttributeValue().add(value);
        fl.getFeature().add(f);
        return response;
    }

    private String getConvertedAttributeValue(FeatureSearchResult result)
    {
        return (String) result.getFeatureCollections()
                .get("someTable").getFeatures().get(0).getProperties()
                .get("testAttribute");
    }
    
}
