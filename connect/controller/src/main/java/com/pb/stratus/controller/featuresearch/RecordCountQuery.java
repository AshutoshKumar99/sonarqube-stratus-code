package com.pb.stratus.controller.featuresearch;
import com.mapinfo.midev.service.feature.v1.BoundParameter;
import com.pb.stratus.controller.featuresearch.Query;
import com.pb.stratus.controller.featuresearch.QueryHelper;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.service.SearchByQueryParams;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: VI021CH
 * Date: 12/8/14
 * Time: 11:08 AM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Builds the SQL query for getting unique record values and corresponding count of a column . E.g.
 *  select Grade, Count(*) from "/CamdenBNG/NamedTables/ListedBuildings" GROUP BY Grade
 */
public class RecordCountQuery implements Query {

    @Override
    public String createSQLQuery(QueryMetadata queryMetadata,
                                 SearchByQueryParams params, boolean getCount,
                                 List<BoundParameter> boundParams, String geometryAttribute) {


        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        String columnName = params.getColumnName();
        String escapedAndQuotedColumnName = QueryHelper.wrapWithQuotes(StringUtils.trim(columnName));
        sqlQuery.append(escapedAndQuotedColumnName);
        sqlQuery.append("as FTR_VALUE");
        sqlQuery.append(",COUNT(*) as FTR_COUNT ");
        sqlQuery.append("FROM ");
        String tableName = params.getTableName();
        String escapedAndQuotedTableName = QueryHelper.wrapWithQuotes(StringUtils.trim(tableName));
        sqlQuery.append(escapedAndQuotedTableName);
        sqlQuery.append(" GROUP BY ");
        sqlQuery.append(escapedAndQuotedColumnName);
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

