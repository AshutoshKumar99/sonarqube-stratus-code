package com.pb.stratus.controller.action;

import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchByExpressionParams;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This class handles the requests for getting the feature information using search expression.
 */
public class FeatureSearchByExpressionAction extends FeatureSearchAction
{
    
    public FeatureSearchByExpressionAction(FeatureService featureService)
    {
        super(featureService);
    }

    protected Object createObject(HttpServletRequest request) 
            throws ServletException, IOException
    {
        
        FeatureSearchResult searchResult = getFeatureService()
                .searchByExpression(createParams(request));
        return searchResult;
    }
    
    private SearchByExpressionParams createParams(HttpServletRequest request)
    {
        SearchByExpressionParams params = new SearchByExpressionParams();
        populateParams(params, request);
        String tableName = request.getParameter("tableName");
        params.setTable(tableName);
        String searchExpression = request.getParameter("searchExpression");
        params.setExpression(searchExpression);
        return params;
        
    }
}
