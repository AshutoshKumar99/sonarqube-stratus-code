package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometry.v1.AreaRequest;
import com.mapinfo.midev.service.geometry.v1.GeometryServiceRequest;
import com.mapinfo.midev.service.geometry.v1.LengthRequest;
import com.mapinfo.midev.service.geometry.v1.PerimeterRequest;
import com.mapinfo.midev.service.geometry.ws.v1.GeometryServiceInterface;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;
import com.pb.stratus.core.configuration.ControllerConfiguration;

/**
 * Adapter class for GeometryService to GeometryMeasureExecutor. Internally
 * we delegate it to the GeometryService
 */
public class GeometryMeasureExecutorAdapter extends GeometryServiceImpl
        implements GeometryMeasureExecutor{

    public GeometryMeasureExecutorAdapter(ControllerConfiguration config, GeometryServiceInterface service)
    {
        super(service, config);
    }

    /**
     * For JUNIT only
     */
    public GeometryMeasureExecutorAdapter()
    {
        super();
    }


    @Override
    public Measure getMeasurementOfGeometry(GeometryServiceRequest
            geometryServiceRequest) throws ServiceException {
        Measure measure= null;
        if(geometryServiceRequest instanceof AreaRequest)
        {
            measure = getAreaOfTheGeometry((AreaRequest)geometryServiceRequest);
        }
        if(geometryServiceRequest instanceof LengthRequest)
        {
            measure = getLengthOfTheGeometry((LengthRequest)geometryServiceRequest);
        }
        if(geometryServiceRequest instanceof PerimeterRequest)
        {
            measure = getPerimeterOfTheGeometry((PerimeterRequest)geometryServiceRequest);
        }
        return measure;
    }
}
