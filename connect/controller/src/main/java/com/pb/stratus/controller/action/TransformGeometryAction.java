package com.pb.stratus.controller.action;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.geometry.GeometryCollection;
import com.pb.stratus.controller.geometry.GeometryService;
import com.pb.stratus.controller.json.geojson.GeoJsonParser;
import com.pb.stratus.core.common.Preconditions;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransformGeometryAction extends DataInterchangeFormatControllerAction{

    private GeometryService geometryService;

    public TransformGeometryAction(GeometryService geometryService)
    {
        this.geometryService = geometryService;
    }

    @Override
    protected Object createObject(HttpServletRequest request)
            throws ServletException, IOException, InvalidGazetteerException {
        String[] geoJSonGeometries = getGeoJsonGeometries(request);
        List<Geometry> geometries =
                getSSGeometriesFromGeoJsonGeometries(geoJSonGeometries,
                getSourceSrs(request));
        List<Geometry> transformedGeometries =  null;
        try {
            transformedGeometries = this.geometryService.transformGeometries(
                    geometries, getTargetSrs(request));
        } catch (ServiceException e) {
            return new GeometryCollection(Collections.EMPTY_LIST);
        }
        return new GeometryCollection(transformedGeometries);
    }

    private String[] getGeoJsonGeometries(HttpServletRequest request)
    {
        String[] geometries = request.getParameterValues("geometries");
        Preconditions.checkState(geometries != null && geometries.length != 0,
                "geometries cannot be null or empty");
        return geometries;
    }

    /**
     * It is protected for testing purpose only.
     * @param geoJsonGeometries
     * @param sourceSrs
     * @return
     */
    protected List<Geometry> getSSGeometriesFromGeoJsonGeometries(String[]
           geoJsonGeometries, String sourceSrs)
    {
        List<Geometry> geometries = new ArrayList<Geometry>();
        GeoJsonParser parser = new GeoJsonParser(sourceSrs);
        for(String geoJSonGeometry : geoJsonGeometries)
        {
            geometries.add(parser.parseGeometry(geoJSonGeometry));
        }
        return  geometries;
    }

    private String getSourceSrs(HttpServletRequest request)
    {
        String sourceSrs = request.getParameter("sourceSrs");
        Preconditions.checkState(!StringUtils.isBlank(sourceSrs),
                "sourceSrs cannot be blank");
        return sourceSrs;
    }

    private String getTargetSrs(HttpServletRequest request)
    {
        String targetSrs = request.getParameter("targetSrs");
        Preconditions.checkState(!StringUtils.isBlank(targetSrs),
                "targetSrs cannot be empty");
        return targetSrs;
    }
}
