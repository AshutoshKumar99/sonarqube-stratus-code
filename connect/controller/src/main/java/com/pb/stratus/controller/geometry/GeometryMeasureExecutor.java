package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometry.v1.GeometryServiceRequest;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;

public interface GeometryMeasureExecutor {

    Measure getMeasurementOfGeometry(GeometryServiceRequest geometryServiceRequest)
            throws ServiceException;
}
