package com.pb.stratus.controller.print;

import com.mapinfo.midev.service.geometries.v1.Point;
import com.pb.stratus.controller.geometry.GeometryService;
import com.pb.stratus.controller.i18n.LocaleResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.awt.*;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ScaleBarFactoryTest
{
    
    private Locale oldLocale;
    
    private ScaleBarFactory factory;

    private GeometryService mockGeomService;

    @Before
    public void setUp() throws Exception
    {
        oldLocale = LocaleResolver.getLocale();
        LocaleResolver.setLocale(new Locale("en"));
        mockGeomService = mock(GeometryService.class);
        com.mapinfo.midev.service.units.v1.Distance mockDistance 
                = new com.mapinfo.midev.service.units.v1.Distance();
        mockDistance.setUom(com.mapinfo.midev.service.units.v1.DistanceUnit.METER);
        mockDistance.setValue(100);
        when(mockGeomService.getDistanceBetweenTwoPoints(any(List.class)))
                .thenReturn(mockDistance);
        factory = new ScaleBarFactory(mockGeomService);
    }
    
    @After
    public void tearDown()
    {
        LocaleResolver.setLocale(oldLocale);
    }
    
    @Test
    public void shouldCreateExpectedScaleBar() throws Exception
    {
        ScaleBar expected = new ScaleBar(
                new Distance(DistanceUnit.KM, 30), 1d / 591000);
        ScaleBar actual = createScaleBarForMapThatIsRoughly20CentimetersWide();
        assertTrue(expected.isEquivalent(actual, 0.00001));
    }
    
    private ScaleBar createScaleBarForMapThatIsRoughly20CentimetersWide()
    {
        BoundingBox anyBoundingBox = new BoundingBox(2, 1, 1, 2, "someSrs"); 
        return factory.createScaleBar(
                anyBoundingBox, 
                new Dimension(1182, 1182), new Resolution(150), true);
    }
    
    @Test
    public void shouldRequestWidthOfCenterPixelFromGeometryService()
            throws Exception
    {
        factory.createScaleBar(new BoundingBox(100, 0, 0, 200, "someSrs"), 
                new Dimension(40, 20), new Resolution(150), true);
        ArgumentCaptor<List> arg = ArgumentCaptor.forClass(List.class);
        verify(mockGeomService).getDistanceBetweenTwoPoints(arg.capture());
        List<Point> points = (List<Point>) arg.getValue();
        assertEquals(100, points.get(0).getPos().getX(), 0d);
        assertEquals(50, points.get(0).getPos().getY(), 0d);
        assertEquals("someSrs", points.get(0).getSrsName());
        assertEquals(105, points.get(1).getPos().getX(), 0d);
        assertEquals(50, points.get(1).getPos().getY(), 0d);
        assertEquals("someSrs", points.get(1).getSrsName());
    }

}
