package com.pb.gazetteer;

/**
 * An abstract factory to create IndexSearches, which can then be used to 
 * perform actual address searches.
 */
public interface IndexSearchFactory
{
    public IndexSearch getIndex(LocateInstance locateInstance);

    void shutdown();
}
