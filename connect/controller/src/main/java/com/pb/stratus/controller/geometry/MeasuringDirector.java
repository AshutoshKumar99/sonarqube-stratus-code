package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.v1.ComputationType;
import com.mapinfo.midev.service.geometry.v1.GeometryServiceRequest;
import com.mapinfo.midev.service.units.v1.AreaUnit;
import com.mapinfo.midev.service.units.v1.DistanceUnit;

public class MeasuringDirector {

    public GeometryServiceRequest build(AbstractMeasuringBuilder abstractMeasuringBuilder,
        String responseSrs, Geometry geometry)
    {
       return this.build(abstractMeasuringBuilder, null, responseSrs, geometry);
    }

    public GeometryServiceRequest build(AbstractMeasuringBuilder abstractMeasuringBuilder,
        String id, String responseSrs, Geometry geometry)
    {
        return this.build(abstractMeasuringBuilder, id, responseSrs, geometry,
                null, null);
    }

    public GeometryServiceRequest build(AbstractMeasuringBuilder abstractMeasuringBuilder,
        String id, String responseSrs, Geometry geometry, DistanceUnit lengthUnit)
    {
        return this.build(abstractMeasuringBuilder, id, responseSrs, geometry,
                null, lengthUnit);
    }

    public GeometryServiceRequest build(AbstractMeasuringBuilder abstractMeasuringBuilder,
        String id, String responseSrs, Geometry geometry, AreaUnit areaUnit)
    {
        return this.build(abstractMeasuringBuilder, id, responseSrs, geometry,
                areaUnit, null);

    }

    public GeometryServiceRequest build(AbstractMeasuringBuilder abstractMeasuringBuilder,
        String id, String responseSrs, Geometry geometry, AreaUnit areaUnit,
        DistanceUnit lengthUnit)
    {
        return this.build(abstractMeasuringBuilder, id, responseSrs, geometry, areaUnit,
                lengthUnit, null);
    }



    public GeometryServiceRequest build(AbstractMeasuringBuilder abstractMeasuringBuilder,
        String id, String responseSrs, Geometry geometry, AreaUnit areaUnit,
        DistanceUnit lengthUnit, ComputationType computationType)
    {
        abstractMeasuringBuilder.createGeometryServiceRequest();
        abstractMeasuringBuilder.setId(id);
        abstractMeasuringBuilder.setLocale();
        abstractMeasuringBuilder.setResponseSrsName(responseSrs);
        abstractMeasuringBuilder.setGeometry(geometry);
        abstractMeasuringBuilder.setAreaUnit(areaUnit);
        abstractMeasuringBuilder.setLengthUnit(lengthUnit);
        abstractMeasuringBuilder.setComputationType(computationType);
        return abstractMeasuringBuilder.getGeometryServiceRequest();
    }
}
