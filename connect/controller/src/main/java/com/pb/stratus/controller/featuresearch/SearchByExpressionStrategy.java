package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.SearchBySQLRequest;
import com.mapinfo.midev.service.feature.v1.SearchBySQLResponse;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchByExpressionParams;
import com.pb.stratus.controller.service.SearchParams;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
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
public class SearchByExpressionStrategy extends BaseSearchStrategy
{
    
    public SearchByExpressionStrategy(
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
        FeatureSearchResult result =convertWebServiceResponse(searchBySqlParams.getTable(), 
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
        StringBuilder b = new StringBuilder("select ");
        b.append(constructSqlAttributeList(params));
        b.append(" from ");
        String tableName = params.getTable();
        String escapedAndQuotedTableName = wrapWithQuotes(tableName);
        b.append(escapedAndQuotedTableName);
        String expression = params.getExpression();
        if (StringUtils.isBlank(expression))
        {
            return b.toString();
        }
        b.append(" where ");
        b.append(expression);
        return b.toString();
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
        if (params.isIncludeGeometry())
        {
            b.append(", ");
            b.append("\"");
            b.append(getGeometryAttributeFromTable(params.getTable()));
            b.append("\"");
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
