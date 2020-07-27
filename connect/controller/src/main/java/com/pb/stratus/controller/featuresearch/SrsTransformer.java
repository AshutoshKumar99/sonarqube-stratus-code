package com.pb.stratus.controller.featuresearch;

import com.pb.stratus.controller.geometry.GeometryService;
import com.pb.stratus.controller.info.Feature;
import com.pb.stratus.controller.info.FeatureCollection;
import com.pb.stratus.controller.info.FeatureSearchResult;

import java.util.LinkedList;
import java.util.List;

public class SrsTransformer
{
    
    private GeometryService geometryService;
    
    public SrsTransformer(GeometryService geometryService)
    {
        this.geometryService = geometryService;
    }

    public FeatureSearchResult transform(FeatureSearchResult result, 
            String targetSrs)
    {
        List<String> tableNames = new LinkedList<String>(
                result.getFeatureCollections().keySet());
        List<Feature> features = collectFeatures(tableNames, result);
        List<Feature> transformedFeatures = getTransformedFeatures(features, 
                targetSrs);
        return convertFeaturesIntoResult(tableNames, result, 
                transformedFeatures);
    }
    
    private FeatureSearchResult convertFeaturesIntoResult(
            List<String> tableNames, FeatureSearchResult untransformedResult,
            List<Feature> transformedFeatures)
    {
        FeatureSearchResult newResult = new FeatureSearchResult();
        int startIndex = 0;
        for (String tableName : tableNames)
        {
            int originalSize = untransformedResult.getFeatureCollections()
                    .get(tableName).getFeatures().size();
            List<Feature> subList = transformedFeatures.subList(startIndex, 
                    startIndex + originalSize);
            startIndex += originalSize;
            long totalCount = untransformedResult.getFeatureCollections()
                    .get(tableName).getTotalCount();
            FeatureCollection featureCollection = new FeatureCollection(subList);
            featureCollection.setTotalCount(totalCount);
            newResult.addFeatureCollection(tableName, featureCollection);
        }
        return newResult;
    }

    private List<Feature> getTransformedFeatures(List<Feature> features, 
            String targetSrs)
    {
        FeatureCollection fc = geometryService.transformFeatureCollection(
                features, targetSrs);
        return fc.getFeatures();
        
    }

    private List<Feature> collectFeatures(List<String> tableNames, 
            FeatureSearchResult result)
    {
        List<Feature> features = new LinkedList<Feature>();
        for (FeatureCollection fc : result.getFeatureCollections().values())
        {
            features.addAll(fc.getFeatures());
        }
        return features;
    }

}
