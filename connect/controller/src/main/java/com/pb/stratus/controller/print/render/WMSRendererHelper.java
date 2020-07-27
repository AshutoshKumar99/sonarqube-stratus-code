package com.pb.stratus.controller.print.render;


import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.DocumentParameters;
import com.pb.stratus.controller.print.content.WMSMap;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import uk.co.graphdata.utilities.contract.Contract;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * Renders the specified map as a BufferedImage, by calling HTTPProxyAction (which is assigned in the constructor).
 */
public class WMSRendererHelper
{
    private static final Logger logger = LogManager.getLogger(WMSRendererHelper.class);
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String WMS_NEW_VERSION = "1.3.0";
    private static final String FEATURE_COUNT = "100";
    private static final String CALLOUT_REQUEST = "GetFeatureInfo";
    private static final String MAP_URL = "&MAP_URL=";
    private static final String SECURE = "&SECURE=";
    private static final String WMS_URL = "&WMS_URL=";

    /**
     *  This method builds the complete url for getting the wms map image from layerRenderParams
     *
     * @param boundingBox
     * @param url
     * @return
     * @throws java.io.UnsupportedEncodingException
     */
    public StringBuilder buildURL(BoundingBox boundingBox, Double height, Double width,
                                  String url, WMSMap wmsMap, boolean isCallout)
    {
        StringBuilder wmsURLBuilder = new StringBuilder();
        String layerName = wmsMap.getName();

        Contract.pre(StringUtils.isNotEmpty(layerName), "LayerName cannot be null");
        Contract.pre((boundingBox != null), "BoundingBox cannot be null");
        Contract.pre(StringUtils.isNotEmpty(url), "WMS URL cannot be null");
        Contract.pre(StringUtils.isNotEmpty(wmsMap.getLayers()), "WMS Layers cannot be null");
        Contract.pre(StringUtils.isNotEmpty(wmsMap.getVersion()), "WMS Version cannot be null");
        Contract.pre(StringUtils.isNotEmpty(wmsMap.getRequest()), "WMS request type cannot be null");
        if(wmsMap.getSrs() == null && wmsMap.getCrs() == null)
        {
            Contract.pre(StringUtils.isNotEmpty(wmsMap.getSrs()), "WMS SRS/CRS cannot be null");
        }
        try {
            wmsURLBuilder.append(url);
            if(wmsMap.getUrl().indexOf("?")>-1 || wmsMap.getUrl().indexOf("%3F")>-1)
            {
                this.appendToStringBuilder("&VERSION=", wmsMap.getVersion(), wmsURLBuilder);
            }
            else
            {
                this.appendToStringBuilder("?VERSION=", wmsMap.getVersion(), wmsURLBuilder);
            }
            if("true".equalsIgnoreCase(wmsMap.getReverseAxisOrder()) && WMS_NEW_VERSION.equals(wmsMap.getVersion()))
            {
                wmsURLBuilder.append("&BBOX="+boundingBox.getSouth()+"," +boundingBox.getWest()+","+boundingBox.getNorth()+","+boundingBox.getEast());
            }
            else
            {
                wmsURLBuilder.append("&BBOX="+boundingBox.getWest()+"," +boundingBox.getSouth()+","+boundingBox.getEast()+","+boundingBox.getNorth());
            }
            if(isCallout)
            {
                wmsURLBuilder.append("&WIDTH="+ wmsMap.getMapWidth());

                wmsURLBuilder.append("&HEIGHT=" + wmsMap.getMapHeight());
            }
            else
            {
                wmsURLBuilder.append("&WIDTH=" + new Double(width).intValue());
                wmsURLBuilder.append("&HEIGHT=" + new Double(height).intValue());
            }
            this.appendToStringBuilder("&LAYERS=", wmsMap.getLayers(), wmsURLBuilder);
            if(isCallout)
            {
                wmsURLBuilder.append("&REQUEST=" + CALLOUT_REQUEST);
                wmsURLBuilder.append("&X=" + wmsMap.getX());
                wmsURLBuilder.append("&Y=" + wmsMap.getY());
                wmsURLBuilder.append("&INFO_FORMAT=" + wmsMap.getInfoFormat());
                wmsURLBuilder.append("&FEATURE_COUNT=" + FEATURE_COUNT);
                this.appendToStringBuilder("&QUERY_LAYERS=", wmsMap.getQueryLayers(), wmsURLBuilder);
            }
            else
            {
                this.appendToStringBuilder("&REQUEST=", wmsMap.getRequest(), wmsURLBuilder);
                this.appendToStringBuilder("&EXCEPTIONS=", wmsMap.getExceptions(), wmsURLBuilder);
                this.appendToStringBuilder("&TRANSPARENT=", wmsMap.getTransparent(), wmsURLBuilder);
            }
            wmsURLBuilder.append("&FORMAT=" + wmsMap.getFormat());
            this.appendToStringBuilder("&SERVICE=", wmsMap.getService(), wmsURLBuilder);

            if(WMS_NEW_VERSION.equalsIgnoreCase(wmsMap.getVersion()))
            {
                wmsURLBuilder.append("&CRS=" + wmsMap.getCrs());
            }
            else
            {
                wmsURLBuilder.append("&SRS=" + wmsMap.getSrs());
            }
            if(!(wmsMap.getStyles()).equalsIgnoreCase("null"))
            {
                this.appendToStringBuilder("&STYLES=", validateStyles(wmsMap.getStyles()), wmsURLBuilder);
            }
        }
        catch (UnsupportedEncodingException unSEE)
        {
            logger.warn("Exception while requesting the map image for layer "
                    + layerName + ". Continuing with empty image", unSEE);
        }
        catch (Exception e)
        {
            logger.warn("Exception while requesting the map image for layer "
                    + layerName + ". Continuing with empty image", e);
        }
        return wmsURLBuilder;
    }

    /**
     *
     * @param completeURL
     * @param wmsMap
     * @return
     */
    public String constructSecuredURLForCallout(String completeURL, WMSMap wmsMap) throws UnsupportedEncodingException
    {
        StringBuilder securedURL = new StringBuilder();
        // In case of secured call we append all the parameters and remove the base url from the complete url
        String mapURL = URLDecoder.decode(wmsMap.getUrl(), DEFAULT_ENCODING);
        securedURL.append(wmsMap.getRequestedURLForCallout()).append(MAP_URL).
                    append(mapURL).append(SECURE).append(wmsMap.isSecure());
        String urlParams = completeURL.substring(mapURL.length()+1, completeURL.length());
        securedURL.append("&").append(urlParams);
        return securedURL.toString();
    }


    /**
     * We construct a hashmap with key as map name and value as wms callout url
     * @param params
     * @return
     */
    public Map<String, List<String>> constructURLForWMS(DocumentParameters params)
    {
        Map<String, List<String>> wmsCalloutURLMap = new LinkedHashMap<String, List<String>>();
        List<WMSMap> wmsMapList = params.getWmsMapList();
        Collections.reverse(wmsMapList);
        for(WMSMap wmsMap : wmsMapList)
        {
            // WMS base maps are irrelevant as call out information is only for business maps
            if (wmsMap.isBaseMap() || "false".equalsIgnoreCase(wmsMap.getWMSCallout()))
            {
                continue;
            }
            try
            {
                List<String> wmsCalloutURL = new ArrayList<String>();
                String url = wmsMap.getUrl();
                if(wmsMap.isSecure())
                {
                    url = URLDecoder.decode(wmsMap.getUrl(), DEFAULT_ENCODING) ;
                }
                StringBuilder completeURL = buildURL(params.getBoundingBox(), null, null, url, wmsMap, true);
                if(wmsMap.isSecure())
                {
                    wmsCalloutURL.add(constructSecuredURLForCallout(
                            completeURL.toString(), wmsMap));
                }
                else
                {
                    wmsCalloutURL.add(completeURL.toString());
                }
                wmsCalloutURLMap.put(wmsMap.getName(), wmsCalloutURL);
            }
            catch(UnsupportedEncodingException ex)
            {
                logger.warn("Exception while requesting the map image for layer "
                        + wmsMap.getName() + ". Continuing with empty image", ex);
            }
        }
        return wmsCalloutURLMap;
    }


    /**
     *  This method validates the Style for the wms layer.
     *  Style is ideally a comma separated string containing individual styles.
     *
     * @param styles
     * @return
     */
    private String validateStyles(String styles)
    {
        String stylesString = "";
        if(styles == null || "".equalsIgnoreCase(styles.trim()))
        {
            return stylesString;
        }
        String[] splitStyles = styles.split(",");
        // In some cases style can be null or empty or a string like " , , "
        for (String style : splitStyles)
        {
            if(!("null".equalsIgnoreCase(style.trim()) || "".equalsIgnoreCase(styles.trim())))
            {
                stylesString += style.trim() + ",";
            }
        }
        if(stylesString.length()> 0 && stylesString.charAt(stylesString.length()-1)== ',')
        {
            return stylesString.substring(0, stylesString.length()-1);
        }
        return stylesString;
    }

    /**
     *
     * @param key
     * @param needToBeAppended
     * @param builder
     * @throws java.io.UnsupportedEncodingException
     */
    private void appendToStringBuilder (String key, String needToBeAppended,
                        StringBuilder builder) throws UnsupportedEncodingException
    {
          if (builder == null)
              return;

          if(key != null)
          {
              builder.append(key);
          }
          builder.append(URLEncoder.encode(needToBeAppended, "UTF-8"));
    }
}
