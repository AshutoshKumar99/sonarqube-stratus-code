package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.v1.ComputationType;
import com.mapinfo.midev.service.units.v1.AreaUnit;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class MeasuringDirectorTest
{
    private MeasuringDirector measuringDirector;
    private String responseSrs = "EPSG:3857";

    @Before
    public void setUp()
    {
        measuringDirector = new MeasuringDirector();
    }

    @Test
    public void shouldConstructObjectByCallingMethodsInOrder()
    {
        AbstractMeasuringBuilder mockAbstractMeasuringBuilder =
                mock(AbstractMeasuringBuilder.class);
        Geometry mockGeometry = mock(Geometry.class);
        measuringDirector.build(mockAbstractMeasuringBuilder, responseSrs,
                mockGeometry);
        InOrder inOrder = inOrder(mockAbstractMeasuringBuilder);
        inOrder.verify(mockAbstractMeasuringBuilder).createGeometryServiceRequest();
        inOrder.verify(mockAbstractMeasuringBuilder).setId(any(String.class));
        inOrder.verify(mockAbstractMeasuringBuilder).setLocale();
        inOrder.verify(mockAbstractMeasuringBuilder).setResponseSrsName(responseSrs);
        inOrder.verify(mockAbstractMeasuringBuilder).setGeometry(mockGeometry);
        inOrder.verify(mockAbstractMeasuringBuilder).setAreaUnit(any(AreaUnit.class));
        inOrder.verify(mockAbstractMeasuringBuilder).setLengthUnit(any(DistanceUnit.class));
        inOrder.verify(mockAbstractMeasuringBuilder).setComputationType(any(ComputationType.class));
    }
}
