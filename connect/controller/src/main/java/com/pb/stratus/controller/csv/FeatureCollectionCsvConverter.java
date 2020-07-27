package com.pb.stratus.controller.csv;


import com.pb.stratus.controller.info.Feature;
import com.pb.stratus.controller.info.FeatureCollection;
import com.pb.stratus.core.common.Preconditions;

import java.util.*;

/**
 * The Csv converter for FeatureCollection class.
 */
public class FeatureCollectionCsvConverter extends BaseCsvConverter
{
    private FeatureCollection featureCollection;

    public FeatureCollectionCsvConverter(FeatureCollection featureCollection)
    {
        Preconditions.checkNotNull(featureCollection,
                "FeatureCollection cannot be null");
        this.featureCollection  = featureCollection;
    }

    /**
     * Convert the given feature collection into a Csv string.
     * @param separator char CSV field separator
     * @param lineBreak String the line break after each row of the CSV.
     * @return String
     */
    public String getCsv(char separator, String lineBreak)
    {
        List<Feature> features = this.featureCollection.getFeatures();
        if(features.size() > 0)
        {
            Map<String, Object> properties = features.get(0).getProperties();
            Set<String> keySet = new LinkedHashSet<String>();
            keySet.addAll(properties.keySet());
            // need to remove "stratusid" field as it is a marker added by the
            // controller.
            keySet.remove("stratusid");
            keySet.remove("MI_PRINX");
            keySet.remove("MI_KEY");

            addCsvHeader(keySet, separator, lineBreak);
            for(Feature feature : features)
            {
                addRow(feature.getProperties(), keySet, separator, lineBreak);
            }
        }
        return sb.toString();
    }

    /**
     * This method is used to add a CSv header.
     * @param header
     * @param separator
     * @param lineBreak
     */
    protected void addCsvHeader(Set<String> header, char separator, String lineBreak)
    {
        List<String> headerList = new ArrayList<String>();
        headerList.addAll(header);
        super.addRow(headerList, separator, lineBreak);
    }

    private void addRow(Map<String, Object> properties, Set<String> keySet,
            char separator, String lineBreak)
    {
        List<String> rowList = new ArrayList<String>();
        for(Iterator<String> it = keySet.iterator(); it.hasNext();)
        {
            String key = it.next();
            rowList.add((String)properties.get(key));
        }
        super.addRow(rowList, separator, lineBreak);
    }
}
