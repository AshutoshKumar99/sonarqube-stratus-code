package com.pb.stratus.controller.info;

import com.pb.stratus.controller.csv.CsvConverter;
import com.pb.stratus.controller.csv.CsvConvertible;
import com.pb.stratus.controller.csv.FeatureCollectionCsvConverter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This object represents the GeoJson structure of the feature search result
 * object.
 * This object contains only one table and the map is used to create JSOn
 * structure.
 * 
 * @author KA001YE
 */
public class FeatureSearchResult implements CsvConvertible
{
    private Map<String, FeatureCollection> featureCollections = new 
            LinkedHashMap<String, FeatureCollection>();
    private FeatureCollection featureCollection;
    private Map<String, Map<String, String>> featureCollectionsMetadata = new
            LinkedHashMap<String, Map<String, String>>();
    private String tableName;
    private CsvConverter csvConverter;
    private String querySql;
    private int maxFeaturesCount;


    public Map<String, FeatureCollection> getFeatureCollections()
    {
        return Collections.unmodifiableMap(featureCollections);
    }

    public Map<String, Map<String, String>> getFeatureCollectionsMetadata()
    {
        return Collections.unmodifiableMap(featureCollectionsMetadata);
    }

    public void addFeatureCollection(String tableName, FeatureCollection 
            featureCollection)
    {
        this.featureCollection = featureCollection;
        this.tableName = tableName;
        featureCollections.put(tableName, featureCollection);
    }

    public FeatureCollection getFeatureCollection()
    {
        return this.featureCollection;
}

    public String getTableName()
    {
        return this.tableName;
    }

    public CsvConverter getCsvConverter()
    {
        if (this.csvConverter == null){
            csvConverter = new FeatureCollectionCsvConverter(this.featureCollection);
        }
        return this.csvConverter;
    }

    public void setCsvConverter(FeatureCollectionCsvConverter csvConverter ){
             this.csvConverter = csvConverter;
    }

    public void addFeatureCollectionMetadata(String tableName, Map<String, String> featureCollectionMetadata) {
        featureCollectionsMetadata.put(tableName, featureCollectionMetadata);
    }

    public void setQuerySql (String sql){
        this.querySql=sql;
    }

    public String getQuerySql () {
        return this.querySql ;
    }
    public void setMaxFeaturesCount (int maxCount ){
         this.maxFeaturesCount= maxCount;
    }

    public int getMaxFeaturesCount () {
        return this.maxFeaturesCount ;
    }
}
