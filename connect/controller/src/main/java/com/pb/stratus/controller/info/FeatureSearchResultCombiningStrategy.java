package com.pb.stratus.controller.info;

import com.pb.stratus.core.common.Preconditions;

import java.util.List;

/**
 * Given a List of FeatureSearchResult of the same table,
 * this class will combine all the FeatureCollection into one collection. It
 * is upto the user to make sure that the List contains FeatureCollection of
 * the same table.
 */
public final class FeatureSearchResultCombiningStrategy
{
    private FeatureSearchResultCombiningStrategy(){}

    /**
     * Combine a List of FeatureSearchResult into one FeatureSearchResult.
     * @param featureSearchResults List<FeatureSearchResult> the list to be
     * combined.
     * @return  FeatureSearchResult the combined result.
     */
    public static FeatureSearchResult combine(List<FeatureSearchResult>
            featureSearchResults)
    {
        Preconditions.checkNotNull(featureSearchResults,
                "List<FeatureSearchResult> should not be null");
        FeatureSearchResult featureSearchResult = new FeatureSearchResult();
        FeatureCollection combiningCollection = new FeatureCollection();
        String tableName = null;
        for(FeatureSearchResult result : featureSearchResults)
        {
            FeatureCollection featureCollection = result.getFeatureCollection();
            combiningCollection.addFeatures(featureCollection.getFeatures());
            if(tableName == null)
            {
                tableName = result.getTableName();
            }
        }
        featureSearchResult.addFeatureCollection(tableName, combiningCollection);
        return featureSearchResult;
    }
}
