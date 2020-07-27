package com.pb.stratus.controller.info;

/**
 * Created with IntelliJ IDEA.
 * User: SU019CH
 * Date: 9/20/13
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class LayerInfoBean {

    private String layerName;
    private String layerPath;
    private float layerOpacity;
    private int layerIndex;
    private boolean layerVisibility;
    private String layerMapPath;
    private String layerLabelPath;

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public float getLayerOpacity() {
        return layerOpacity;
    }

    public void setLayerOpacity(float layerOpacity) {
        this.layerOpacity = layerOpacity;
    }

    public int getLayerIndex() {
        return layerIndex;
    }

    public void setLayerIndex(int layerIndex) {
        this.layerIndex = layerIndex;
    }

    public boolean isLayerVisibility() {
        return layerVisibility;
    }

    public void setLayerVisibility(boolean layerVisibility) {
        this.layerVisibility = layerVisibility;
    }

    public String getLayerMapPath() {
        return layerMapPath;
    }

    public void setLayerMapPath(String layerMapPath) {
        this.layerMapPath = layerMapPath;
    }

    public String getLayerLabelPath() {
        return layerLabelPath;
    }

    public void setLayerLabelPath(String layerLabelPath) {
        this.layerLabelPath = layerLabelPath;
    }

    public String getLayerPath() {
        return layerPath;
    }

    public void setLayerPath(String layerPath) {
        this.layerPath = layerPath;
    }
}
