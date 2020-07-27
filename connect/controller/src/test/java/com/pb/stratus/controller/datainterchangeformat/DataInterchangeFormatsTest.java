package com.pb.stratus.controller.datainterchangeformat;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class DataInterchangeFormatsTest {

    @Test
    public void shouldReturnJSonAsDefaultFormat()
    {
        assertEquals(DataInterchangeFormats.JSON,
                DataInterchangeFormats.getDataInterchangeFormat("not present"));

        assertEquals(DataInterchangeFormats.JSON,
                DataInterchangeFormats.getDataInterchangeFormat(null));
    }

    @Test
    public void shouldReturnCSV()
    {
        assertEquals(DataInterchangeFormats.CSV,
                DataInterchangeFormats.getDataInterchangeFormat("csv"));

    }

    @Test
    public void shouldReturnKML()
    {
        assertEquals(DataInterchangeFormats.KML,
                DataInterchangeFormats.getDataInterchangeFormat("kml"));
    }

    @Test
    public void shouldReturnJSON()
    {
        assertEquals(DataInterchangeFormats.JSON,
                DataInterchangeFormats.getDataInterchangeFormat("json"));

    }
}
