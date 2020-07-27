package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.BoundParameter;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.service.SearchByQueryParams;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by vi012gu on 7/14/2015.
 */
public class ApplicationLinkingQuery implements Query {

    @Override
    public String createSQLQuery(QueryMetadata queryMetadata, SearchByQueryParams params, boolean getCount,
                                 List<BoundParameter> boundParams, String geometryAttribute) {
        /**
         * example : SELECT MI_AggregateEnvelope("Obj") FROM "/nonspatialdata/nsHouseDetail" WHERE HouseNumber  IN ('132')
         */
        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append(constructSqlFunctionalAttributeList(params));
        sqlQuery.append(" FROM ");
        String table = params.getTableName();
        String escapedAndQuotedTableName = QueryHelper.wrapWithQuotes(StringUtils.trim(table));
        sqlQuery.append(escapedAndQuotedTableName);
        sqlQuery.append(" WHERE ");
        sqlQuery.append(params.getColumnName());
        sqlQuery.append(" IN");
        sqlQuery.append(" ("+params.getParams()+")");
        return sqlQuery.toString();
    }

    /**
     * Builds the SQL Select clause with all the functions that need to be applied on the given columns
     * @param params
     * @return select portion of the SQL
     */
    private String constructSqlFunctionalAttributeList(SearchByQueryParams params) {
        StringBuilder selectClause = new StringBuilder();
        Map<String, List<String>> functionAttributesMap = params.getFunctionAttributes();
        for (Map.Entry<String, List<String>> entry : functionAttributesMap.entrySet()) {
            String attribute = entry.getKey();
            String escapedAndQuotedAttribute = QueryHelper.wrapWithQuotes(
                    StringEscapeUtils.escapeSql(attribute));

            List<String> functionsList = entry.getValue();
            for (String function : functionsList) {
                if (selectClause.length() > 0) {
                    selectClause.append(", ");
                }
                selectClause.append(function);
                selectClause.append(QueryHelper.wrapWithParentheses(escapedAndQuotedAttribute));
                selectClause.append(" AS ");
                selectClause.append(function + "_" + attribute);
            }
        }
        return selectClause.toString();
    }

    /**
     * Returns true always. It is more meaningful in its sibling implementation.
     * @param params
     * @return
     */
    @Override
    public boolean isCustomQuery(SearchByQueryParams params) {
        return true;
    }
}
