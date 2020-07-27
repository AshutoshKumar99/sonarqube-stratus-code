package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.AttributeList;
import com.mapinfo.midev.service.feature.v1.SearchIntersectsRequest;
import com.mapinfo.midev.service.feature.v1.SearchResponse;
import com.mapinfo.midev.service.feature.v1.SpatialSearchRequest;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.geometries.v1.Envelope;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.mapinfo.midev.service.geometries.v1.Pos;
import com.mapinfo.midev.service.table.v1.ViewTable;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchAtPointParams;
import com.pb.stratus.controller.service.SearchParams;
import com.pb.stratus.controller.util.EncodingUtil;
import uk.co.graphdata.utilities.contract.Contract;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Searches for features at a given point. An error margin in the form of a 
 * square with and edge length of <code>width</code> around the reference
 * point is taken into account. All features that intersect with the given
 * square will be returned by this search.
 */
public class SearchAtPointStrategy extends BaseSearchStrategy
{
    
    public SearchAtPointStrategy(FeatureServiceInterface featureWebService, 
            FeatureSearchResultConverterFactory converterFactory, 
            int maxResults)
    {
        super(featureWebService, converterFactory, maxResults);
    }

    public FeatureSearchResult search(SearchParams searchParams)
    {
        Contract.pre(searchParams instanceof SearchAtPointParams, 
                "SearchParams must be an instance of " 
                + SearchAtPointParams.class);
        SearchAtPointParams searchAtPointParams 
                = (SearchAtPointParams) searchParams;
        Map<String, SearchResponse> responses 
                = new LinkedHashMap<String, SearchResponse>();
        int remainingResultsCount = maxResults;
        for (String tableName : searchAtPointParams.getTables())
        {
			if (remainingResultsCount > 0) {
				SearchIntersectsRequest request = createRequest(tableName,
						searchAtPointParams, remainingResultsCount);
				// make sure we're not blowing away any existing attributes
				if(!searchAtPointParams.isIncludeGeometry() && !containsAttributes(request))
				{
					// we can assume that the geometry column will have one of the following names
					// we can just exclude them from the response - avoiding a describeTable call
					AttributeList excludeList = new AttributeList();
					excludeList.setExclude(Boolean.TRUE);
					excludeList.getAttributeName().add("OBJ");
					excludeList.getAttributeName().add("GEOM");
					request.setAttributeList(excludeList);
				}
            	SearchResponse response = performSearch(request);
                //commenting out so that will get given number of features per layer
//				remainingResultsCount -= response.getFeatureCollection()
//						.getFeatureCollectionMetadata().getCount();
				responses.put(tableName, response);
			}
        }
        return convertResponsesToFeatureSearchResult(responses, searchAtPointParams);
    }

    /**
	 * Determine whether or not the request contains any attributes.
	 */
	private static boolean containsAttributes(SearchIntersectsRequest request)
	{
		return request.getAttributeList() != null &&
				!request.getAttributeList().getAttributeName().isEmpty();
	}

    private FeatureSearchResult convertResponsesToFeatureSearchResult(
            Map<String, SearchResponse> responses, SearchParams params )
    {
        FeatureSearchResult joined = new FeatureSearchResult();
        for (Map.Entry<String, SearchResponse> entry : responses.entrySet())
        {
            String tableName = entry.getKey();
            FeatureSearchResult result = convertWebServiceResponse(
                    tableName, entry.getValue(), params);
            joined.addFeatureCollection(tableName,
                    result.getFeatureCollections().get(tableName));
            joined.addFeatureCollectionMetadata(tableName,
                    result.getFeatureCollectionsMetadata().get(tableName));
            joined.setMaxFeaturesCount(maxResults);
        }
        return joined;
    }

    private SearchResponse performSearch(SearchIntersectsRequest request)
    {
        try
        {
            return featureWebService.searchIntersects(request);
        }
        catch (ServiceException sx)
        {
            throw new Error("Unhandled service exception", sx);
        }
    }

    private SearchIntersectsRequest createRequest(String tableName,
            SearchAtPointParams params, int remainingResultsCount)
    {
        SearchIntersectsRequest request = new SearchIntersectsRequest();
        String querySQL= params.getQuerySQL(tableName);
        if (querySQL.contains("*")) {
            setViewTableForQuery(querySQL,request);
        }
        else
        {
            popuplateBaseValues(tableName, request, params);
        }


        double bufferMargin = params.getBufferMargin(tableName);
        Envelope env = createEnvelope(params.getPoint(), bufferMargin);
        request.setGeometry(env);
        request.setReturnTotalCount(params.getReturnTotalCount());
        request.setPageLength(remainingResultsCount);
        return request;
    }

    private void setViewTableForQuery (String querySQL,
                                       SearchIntersectsRequest request){
        ViewTable viewTable = new ViewTable();
        viewTable.setName("outer");
        viewTable.setSQL(EncodingUtil.decodeURIComponent(querySQL));
        request.setTable(viewTable);
    }
    
    private Envelope createEnvelope(Point point, double bufferMargin)
    {
        Envelope envelope = new Envelope();
        Pos ll = new Pos();
        double x = point.getPos().getX();
        double y = point.getPos().getY(); 
        ll.setX(x - bufferMargin);
        ll.setY(y - bufferMargin);
        Pos tr = new Pos();
        tr.setX(x + bufferMargin);
        tr.setY(y + bufferMargin);
        envelope.getPos().add(ll);
        envelope.getPos().add(tr);
        envelope.setSrsName(point.getSrsName());
        return envelope;
    }
}
