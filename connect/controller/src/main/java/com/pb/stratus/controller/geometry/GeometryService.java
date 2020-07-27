package com.pb.stratus.controller.geometry;

import com.mapinfo.midev.service.geometries.v1.*;
import com.mapinfo.midev.service.geometry.v1.*;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;
import com.mapinfo.midev.service.units.v1.Distance;
import com.pb.stratus.controller.info.Feature;
import com.pb.stratus.controller.info.FeatureCollection;

import java.util.List;


/**
 * Interface for accessing services, supported
 * by Midev <code>GeometryService</code> web service.  
 * 
 * @author ku002va
 *
 */
public interface GeometryService 
{

    /**
     * Transforms a geographic position from one coordinate system(source) 
     * to another(target).
     * 
     * @param position Position Coordinates  in source coordinate system.
     * @param sourceCoordsys the coordinate system the position is in 
     * before transform.
     * @param targetCoordSys the coordinate system the position should be 
     * in after transform.
     * @return position coordinates in targetCoordSys coordinate system.
     * @throws com.mapinfo.midev.service.geometry.ws.v1.ServiceException
     */
    Pos transformPos(Pos position, String sourceCoordsys, String targetCoordSys)
       throws ServiceException;
    /**
     * Transforms a collection of <code>Point</code> to a target 
     * coordinate system.
     * 
     * @param points a collection of Point objects, to be transformed to a 
     * target coordinate system.
     * @param coordsys target coordinate system
     * @return transformed collection of Point objects.
     */
    List<Geometry> transformPoints(List<Point> points, String coordsys);

    /**
     * Transforms a <code>Geometry</code> to a specified coordinate system.
     * 
     * @param geometry a Geometry object be transformed to a target coordinate system.
     * @param coordsys target coordinate system
     * @return transformed Geometry Object.
     */
    Geometry transformGeometry(Geometry geometry, String coordsys) 
      throws ServiceException;

    /**
     * Transforms a collection of <code>Geometry</code> objects to a 
     * specified coordinate system.
     * 
     * @param geometryList a collection of <code>Geometry</code> objects be transformed 
     * to a target coordinate system.
     * @param targetSrs target coordinate system
     * @return tranformed collection of <code>Geometry</code> Objects.
     */
    List<Geometry> transformGeometries(List<Geometry> geometryList, String targetSrs) 
      throws ServiceException;

    
    /**
     * Transforms a collection of <code>Feature</code> objects to a 
     * specified coordinate system.
     * 
     * @param features a collection of <code>Feature</code> objects be transformed 
     * to a target coordinate system.
     * @param targetSrs target coordinate system
     * @return transformed collection of <code>Feature</code> Objects.
     */
    FeatureCollection transformFeatureCollection(List<Feature> features,
                                                 String targetSrs);

    /**
     * Used to get distance between two points(in meters) 
     * @param points - list of points
     * @return - Distance object
     * @throws com.mapinfo.midev.service.geometry.ws.v1.ServiceException
     */
    Distance getDistanceBetweenTwoPoints(List<Point> points);

    /**
     * Used to get Area of geometry
     * @param geometryServiceRequest -(geometry,computationType, areaUnit)
     * @return - Measure object - (measurement type,measurement value, measurement unit)
     * @throws com.mapinfo.midev.service.geometry.ws.v1.ServiceException
     */
    Measure getAreaOfTheGeometry(AreaRequest geometryServiceRequest) throws ServiceException;

    /**
     * Used to get Length of line geometry
     * @param geometryServiceRequest -(geometry,computationType, lengthUnit)
     * @return - Measure object - (measurement type,measurement value, measurement unit)
     * @throws com.mapinfo.midev.service.geometry.ws.v1.ServiceException
     */
    Measure getLengthOfTheGeometry(LengthRequest geometryServiceRequest) throws ServiceException;

    /**
     * Used to get Perimeter of geometry
     * @param geometryServiceRequest -(geometry,computationType, perimeterUnit)
     * @return - Measure object - (measurement type,measurement value, measurement unit)
     * @throws com.mapinfo.midev.service.geometry.ws.v1.ServiceException
     */
    Measure getPerimeterOfTheGeometry(PerimeterRequest geometryServiceRequest) throws ServiceException;

    BufferResponse getBufferedGeometry(BufferRequest bufferRequest) throws ServiceException;
}
