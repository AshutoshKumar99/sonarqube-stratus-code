package com.pb.stratus.controller.geometry;

import com.mapinfo.midev.service.geometries.v1.Pos;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;
import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.action.DataInterchangeFormatControllerAction;
import com.pb.stratus.geojson.JsonPoint;
import com.pb.stratus.geojson.JsonPosition;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static com.pb.stratus.controller.util.TypeConversionUtils.getDoubleValue;
import static com.pb.stratus.controller.util.TypeConversionUtils.getStringValue;

public class GeometryAction extends DataInterchangeFormatControllerAction

{
    private static final Logger logger = LogManager.getLogger(GeometryAction.class);

    protected static final String SOURCE_COORD_SYS_PARAM_NAME = "sourcesrs";
    protected static final String TARGET_COORD_SYS_PARAM_NAME = "targetsrs";
    protected static final String X_PARAM_NAME = "x";
    protected static final String Y_PARAM_NAME = "y";

    private GeometryService geometryService;
    
    public GeometryAction(GeometryService geometryService)
    {
        this.geometryService = geometryService;
    }

    protected Object createObject(HttpServletRequest request) 
            throws ServletException, IOException
    {

        Map parameters = request.getParameterMap();

        String sourceCoordSys = getStringValue(SOURCE_COORD_SYS_PARAM_NAME,
                parameters, null);
        String targetCoordSys = getStringValue(TARGET_COORD_SYS_PARAM_NAME,
                parameters, null);
        Double x = getDoubleValue(X_PARAM_NAME, parameters, null);
        Double y = getDoubleValue(Y_PARAM_NAME, parameters, null);

        if (x == null || y == null || sourceCoordSys == null
                || targetCoordSys == null)
        {
            throw new IllegalRequestException(
                    "Missing Parameters for geometry/transform");
        }
        try
        {
            JsonPosition position = new JsonPosition(x, y);
            Pos result = geometryService.transformPos(position.toEnvinsa(),
                    sourceCoordSys, targetCoordSys);
            return new JsonPoint(result,targetCoordSys);
        }
        catch (ServiceException exception)
        {
            logger.debug(exception);
            throw new IllegalRequestException(exception);

        }
    }
}
