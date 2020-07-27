package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.BoundParameter;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.service.SearchByQueryParams;

import java.util.List;

/**
 * Defines a strategy interface for creating custom SQL queries.
 * User: VI012GU
 * Date: 9/4/14
 * Time: 12:21 PM
 */
public interface Query {

    /**
     * Creates the SQL query for selecting features that intersects or are with in the given geometry.
     * @param queryMetadata
     * @param params
     * @param getCount
     * @param boundParams
     * @param geometryAttribute
     * @return SQL query string
     */
    String createSQLQuery(QueryMetadata queryMetadata,
                          SearchByQueryParams params, boolean getCount,
                          List<BoundParameter> boundParams, String geometryAttribute);

    /**
     * Provided for retrofitting the implementation for custom filter query
     * @param params
     * @return
     */
    boolean isCustomQuery(SearchByQueryParams params);
}
