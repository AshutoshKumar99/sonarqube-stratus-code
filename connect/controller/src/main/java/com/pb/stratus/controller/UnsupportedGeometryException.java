package com.pb.stratus.controller;

public class UnsupportedGeometryException extends RuntimeException
{
    private static final long serialVersionUID = -5765010225734967001L;

    public UnsupportedGeometryException()
    {
    }

    public UnsupportedGeometryException(String message)
    {
        super(message);
    }
}
