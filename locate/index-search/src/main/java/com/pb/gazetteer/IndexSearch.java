
package com.pb.gazetteer;

import com.pb.gazetteer.search.SearchLogic;

import java.util.List;

/**
 * An IndexSearch queries a data store for a list of addresses matching the 
 * given query string. The query string is a potentially partial address.
 */
public interface IndexSearch
{
    /**
     * Search using the default logic.
     * @param query
     * @param maxRecords
     * @return
     * @throws IndexSearchException
     */
    public List<Address> search(String query, int maxRecords)
            throws IndexSearchException;

    /**
     * Search using a defined logic.
     * @param query
     * @param option
     * @param maxRecords
     * @return
     * @throws IndexSearchException
     */
    public List<Address> search(String query, SearchLogic option, int maxRecords)
            throws IndexSearchException;

}
