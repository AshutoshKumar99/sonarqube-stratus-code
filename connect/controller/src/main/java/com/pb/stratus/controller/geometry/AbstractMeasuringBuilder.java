package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.v1.ComputationType;
import com.mapinfo.midev.service.geometry.v1.GeometryServiceRequest;
import com.mapinfo.midev.service.units.v1.AreaUnit;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import com.pb.stratus.controller.i18n.LocaleResolver;

import java.util.Locale;

/**
 * Builder to support making of Request object for geometry call. All supported
 * methods of subclasses should be put here. the methods that are not relevant for
 * a concrete builder should be left empty and will have no side effect in the way
 * the objects get created.
 * If new methods are added to the class then they should be registered in the
 * Measuring director class.
 */
public abstract class AbstractMeasuringBuilder {
    protected GeometryServiceRequest geometryServiceRequest;

    /**
     * Default constructor.
     */
    public AbstractMeasuringBuilder()
    {

    }

    public void setId(String id)
    {
        geometryServiceRequest.setId(id);
    }

    public void setLocale()
    {
        Locale locale = LocaleResolver.getLocale();
        geometryServiceRequest.setLocale(locale.getLanguage() + "_" +
                locale.getCountry());
    }

    public void setResponseSrsName(String responseSrs)
    {
        geometryServiceRequest.setResponseSrsName(responseSrs);
    }

    /**
     * Set Geometry in the GeometryServiceRequest if supported
     * @param geometry
     */
    public abstract void setGeometry(Geometry geometry);

    /**
     * Set AreaUnit in the GeometryServiceRequest if supported
     * @param areaUnit
     */
    public abstract void setAreaUnit(AreaUnit areaUnit);

    /**
     * Set DistanceUnit in the GeometryServiceRequest if supported
     * @param lengthUnit
     */
    public abstract void setLengthUnit(DistanceUnit lengthUnit);

    /**
     * This method may be used to mark the concrete builder
     * @return
     */
    public abstract MeasurementEnum getMeasurementType();

    /**
     * Set ComputationType in the GeometryServiceRequest if supported
     * @param computationType
     */
    public abstract void setComputationType(ComputationType computationType);

    /**
     * This method is called by the director before any other method of this
     * or sub-class. The sub classes should instantiate the type GeometryServiceRequest
     * in this method.
     */
    protected abstract void createGeometryServiceRequest();

    /**
     * Returns the GeometryServiceRequest
     * @return
     */
    public GeometryServiceRequest getGeometryServiceRequest()
    {
        return this.geometryServiceRequest;
    }
}
