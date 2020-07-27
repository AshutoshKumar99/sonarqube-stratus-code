//FIXME this will be reused across other application parts, so the 
//      package name should be something like com.pb.stratus.controller.geometry
package com.pb.stratus.controller.info;

import java.util.ArrayList;

//FIXME comments
/**
 *  Abstract class to represent a Point/MultiPoint/Polygon/MultiPolygone/Curve.
 * @author KA001YE
 *
 */
public abstract class Geometry
{
 private String type;
 private ArrayList<Point> coords= new ArrayList<Point>();

/**
 * @param coords the coords to set
 */
public void setCoords(ArrayList<Point> coords)
{
    this.coords = coords;
}

/**
 * @return the coords
 */
public ArrayList<Point> getCoords()
{
    return coords;
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
 

}
