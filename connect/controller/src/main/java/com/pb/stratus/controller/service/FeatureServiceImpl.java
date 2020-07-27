package com.pb.stratus.controller.service;

import com.mapinfo.midev.service.feature.v1.*;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.pb.stratus.controller.featuresearch.*;
import com.pb.stratus.controller.info.FeatureSearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;


public class FeatureServiceImpl implements FeatureService
{

    private static final Logger log = LogManager.getLogger(FeatureServiceImpl.class);
    
	private FeatureServiceInterface featureWebService;

    private SearchStrategyRepository searchStrategyRepository;

    private SrsTransformer transformer;

    public FeatureServiceImpl(FeatureServiceInterface featureWebService,
            SearchStrategyRepository searchStrategyRepository,
            SrsTransformer transformer)
    {
        this.featureWebService = featureWebService;
        this.searchStrategyRepository = searchStrategyRepository;
        this.transformer = transformer;
    }

    public FeatureSearchResult searchNearest(SearchNearestParams params)
    {
        return search(params, "searchNearest");
    }

    // this will be removed
    public FeatureSearchResult searchAtPoint(SearchAtPointParams params)
    {
        return search(params, "searchAtPoint");
    }

    public FeatureSearchResult searchByExpression(
            SearchByExpressionParams params)
    {
        return search(params, "searchByExpression");
    }

   
    public FeatureSearchResult searchByGeometry(SearchParams params)
    {

        String searchType =FeatureSearchStrategyResolver.
                getSearchType(params);

        return search(params, searchType);

    }
    
    public FeatureSearchResult searchByQuery(
            SearchByQueryParams params)
    {
        return search(params, "searchByQuery");
    }

    public List<String> listTableNames() throws ServiceException
    {
        ListTableNamesRequest request = new ListTableNamesRequest();
        return featureWebService.listTableNames(request).getTable();
    }

    public DescribeTableResponse describeTable(String tableName)
            throws ServiceException
    {
        DescribeTableRequest request = new DescribeTableRequest();
        request.setTable(tableName);
        return  featureWebService.describeTable(request);
    }
    
    /**
     * 
     */
    public FeatureSearchResult listFeatures(SearchFeaturesListParams searchFeaturesListParams)
                                    throws ServiceException
    {
        return search(searchFeaturesListParams, "listFeatures");
    }
    
    /**
     * This method is used to get distinct records for a given column in a table
     */
    public FeatureSearchResult distinctRecords (SearchByExpressionParams params)
                                    throws ServiceException
    {
        return search(params, "searchByExpressionGroupBy");
    }

    @Override
    public SearchBySQLResponse executeSql(String sql) throws ServiceException {
        SearchBySQLRequest request = new SearchBySQLRequest();
        request.setSQL(sql);
        return featureWebService.searchBySQL(request);
    }

    private FeatureSearchResult search(SearchParams params, String searchType)
    {
        log.info("|SearchType : "+searchType+" Entry");

        SearchStrategy strat =
                searchStrategyRepository.getSearchStrategy(searchType);
        FeatureSearchResult result = strat.search(params);
        String targetSrs = params.getTargetSrs();
        if (StringUtils.isBlank(targetSrs) || !params.isIncludeGeometry())
        {
            return result;
        }
        else
        {
            return transformer.transform(result, targetSrs);
        }
    }

}
