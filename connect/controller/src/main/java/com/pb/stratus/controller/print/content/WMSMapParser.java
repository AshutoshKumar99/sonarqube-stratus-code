package com.pb.stratus.controller.print.content;

import com.pb.stratus.controller.httpclient.HttpClientFactory;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;

/**
 * Encapsulates the information of a WmsBaseMap
 */
public class WMSMapParser
{
    private static final Logger logger = LogManager.getLogger(WMSMapParser.class);

    HttpClientFactory httpClientFactory = null;
    private ControllerConfiguration controllerConfig;
    private String mapCfg;

    public WMSMapParser(HttpClientFactory httpClientFactory,
                        ControllerConfiguration controllerConfig)
    {
        this.httpClientFactory = httpClientFactory;
        this.controllerConfig = controllerConfig;
    }

    /**
     *
     * @param json
     * @param isBaseMap
     * @return
     */
    public WMSMap parseWmsMapJson(String json, boolean isBaseMap)
    {
        if (json == null) {
            logger.error(" Empty json for WmsBaseMap - in WMSMapParser .");
            return null;
        }
        JSONObject obj = JSONObject.fromObject(json);
        int x = JSONObject.fromObject(json).optInt("x");
        int y = JSONObject.fromObject(json).optInt("y");
        return this.setValues(obj, isBaseMap, x, y);
    }

    public void setMapCfg(String mapCfg)
    {
        this.mapCfg = mapCfg;
    }

    /**
     *
     * @param jsonRep
     * @param isBaseMap
     * @return
     */
    public List<WMSMap> parseWMSMapJsonArray(String jsonRep, boolean isBaseMap)
    {
        List<WMSMap> wmsMapList = new LinkedList<WMSMap>();
        if (jsonRep == null) {
            logger.debug(" Empty json for wmsMapList");
            return wmsMapList;
        }
        JSONArray wmsMaps = JSONObject.fromObject(jsonRep).getJSONArray("results");
        int x = JSONObject.fromObject(jsonRep).optInt("x");
        int y = JSONObject.fromObject(jsonRep).optInt("y");

        if (wmsMaps != null)
        {
            for (int i = 0; i < wmsMaps.size(); i++)
            {
                JSONObject jsonObj = JSONObject.fromObject(wmsMaps.get(i));
                wmsMapList.add(this.setValues(jsonObj, isBaseMap, x, y));
            }
        }
        return wmsMapList;
    }

    /**
     *
     * @param obj
     * @param isBaseMap
     * @return
     */
    private WMSMap setValues(JSONObject obj, boolean isBaseMap, int x, int y)
    {
        String name = getSummaryFieldValue(obj, "name");
        String layerType = getSummaryFieldValue(obj, "layerType");
        String url = getSummaryFieldValue(obj, "url");
        String tileHeight = getSummaryFieldValue(obj, "tileHeight");
        String tileWidth = getSummaryFieldValue(obj, "tileWidth");
        String format = getSummaryFieldValue(obj, "format");
        String version = getSummaryFieldValue(obj, "version");
        String exceptions = getSummaryFieldValue(obj, "exceptions");
        String request = getSummaryFieldValue(obj, "request");
        String service = getSummaryFieldValue(obj, "service");
        String srs = getSummaryFieldValue(obj, "srs");
        String crs = getSummaryFieldValue(obj, "crs");
        String styles = getSummaryFieldValue(obj, "styles");
        String opacity = getSummaryFieldValue(obj, "opacity");
        String transparent = getSummaryFieldValue(obj, "transparent");
        String layers = getSummaryFieldValue(obj, "layers");
        String queryLayers = getSummaryFieldValue(obj, "queryLayers");
        String reverseAxisOrder = getSummaryFieldValue(obj, "reverseAxisOrder");
        JSONObject jsonLegendUrlMap = obj.getJSONObject("legendUrlMap");
        String secure = getSummaryFieldValue(obj, "secure");
        String infoFormat = getSummaryFieldValue(obj, "infoFormat");
        int mapHeight = JSONObject.fromObject(obj).optInt("mapHeight");
        int mapWidth = JSONObject.fromObject(obj).optInt("mapWidth");
        String isWMSCallout = getSummaryFieldValue(obj, "isWMSCallout");
        // This is for WMS callout as for secured URLs we need to build the URL
        StringBuilder requestedURLForCallout = new StringBuilder();
        if(!isBaseMap)
        {
            String themeURL = controllerConfig.getTenantThemeUrl().toString();
            if(themeURL != null)
            {
                if(themeURL.indexOf("https") != -1)
                {
                    themeURL = themeURL.replaceAll("https", "http");
                }
                requestedURLForCallout.append(themeURL.substring(0, themeURL.toLowerCase().indexOf("theme")));
                requestedURLForCallout.append("controller/wms/getwms?mapcfg=").append(this.mapCfg);
            }
        }

        boolean isSecure = false;
        if (secure != null && "true".equalsIgnoreCase(secure))
        {
            isSecure = true;
        }
        Map<String, String> legendUrlMap = new LinkedHashMap<String, String>();
        if(jsonLegendUrlMap != null && jsonLegendUrlMap.size() > 0)
        {
            Set<String> keySet = jsonLegendUrlMap.keySet();
            Iterator<String> iter = keySet.iterator();
            while (iter.hasNext())
            {
                String legendName = iter.next();
                Object legendObj = jsonLegendUrlMap.get(legendName);
                if(legendObj != null && !legendObj.equals("null"))  {
                    legendUrlMap.put(legendName, (String)legendObj);
                }
            }
        }
        return new WMSMap(name, layerType, url, tileHeight, tileWidth, format, version,
                  exceptions, request, service, srs, crs, styles, opacity, transparent, layers,
                  queryLayers, isBaseMap, reverseAxisOrder, legendUrlMap, isSecure, infoFormat,
                 isWMSCallout, x, y, mapHeight, mapWidth, requestedURLForCallout.toString(), this.httpClientFactory);
    }

    private String getSummaryFieldValue(JSONObject obj, String field)
    {
        try
        {
            String value  = obj.getString(field);
            return value;
        }
        catch(Exception ex)
        {
            logger.debug("Exception occurred while reading value of field :"+ field + " from params of wms map ", ex);
            return null;
        }
    }

}
