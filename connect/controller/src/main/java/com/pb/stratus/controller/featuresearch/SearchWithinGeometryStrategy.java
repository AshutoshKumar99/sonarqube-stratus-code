package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.SearchIntersectsRequest;
import com.mapinfo.midev.service.feature.v1.SearchResponse;
import com.mapinfo.midev.service.feature.v1.SearchWithinPolygonRequest;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.geometries.v1.MultiPolygon;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchParams;
import com.pb.stratus.controller.service.SearchWithinGeometryParams;
import com.pb.stratus.controller.service.SearchWithinGeometryParams.SpatialOperation;
import uk.co.graphdata.utilities.contract.Contract;

/**
 * Searches for features inside a given goemetry.
 */
public class SearchWithinGeometryStrategy extends BaseSearchStrategy
{

    public SearchWithinGeometryStrategy(
            FeatureServiceInterface featureWebService,
            FeatureSearchResultConverterFactory converterFactory, int maxResults)
    {
        super(featureWebService, converterFactory, maxResults);
    }

    public FeatureSearchResult search(SearchParams searchParams)
    {
        Contract.pre(searchParams instanceof SearchWithinGeometryParams,
                "SearchParams must be an instance of " +
                		SearchWithinGeometryParams.class);
        SearchWithinGeometryParams searchWithinGeometryParams =
                (SearchWithinGeometryParams) searchParams;
        SearchResponse response = performSearch(searchWithinGeometryParams);
        return convertWebServiceResponse(searchWithinGeometryParams.getTable(),
                response, searchParams);
    }

    private SearchResponse performSearch(
    		SearchWithinGeometryParams searchByPolygonParams)
    {
        if (searchByPolygonParams.getSpatialOperation() == SpatialOperation.ENTIRELYWITHIN)
        {
            SearchWithinPolygonRequest request =
            		createWithinGeometryRequest(searchByPolygonParams);
            return performSearchWithinGeometry(request);
        }
        else
        {
            SearchIntersectsRequest request =
                    createIntersectsRequest(searchByPolygonParams);
            return performSearchIntersects(request);

        }
    }

    private SearchWithinPolygonRequest createWithinGeometryRequest(
    		SearchWithinGeometryParams searchParams)
    {
        SearchWithinPolygonRequest request = new SearchWithinPolygonRequest();
        popuplateBaseValues(searchParams.getTable(), request, searchParams);
        request.setMultiPolygon((MultiPolygon) searchParams.getGeometry());
        return request;
    }

    private SearchIntersectsRequest createIntersectsRequest(
    		SearchWithinGeometryParams searchParams)
    {
        SearchIntersectsRequest request = new SearchIntersectsRequest();
        popuplateBaseValues(searchParams.getTable(), request, searchParams);
        request.setGeometry(searchParams.getGeometry());
        return request;
    }

    private SearchResponse performSearchWithinGeometry(
            SearchWithinPolygonRequest request)
    {
        try
        {
            return featureWebService.searchWithinPolygon(request);
        }
        catch (ServiceException sx)
        {
            throw new Error("Unhandled ServiceException", sx);
        }
    }

    private SearchResponse performSearchIntersects(
            SearchIntersectsRequest request)
    {
        try
        {
            return featureWebService.searchIntersects(request);
        }
        catch (ServiceException sx)
        {
            throw new Error("Unhandled ServiceException", sx);
        }
    }
}
