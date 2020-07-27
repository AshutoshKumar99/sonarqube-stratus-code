package com.pb.stratus.controller.service;

import com.mapinfo.midev.service.geometries.v1.Point;
import uk.co.graphdata.utilities.contract.Contract;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class SearchAtPointParams extends SearchParams 
{
    
    private Point point;
    
    private Map<String, Double> tablesAndMargins 
            = new LinkedHashMap<String, Double>();

    private Map<String, String> tablesAndSql
            = new LinkedHashMap<String, String>();

    public Collection<String> getTables() 
    {
        return tablesAndMargins.keySet();
    }
    
    public void addTable(String tableName, double bufferMargin)
    {
        tablesAndMargins.put(tableName, bufferMargin);
    }

    public void addQuerySQL(String tableName, String sql)
    {
        tablesAndSql.put(tableName, sql);
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }
    
    public double getBufferMargin(String tableName)
    {
        Contract.pre(tablesAndMargins.containsKey(tableName), 
                "TableName must have been added before");
        return tablesAndMargins.get(tableName);
    }

    public String getQuerySQL(String tableName)
    {
        Contract.pre(tablesAndSql.containsKey(tableName),
                "TableName must have been added before");
        return tablesAndSql.get(tableName);
    }
}
