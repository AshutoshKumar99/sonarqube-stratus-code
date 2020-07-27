package com.pb.gazetteer;

/**
 * A base class for all exceptions thrown by this application 
 */
public class IndexSearchException extends RuntimeException
{
    public IndexSearchException()
    {
    }

    public IndexSearchException(String message)
    {
        super(message);
    }

    public IndexSearchException(Throwable cause)
    {
        super(cause);
    }

    public IndexSearchException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
