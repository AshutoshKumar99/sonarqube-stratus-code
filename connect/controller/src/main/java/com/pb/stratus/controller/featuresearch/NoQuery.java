package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.BoundParameter;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.service.SearchByQueryParams;

import java.util.List;

/**
 * A null query object. Useful when there is no meaningful query object to return
 * Helps in circumventing null query object checking
 */
public class NoQuery implements Query {
    @Override
    public String createSQLQuery(QueryMetadata queryMetadata, SearchByQueryParams params, boolean getCount, List<BoundParameter> boundParams, String geometryAttribute) {
        return "";
    }

    @Override
    public boolean isCustomQuery(SearchByQueryParams params) {
        return false;
    }
}
