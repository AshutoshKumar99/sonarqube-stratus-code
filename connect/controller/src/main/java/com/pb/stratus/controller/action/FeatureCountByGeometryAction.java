package com.pb.stratus.controller.action;

import com.pb.stratus.controller.QueryType;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.service.SearchByQueryParams;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 10/10/14
 * Time: 12:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeatureCountByGeometryAction extends FeatureSearchAction {

    public static final String TABLE_NAME = "tableName";

    public FeatureCountByGeometryAction(FeatureService featureService) {
        super(featureService);
    }

    @Override
    protected Object createObject(HttpServletRequest request) {
        SearchByQueryParams params = new SearchByQueryParams();
        populateParams(params, request);
        return getFeatureService().searchByQuery(params);
    }

    protected void populateParams(SearchByQueryParams params, HttpServletRequest request) {
        super.populateParams(params, request);
        String tableName = request.getParameter(TABLE_NAME);
        params.setTableName(tableName);
        params.setQueryType(QueryType.GeometryCountQuery);
    }

}
