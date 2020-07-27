package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.SearchBySQLRequest;
import com.mapinfo.midev.service.feature.v1.SearchBySQLResponse;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchByExpressionParams;
import com.pb.stratus.controller.service.SearchParams;
import org.apache.commons.lang.StringEscapeUtils;
import uk.co.graphdata.utilities.contract.Contract;

import java.util.List;

/**
 * Searches for features based on an MISQL search expression. More 
 * specifically, the search expression corresponds to the WHERE clause
 * of a full MISQL query. 
 * 
 * @return FeatureSearchResult a possibly empty set of features grouped
 *         by table name
 */
public class SearchByExpressionGroupByStrategy extends BaseSearchStrategy
{
    
    public SearchByExpressionGroupByStrategy(
            FeatureServiceInterface featureWebService,
            FeatureSearchResultConverterFactory converterFactory, 
            int maxResults)
    {
        super(featureWebService, converterFactory, maxResults);
    }

    public FeatureSearchResult search(SearchParams searchParams)
    {
        Contract.pre(searchParams instanceof SearchByExpressionParams, 
                "SearchParams must be an instance of " 
                + SearchByExpressionParams.class);
        SearchByExpressionParams searchBySqlParams 
                = (SearchByExpressionParams) searchParams;
        SearchBySQLRequest request = createRequest(searchBySqlParams);
        SearchBySQLResponse response = performRequest(request);
        FeatureSearchResult result = convertWebServiceResponse(searchBySqlParams.getTable(), 
                response);
        return result;
    }
    
    private SearchBySQLRequest createRequest(SearchByExpressionParams params)
    {
        SearchBySQLRequest request = new SearchBySQLRequest();
        super.popuplateBaseValues(params.getTable(), request, 
                params);
        request.setSQL(constructSqlQuery(params));
        return request;
    }
    
    private String constructSqlQuery(SearchByExpressionParams params)
    {
        StringBuilder sb = new StringBuilder("select ");
        String tableName = wrapWithQuotes(params.getTable());
        sb.append(constructSqlAttributeList(params));
        sb.append(" from " + tableName);
        sb.append(" Group by " + constructSqlAttributeList(params));
        sb.append(" order by " + constructSqlAttributeList(params));
        return sb.toString();
    }
    
    private String constructSqlAttributeList(SearchByExpressionParams params)
    {
        List<String> attrs = params.getAttributes();
        if (attrs.isEmpty())
        {
            return "*";
        }
        StringBuilder b = new StringBuilder();
        int idx = 0;
        for (String attr : attrs)
        {
            if (idx++ > 0)
            {
                b.append(", ");
            }
            String escapedAndQuotedAttr = wrapWithQuotes(
                    StringEscapeUtils.escapeSql(attr));
            b.append(escapedAndQuotedAttr);
        }
        return b.toString();
    }
    

    /**
     * Wrap the columns and table name in quotes as this fixes the problem
     * with spaces in columns or table names
     * @param value to have quotes applied
     * @return a quoted string value
     */
    private String wrapWithQuotes(String value)
    {
        return "\"" + value + "\"";
    }

    private SearchBySQLResponse performRequest(SearchBySQLRequest request)
    {
        try
        {
            return featureWebService.searchBySQL(request);
        } 
        catch (ServiceException e)
        {
            throw new Error("Unhandled ServiceException", e);
        }
    }
    
}
