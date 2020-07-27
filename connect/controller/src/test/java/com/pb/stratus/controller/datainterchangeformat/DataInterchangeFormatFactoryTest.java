package com.pb.stratus.controller.datainterchangeformat;


import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class DataInterchangeFormatFactoryTest {

    @Test
    public void shouldReturnJsonResponseAsDefault()
    {
        DataInterchangeFormatResponse actual =
                DataInterchangeFormatFactory.getDataInterchangeFormatResponse(
                        "some value");
        assertTrue(actual instanceof JsonFormatResponse);
    }

    @Test
    public void shouldReturnCSVFileFormat()
    {
        DataInterchangeFormatResponse actual =
                DataInterchangeFormatFactory.getDataInterchangeFormatResponse(
                        "csv");
        assertTrue(actual instanceof CsvFileFormatResponse);
    }

    @Test
    public void shouldReturnKMLFileFormat()
    {
        DataInterchangeFormatResponse actual =
                DataInterchangeFormatFactory.getDataInterchangeFormatResponse(
                        "kml");
        assertTrue(actual instanceof KMLFileFormatResponse);
    }
}
