package com.pb.stratus.controller.geometry;

import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.v1.AreaRequest;
import com.mapinfo.midev.service.geometry.v1.ComputationType;
import com.mapinfo.midev.service.units.v1.AreaUnit;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import com.pb.stratus.core.common.Preconditions;

/**
 * This is a concrete implementation of the AreaRequest.
 */
public class AreaRequestBuilder extends AbstractMeasuringBuilder{

    public AreaRequestBuilder()
    {
        super();
    }

    @Override
    public void setGeometry(Geometry geometry) {
        Preconditions.checkNotNull(geometry, "geometry cannot be null");
        ((AreaRequest) this.geometryServiceRequest).setGeometry(geometry);
    }

    /**
     * This method is for testing purpose only.
     * @return
     */
    public Geometry getGeometry()
    {
        return ((AreaRequest) this.geometryServiceRequest).getGeometry();
    }

    @Override
    public void setAreaUnit(AreaUnit areaUnit) {
        if(areaUnit == null)
        {
            areaUnit = AreaUnit.SQUARE_METER;
        }
        ((AreaRequest)this.geometryServiceRequest).setAreaUnit(areaUnit);
    }

    /**
     * This method is for testing purpose only.
     * @return
     */
    public AreaUnit getAreaUnit()
    {
        return ((AreaRequest)this.geometryServiceRequest).getAreaUnit();
    }

    @Override
    public void setLengthUnit(DistanceUnit lengthUnit) {
        // not supported
    }

    @Override
    public MeasurementEnum getMeasurementType() {
        return MeasurementEnum.AREA;
    }

    @Override
    public void setComputationType(ComputationType computationType) {
        if(computationType == null)
        {
            computationType  =  ComputationType.SPHERICAL;
        }
        ((AreaRequest)this.geometryServiceRequest).setComputationType(computationType);
    }

    public ComputationType getComputationType()
    {
        return ((AreaRequest)this.geometryServiceRequest).getComputationType();
    }

    @Override
    protected void createGeometryServiceRequest() {
        this.geometryServiceRequest = new AreaRequest();
    }
}
