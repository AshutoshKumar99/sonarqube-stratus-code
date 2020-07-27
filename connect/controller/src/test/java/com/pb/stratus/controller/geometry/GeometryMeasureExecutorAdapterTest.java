package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometry.v1.AreaRequest;
import com.mapinfo.midev.service.geometry.v1.AreaResponse;
import com.mapinfo.midev.service.geometry.v1.LengthRequest;
import com.mapinfo.midev.service.geometry.v1.LengthResponse;
import com.mapinfo.midev.service.geometry.ws.v1.GeometryServiceInterface;
import com.mapinfo.midev.service.geometry.ws.v1.ServiceException;
import com.mapinfo.midev.service.units.v1.Area;
import com.mapinfo.midev.service.units.v1.AreaUnit;
import com.mapinfo.midev.service.units.v1.Distance;
import com.mapinfo.midev.service.units.v1.DistanceUnit;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class GeometryMeasureExecutorAdapterTest {

    private GeometryMeasureExecutorAdapter geometryMeasureExecutorAdapter;
    private GeometryServiceInterface mockGeometryServiceInterface;
    private AreaResponse areaResponse;
    private LengthResponse lengthResponse;

    @Before
    public void setUp() throws ServiceException
    {
        mockGeometryServiceInterface = mock(GeometryServiceInterface.class);
        geometryMeasureExecutorAdapter = new GeometryMeasureExecutorAdapter();
        geometryMeasureExecutorAdapter.setGeometryWebService(
                mockGeometryServiceInterface);
        areaResponse = new AreaResponse();
        Area area = new Area();
        area.setValue(2.13);
        area.setUom(AreaUnit.ACRE);
        areaResponse.setArea(area);
        when(mockGeometryServiceInterface.area(any(AreaRequest.class))).
                thenReturn(areaResponse);
        lengthResponse = new LengthResponse();
        Distance distance = new Distance();
        distance.setValue(3.33);
        distance.setUom(DistanceUnit.CHAIN);
        lengthResponse.setLength(distance);
        when(mockGeometryServiceInterface.length(any(LengthRequest.class))).
                thenReturn(lengthResponse);
        geometryMeasureExecutorAdapter= spy(geometryMeasureExecutorAdapter);
    }

    @Test
    public void shouldSupportAreaRequest() throws ServiceException
    {
        AreaRequest mockAreaRequest = mock(AreaRequest.class);
        geometryMeasureExecutorAdapter.getMeasurementOfGeometry(mockAreaRequest);
        verify(geometryMeasureExecutorAdapter).getAreaOfTheGeometry(mockAreaRequest);
    }

    @Test
    public void shouldReturnCorrectMeasureForAreaRequest() throws ServiceException {
        AreaRequest mockAreaRequest = mock(AreaRequest.class);
        Measure result =
                geometryMeasureExecutorAdapter.getMeasurementOfGeometry(mockAreaRequest);
        Measure expected = new Measure(MeasurementEnum.AREA.value(), 2.13,
                AreaUnit.ACRE.value());
        assertEquals(expected, result);
    }

    @Test
    public void shouldSupportLengthRequest() throws ServiceException {
        LengthRequest mockLengthRequest = mock(LengthRequest.class);
        geometryMeasureExecutorAdapter.getMeasurementOfGeometry(
                mockLengthRequest);
        verify(geometryMeasureExecutorAdapter).getLengthOfTheGeometry(
                mockLengthRequest);
    }

    @Test
    public void shouldReturnCorrectMeasureForLengthRequest() throws ServiceException {
        LengthRequest mockLengthRequest = mock(LengthRequest.class);
        Measure result = geometryMeasureExecutorAdapter.getMeasurementOfGeometry(
                mockLengthRequest);
        Measure expected = new Measure(MeasurementEnum.LENGTH.value(), 3.33,
                DistanceUnit.CHAIN.value());
        assertEquals(expected, result);
    }
}
