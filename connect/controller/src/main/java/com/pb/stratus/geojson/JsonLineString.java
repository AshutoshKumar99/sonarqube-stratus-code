package com.pb.stratus.geojson;

import com.mapinfo.midev.service.geometries.v1.LineString;
import com.mapinfo.midev.service.geometries.v1.Pos;

/**
 * 
 * Represents a Line String to be serialsed as GeoJSON
 * 
 * @author Vin Lam
 *
 */
public class JsonLineString extends JsonGeometry
{

    Double[][] coordinates;
    public JsonLineString()
    {        
    }
    public JsonLineString(Double[][] coordinates)
    {
        setCoordinates(coordinates);
    }
    public JsonLineString(LineString lineString)
    {
        
    }
    public Double[][] getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates(Double[][] coordinates)
    {
        this.coordinates = coordinates;
    }
    public void setCoordinates(LineString lineString)
    {
        int i=0;
        for(Pos pos : lineString.getPos())
        {
            coordinates[i][0] = pos.getX(); 
            coordinates[i][1] = pos.getY();
            i++;
        }
    }

    public String getType()
    {
        return "LineString";
    }
}
