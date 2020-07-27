package com.pb.gazetteer;

/**
 * An exception that is thrown if an attempt is made to create a Lucene index
 * that already exists.
 */
public class IndexAlreadyExistsException extends IndexSearchException
{

    public IndexAlreadyExistsException(String message)
    {
        super(message);
    }

}
