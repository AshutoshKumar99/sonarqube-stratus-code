package com.pb.stratus.controller.print;

import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.info.Feature;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.print.content.*;

import java.util.List;
import java.util.Map;

/**
 * Values required to render a document from a template.  
 */
public class DocumentParameters
{
    
    private String title;
    
    private List<Feature> callOutFeatures;

    private Marker callOutMarker;

    private Boolean isShowAllInfoFields;
    
    private String legendTitle;
    
    private List<LayerBean> layers;
    
    private BoundingBox boundingBox;
    
    private String mapConfigName;
    
    private FmnResultsCollection fmnResults;

    private List<Annotation> annotations;

    private boolean useMetricDisplayUnits;

    private Marker locatorMarker;

    private List<WMSMap> wmsMapList;

    private List<ThematicMap> thematicMapList;

    List<QueryResultOverlayMap> queryResultOverlayMapsList;

    private int zoomLevel;

    private Boolean isScaledPrint;

    private int printResolution;

    private String printScaleText;

    private String displayUnit;

    private Map<String, List<LayerInfoBean>> namedLayersInfo;

    public DocumentParameters(String title,
            List<Feature> callOutFeatures,
            Marker callOutMarker,
            Boolean isShowAllInfoFields,
            String legendTitle,
            List<LayerBean> layers,
            BoundingBox boundingBox, String mapConfigName, 
            FmnResultsCollection fmnResults,
            List<Annotation> annotations,
            Marker locatorMarker,
            List<WMSMap> wmsMapList,
            List<ThematicMap> thematicMapList,
            List<QueryResultOverlayMap> queryResultOverlayMapsList,
            int zoomLevel,
            Boolean isScaledPrint,
            int printResolution,
            String printScaleText,
            String displayUnit, boolean useMetricDisplayUnits,
            Map<String, List<LayerInfoBean>> namedLayersInfo)
    {

        this.title = title;
        this.callOutFeatures = callOutFeatures;
        this.callOutMarker = callOutMarker;
        this.isShowAllInfoFields = isShowAllInfoFields;
        this.legendTitle = legendTitle;
        this.layers = layers;
        this.boundingBox = boundingBox;
        this.mapConfigName = mapConfigName;
        this.fmnResults = fmnResults;
        this.annotations = annotations;
        this.locatorMarker = locatorMarker;
        this.wmsMapList = wmsMapList;
        this.thematicMapList = thematicMapList;
        this.queryResultOverlayMapsList = queryResultOverlayMapsList;
        this.useMetricDisplayUnits = useMetricDisplayUnits;
        this.zoomLevel = zoomLevel;
        this.isScaledPrint = isScaledPrint;
        this.printResolution = printResolution;
        this.printScaleText = printScaleText;
        this.displayUnit = displayUnit;
        this.namedLayersInfo = namedLayersInfo;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public List<Feature> getCallOutFeatures()
    {
        return callOutFeatures;
    }

    public Marker getCallOutMarker()
    {
        return callOutMarker;
    }

    public Boolean getShowAllInfoFields()
    {
        return isShowAllInfoFields;
    }
    
    public String getLegendTitle()
    {
        return legendTitle;
    }

    public List<LayerBean> getLayers()
    {
        return layers;
    }

    public BoundingBox getBoundingBox()
    {
        return boundingBox;
    }

    public String getMapConfigName()
    {
        return mapConfigName;
    }

    public FmnResultsCollection getFmnResults()
    {
        return fmnResults;
    }

    public List<Annotation> getAnnotations()
    {
        return this.annotations;
    }

    public Marker getLocatorMarker()
    {
        return locatorMarker;
    }

    public boolean useMetricDisplayUnits()
    {
        return useMetricDisplayUnits;
    }

    public List<WMSMap> getWmsMapList()
    {
        return wmsMapList;
    }

    public int getZoomLevel()
    {
        return zoomLevel;

    }

    public Boolean getScaledPrint() {
        return isScaledPrint;
    }

    public String getPrintScaleText() {
        return printScaleText;
    }

    public int getPrintResolution() {
        return printResolution;

    }

    public String getDisplayUnit()
    {
        return displayUnit;
    }

    public Map<String, List<LayerInfoBean>> getNamedLayersInfo() {
        return namedLayersInfo;
    }

    public List<ThematicMap> getThematicMapList() {
        return thematicMapList;
    }

    public List<QueryResultOverlayMap> getQueryResultOverlayMapsList() { return queryResultOverlayMapsList; }

}
