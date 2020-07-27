package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.v1.GeometryServiceRequest;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.action.DataInterchangeFormatControllerAction;
import com.pb.stratus.controller.json.geojson.GeoJsonParser;
import com.pb.stratus.core.common.Preconditions;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class GeometryMeasureAction extends DataInterchangeFormatControllerAction
{

    private String GEOMETRY = "geometry";

    private String SRS = "srs";

    private String OPERATION_TYPE = "opType";

    private GeometryRequestFactory geometryRequestFactory;

    private GeometryMeasureExecutorAdapter geometryMeasureExecutorAdapter;

    public GeometryMeasureAction(GeometryMeasureExecutorAdapter geometryMeasureExecutorAdapter,
            GeometryRequestFactory geometryRequestFactory)
    {
        this.geometryMeasureExecutorAdapter = geometryMeasureExecutorAdapter;
        this.geometryRequestFactory = geometryRequestFactory;
    }


    @Override
    protected Object createObject(HttpServletRequest request)
            throws ServletException, IOException, InvalidGazetteerException
    {
        Geometry geometry = getGeometry(request);
        String opType = getOperationType(request);
        String responseSrs  = getSrs(request);
        GeometryServiceRequest geometryServiceRequest  =
                geometryRequestFactory.getGeometryServiceRequest(opType,
                        responseSrs, geometry);
        try {
            return this.geometryMeasureExecutorAdapter.getMeasurementOfGeometry(
                    geometryServiceRequest);
        } catch (ServiceException e) {
            throw new IllegalStateException("there was an error from geometry service", e);
        }
    }

    protected Geometry getGeometry(HttpServletRequest request)
    {
        GeoJsonParser parser = new GeoJsonParser(getSrs(request));
        String geoJsonGeometry = getGeoJsonGeometry(request);
        return parser.parseGeometry(geoJsonGeometry);
    }

    protected String getOperationType(HttpServletRequest request)
    {
        String opType = request.getParameter(OPERATION_TYPE);
        Preconditions.checkState(!StringUtils.isBlank(opType),
                "opType cannot be blank");
        return opType;
    }


    protected String getGeoJsonGeometry(HttpServletRequest request)
    {
        String geoJsonString = request.getParameter(GEOMETRY);
        Preconditions.checkNotNull(!StringUtils.isBlank(geoJsonString), "geometry cannot be empty");
        return geoJsonString;
    }


    protected String getSrs(HttpServletRequest request)
    {
        String srs = request.getParameter(SRS);
        Preconditions.checkState(!StringUtils.isBlank(srs), "srs cannot be blank");
        return srs;
    }
}
