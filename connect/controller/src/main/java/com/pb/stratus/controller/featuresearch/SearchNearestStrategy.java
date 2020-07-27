package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.SearchNearestRequest;
import com.mapinfo.midev.service.feature.v1.SearchNearestResponse;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchNearestParams;
import com.pb.stratus.controller.service.SearchParams;
import uk.co.graphdata.utilities.contract.Contract;

/**
 * Search for features nearest to a given reference point. 
 */
public class SearchNearestStrategy extends BaseSearchStrategy
{

    public SearchNearestStrategy(FeatureServiceInterface featureWebService,
            FeatureSearchResultConverterFactory converterFactory, 
            int maxResults)
    {
        super(featureWebService, converterFactory, maxResults);
    }

    public FeatureSearchResult search(SearchParams searchParams)
    {
        Contract.pre(searchParams instanceof SearchNearestParams, 
                "SearchParams must be an instance of " 
                + SearchNearestParams.class);
        SearchNearestParams searchNearestParams 
                = (SearchNearestParams) searchParams; 
        SearchNearestRequest request = createRequest(searchNearestParams);
        SearchNearestResponse response = performSearch(request);
        return convertWebServiceResponse(searchNearestParams.getTable(), 
                response, searchNearestParams);
    }
    
    private SearchNearestRequest createRequest(SearchNearestParams params)
    {
        SearchNearestRequest request = new SearchNearestRequest();
        popuplateBaseValues(params.getTable(), request, params);
        request.setGeometry(params.getPoint());
        // Specifically removing MI_KEY from SearchNearestRequest as MI_KEY is not supported attribute for this request
        if(request.getAttributeList()!=null && request.getAttributeList().getAttributeName()!=null){
            request.getAttributeList().getAttributeName().remove("MI_KEY");
        }
        request.setMaxDistance(params.getDistance());
        request.setReturnedDistanceAttributeName(
                params.getReturnedDistanceAttributeName());
        request.setMaxNumberOfCandidates(params.getMaxResults());
        request.setReturnedDistanceUnit(params.getReturnedDistanceUnit());
        return request;
    }

    private SearchNearestResponse performSearch(SearchNearestRequest request)
    {
        try
        {
            return featureWebService.searchNearest(request);
        }
        catch (ServiceException sx)
        {
            throw new Error("Unhandled ServiceException", sx);
        }
    }

}
