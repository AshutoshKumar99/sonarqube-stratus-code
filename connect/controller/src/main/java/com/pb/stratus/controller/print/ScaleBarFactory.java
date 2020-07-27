package com.pb.stratus.controller.print;

import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.geometries.v1.Pos;
import com.pb.stratus.controller.geometry.GeometryService;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

/**
 * Creates new ScaleBars 
 */
public class ScaleBarFactory
{
    
    private static final double MIN_WIDTH_IN_CM = 4;
    private GeometryService geometryService;
    
    public ScaleBarFactory(GeometryService geometryService)
    {
        this.geometryService = geometryService;
    }
    
    /**
     * Creates a ScaleBar that reflects the average scale of the map region
     * within the given bounds. The mapImageSize and resolution parameters
     * indicate the size of the map the scale bar is intended for. 
     * The metric parameter indicates whether or not to use metric units as 
     * opposed to imperial units.
     */
    public ScaleBar  createScaleBar(BoundingBox bounds, Dimension mapImageSize,
            Resolution resolution, boolean metric)
    {
        List<Point> line = calculateOnePixelLineInCenterOfMap(bounds, 
                mapImageSize);
        double pixelWidthInMetersOnEarth = calculateWidthInMeters(line);
        double pixelWidthInMetersOnMap = resolution.calculatePixelWidthInCm(1) 
                / 100;
        double scale = pixelWidthInMetersOnMap / pixelWidthInMetersOnEarth;
        Distance scaleBarWidth = calculateBestScaleBarWidth(
                pixelWidthInMetersOnEarth,
                resolution, mapImageSize.width, metric);
        return new ScaleBar(scaleBarWidth, scale);
    }
    
    private List<Point> calculateOnePixelLineInCenterOfMap(BoundingBox bounds,
            Dimension mapImageSize)
    {
        Point2D center = bounds.getCenter();
        double widthOfOnePixelInProjectedUnits 
                = bounds.getWidth() / mapImageSize.width;
        Point w = new Point();
        Pos wPos = new Pos();
        wPos.setX(center.getX());
        wPos.setY(center.getY());
        w.setPos(wPos);
        w.setSrsName(bounds.getSrs());
        Point e = new Point();
        Pos ePos = new Pos();
        ePos.setX(center.getX() + widthOfOnePixelInProjectedUnits);
        ePos.setY(center.getY());
        e.setPos(ePos);
        e.setSrsName(bounds.getSrs());
        return Arrays.asList(w, e);
    }

    private double calculateWidthInMeters(List<Point> line)
    {
        com.mapinfo.midev.service.units.v1.Distance d = geometryService.getDistanceBetweenTwoPoints(line);

        if(d != null){
            System.out.println(d.getUom().value());
            return d.getValue();
        }

        return 0;
    }

    private Distance calculateBestScaleBarWidth(
            double pixelWidthInMetersOnEarth,
            Resolution resolution, int width, boolean metric)
    {
        double fullWidthInMetersOnEarth = pixelWidthInMetersOnEarth * width;
        double fullWidthInCmOnMap = resolution.calculatePixelWidthInCm(width);
        double factor =  Math.max(MIN_WIDTH_IN_CM, fullWidthInCmOnMap / 4) 
                / fullWidthInCmOnMap;
        return calculateClosestRoundDistance(fullWidthInMetersOnEarth * factor, 
                metric);
    }

    private Distance calculateClosestRoundDistance(double widthInMeters,
            boolean metric)
    {
        Distance d = new Distance(DistanceUnit.M, widthInMeters);
        if (metric)
        {
            d = d.getClosestRoundDistance();
            if (d.isGreaterOrEqual(new Distance(DistanceUnit.KM, 1)))
            {
                d = d.convert(DistanceUnit.KM);
            }
        }
        else
        {
            if (d.isGreaterOrEqual(new Distance(DistanceUnit.MI, 1)))
            {
                d = d.convert(DistanceUnit.MI);
            }
            else
            {
                d = d.convert(DistanceUnit.FT);
            }
            d = d.getClosestRoundDistance();
        }
        return d;
    }

}
