package com.pb.stratus.geojson;

import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @author Vin Lam
 * 
 * 
 *         "crs": { 
 *             "type": "name", 
 *             "properties": { 
 *                 "name": "EPSG:27700" 
 *             } 
 *         }
 * 
 * 
 */
public class JsonCoordinateReferenceSystem
{
    private Map<String, String> properties = new TreeMap<String, String>();
    private String type;

    public JsonCoordinateReferenceSystem(String name)
    {
        properties.put("name", name);
        this.type = "name";
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }

    public void setName(String name)
    {
        this.properties.put("name", name);
    }

}
