package com.pb.stratus.controller;

/**
 * Indicates that an unknown attribute was requested from the FeatureService
 * 
 * @author Volker Leidl
 */
public class UnknownPropertyException extends RuntimeException
{

    private static final long serialVersionUID = -5473729916462409453L;

    private String propertyName;

    public UnknownPropertyException(String propertyName)
    {
        this.propertyName = propertyName;
    }

    public String getPropertyName()
    {
        return propertyName;
    }

}
