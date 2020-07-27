package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.v1.ComputationType;
import com.mapinfo.midev.service.geometry.v1.LengthRequest;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class LengthRequestBuilderTest {

    private LengthRequestBuilder lengthRequestBuilder;

    @Before
    public void setUp()
    {
        lengthRequestBuilder = new LengthRequestBuilder();
        lengthRequestBuilder.createGeometryServiceRequest();
    }

    @Test
    public void shouldSetCorrectGeometry()
    {
        Geometry mockGeometry = mock(Geometry.class);
        lengthRequestBuilder.setGeometry(mockGeometry);
        assertEquals(mockGeometry, lengthRequestBuilder.getGeometry());
    }

    @Test
    public void shouldSetDefaultLengthUnitToMeter()
    {
        lengthRequestBuilder.setLengthUnit(null);
        assertEquals(DistanceUnit.METER, lengthRequestBuilder.getLengthUnit());
    }

    @Test
    public void shouldSetSpecifiedLengthUnit()
    {
        lengthRequestBuilder.setLengthUnit(DistanceUnit.CHAIN);
        assertEquals(DistanceUnit.CHAIN, lengthRequestBuilder.getLengthUnit());
    }

    @Test
    public void shouldReturnLengthMeasurementType()
    {
        assertEquals(MeasurementEnum.LENGTH,
                lengthRequestBuilder.getMeasurementType());
    }

    @Test
    public void shouldSetDefaultComputationTypeToSpherical()
    {
        lengthRequestBuilder.setComputationType(null);
        assertEquals(ComputationType.SPHERICAL,
                lengthRequestBuilder.getComputationType());
    }

    @Test
    public void shouldSetSpecifiedComputationType()
    {
        lengthRequestBuilder.setComputationType(ComputationType.CARTESIAN);
        assertEquals(ComputationType.CARTESIAN,
                lengthRequestBuilder.getComputationType());
    }

    @Test
    public void shouldReturnInstanceofLengthRequest()
    {
        assertTrue(lengthRequestBuilder.getGeometryServiceRequest() instanceof
                LengthRequest);
    }
}
