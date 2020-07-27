package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.v1.ComputationType;
import com.mapinfo.midev.service.units.v1.AreaUnit;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class AreaRequestBuilderTest {

    private AreaRequestBuilder areaRequestBuilder;
    @Before
    public void setUp()
    {
        areaRequestBuilder = new AreaRequestBuilder();
        areaRequestBuilder.createGeometryServiceRequest();
    }

    @Test
    public void shouldSetCorrectGeometry()
    {
        Geometry mockGeometry = mock(Geometry.class);
        areaRequestBuilder.setGeometry(mockGeometry);
        assertEquals(mockGeometry, areaRequestBuilder.getGeometry());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowExceptionForNullGeometry()
    {
        areaRequestBuilder.setGeometry(null);
    }

    @Test
    public void shouldSetDefaultAreaUnit()
    {
        areaRequestBuilder.setAreaUnit(null);
        assertEquals(AreaUnit.SQUARE_METER, areaRequestBuilder.getAreaUnit());
    }


    @Test
    public void shouldSetGivenAreaUnit()
    {
        areaRequestBuilder.setAreaUnit(AreaUnit.ACRE);
        assertEquals(AreaUnit.ACRE, areaRequestBuilder.getAreaUnit());
    }

    @Test
    public void shouldReturnCorrectMeasurementType()
    {
        assertEquals(MeasurementEnum.AREA, areaRequestBuilder.getMeasurementType());
    }

    @Test
    public void shouldSetDefaultComputationType()
    {
        areaRequestBuilder.setComputationType(null);
        assertEquals(ComputationType.SPHERICAL, areaRequestBuilder.getComputationType());
    }
}
