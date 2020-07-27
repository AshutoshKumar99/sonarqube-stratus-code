package com.pb.stratus.geojson;


/**
 * 
 * Used to generate GeoJSON
 * 
 * @author Vin Lam
 * 
 */
public abstract class JsonGeometry
{
    protected String type;
    private JsonCoordinateReferenceSystem crs;

    public JsonCoordinateReferenceSystem getCrs()
    {
        return crs;
    }

    public void setCrs(JsonCoordinateReferenceSystem crs)
    {
        this.crs = crs;
    }

    // to be impleneted by Concrete Geometry Class
    public String getType()
    {
        return "";
    }

    protected JsonGeometry()
    {
        this.type = this.getType();
    }

        }
