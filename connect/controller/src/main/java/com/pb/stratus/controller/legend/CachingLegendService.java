package com.pb.stratus.controller.legend;

import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.infrastructure.cache.CacheHub;
import com.pb.stratus.controller.infrastructure.cache.CacheType;
import com.pb.stratus.controller.infrastructure.cache.Cacheable;
import com.pb.stratus.controller.print.content.QueryResultOverlayMap;
import com.pb.stratus.controller.print.content.ThematicMap;
import com.pb.stratus.controller.print.content.WMSMap;
import com.pb.stratus.core.common.Preconditions;
import com.pb.stratus.core.configuration.Tenant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CachingLegendService implements LegendService
{
    private LegendService targetService;
    
    // currently injecting tenant. after multi-tenancy this need to be
    // changed. since i cannot anticipate how tenant info will come then,
    // i thought it would be better to decouple tenant info altogether.
    private Tenant tenant;
    
    private CacheHub cacheHub;
    private static final Logger logger = LogManager.getLogger(CachingLegendService.class);

    public CachingLegendService(LegendService targetService,
           Tenant tenant, CacheHub cacheHub)
    {
        Preconditions.checkNotNull(tenant, "tenant cannot be null");
        Preconditions.checkNotNull(cacheHub, "cache-hub cannot be null");
        this.targetService = targetService;
        this.tenant = tenant;
        this.cacheHub = cacheHub;
    }
    
    /**
     * The logic followed here is that , cache is checked for each non wms overlay.
     * The non wms overlays which are not found in the cache are then requested from
     * MiDev and put in the cache.  The wms overlays are also fetched here while printing.
     * @param locale
     * @param overlayNames
     * @return
     */
    public LegendData getLegendData(Locale locale, List<WMSMap> wmsMapList, Map<String,List<LayerInfoBean>> namedLayersInfo, String... overlayNames)
    {
        LegendCacheData legendCacheData = LegendCacheHelper
                .createLegendDataFromCache(getTenantCache(), tenant, overlayNames);
        LegendData legendData = legendCacheData.getLegendData();
        if(namedLayersInfo!=null){
            legendData.setOverlayLegends(filterVisibleLegendItems(namedLayersInfo,legendData.getOverlayLegends()));
        }
        // merge non-wms legend data
        if(!legendCacheData.getLegendsNotPresentInCache().isEmpty())
        {
            String [] overlayNamesNotPresent =
                    legendCacheData.getOverlaysNotPresentInCacheAsArray();

            LegendData uncachedLegendData = getOverlayLegendData(locale,namedLayersInfo, overlayNamesNotPresent);

            LegendCacheHelper.putLegendDataInCache(getTenantCache(), tenant,
                    uncachedLegendData);
            // need to merge the LegendData from the cache and the one from
            // MiDev to make a combined and complete result.
            LegendCacheHelper.mergeLegendData(legendData, uncachedLegendData);
        }

        if (wmsMapList != null && !wmsMapList.isEmpty()) {
            LegendData wmsLegendData = getWMSLegendData(wmsMapList);
            LegendCacheHelper.mergeLegendData(legendData, wmsLegendData);
        }

        return legendData;
    }

    public LegendData getLegendData(Locale locale, List<WMSMap> wmsMapList,List<ThematicMap> thematicMapList,
            List<QueryResultOverlayMap> queryResultOverlayMapList, Map<String,List<LayerInfoBean>> namedLayersInfo,
            String... overlayNames)
    {
        LegendCacheData legendCacheData = LegendCacheHelper
                .createLegendDataFromCache(getTenantCache(), tenant, overlayNames);
        LegendData legendData = legendCacheData.getLegendData();
        if(namedLayersInfo!=null){
            legendData.setOverlayLegends(filterVisibleLegendItems(namedLayersInfo,legendData.getOverlayLegends()));
        }
        // merge non-wms legend data
        if(!legendCacheData.getLegendsNotPresentInCache().isEmpty())
        {
            String [] overlayNamesNotPresent =
                    legendCacheData.getOverlaysNotPresentInCacheAsArray();

            LegendData uncachedLegendData = getOverlayLegendData(locale,namedLayersInfo, overlayNamesNotPresent);

            LegendCacheHelper.putLegendDataInCache(getTenantCache(), tenant,
                    uncachedLegendData);
            // need to merge the LegendData from the cache and the one from
            // MiDev to make a combined and complete result.
            LegendCacheHelper.mergeLegendData(legendData, uncachedLegendData);
        }

        if (wmsMapList != null && !wmsMapList.isEmpty()) {
            LegendData wmsLegendData = getWMSLegendData(wmsMapList);
            LegendCacheHelper.mergeLegendData(legendData, wmsLegendData);
        }

        if (thematicMapList != null && !thematicMapList.isEmpty()) {
            for (ThematicMap thematicMap : thematicMapList ){
                try {
                    LegendData thematicLegendData = getThematicLegendData (thematicMap);
                    LegendCacheHelper.mergeLegendData (legendData, thematicLegendData);
                } catch (ServiceException e) {
                    logger.error("Exception while getting legend image for end user thematic ", e);
                }
            }
        }

        if (null != queryResultOverlayMapList && queryResultOverlayMapList.size() > 0) {
            for (QueryResultOverlayMap queryResultOverlayMap : queryResultOverlayMapList) {
                LegendData queryResultLegendData = getQueryResultLegendData(queryResultOverlayMap);
                LegendCacheHelper.mergeLegendData (legendData, queryResultLegendData);
            }
        }

        return legendData;
    }

    @Override
    public LegendData getWMSLegendData(List<WMSMap> wmsMapList) {
        return this.targetService.getWMSLegendData(wmsMapList);
    }

    @Override
    public List<OverlayLegend> filterVisibleLegendItems(Map<String,List<LayerInfoBean>> namedLayersInfo,List<OverlayLegend> legends) {
        return this.targetService.filterVisibleLegendItems(namedLayersInfo, legends);
    }

    @Override
    public LegendData getThematicLegendData(String thematicMapName, com.mapinfo.midev.service.mapping.v1.Map map) throws ServiceException {
        return this.targetService.getThematicLegendData(thematicMapName, map);
    }

    @Override
    public LegendData getThematicLegendData(ThematicMap thematicMap) throws ServiceException {
        return this.targetService.getThematicLegendData(thematicMap);
    }

    @Override
    public LegendData getQueryResultLegendData(QueryResultOverlayMap queryResultOverlayMap) {
        return this.targetService.getQueryResultLegendData(queryResultOverlayMap);
    }

    @Override
    public LegendData getOverlayLegendData(Locale locale,  Map<String,List<LayerInfoBean>> namedLayersInfo,String... overlayNames) {
        return this.targetService.getOverlayLegendData(locale,namedLayersInfo, overlayNames);
    }

    /**
     * Get cache for the particular tenant.
     * @return Cacheable .. cache for the particular tenant.
     */
    private Cacheable getTenantCache()
    {
        return this.cacheHub.getCacheForTenant(tenant, CacheType.LEGEND_CACHE);
    }
}
