package com.pb.stratus.controller.featuresearch;

import com.pb.stratus.controller.QueryType;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 9/26/14
 * Time: 7:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class QueryFactory {

    public Query createQuery(QueryType queryType) {
        switch(queryType) {
            case SummarizedQuery:
                return new SummarizedQuery();
            case FilteredQuery:
                return new FilteredQuery();
            case GeometryCountQuery:
                return new FeatureCountByGeometryQuery();
            case RecordCountQuery:
                return new RecordCountQuery();
            case DataBindingQuery:
                return new DataBindingQuery();
            case ApplicationLinkingQuery:
                return new ApplicationLinkingQuery();
            default:
                return new NoQuery();
        }
    }
}
