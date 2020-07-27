package com.pb.stratus.controller.util;

public class ResourceNotFoundException extends RuntimeException
{

    private static final long serialVersionUID = 586873887769342952L;

    public ResourceNotFoundException()
    {
        super();
    }

    public ResourceNotFoundException(String message)
    {
        super(message);
    }

}
