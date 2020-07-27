package com.pb.stratus.controller.action;

import com.pb.stratus.controller.QueryType;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.service.SearchByQueryParams;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: VI021CH
 * Date: 12/5/14
 * Time: 5:37 PM
 * To change this template use File | Settings | File Templates.
 */
  /*This is used to get the unique record count of a column(specified in columnName).*/
public class RecordCountQueryAction extends FeatureSearchAction {

    public static final String TABLE_NAME = "tableName";
    public static final String COLUMN_NAME = "columnName";

    public RecordCountQueryAction(FeatureService featureService) {
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
        String columnName = request.getParameter(COLUMN_NAME);
        params.setColumnName(columnName);
        params.setQueryType(QueryType.RecordCountQuery);
    }

}

