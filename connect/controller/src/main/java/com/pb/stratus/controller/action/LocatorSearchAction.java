package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.geometries.v1.Pos;
import com.pb.stratus.controller.geometry.GeometryService;
import com.pb.stratus.controller.geometry.GeometryServiceImpl;
import com.pb.stratus.controller.locator.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * Added this to provide common methods between the address services. (Was
 * formerly in SearchHelper which looked really ugly)
 * 
 * @author Vin Lam
 */
public abstract class LocatorSearchAction extends DataInterchangeFormatControllerAction
{
    protected static final String QUERY_PARAM_NAME = "query";
    protected static final String SOURCE_COORD_SYS_PARAM_NAME = "srs";
    protected static final String TARGET_COORD_SYS_PARAM_NAME = "targetsrs";
    protected GeometryService geometryService;

    @SuppressWarnings("unchecked")
    protected static boolean isSearchRequest(Map parameters)
    {
        return parameters.containsKey(QUERY_PARAM_NAME);
    }


    /**
     * Transforms Locations from one coordsys to another This is done here
     * instead of Geometry Service Resovler because GeometryServiceImpl does
     * not need to work specifically with Locations
     * 
     * @param locations
     *            List of Locations
     * @return transformed list of Locations
     */
    protected List<Location> transformLocations(List<Location> locations,
            String sourceCoordsys, String targetCoordSys)
    {
        List<Point> points = new ArrayList<Point>();
        List<Location> results = new ArrayList<Location>();

        // Create Points Array from locations
        for (Location location : locations)
        {
            Pos pos = new Pos();
            pos.setX(location.getX());
            pos.setY(location.getY());
            Point point = new Point();
            point.setPos(pos);
            point.setSrsName(sourceCoordsys);
            points.add(point);
        }
        List<Geometry> transformedPoints = geometryService.transformPoints(
                points, targetCoordSys);

        // Create new Location Array from Results
        for (int i = 0; i < locations.size(); i++)
        {
            Location currentLocation = locations.get(i);
            Point currentPoint = (Point) transformedPoints.get(i);
            Location newLocation = new Location(currentLocation.getId(),
                    currentLocation.getScore(), currentLocation.getName(),
                    currentPoint.getPos().getX(), currentPoint.getPos()
                            .getY(),targetCoordSys);
            results.add(newLocation);
        }
        return results;
    }
    
}