package com.pb.stratus.controller.legend;

import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.print.content.QueryResultOverlayMap;
import com.pb.stratus.controller.print.content.ThematicMap;
import com.pb.stratus.controller.print.content.WMSMap;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Retrieves legend data for given overlays.
 */
public interface LegendService
{
    
    public LegendData getLegendData(Locale locale, List<WMSMap> wmsMapList,  Map<String,List<LayerInfoBean>> namedLayersInfo, String... overlayNames);

    public LegendData getLegendData(Locale locale, List<WMSMap> wmsMapList, List<ThematicMap> thematicMapList,
            List<QueryResultOverlayMap> queryResultOverlayMapList, Map<String, List<LayerInfoBean>> namedLayersInfo, String... overlayNames);

    public LegendData getWMSLegendData(List<WMSMap> wmsMapList);

    public LegendData getOverlayLegendData(Locale locale,  Map<String,List<LayerInfoBean>> namedLayersInfo,String... overlayNames);

    public List<OverlayLegend> filterVisibleLegendItems(Map<String,List<LayerInfoBean>> namedLayersInfo,List<OverlayLegend> legends);

    public LegendData getThematicLegendData(String thematicMapName, com.mapinfo.midev.service.mapping.v1.Map map)  throws ServiceException;

    public LegendData getThematicLegendData(ThematicMap thematicMap) throws ServiceException;

    public LegendData getQueryResultLegendData(QueryResultOverlayMap queryResultOverlayMap);
}
