package com.pb.stratus.geojson;

import com.mapinfo.midev.service.geometries.v1.Pos;

public class JsonPosition
{
    private double x;
    private double y;

    public JsonPosition(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public JsonPosition(Pos position)
    {
        this.x = position.getX();
        this.y = position.getY();
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public Pos toEnvinsa()
    {
        Pos result = new Pos();
        result.setX(x);
        result.setY(y);
        return result;
    }

}
