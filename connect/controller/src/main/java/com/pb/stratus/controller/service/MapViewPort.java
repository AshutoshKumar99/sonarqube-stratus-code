package com.pb.stratus.controller.service;

import com.mapinfo.midev.service.units.v1.DistanceUnit;


public class MapViewPort implements Cloneable {
    private double mapXpos;
    private double mapYpos;
    private String mapSrs;
    private double mapZoom;
    private int mapHeight;
    private int mapWidth;
    private DistanceUnit distanceUnit;
    
    public DistanceUnit getDistanceUnit() 
    {
        return distanceUnit;
    }
    
    public void setDistanceUnit(DistanceUnit distanceUnit) 
    {
        this.distanceUnit = distanceUnit;
    }
    
    public double getMapXpos() 
    {
        return mapXpos;
    }
    
    public void setMapXpos(double mapXpos) 
    {
        this.mapXpos = mapXpos;
    }
    
    public double getMapYpos() 
    {
        return mapYpos;
    }

    public void setMapYpos(double mapYpos) 
    {
        this.mapYpos = mapYpos;
    }
    
    public String getMapSrs() 
    {
        return mapSrs;
    }
    
    public void setMapSrs(String mapSrs) 
    {
        this.mapSrs = mapSrs;
    }
    
    public double getMapZoom() 
    {
        return mapZoom;
    }
    
    public void setMapZoom(double mapZoom) 
    {
        this.mapZoom = mapZoom;
    }
    public int getMapHeight() 
    {
        return mapHeight;
    }

    public void setMapHeight(int mapHeight) 
    {
        this.mapHeight = mapHeight;
    }
    public int getMapWidth() 
    {
        return mapWidth;
    }
    public void setMapWidth(int widthWidth) 
    {
        this.mapWidth = widthWidth;
    }
    
    
}
