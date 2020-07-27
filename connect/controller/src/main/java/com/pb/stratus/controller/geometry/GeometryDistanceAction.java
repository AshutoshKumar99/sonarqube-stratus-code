package com.pb.stratus.controller.geometry;

import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.units.v1.Distance;
import com.pb.stratus.controller.action.DataInterchangeFormatControllerAction;
import com.pb.stratus.controller.json.geojson.GeoJsonParser;
import com.pb.stratus.core.common.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeometryDistanceAction extends DataInterchangeFormatControllerAction
{
    private static final Logger logger = LogManager.getLogger(GeometryDistanceAction.class);

    private GeometryService geometryService;

    public GeometryDistanceAction(GeometryService geometryService)
    {
        this.geometryService = geometryService;
    }

    protected Object createObject(HttpServletRequest request) 
            throws ServletException, IOException
    {
        String srs = getSrs(request) ;
        GeoJsonParser parser = new GeoJsonParser(srs);
        Geometry point1 = parser.parseGeometry(request.getParameter("point1"));
        Geometry point2 =  parser.parseGeometry(request.getParameter("point2"));
        List<Point> points = new ArrayList<Point>();
        points.add((Point)point1);
        points.add((Point)point2);
        Distance geodesicDistance = geometryService.getDistanceBetweenTwoPoints(points);
        if(geodesicDistance != null){
            return  geodesicDistance.getValue();
        }
        return null;

    }

    protected String getSrs(HttpServletRequest request)
    {
        String srs = request.getParameter("srs");
        Preconditions.checkState(!StringUtils.isBlank(srs), "srs cannot be blank");
        return srs;
    }
}
