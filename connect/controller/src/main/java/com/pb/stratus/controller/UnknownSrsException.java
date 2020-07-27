package com.pb.stratus.controller;

/**
 * Indicates that an unknown spatial reference system was passed into one of the service methods.
 * 
 * @author Volker Leidl
 */
public class UnknownSrsException extends RuntimeException
{

    private static final long serialVersionUID = -5473729916462409453L;

    private String srsName;

    /**
     * Creates a new instance of this exception without srsName. We need to provide this constructor because Midev
     * doesn't return the SRS name it choked on as part of their ServiceException.
     */
    public UnknownSrsException()
    {
    }

    public UnknownSrsException(String srsName)
    {
        this.srsName = srsName;
    }

    public String getSrsName()
    {
        return srsName;
    }

}
