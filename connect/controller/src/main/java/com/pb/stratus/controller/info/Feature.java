package com.pb.stratus.controller.info;

import com.mapinfo.midev.service.geometries.v1.Geometry;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

//FIXME comments
/**
 * Feature represent a row in the table.
 * @author KA001YE
 * 
 */
public class Feature
{
    private String type = "Feature";
    
    private Geometry geometry;

    //@AW001AG Unique Id of Feature represented by MI_KEY
    // (This is a psuedo key which represents either primary key of
    // table or unique id in tab file )on spatial side
    private String id;
    
    Map<String, Object> properties = new LinkedHashMap<String, Object>();
    
    /**
     * constructor for Feature object.
     * 
     * @param type
     * @param geometry
     * @param fields
     */
    public Feature(Geometry geometry, Map<String, Object> properties)
    {

    	applyBasicFeatureInfo(geometry,properties);
    }
    
    /**
     * This constructor for the Feature object includes an optional
     * id string which is inserted into the properties map for the final
     * feature object.
     * 
     * @param geometry
     * @param properties
     * @param idstring
     */
    public Feature(Geometry geometry, Map<String, Object> properties, String idstring)
    {
    	this.properties.put("stratusid", idstring);
    	applyBasicFeatureInfo(geometry,properties);
    	
    }
    
    /**
     * Common constructor helper method.
     * 
     * @param geometry
     * @param properties
     */
    private void applyBasicFeatureInfo(Geometry geometry, Map<String, Object> properties)
    {
        this.setType(type);
        this.setId((String)properties.get("id"));
        properties.remove("id");
        this.setGeometry(geometry);
        this.properties.putAll(properties);
    }

    /**
     * @param type the type to set
     */
    public void setType(String type)
    {
        this.type = type;
    }
    /**
     * @return the type
     */
    public String getType()
    {
        return type;
    }
    
    public Map<String, Object> getProperties()
    {
    	return Collections.unmodifiableMap(properties);
    }
    
    /**
     * @return the geometry
     */
    public Geometry getGeometry()
    {
        return geometry;
    }

    /**
     * 
     * @param geometry
     */
    public void setGeometry(Geometry geometry)
    {
    	this.geometry = geometry;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
