package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.BoundParameter;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.service.SearchByQueryParams;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: VI012GU
 * Date: 10/10/14
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeatureCountByGeometryQuery implements Query {

    @Override
    public String createSQLQuery(QueryMetadata queryMetadata,
                                 SearchByQueryParams params, boolean getCount,
                                 List<BoundParameter> boundParams, String geometryAttribute) {
        /*
         * example> "select MI_GeometryType(Obj) as MI_FEATURETYPE, count(*) as FTR_COUNT
         * from "/QA-Maps/NamedTables/ListedBuildings" GROUP BY MI_GeometryType(Obj)
         */

        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append("MI_GeometryType(Obj) as MI_FEATURETYPE, ");
        sqlQuery.append("COUNT(*) as FTR_COUNT ");
        sqlQuery.append("FROM ");
        String tableName = params.getTableName();
        String escapedAndQuotedTableName = QueryHelper.wrapWithQuotes(StringUtils.trim(tableName));
        sqlQuery.append(escapedAndQuotedTableName);
        sqlQuery.append(" GROUP BY MI_GeometryType(Obj)");
        return sqlQuery.toString();
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