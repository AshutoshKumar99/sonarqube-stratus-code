package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.BoundParameter;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.service.SearchByQueryParams;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ar009sh
 * Date: 1/12/15
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataBindingQuery implements Query {


    @Override
    public String createSQLQuery(QueryMetadata queryMetadata, SearchByQueryParams params, boolean getCount, List<BoundParameter> boundParams, String geometryAttribute) {
        /**
         * example : SELECT HouseNumber FROM "/nonspatialdata/nsHouseDetail" WHERE HouseNumber  IN ('132') LIMIT 1000
         * Tested in spatialOpInstall
         */
        StringBuilder sqlQuery = new StringBuilder("SELECT");
        sqlQuery.append(" * ");
        sqlQuery.append("FROM ");
        String nonspatialTable = params.getTableName();
        String escapedAndQuotedTableName = QueryHelper.wrapWithQuotes(StringUtils.trim(nonspatialTable));
        sqlQuery.append(escapedAndQuotedTableName);
        sqlQuery.append(" WHERE ");
        //spatialtable.databindColumn
        String bindColumnName = params.getColumnName();
        sqlQuery.append(bindColumnName);
        sqlQuery.append(" IN");
        //value of spatialColumn
        sqlQuery.append(" ("+params.getParams()+")");
        return sqlQuery.toString();

    }

    @Override
    public boolean isCustomQuery(SearchByQueryParams params) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
