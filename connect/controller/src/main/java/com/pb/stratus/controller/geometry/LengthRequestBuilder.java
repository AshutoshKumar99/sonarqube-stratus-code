package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.v1.ComputationType;
import com.mapinfo.midev.service.geometry.v1.LengthRequest;
import com.mapinfo.midev.service.units.v1.AreaUnit;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import com.pb.stratus.core.common.Preconditions;

public class LengthRequestBuilder extends AbstractMeasuringBuilder{

    public LengthRequestBuilder()
    {
        super();
    }
    @Override
    public void setGeometry(Geometry geometry) {
        Preconditions.checkNotNull(geometry, "geometry cannot be null");
                ((LengthRequest) this.geometryServiceRequest).setGeometry(geometry);
    }

    /**
     * for testing purpose only
     * @param
     */
    public Geometry getGeometry()
    {
        return ((LengthRequest) this.geometryServiceRequest).getGeometry();
    }

    @Override
    public void setAreaUnit(AreaUnit areaUnit) {
        // not supported
    }

    @Override
    public void setLengthUnit(DistanceUnit lengthUnit) {
        if(lengthUnit == null)
        {
            lengthUnit = DistanceUnit.METER;
        }
        ((LengthRequest)this.geometryServiceRequest).setLengthUnit(lengthUnit);
    }

    /**
     * For testing purpose only.
     * @return
     */
    public DistanceUnit getLengthUnit()
    {
        return ((LengthRequest)this.geometryServiceRequest).getLengthUnit();
    }

    @Override
    public MeasurementEnum getMeasurementType()
    {
        return MeasurementEnum.LENGTH;
    }

    @Override
    public void setComputationType(ComputationType computationType)
    {
        if(computationType == null)
        {
            computationType  =  ComputationType.SPHERICAL;
        }
        ((LengthRequest)this.geometryServiceRequest).setComputationType(computationType);
    }

    /**
     * For testing purpose only.
     * @return
     */
    public ComputationType getComputationType()
    {
        return ((LengthRequest)this.geometryServiceRequest).getComputationType();
    }

    @Override
    protected void createGeometryServiceRequest() {
        this.geometryServiceRequest = new LengthRequest();
    }
}
