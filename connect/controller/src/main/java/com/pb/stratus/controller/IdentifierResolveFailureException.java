package com.pb.stratus.controller;

/**
 * Indicates that an wrong identifier was requested from the FeatureService
 * 
 * @author Pankaj Wadhwa
 */
public class IdentifierResolveFailureException extends RuntimeException
{
    private static final long serialVersionUID = -2397088886510188282L;

    private String identifierName;

    public IdentifierResolveFailureException(String identifierName)
    {
        this.identifierName = identifierName;
    }

    public String getIdentifierName()
    {
        return identifierName;
    }
}
