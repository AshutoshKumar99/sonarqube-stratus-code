package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.ListFeaturesRequest;
import com.mapinfo.midev.service.feature.v1.ListFeaturesResponse;
import com.mapinfo.midev.service.feature.v1.SearchResponse;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchFeaturesListParams;
import com.pb.stratus.controller.service.SearchParams;
import uk.co.graphdata.utilities.contract.Contract;

/**
 * Searches for features inside a given map (will list all the features).
 */
public class SearchFeaturesListStrategy extends BaseSearchStrategy
{

    /**
     * 
     * @param featureWebService
     * @param converterFactory
     * @param maxResults
     */
    public SearchFeaturesListStrategy(
            FeatureServiceInterface featureWebService,
            FeatureSearchResultConverterFactory converterFactory, int maxResults)
    {
        super(featureWebService, converterFactory, maxResults);
    }

    /**
     * 
     */
    public FeatureSearchResult search(SearchParams searchParams)
    {
        Contract.pre(searchParams instanceof SearchFeaturesListParams,
                "SearchParams must be an instance of " +
                SearchFeaturesListParams.class);
        SearchFeaturesListParams searchParamsListFeatures = (SearchFeaturesListParams)
                searchParams;
        SearchResponse response = performSearch(searchParamsListFeatures);
        return convertWebServiceResponse(searchParamsListFeatures.getTable(),
                response, searchParams);
    }

    /**
     * 
     * @param searchParamsListFeatures
     * @return
     */
    private SearchResponse performSearch(
            SearchFeaturesListParams searchParamsListFeatures)
    {
        ListFeaturesRequest request = new ListFeaturesRequest();
        popuplateBaseValues(searchParamsListFeatures.getTable(), 
                    request, searchParamsListFeatures);
        return performSearchIntersects(request);
    }

    /**
     * 
     * @param request
     * @return
     */
    private ListFeaturesResponse performSearchIntersects(
            ListFeaturesRequest request)
    {
        try
        {
            return featureWebService.listFeatures(request);
        }
        catch (ServiceException sx)
        {
            throw new Error("Unhandled ServiceException", sx);
        }
    }
}
