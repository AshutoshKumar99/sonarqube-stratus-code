package com.pb.stratus.controller.info;

import java.util.ArrayList;
import java.util.List;

// FIXME comments
/**
 * FeatureCollection object contains features for a layer.
 */
public class FeatureCollection
{
    private String type;
    /**
     * This is the total count of the features present within the given
     * bounds of the map.
     */
    private long totalCount;
    private List<Feature> features = new ArrayList<Feature>();

    public FeatureCollection()
    {
        this.type = "FeatureCollection";
    }

    /**
     * Constructor for the FeatureCollection object.
     * 
     * @param features
     */
    public FeatureCollection(List<Feature> features)
    {
        this.type = "FeatureCollection";
        this.setFeatures(features);
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param features
     *            the features to set
     */
    public void setFeatures(List<Feature> features)
    {
        this.features = features;
    }

    /**
     * @return the features
     */
    public List<Feature> getFeatures()
    {
        return features;
    }

    public long getTotalCount()
    {
        return totalCount;
    }

    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }

    public void addFeatures(List<Feature> features)
    {
        this.features.addAll(features);
}
}
