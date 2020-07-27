package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.SearchResponse;
import com.mapinfo.midev.service.feature.v1.SpatialSearchResponse;
import com.mapinfo.midev.service.featurecollection.v1.*;
import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.pb.stratus.controller.info.Feature;
import com.pb.stratus.controller.info.FeatureCollection;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchParams;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Converts an MIDev SearchResponse object into a FeatureSearchResult.
 * This implementation is not thread-safe.
 */
public class FeatureSearchResultConverter
{
    
    private int currentStratusId = 1;
    
    private String baseStratusId = null;

    public FeatureSearchResult convert(String tableName,
            SearchResponse response, SearchParams searchParams)
    {
        baseStratusId = tableName;
        Map<String, String> featureCollectionMetadata = extractMetaData(response.getFeatureCollection()
                .getFeatureCollectionMetadata());
        List<Feature> features = extractFeatures(response);
        FeatureCollection featureCollection = new FeatureCollection(features);
        FeatureSearchResult result = new FeatureSearchResult();
        result.addFeatureCollection(tableName, featureCollection);
        result.addFeatureCollectionMetadata(tableName,featureCollectionMetadata);
        if (searchParams != null)
        {
            addTotalCountIfPresent(response, featureCollection, searchParams);
        }
        return result;
    }

    private Map<String, String> extractMetaData(FeatureCollectionMetadata metadata){
        Map<String, String> metadataMap = new HashMap<String, String>();
        for(AttributeDefinition attribute :metadata
                .getAttributeDefinitionList().getAttributeDefinition()){
            metadataMap.put(attribute.getName(),attribute.getDataType().value());
        }
        return metadataMap;
    }

    private void addTotalCountIfPresent(SearchResponse response,
            FeatureCollection featureCollection, SearchParams searchParams)
    {
        if(response instanceof SpatialSearchResponse && searchParams.
                getReturnTotalCount())
        {
            featureCollection.setTotalCount(((SpatialSearchResponse)response).
                    getTotalCount());
        }
    }

    private List<Feature> extractFeatures(SearchResponse response)
    {
        List<Feature> features = new LinkedList<Feature>();
        FeatureCollectionMetadata metadata = response.getFeatureCollection()
                .getFeatureCollectionMetadata();
        for (com.mapinfo.midev.service.featurecollection.v1.Feature miDevFeature 
                : response.getFeatureCollection().getFeatureList().getFeature())
        {
            features.add(convertFeature(metadata, miDevFeature));
        }
        return features;
    }

    private Feature convertFeature(
            FeatureCollectionMetadata metadata, 
            com.mapinfo.midev.service.featurecollection.v1.Feature miDefFeature)
    {
        Geometry geom = getGeometry(miDefFeature);
        Map<String, Object> attrs = getNonGeometryAttributes(metadata, 
                miDefFeature);
        Feature feature = new Feature(geom, attrs);
        return feature;
    }

    private Geometry getGeometry(
            com.mapinfo.midev.service.featurecollection.v1.Feature miDefFeature)
    {
        for (AttributeValue value : miDefFeature.getAttributeValue())
        {
            if (value instanceof GeometryValue)
            {
                return ((GeometryValue) value).getFeatureGeometry();
            }
        }
        return null;
    }
    
    private Map<String, Object> getNonGeometryAttributes(
            FeatureCollectionMetadata metadata,
            com.mapinfo.midev.service.featurecollection.v1.Feature miDefFeature)
    {
        List<AttributeDefinition> attDefs = metadata
                .getAttributeDefinitionList().getAttributeDefinition();
        List<AttributeValue> attValues = miDefFeature.getAttributeValue();
        Map<String, Object> convertedAtts = new LinkedHashMap<String, Object>();
        for (int i = 0; i < attDefs.size(); i++)
        {
            AttributeDefinition attDef = attDefs.get(i);
            AttributeValue attValue = attValues.get(i);
            //CONN15513 - removed MI_PRINX condition so that convertedAttributeList
            // can have the MI_PRINX value.
            if (attValue instanceof GeometryValue || attValue instanceof
                    StyleValue)
            {
                continue;
            }
            String convertedValue = convertAttributeValueToString(attValue);
            convertedAtts.put(attDef.getName(), convertedValue);
        }
        convertedAtts.put("stratusid",  baseStratusId + "-" 
                + Integer.toString(currentStratusId++));
        //Adding Id for PickFromMap feature for new Mobile First UI as not unique Id returned for a feature to query..
        convertedAtts.put("id",miDefFeature.getId());
        return convertedAtts;
    }

    private String convertAttributeValueToString(AttributeValue attValue)
    {
        return getValue(attValue);
    }
    
    private String getValue(AttributeValue attValue)
    {
        Object value;
        if (attValue instanceof BooleanValue)
        {
            value = ((BooleanValue) attValue).isValue();
        }
        else
        {
            try
            {
                value = PropertyUtils.getProperty(attValue, "value");
            }
            catch (Exception x)
            {
                throw new IllegalStateException("Encountered AttributeValue " 
                        + "without value property", x);
            }
        }
        if (value == null)
        {
            return "";
        }
        else if (value instanceof byte[])
        {
            byte[] encoded = Base64.encodeBase64((byte[]) value);
            try
            {
                return new String(encoded, "UTF-8");
            }
            catch (UnsupportedEncodingException ux)
            {
                // UTF-8 Required by Java Spec
                throw new Error(ux);
            }
        }
        else
        {
            return value.toString();
        }
        
    }
}
