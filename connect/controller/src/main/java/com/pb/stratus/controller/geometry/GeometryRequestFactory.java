package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.v1.GeometryServiceRequest;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;

/**
 * This factory returns a GeometryServiceRequest which is mapped to the enum
 * MeasurementEnum. This class is supposed to only return the Request objects
 * that are registered in the map.
 * Internally it uses builder to build the Request.
 */
public class GeometryRequestFactory {

    private Map<MeasurementEnum, Class<? extends AbstractMeasuringBuilder>> register;
    private MeasuringDirector measuringDirector;
    private static Logger logger =
            LogManager.getLogger(GeometryRequestFactory.class.getName());

    public GeometryRequestFactory()
    {
        register = new HashMap<MeasurementEnum,
                Class<? extends AbstractMeasuringBuilder>>();
        register.put(MeasurementEnum.AREA, AreaRequestBuilder.class);
        register.put(MeasurementEnum.LENGTH, LengthRequestBuilder.class);
        register.put(MeasurementEnum.PERIMETER, PerimeterRequestBuilder.class);
        measuringDirector = new MeasuringDirector();
    }

    public GeometryServiceRequest getGeometryServiceRequest(String opType,
        String responseSrs, Geometry geometry)  {
        MeasurementEnum measurementType = MeasurementEnum.getMeasurement(opType);
        Class builderClazz = register.get(measurementType);
        if(builderClazz == null)
        {
            throw new IllegalArgumentException("Operation type" + opType +
                    "not supported");
        }
        try {
            AbstractMeasuringBuilder abstractMeasuringBuilder =
                    (AbstractMeasuringBuilder) Class.forName(
                            builderClazz.getName()).newInstance();
            return measuringDirector.build(abstractMeasuringBuilder,
                    responseSrs, geometry);
        } catch (InstantiationException e) {
           logger.error(e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        throw new IllegalStateException("control should never come here");
    }
}
