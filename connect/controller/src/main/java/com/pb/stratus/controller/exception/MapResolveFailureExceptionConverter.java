package com.pb.stratus.controller.exception;

import com.pb.stratus.controller.MapResolveFailureException;
import com.pb.stratus.core.exception.DefaultExceptionConverter;
import uk.co.graphdata.utilities.contract.Contract;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MapResolveFailureExceptionConverter extends
        DefaultExceptionConverter
{
    public static final String MAP_HEADER_NAME = "MapName";

    public MapResolveFailureExceptionConverter(int errorCode)
    {
        super(errorCode);
    }

    @Override
    public void sendError(Exception x, HttpServletResponse response)
            throws IOException
    {
        Contract.pre(x instanceof MapResolveFailureException,
                "Exception must be an instance of " +
                        MapResolveFailureException.class.getName());
        MapResolveFailureException umx = (MapResolveFailureException) x;
        response.setHeader(MAP_HEADER_NAME, umx.getMapName());
        super.sendError(x, response);
    }
}
