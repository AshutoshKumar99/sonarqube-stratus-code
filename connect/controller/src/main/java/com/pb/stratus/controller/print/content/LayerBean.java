package com.pb.stratus.controller.print.content;

import java.util.List;

/**
 * Encapsulates the information for a Non-wms Map
 */
public class LayerBean
{
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLayerType() {
        return layerType;
    }

    public void setLayerType(String layerType) {
        this.layerType = layerType;
    }

    public List<String> getUrlArray() {
        return urlArray;
    }

    public void setUrlArray(List<String> urlArray) {
        this.urlArray = urlArray;
    }

    public Boolean getZeroBased() {
        return zeroBased;
    }

    public void setZeroBased(Boolean zeroBased) {
        this.zeroBased = zeroBased;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    private String layerType;

    private List<String> urlArray;

    private Boolean zeroBased;

    private String format;

    public LayerBean(String name, String layerType, List<String> urlArray, Boolean zeroBased,
                     String format)
    {
        this.name = name;
        this.layerType = layerType;
        this.urlArray = urlArray;
        this.zeroBased = zeroBased;
        this.format = format;
    }

    public LayerBean(String name)
    {
        this.name = name;
        this.layerType = null;
        this.urlArray = null;
        this.zeroBased = null;
        this.format = null;
    }

}
