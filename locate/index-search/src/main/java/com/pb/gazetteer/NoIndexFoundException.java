package com.pb.gazetteer;

/**
 * An exception thrown if no suitable index can be found to satisfy an incoming
 * request 
 */
public class NoIndexFoundException extends
        IndexSearchException
{

    public NoIndexFoundException()
    {
    }

    public NoIndexFoundException(String message)
    {
        super(message);
    }

    public NoIndexFoundException(Throwable cause)
    {
        super(cause);
    }

    public NoIndexFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
