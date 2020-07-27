package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.DescribeTableResponse;
import com.mapinfo.midev.service.feature.v1.SearchBySQLResponse;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.*;

import java.util.List;

/**
 * Provides a uniform interface for accessing all the methods required for
 * performing all features related search and other midev feature service calls
 * like list, describe
 */
public interface FeatureService
{

    public FeatureSearchResult searchNearest(SearchNearestParams params);

    public FeatureSearchResult searchAtPoint(SearchAtPointParams params);

    public FeatureSearchResult searchByExpression(
            SearchByExpressionParams params);

    public FeatureSearchResult searchByQuery(SearchByQueryParams params);

    public FeatureSearchResult searchByGeometry(SearchParams params);

    public List<String> listTableNames() throws ServiceException;

    public DescribeTableResponse describeTable(String tableName)
            throws ServiceException;

    public FeatureSearchResult listFeatures(SearchFeaturesListParams searchFeaturesListParams)
            throws ServiceException;
    
    public FeatureSearchResult distinctRecords(SearchByExpressionParams params)
            throws ServiceException;

    public SearchBySQLResponse executeSql(String sql)
            throws ServiceException;
}
