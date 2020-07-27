package com.pb.stratus.controller;

public class LocateServiceNotFoundException extends RuntimeException
{
    /**
     * This class is used to indicate that Locate Service is not available 
     */
    private static final long serialVersionUID = 1L;
    public LocateServiceNotFoundException()
    {
        
    }
    public LocateServiceNotFoundException(String message)
    {
        super(message);
    }
    public LocateServiceNotFoundException(Throwable throwable)
    {
        super(throwable);
    }
}
