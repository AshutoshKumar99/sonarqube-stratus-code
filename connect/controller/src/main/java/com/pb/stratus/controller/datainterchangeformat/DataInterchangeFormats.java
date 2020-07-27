package com.pb.stratus.controller.datainterchangeformat;

/**
 * An enum containing the Formats that are supported. IF an unsupported
 * format is requested the the default JSON format would be returned.
 */
public enum DataInterchangeFormats
{
    JSON("json"), CSV("csv"), KML("kml"), PLAIN_TEXT("plain-text"),
    DOJO_IOFRAME("dojo-ioframe");

    private String contentType;

    DataInterchangeFormats(String contentType)
    {
        this.contentType = contentType;
    }

    public String toString()
    {
        return this.contentType;
    }

    public static DataInterchangeFormats getDataInterchangeFormat(String
            interchangeFormat)
    {
        for(DataInterchangeFormats dataInterchangeFormat :
                DataInterchangeFormats.values())
        {
            if(dataInterchangeFormat.toString().equalsIgnoreCase(interchangeFormat))
            {
                return dataInterchangeFormat;
            }
        }
        return  JSON;
    }
}
