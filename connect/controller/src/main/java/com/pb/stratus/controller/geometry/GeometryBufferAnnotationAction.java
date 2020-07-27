package com.pb.stratus.controller.geometry;

import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.v1.BufferRequest;
import com.mapinfo.midev.service.geometry.v1.BufferResponse;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;
import com.mapinfo.midev.service.units.v1.Distance;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.action.DataInterchangeFormatControllerAction;
import com.pb.stratus.controller.json.geojson.GeoJsonParser;
import com.pb.stratus.core.common.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GeometryBufferAnnotationAction extends DataInterchangeFormatControllerAction
{
    private static final Logger logger = LogManager.getLogger(GeometryBufferAnnotationAction.class);

    private GeometryService geometryService;

    private String GEOMETRY = "geometry";

    private String SRS = "srs";

    private String RESOLUTION = "resolution";

    private String VALUE = "value";

    private String UNIT = "unit";

    public GeometryBufferAnnotationAction(GeometryService geometryService)
    {
        this.geometryService = geometryService;
    }

    protected Object createObject(HttpServletRequest request) 
            throws ServletException, IOException
    {
        Geometry geometry = getGeometry(request);
        String resolution = getResolution(request);

        BufferRequest bufferRequest = new BufferRequest();
        bufferRequest.setDistance(getDistance(request));
        bufferRequest.setGeometry(geometry);
        bufferRequest.setResolution(Integer.parseInt(resolution));

        try
        {
            BufferResponse bufferResponse = geometryService.getBufferedGeometry(bufferRequest);
            if(bufferResponse != null)
            {
                return bufferResponse.getGeometry();
            }
        }
        catch (ServiceException exception)
        {
            logger.debug(exception);
            Map map = new HashMap<String,String>();
            map.put("error", "true");
            map.put("exception", exception.getClass().getName());
            map.put("message", exception.getMessage());
            map.put("buffer", getBufferValue(request));
            return map;
        }
        return null;
    }

    protected Geometry getGeometry(HttpServletRequest request)
    {
        GeoJsonParser parser = new GeoJsonParser(getSrs(request));
        String geoJsonGeometry = getGeoJsonGeometry(request);
        return parser.parseGeometry(geoJsonGeometry);
    }

    protected String getGeoJsonGeometry(HttpServletRequest request)
    {
        String geoJsonString = request.getParameter("geometry");
        Preconditions.checkNotNull(!StringUtils.isBlank(geoJsonString), "geometry cannot be empty");
        return geoJsonString;
    }

    protected String getSrs(HttpServletRequest request)
    {
        String srs = request.getParameter("srs");
        Preconditions.checkState(!StringUtils.isBlank(srs), "srs cannot be blank");
        return srs;
    }

    protected String getBufferValue(HttpServletRequest request)
    {
        String bufferValue = request.getParameter(VALUE);
        Preconditions.checkState(!StringUtils.isBlank(bufferValue), "bufferValue cannot be blank");
        return bufferValue;
    }

    protected String getResolution(HttpServletRequest request)
    {
        String resolution = request.getParameter(RESOLUTION);
        Preconditions.checkState(!StringUtils.isBlank(resolution), "resolution cannot be blank");
        return resolution;
    }

    protected String getBufferUnit(HttpServletRequest request)
    {
        String bufferUnit = request.getParameter(UNIT);
        Preconditions.checkState(!StringUtils.isBlank(bufferUnit), "bufferUnit cannot be blank");
        return bufferUnit;
    }

    protected Distance getDistance(HttpServletRequest request)
    {
        Distance distance = new Distance();
        distance.setValue(Double.parseDouble(getBufferValue(request)));
        distance.setUom(DistanceUnit.valueOf(getBufferUnit(request)));
        return distance;
    }
}
