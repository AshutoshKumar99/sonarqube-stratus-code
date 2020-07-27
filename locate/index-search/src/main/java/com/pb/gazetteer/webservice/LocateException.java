package com.pb.gazetteer.webservice;

public class LocateException extends Exception
{
    private static final long serialVersionUID = 4268477161125913950L;

    public LocateException()
    {
    }

    public LocateException(String message)
    {
        super(message);
    }

    public LocateException(Throwable cause)
    {
        super(cause);
    }
}
