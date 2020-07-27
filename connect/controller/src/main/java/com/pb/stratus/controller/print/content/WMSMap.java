package com.pb.stratus.controller.print.content;

import com.pb.stratus.controller.httpclient.HttpClientFactory;

import java.util.Map;

/**
 * Encapsulates the information of a WmsBaseMap
 */
public class WMSMap
{
    private String name;

    private String layerType;

    private String url;

    private String tileHeight;

    private String tileWidth;

    private String format;

    private String version;

    private String exceptions;

    private String request;

    private String service;

    private String srs;

    private String crs;

    private String styles;

    private String opacity;

    private String transparent;

    private String layers;

    private String queryLayers;

    private boolean isBaseMap;

    private String reverseAxisOrder;

    private Map<String, String> legendUrlMap;

    private boolean secure;

    private String infoFormat;

    private String isWMSCallout;

    private String x;

    private String y;

    private String mapHeight;

    private String mapWidth;

    private String requestedURLForCallout;

    private HttpClientFactory httpClientFactory;

    /**
     * CONSTRUCTOR
     * @param name
     * @param layerType
     * @param url
     * @param tileHeight
     * @param tileWidth
     * @param format
     * @param version
     * @param exceptions
     * @param request
     * @param service
     * @param srs
     * @param crs
     * @param styles
     * @param opacity
     * @param transparent
     * @param layers
     * @param queryLayers
     * @param isBaseMap
     * @param reverseAxisOrder
     * @param legendUrlMap
     * @param secure
     * @param infoFormat
     * @param isWMSCallout
     * @param x
     * @param y
     * @param mapHeight
     * @param mapWidth
     * @param requestedURLForCallout
     * @param httpClientFactory
     */
    public WMSMap(String name, String layerType, String url, String tileHeight,
                   String tileWidth, String format, String version, String exceptions,
                   String request, String service, String srs, String crs, String styles, String opacity,
                   String transparent, String layers, String queryLayers, boolean isBaseMap,
                   String reverseAxisOrder, Map<String, String> legendUrlMap, boolean secure,
                   String infoFormat, String isWMSCallout, int x, int y, int mapHeight,
                   int mapWidth, String requestedURLForCallout, HttpClientFactory httpClientFactory)
    {
        this.name = name;
        this.layerType = layerType;
        this.url = url;
        this.tileHeight = tileHeight;
        this.tileWidth = tileWidth;
        this.format = format;
        this.version = version;
        this.exceptions = exceptions;
        this.request = request;
        this.service = service;
        this.srs = srs;
        this.crs = crs;
        this.styles = styles;
        this.opacity = opacity;
        this.transparent = transparent;
        this.layers = layers;
        this.queryLayers = queryLayers;
        this.isBaseMap = isBaseMap;
        this.reverseAxisOrder = reverseAxisOrder;
        this.legendUrlMap = legendUrlMap;
        this.secure = secure;
        this.infoFormat = infoFormat;
        this.isWMSCallout = isWMSCallout;
        this.x = Integer.toString(x);
        this.y = Integer.toString(y);
        this.mapHeight = Integer.toString(mapHeight);
        this.mapWidth = Integer.toString(mapWidth);
        this.requestedURLForCallout = requestedURLForCallout;
        this.httpClientFactory = httpClientFactory;
    }

    public String getName() {
        return name;
    }

    public String getLayerType() {
        return layerType;
    }

    public String getUrl() {
        return url;
    }

    public String getTileHeight() {
        return tileHeight;
    }

    public String getTileWidth() {
        return tileWidth;
    }

    public String getFormat() {
        return format;
    }

    public String getVersion() {
        return version;
    }

    public String getExceptions() {
        return exceptions;
    }

    public String getRequest() {
        return request;
    }

    public String getService() {
        return service;
    }

    public String getSrs() {
        return srs;
    }

    public String getCrs() {
        return crs;
    }

    public String getStyles() {
        return styles;
    }

    public String getTransparent() {
        return transparent;
    }

    public String getLayers() {
        return layers;
    }

    public String getQueryLayers() {
        return queryLayers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBaseMap() {
        return isBaseMap;
    }

    public String getReverseAxisOrder() {
        return reverseAxisOrder;
    }

    public Map<String, String> getLegendUrlMap() {
        return legendUrlMap;
    }

    public boolean isSecure() {
        return secure;
    }

    public String getInfoFormat() {
        return infoFormat;
    }

    public String getWMSCallout() {
        return isWMSCallout;
    }

    public String getRequestedURLForCallout() {
        return this.requestedURLForCallout;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getOpacity() {
        return opacity;
    }

    public String getMapHeight() {

        return mapHeight;
    }

    public String getMapWidth() {
        return mapWidth;
    }

    public HttpClientFactory getHttpClientFactory() {
        return httpClientFactory;
    }
}
