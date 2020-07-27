package com.pb.stratus.controller.csv;

import com.pb.stratus.controller.info.Feature;
import com.pb.stratus.controller.info.FeatureCollection;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;

public class FeatureCollectionCsvConverterTest
{
    FeatureCollectionCsvConverter featureCollectionCsvConverter;

    @Before
    public void setUp()
    {
        featureCollectionCsvConverter = new  FeatureCollectionCsvConverter(
                getFeatureCollection());
    }

    @Test
    public void testFeatureCollectionConversionToCsv()
    {
        String expectedResult = "col1,col2,col3\nrow11,row12,row13\nrow21," +
                "row22,row23\n"+"row31,row32,row33\n";
        String actualResult = featureCollectionCsvConverter.getCsv(',', "\n");
        assertEquals(expectedResult, actualResult);
    }

    private FeatureCollection getFeatureCollection()
    {
        FeatureCollection featureCollection = new FeatureCollection();
        List<Feature> features = new ArrayList<Feature>();
        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put("col1", "row11");
        properties.put("col2", "row12");
        properties.put("col3", "row13");
        Feature feature = new Feature(null, properties);
        features.add(feature);
        properties = new LinkedHashMap<String, Object>();
        properties.put("col1", "row21");
        properties.put("col2", "row22");
        properties.put("col3", "row23");
        feature = new Feature(null, properties);
        features.add(feature);
        properties = new LinkedHashMap<String, Object>();
        properties.put("col1", "row31");
        properties.put("col2", "row32");
        properties.put("col3", "row33");
        properties.put("MI_KEY", "123");
        feature = new Feature(null, properties);
        features.add(feature);
        featureCollection.addFeatures(features);
        return featureCollection;
    }
}
