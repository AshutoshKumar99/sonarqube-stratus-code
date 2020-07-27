package com.pb.stratus.controller.action;

import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.content.LayerBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstraction that encapsulates access to request parameters that define 
 * the contents of a map. 
 */
public class MapImageRequestParameters
{
    
    public static final String LAYERS_PARAM = "layers";
    
    public static final String NORTH_PARAM = "north";
    
    public static final String SOUTH_PARAM = "south";
    
    public static final String WEST_PARAM = "west";
    
    public static final String EAST_PARAM = "east";
    
    public static final String SRS_PARAM = "srs";
    
    public static final String MAP_CONFIG_NAME_PARAM = "mapConfig";

    public static final String ZOOMLEVEL_PARAM = "zoomLevel";

    public static final String RESOLUTION_PARAM = "printResolution";

    private HttpServletRequest request;

    public MapImageRequestParameters(HttpServletRequest request)
    {
        this.request = request;
    }
    
    public BoundingBox getBoundingBox()
    {
        double north = Double.parseDouble(request.getParameter(NORTH_PARAM));
        double south = Double.parseDouble(request.getParameter(SOUTH_PARAM));
        double west = Double.parseDouble(request.getParameter(WEST_PARAM));
        double east = Double.parseDouble(request.getParameter(EAST_PARAM));
        String srs = request.getParameter(SRS_PARAM);
        return new BoundingBox(north, south, west, east, srs);
    }
    
    public List<LayerBean> getLayers()
    {
        String layersJson = request.getParameter(LAYERS_PARAM);
        List<LayerBean> layerList = new ArrayList<LayerBean>();
        if(layersJson!=null && !layersJson.equals("")){
            JSONArray mapLayersArray = JSONArray.fromObject(layersJson);
            if (mapLayersArray != null)
            {
                for (int i = 0; i < mapLayersArray.size(); i++)
                {
                    JSONObject layerObj = JSONObject.fromObject(mapLayersArray.get(i));
                    List<String> urlArray = (List<String>) layerObj.get("urlArray");
                    Boolean isZeroBased = (Boolean)layerObj.get("zeroBased");
                    LayerBean bean = new LayerBean((String)layerObj.get("name"),(String)layerObj.get("layerType"),urlArray,isZeroBased,(String)layerObj.get("format"));
                    layerList.add(bean);
                }
            }
        }
        return layerList;
    }

    public String getMapConfigName()
    {
        return request.getParameter(MAP_CONFIG_NAME_PARAM);        
    }
    
    public int getZoomLevel()
    {
        String zoomLevelStr = request.getParameter(ZOOMLEVEL_PARAM);
        if (StringUtils.isBlank(zoomLevelStr))
        {
            return 0;
        }
        else 
        {
            return Integer.parseInt(zoomLevelStr);
        }
    }

    public int getPrintResolution()
    {
        String zoomLevelStr = request.getParameter(RESOLUTION_PARAM);
        if (StringUtils.isBlank(zoomLevelStr))
        {
            return 0;
        }
        else
        {
            return Integer.parseInt(zoomLevelStr);
        }
    }

}
