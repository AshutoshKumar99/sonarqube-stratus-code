package com.pb.stratus.geojson;

import com.mapinfo.midev.service.geometries.v1.Pos;

/**
 * 
 * Represents a Point to be serialised as GeoJSON
 * 
 * @author Vin Lam
 * 
 */
public class JsonPoint extends JsonGeometry
{
    private Double[] coordinates = new Double[2];

    public JsonPoint(double x, double y)
    {
        this.coordinates[0] = x;
        this.coordinates[1] = y;
    }

    public JsonPoint(Pos position)
    {
        this.coordinates[0] = position.getX();
        this.coordinates[1] = position.getY();
    }

    public JsonPoint(Pos position, String crs)
    {
        this.coordinates[0] = position.getX();
        this.coordinates[1] = position.getY();
        if (crs != null)
        {
            this.setCrs(new JsonCoordinateReferenceSystem(crs));
        }
    }

    public Double[] getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates(Double[] coordinates)
    {
        this.coordinates = coordinates;
    }

    public String getType()
    {
        return "Point";
    }
}
