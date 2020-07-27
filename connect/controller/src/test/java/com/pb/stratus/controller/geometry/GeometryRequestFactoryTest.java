package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometry.v1.AreaRequest;
import com.mapinfo.midev.service.geometry.v1.GeometryServiceRequest;
import com.mapinfo.midev.service.geometry.v1.LengthRequest;
import com.pb.stratus.controller.i18n.LocaleResolver;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class GeometryRequestFactoryTest {

    private GeometryRequestFactory geometryRequestFactory;
    private Geometry mockGeometry;

    @Before
    public void setUp()
    {
        geometryRequestFactory = new GeometryRequestFactory();
        LocaleResolver.setLocale(Locale.ENGLISH);
        mockGeometry = mock(Geometry.class);
    }

    @Test
    public void shouldSupportAreaRequest()
    {
        GeometryServiceRequest result =
                geometryRequestFactory.getGeometryServiceRequest("area", "some-srs",
            mockGeometry);
        assertTrue(result instanceof AreaRequest);
    }

    @Test
    public void shouldSupportLengthRequest()
    {
        GeometryServiceRequest result =
                geometryRequestFactory.getGeometryServiceRequest("length", "some-srs",
                        mockGeometry);
        assertTrue(result instanceof LengthRequest);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionForUnSupportedOperation()
    {
        geometryRequestFactory.getGeometryServiceRequest("unsupported-operation",
                "some-srs", mockGeometry);
    }

}
