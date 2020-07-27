package com.pb.stratus.controller;

/**
 * Indicates that a map could not be resolved(could be coz of various reasons,
 * tab file refered in map not found or data corrupt etc.)
 */

public class MapResolveFailureException extends RuntimeException
{
    private static final long serialVersionUID = -3090076300597595886L;

    private String mapName;

    private String message;

    public MapResolveFailureException(String mapName)
    {
        this.mapName = mapName;
        this.message = this.getClass().getName();
    }

    public MapResolveFailureException(String mapName, String message)
    {
        this.mapName = mapName;
        this.message = message;
    }

    public String getMapName()
    {
        return this.mapName;
    }

    public String getMessage()
    {
        return this.message;
    }

}
