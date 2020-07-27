package com.pb.stratus.controller.service;

import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.units.v1.Distance;
import com.mapinfo.midev.service.units.v1.DistanceUnit;

public class SearchNearestParams extends SearchParams
{
    
    public static final String DISTANCE_ATTR = "Distance";
    
    private String table;
    
    private Point point;
    
    private Distance distance;
    
    private DistanceUnit returnedDistanceUnit;
    
    private int maxResults = 1;
    
    private String returnedDistanceAttributeName = DISTANCE_ATTR;

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) 
    {
        this.point = point;
    }

    public Distance getDistance() {
        return distance;
    }

    public void setDistanceUnit(DistanceUnit unit) 
    {
        if (distance == null)
        {
            distance = new Distance();
        }
        distance.setUom(unit);
    }
    
    public void setDistanceValue(double distanceValue)
    {
        if (distance == null)
        {
            distance = new Distance();
        }
        distance.setValue(distanceValue);
        
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
    
    public DistanceUnit getReturnedDistanceUnit() {
        return returnedDistanceUnit;
    }
    
    public void setReturnedDistanceUnit(DistanceUnit returnedDistanceUnit) 
    {
        this.returnedDistanceUnit = returnedDistanceUnit;
    }

    public String getReturnedDistanceAttributeName()
    {
        return returnedDistanceAttributeName;
    }
    
}
