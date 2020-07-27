package com.pb.stratus.controller.print;

import com.pb.stratus.controller.i18n.LocaleResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class ScaleBarTest
{
    
    private ScaleBar scaleBar;
    
    Locale oldLocale;

    @Before
    public void setUp()
    {
        oldLocale = LocaleResolver.getLocale();
        Locale locale = new Locale("en");
        LocaleResolver.setLocale(locale);
        scaleBar = new ScaleBar(new Distance(DistanceUnit.MI, 10), 1d / 50000);
    }
    
    @After
    public void tearDown()
    {
        LocaleResolver.setLocale(oldLocale);
    }
    
    @Test
    public void shouldGetZeroAsLeftLabel()
    {
        assertEquals("0 mi", scaleBar.getLeftLabel());
    }
    
    @Test
    public void shouldGetWidthAsRightLabel()
    {
        assertEquals("10 mi", scaleBar.getRightLabel());
    }
    
    @Test
    public void shouldGetHalfOfWidthAsMiddleLabel()
    {
        assertEquals("5 mi", scaleBar.getMiddleLabel());
    }

    @Test
    public void shouldUseScaleToCalculateWidthOnMap()
    {
        assertEquals(32.18688, scaleBar.getWidthOnMapInCm(), 0d);
    }
    
    @Test
    public void shouldSupportKm()
    {
        ScaleBar s = new ScaleBar(new Distance(DistanceUnit.KM, 1), 
                1d / 20000);
        assertEquals(5, s.getWidthOnMapInCm(), 0d);
    }
    
    @Test
    public void shouldSupportMeters()
    {
        ScaleBar s = new ScaleBar(new Distance(DistanceUnit.M, 500), 
                1d / 50000);
        assertEquals(1, s.getWidthOnMapInCm(), 0d);
    }
    
    @Test
    public void shouldFormatLabelsLocaleSpecifically()
    {
        LocaleResolver.setLocale(new Locale("de"));
        ScaleBar s = new ScaleBar(new Distance(DistanceUnit.M, 1), 1);
        assertEquals("0,5 m", s.getMiddleLabel());
    }

}
