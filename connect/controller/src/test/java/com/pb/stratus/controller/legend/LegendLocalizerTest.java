package com.pb.stratus.controller.legend;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class LegendLocalizerTest
{

    /**
     * Test of localiseLabelRangesBy method, of class LegendLocaliser.
     */
    @Test
    public void testLocaliseLabelRangesBy()
    {
        Locale locale = new Locale("de");
        String data = "Ranges by density";
        LegendLocalizer localiser = new LegendLocalizer(locale);

        Object expResult = "Bereiche durch density";
        Object result = localiser.localiseLabelRangesBy(data);

        assertEquals(expResult, result);


        data = "Ind. Value with description";
        expResult = "Kategorisiert nach description";
        result = localiser.localiseLabelRangesBy(data);

        assertEquals(expResult, result);

        data = "Alternative Title";
        expResult = "Alternative Title";
        result = localiser.localiseLabelRangesBy(data);

        assertEquals(expResult, result);

        data = "Deranges Bystander";
        expResult = "Deranges Bystander";
        result = localiser.localiseLabelRangesBy(data);

        assertEquals(expResult, result);

        data = "DeRanges Bystander";
        expResult = "DeRanges Bystander";
        result = localiser.localiseLabelRangesBy(data);

        assertEquals(expResult, result);

        data = "Deranges Bystander";
        expResult = "Deranges Bystander";
        result = localiser.localiseLabelRangesBy(data);

        assertEquals(expResult, result);

        data = "ranges by";
        expResult = "ranges by";
        result = localiser.localiseLabelRangesBy(data);

        assertEquals(expResult, result);

        data = "Ranges by a";
        expResult = "Bereiche durch a";
        result = localiser.localiseLabelRangesBy(data);

        assertEquals(expResult, result);


    }

    /**
     * Test of localiseRange method, of class LegendLocaliser.
     */
    @Test
    public void testlocaliseRange()
    {
        Locale locale = new Locale("de");
        String data = "10000.00 to 20000.00";
        LegendLocalizer instance = new LegendLocalizer(locale);

        Object expResult = "10.000 bis 20.000";
        Object result = instance.localiseRange(data);
        assertEquals(expResult, result);


        data = "1 to 2";
        expResult = "1 bis 2";
        result = instance.localiseRange(data);
        assertEquals(expResult, result);

        data = "100 to 200";
        expResult = "100 bis 200";
        result = instance.localiseRange(data);
        assertEquals(expResult, result);
        
        data = "1.25 to 1.55";
        expResult = "1,25 bis 1,55";
        result = instance.localiseRange(data);
        assertEquals(expResult, result);


        data = "1000.25 to 1000.55";
        expResult = "1.000,25 bis 1.000,55";
        result = instance.localiseRange(data);
        assertEquals(expResult, result);
        

        //XXX: Do we want this to be true?
        data = "a to b";
        expResult = "a bis b";
        result = instance.localiseRange(data);
        assertEquals(expResult, result);

        data = "completely inappropriate";
        expResult = "completely inappropriate";
        result = instance.localiseRange(data);
        assertEquals(expResult, result);

        data = "onetothree";
        expResult = "onetothree";
        result = instance.localiseRange(data);
        assertEquals(expResult, result);


    }

}