package com.pb.stratus.controller.datainterchangeformat;

/**
 * This factory currently supports two formats Csv and Json. Json is the
 * default format in which the response will be converted if a illegal or
 * Json format is requested.
 */
public final class DataInterchangeFormatFactory
{
    private DataInterchangeFormatFactory(){}

    public static DataInterchangeFormatResponse
            getDataInterchangeFormatResponse(String interchangeFormat)
    {
        switch(DataInterchangeFormats.getDataInterchangeFormat
                (interchangeFormat))
        {
            case CSV:
                return new CsvFileFormatResponse();
            case KML:
                return new KMLFileFormatResponse();
            case PLAIN_TEXT:
                return new PlainTextFormatResponse();
            case DOJO_IOFRAME:
                return new DojoIoIFrameResponse();
            default:
                return new JsonFormatResponse();
        }
    }
}
