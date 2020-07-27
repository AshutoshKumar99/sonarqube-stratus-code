package com.pb.stratus.controller.print;

import java.awt.*;
import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;

public class BingUrlBuilder
{
    private static final String BING_REST 
        = "https://dev.virtualearth.net/REST/v1/Imagery/Map";
    
    private static final String BING_METADATA_REST
        = "http://dev.virtualearth.net/REST/v1/Imagery/Metadata";

    private static final String BING_PARAMS
        = "/%s/%f,%f/%d?mapSize=%d,%d&key=%s";

    private static final String QUADKEY_PLACEHOLDER = "{quadkey}";
    
    private static final String SUBDOMAIN_PLACEHOLDER = "{subdomain}";
    
    private static final String CULTURE_PLACEHOLDER = "{culture}";
    
    public URL formatBingURL(String mapName, Point2D center,
        Dimension imageSize, int zoomLevel, String apiKey)
    {
        String bingReq = 
            String.format(BING_PARAMS, convertBingMapNameToBingTable(mapName),
                center.getY(), center.getX(), zoomLevel, 
                imageSize.width, imageSize.height, apiKey);

        String restURL = BING_REST + bingReq;
        try 
        {
            return new URL(restURL);
        }
        catch (MalformedURLException mue)
        {
            throw new Error(mue);
        }
    }

    /**
     * 
     * @param mapName
     * @return
     */
    public String convertBingMapNameToBingTable(String mapName)
    {
        // SJB: CONN-12265 - Use Bing.xxxx naming convention
        if (mapName.equals("Bing Aerial"))
        {
            return "Aerial";
        }
        else if (mapName.equals("Bing Hybrid"))
        {
            return "AerialWithLabels";
        }
        else if (mapName.equals("Bing Roads"))
        {
            return "Road";
        }

        return mapName;
    }
    
    /**
     * 
     * @param url
     * @param quadKey
     * @param subDomain
     * @return
     */
    public URL constructBingTileURL(String url, String quadKey, String subDomain, String culture){
        
        url = url.replace(QUADKEY_PLACEHOLDER, quadKey).replace(
                SUBDOMAIN_PLACEHOLDER, subDomain).replace(CULTURE_PLACEHOLDER, culture);
        try
        {
            return new URL(url);
        }
        catch (MalformedURLException e)
        {
            throw new Error(e);
        }
    }
    
    /**
     * 
     * @param layerName
     * @param apiKey
     * @return
     */
    public URL constructBingMetaDataURL(String layerName, String apiKey) {
        try
        {
            return new URL(BING_METADATA_REST
                    + String.format("/%s?key=%s&include=ImageryProviders",
                            convertBingMapNameToBingTable(layerName), apiKey));
        }
        catch (MalformedURLException e)
        {
            throw new Error(e);
        }
    }

}
