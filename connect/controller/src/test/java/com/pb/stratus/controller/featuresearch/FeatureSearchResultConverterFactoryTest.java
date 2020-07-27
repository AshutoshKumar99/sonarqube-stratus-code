package com.pb.stratus.controller.featuresearch;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

public class FeatureSearchResultConverterFactoryTest
{
    
    @Test
    public void shouldCreateNewFeatureSearchResultConverter()
    {
        FeatureSearchResultConverterFactory factory 
                = new FeatureSearchResultConverterFactory();
        FeatureSearchResultConverter converter1 = factory.createConverter();
        FeatureSearchResultConverter converter2 = factory.createConverter();
        assertNotNull(converter1);
        assertNotNull(converter2);
        assertNotSame(converter2, converter1);
    }

}
