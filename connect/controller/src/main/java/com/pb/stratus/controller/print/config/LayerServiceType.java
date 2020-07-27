package com.pb.stratus.controller.print.config;

public enum LayerServiceType
{
    TILE("Envinsa-Tile"),
    MAPPING("Envinsa-Mapping"),
    GOOGLE("Google-Map"),
    BING("Bing-Map"),
    OSM("Open-Street-Map"),
    WMS("Web-Mapping-Service"),
    XYZ("XYZ-Tile-Service"),
    TMS("TMS-Tile-Service"),
    MVT("Mapbox-Vector-Tile"),
    THEMATIC("End-User-Thematic"),
    QUERYRESULT("Query-Result-Overlay");
    
    private String serviceName;

    private LayerServiceType(String serviceName)
    {
        this.serviceName = serviceName;
    }
    
    public static LayerServiceType forName(String serviceName)
    {
        for (LayerServiceType serviceType : LayerServiceType.values())
        {
            if (serviceType.serviceName.equals(serviceName))
            {
                return serviceType;
            }
        }
        throw new IllegalArgumentException("Unsupported service name '" 
                + serviceName + "'");
    }

}
