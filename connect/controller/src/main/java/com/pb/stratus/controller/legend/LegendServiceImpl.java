/**
 * ------------------------------------------------------------
 * Copyright (c) 2014 Pitney Bowes India Software Pvt. Ltd.
 * All Right Reserved.
 * ------------------------------------------------------------
 *
 * SVN revision information:
 * @version Revision: $Revision$
 * @author  Author:   $Author$:
 * @date    Date:     $Date$
 */
package com.pb.stratus.controller.legend;

import com.mapinfo.midev.service.mapping.v1.*;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.pb.stratus.controller.ThreadInterruptedException;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.infrastructure.CombiningExecutor;
import com.pb.stratus.controller.infrastructure.CombiningExecutorFactory;
import com.pb.stratus.controller.print.content.QueryResultOverlayMap;
import com.pb.stratus.controller.print.content.ThematicMap;
import com.pb.stratus.controller.print.content.WMSMap;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.xml.security.utils.Base64;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Map;

/**
 * The concrete implementation for the legend services.
 */
public class LegendServiceImpl implements LegendService
{

   private MappingServiceInterface mappingWebService;
   private CombiningExecutorFactory combiningExecutorFactory;

   private static final String mapLayerNameSeparator = "_NM_";

   private static final Logger logger = LogManager.getLogger (LegendServiceImpl.class);

   public LegendServiceImpl(MappingServiceInterface mappingWebService,
                            CombiningExecutorFactory combiningExecutorFactory)

   {
      this.mappingWebService = mappingWebService;
      this.combiningExecutorFactory = combiningExecutorFactory;

   }

   /**
    * Gets the non-wms and wms legendData
    *
    * @param locale
    * @param wmsMapList
    * @param overlayNames
    *
    * @return legendData LegendData
    */
   public LegendData getLegendData(Locale locale, List<WMSMap> wmsMapList, Map<String, List<LayerInfoBean>> namedLayersInfo, String... overlayNames)
   {
      LegendData legendData = getOverlayLegendData (locale, namedLayersInfo, overlayNames);

      if (wmsMapList != null && !wmsMapList.isEmpty ()) {
         LegendData wmsLegendData = getWMSLegendData (wmsMapList);
         LegendCacheHelper.mergeLegendData (legendData, wmsLegendData);
      }
      return legendData;
   }

    /**
     * Gets the non-wms , wms and end-user thematic legendData
     *
     * @param locale
     * @param wmsMapList
     * @param overlayNames
     *
     * @return legendData LegendData
     */
    public LegendData getLegendData(Locale locale, List<WMSMap> wmsMapList, List<ThematicMap> thematicMapList,
            List<QueryResultOverlayMap> queryResultOverlayMapList, Map<String, List<LayerInfoBean>> namedLayersInfo,
            String... overlayNames)
    {
        LegendData legendData = getOverlayLegendData (locale, namedLayersInfo, overlayNames);

        if (wmsMapList != null && !wmsMapList.isEmpty ()) {
            LegendData wmsLegendData = getWMSLegendData (wmsMapList);
            LegendCacheHelper.mergeLegendData (legendData, wmsLegendData);
        }

        if (thematicMapList != null && !thematicMapList.isEmpty ()) {
            for (ThematicMap thematicMap : thematicMapList ){
                try {
                    LegendData thematicLegendData = getThematicLegendData (thematicMap);
                    LegendCacheHelper.mergeLegendData (legendData, thematicLegendData);
                } catch (ServiceException e) {
                    logger.error ("Exception while getting legend image for Print for end user thematic ", e);
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

    /**
    * Gets Overlays LegendData
    *
    * @param locale
    * @param overlayNames
    *
    * @return legendData
    */
   @Override
   public LegendData getOverlayLegendData(Locale locale, Map<String, List<LayerInfoBean>> namedLayersInfo, String... overlayNames)
   {
      List<OverlayLegend> legends = getLegendsForOverlays (locale, overlayNames);
      legends = filterVisibleLegendItems (namedLayersInfo, legends);
      return new LegendData (legends);
   }

   /**
    * Filters the overlay legend items according to the namedLayerInfo map which
    * contains info of visible overlays and which all layers are enabled and visible within each overlay
    *
    * @param namedLayersInfo
    * @param legends
    *
    * @return List<OverlayLegend>
    */
   public List<OverlayLegend> filterVisibleLegendItems(Map<String, List<LayerInfoBean>> namedLayersInfo, List<OverlayLegend> legends)
   {
      if (namedLayersInfo != null) {
         List<OverlayLegend> visibleLegends = new ArrayList<OverlayLegend> ();
         for (OverlayLegend overlayLegend : legends) {
            // check if the overlay is present in  namedLayersInfo map
            if (namedLayersInfo.get (overlayLegend.getTitle ()) != null) {
               // retrieve list of visible layer names for the overlay
               List<LayerInfoBean> visibleLayers = namedLayersInfo.get (overlayLegend.getTitle ());
               List<String> visibleLayerNames = new ArrayList<String> ();
               for (LayerInfoBean layerInfoBean : visibleLayers) {
                  visibleLayerNames.add (layerInfoBean.getLayerName ());
               }
               // filter out the legendItems for the visible layers
               List<LegendItem> visibleLegendItems = new ArrayList<LegendItem> ();
               for (LegendItem legendItem : overlayLegend.getLegendItems ()) {
                  String legendItemLayerName =
                         overlayLegend.getOverlayName () + mapLayerNameSeparator + legendItem.getTitle ();
                  if (visibleLayerNames.contains (legendItemLayerName)) {
                     visibleLegendItems.add (legendItem);
                  }
               }
               visibleLegends.add (new OverlayLegend (overlayLegend.getTitle (), visibleLegendItems));
            }
         }
         return visibleLegends;
      }
      return legends;
   }


    /**
    * Gets overlay legends by passing overlayNames
    *
    * @param locale
    * @param overlayNames
    *
    * @return List<OverlayLegend>
    */
   private List<OverlayLegend> getLegendsForOverlays(Locale locale, String... overlayNames)
   {
      CombiningExecutor<OverlayLegend> executor = combiningExecutorFactory
                                                         .createCombiningExecutor (OverlayLegend.class);
      for (String overlayName : overlayNames) {
         GetLegendTask task = new GetLegendTask (overlayName, locale,
                                                 mappingWebService, new LegendConverter (),
                                                 SecurityContextHolder.getContext ());
         executor.addTask (task);
         //gdutt: executor.addTask(new TokenStoreCallableWrapper<OverlayLegend>(task, concurrentTokenStore));
      }
      try {
         return executor.getResults ();
      }
      catch (InterruptedException ix) {
         Thread.currentThread ().interrupt ();
         throw new ThreadInterruptedException (ix);
      }
   }

   /**
    * Constructs the wms legend data separately as it is not cached and fetched from url while printing
    *
    * @param wmsMapList
    *
    * @return legendData
    */
   public LegendData getWMSLegendData(List<WMSMap> wmsMapList)
   {
      List<OverlayLegend> legends = new ArrayList<OverlayLegend> ();
      for (WMSMap wmsMap : wmsMapList) {
         if (wmsMap.isBaseMap ()) { continue; }
         legends.add (getWMSOverlayLegend (wmsMap));
      }
      return new LegendData (legends);
   }

   /**
    * constructs an OverlayLegend object for single wms legend
    *
    * @param wmsMap
    *
    * @return
    */
   private OverlayLegend getWMSOverlayLegend(WMSMap wmsMap)
   {
      List<LegendItem> legendItems = new ArrayList<LegendItem> ();
      Map<String, String> legendURLMap = wmsMap.getLegendUrlMap ();
      for (Map.Entry<String, String> legendEntry : legendURLMap.entrySet()) {
         String legendUrl = legendEntry.getValue();
         WMSLegendItem legendItem = new WMSLegendItem (legendEntry.getKey(), legendEntry.getKey(), wmsMap.getUrl (),
                                                       wmsMap.isSecure (), legendUrl, wmsMap.getHttpClientFactory ());
         SingleLegendItem singleLegendItem = null;
         List<BufferedImage> imageList = (List) legendItem.getIcons ();
         if (imageList != null && (!imageList.isEmpty())) {
            for (BufferedImage image : imageList) {
               byte[] imageBytes = null;
               try {
                  ByteArrayOutputStream baos = new ByteArrayOutputStream ();
                  ImageIO.write (image, "png", baos);
                  baos.flush ();
                  imageBytes = baos.toByteArray ();
                  baos.close ();
               }
               catch (IOException ex) {
                  logger.error ("Exception while reading the legend icon image", ex);
                  continue;
               }
               if (imageBytes != null)
               {
                   singleLegendItem = new SingleLegendItem (legendEntry.getKey(), imageBytes);
                   singleLegendItem.setWMSLegendItem (true);
                   legendItems.add(singleLegendItem);
               }
            }
         }
      }
      // reversing so that the legends order in print is same as that in connect
      Collections.reverse (legendItems);
      return new OverlayLegend (wmsMap.getName (), legendItems);
   }

    public LegendData getThematicLegendData(String thematicMapName, com.mapinfo.midev.service.mapping.v1.Map map)  throws ServiceException {
        GetMapLegendsRequest getMapLegendsRequest = new GetMapLegendsRequest();
        getMapLegendsRequest.setImageMimeType("image/png");
        GetMapLegendsResponse getMapLegendsResponse = null;
        if(map != null){
            if(map.getLayer() != null && map.getLayer().size() >0){
                if(map.getLayer().get(0) instanceof GraduatedSymbolLayer){
                    getMapLegendsRequest.setLegendImageHeight(64);
                    getMapLegendsRequest.setLegendImageWidth(64);
                }else{
                    getMapLegendsRequest.setLegendImageHeight(25);
                    getMapLegendsRequest.setLegendImageWidth(25);
                }
            }
            getMapLegendsRequest.setMap(map);
            getMapLegendsResponse = mappingWebService.getMapLegends(getMapLegendsRequest);
        }
        LegendConverter converter = new LegendConverter ();
        OverlayLegend overlayLegend = converter.convertThematicLegend(thematicMapName, getMapLegendsResponse);

        if (overlayLegend == null)
            return new LegendData(new ArrayList<OverlayLegend>());

        overlayLegend.setEndUserThematic(true);
        if(map != null && map.getLayer() != null && map.getLayer().size() >0){
            if(map.getLayer().get(0) instanceof GraduatedSymbolLayer){
                overlayLegend.setTableName(((GraduatedSymbolLayer)map.getLayer().get(0)).getTable().getName());
            }else{
                overlayLegend.setTableName(((FeatureLayer)map.getLayer().get(0)).getTable().getName());
            }
        }
        return new LegendData(Arrays.asList(overlayLegend));
    }

    public LegendData getThematicLegendData(ThematicMap thematicMap)  throws ServiceException {
        GetMapLegendsRequest getMapLegendsRequest = new GetMapLegendsRequest();
        //getMapLegendsRequest.setLegendImageHeight(64);
        //getMapLegendsRequest.setLegendImageWidth(64);
        getMapLegendsRequest.setImageMimeType("image/png");
        GetMapLegendsResponse getMapLegendsResponse = null;

        com.mapinfo.midev.service.mapping.v1.Map map = thematicMap.getMapObject();
        if(map != null){
            if(map.getLayer() != null && map.getLayer().size() >0){
                if(map.getLayer().get(0) instanceof GraduatedSymbolLayer){
                    getMapLegendsRequest.setLegendImageHeight(64);
                    getMapLegendsRequest.setLegendImageWidth(64);
                }else{
                    getMapLegendsRequest.setLegendImageHeight(25);
                    getMapLegendsRequest.setLegendImageWidth(25);
                }
            }
            getMapLegendsRequest.setMap(map);
            getMapLegendsResponse = mappingWebService.getMapLegends(getMapLegendsRequest);
        }
        LegendConverter converter = new LegendConverter ();
        OverlayLegend overlayLegend = converter.convertThematicLegend(thematicMap.getName(), getMapLegendsResponse);

        if (overlayLegend == null)
            return new LegendData(new ArrayList<OverlayLegend>());

        overlayLegend.setEndUserThematic(true);
        overlayLegend.setOverlayOrder(thematicMap.getOverlayOrder());

        if(map != null && map.getLayer() != null && map.getLayer().size() >0){
            if(map.getLayer().get(0) instanceof GraduatedSymbolLayer){
                overlayLegend.setTableName(((GraduatedSymbolLayer)map.getLayer().get(0)).getTable().getName());
            }else{
                overlayLegend.setTableName(((FeatureLayer)map.getLayer().get(0)).getTable().getName());
            }
        }
        return new LegendData(Arrays.asList(overlayLegend));
    }

    @Override
    public LegendData getQueryResultLegendData(QueryResultOverlayMap queryResultOverlayMap) {
        QueryResultLegendItem legendItem = new QueryResultLegendItem (queryResultOverlayMap.getName(),
                LegendType.FEATURE_STYLE_OVERRIDE.toString());
        List<LegendItem> legendItems = new ArrayList<LegendItem> ();
        SingleLegendItem singleLegendItem = null;
        List<BufferedImage> imageList = (List) legendItem.getIcons ();
        if (imageList != null && (!imageList.isEmpty())) {
            for (BufferedImage image : imageList) {
                byte[] imageBytes = null;
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream ();
                    ImageIO.write (image, "png", baos);
                    baos.flush ();
                    imageBytes = baos.toByteArray();
                    baos.close ();
                }
                catch (IOException ex) {
                    logger.error ("Exception while reading the legend icon image", ex);
                    continue;
                }

                if (imageBytes != null)
                {
                    singleLegendItem = new SingleLegendItem (getTableName(queryResultOverlayMap.getQueryTable()), imageBytes);
                    singleLegendItem.setBase64image(Base64.encode(imageBytes));
                    legendItems.add(singleLegendItem);
                }
            }
        }
        OverlayLegend overlayLegend = new OverlayLegend(queryResultOverlayMap.getName(), legendItems);
        overlayLegend.setTableName(queryResultOverlayMap.getQueryTable());
        overlayLegend.setOverlayOrder(queryResultOverlayMap.getOverlayOrder());
        return new LegendData(Arrays.asList(overlayLegend));
    }

    private String getTableName(String tablePath) {
        if(null != tablePath && tablePath.indexOf('/')!=-1) {
            return tablePath.substring(tablePath.lastIndexOf('/')+1);
        }
        else {
            return tablePath;
        }
    }

}
