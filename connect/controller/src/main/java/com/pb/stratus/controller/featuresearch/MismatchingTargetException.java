package com.pb.stratus.controller.featuresearch;

public class MismatchingTargetException extends RuntimeException
{

    public MismatchingTargetException(String message, 
            IllegalAccessException cause)
    {
        super(message, cause);
    }

}
