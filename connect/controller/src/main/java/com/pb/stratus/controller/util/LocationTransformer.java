package com.pb.stratus.controller.util;

import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.geometries.v1.Pos;
import com.pb.stratus.controller.geometry.GeometryService;
import com.pb.stratus.controller.locator.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/27/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class LocationTransformer {

    private GeometryService geometryService;

    public LocationTransformer(GeometryService geometryService){
        this.geometryService = geometryService;
    }

    /**
     * Transforms Locations from one coordsys to another This is done here
     * instead of Geometry Service Resovler because GeometryServiceImpl does
     * not need to work specifically with Locations
     *
     * @param locations List of Locations
     * @return transformed list of Locations
     */
    public List<Location> transformLocations(List<Location> locations,
                                                String targetSrs) {
        List<Point> points = new ArrayList<Point>();
        List<Location> results = new ArrayList<Location>();

        // Create Points Array from locations
        for (Location location : locations) {
            Pos pos = new Pos();
            pos.setX(location.getX());
            pos.setY(location.getY());
            Point point = new Point();
            point.setPos(pos);
            point.setSrsName(location.getSrs());
            points.add(point);
        }

        // Transform them
        List<Geometry> transformedPoints = geometryService.transformPoints(
                points, targetSrs);
        // Create new Location Array from Results
        for (int i = 0; i < locations.size(); i++) {
            Location currentLocation = locations.get(i);
            Point currentPoint = (Point) transformedPoints.get(i);
            Location newLocation = new Location(currentLocation.getId(),
                    currentLocation.getScore(), currentLocation.getName(),
                    currentPoint.getPos().getX(), currentPoint.getPos()
                    .getY(), currentPoint.getSrsName());
            results.add(newLocation);
        }
        return results;
    }

}
