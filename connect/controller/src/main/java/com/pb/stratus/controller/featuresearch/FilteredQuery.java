package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.BoundParameter;
import com.mapinfo.midev.service.geometries.v1.*;
import com.pb.stratus.controller.Constants;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.service.SearchByQueryParams;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Builds custom sql query when invoked using custom filter option from the query panel.
 */
public class FilteredQuery implements Query {

    private static final Logger log = LogManager.getLogger(FilteredQuery.class);

    @Override
    public String createSQLQuery(QueryMetadata queryMetadata,
                                   SearchByQueryParams params, boolean getCount,
                                   List<BoundParameter> boundParams, String geometryAttribute) {

        /*
         * example> "select \"Proposal01\" from \"PlanningApplications\" where
         * MI_Intersects(@POLYGON]',Obj) AND \"AppNumber\" = '9200153'ORDER BY
         * \"AppNumber\" AND time =@Time
         */

        StringBuilder b = new StringBuilder("select ");

        if (getCount) {
            b.append("count(*)");
        } else {
            b.append(constructSqlAttributeList(params, geometryAttribute));
        }

        b.append(" from ");
        String tableName = params.getTableName();
        String escapedAndQuotedTableName = QueryHelper.wrapWithQuotes(tableName.trim());
        b.append(escapedAndQuotedTableName);
        b.append(" where ");
        if (params.getGeometry() != null && params.getGeometry() instanceof Polygon) {
            b.append("MI_Intersects(@POLYGON,Obj) AND ");

        } else if (params.getGeometry() != null && params.getGeometry() instanceof LineString) {
            b.append("MI_Intersects(@MULTICURVE,Obj) AND ");
        } else if (params.getGeometry() != null && params.getGeometry() instanceof Point) {
            b.append("MI_Intersects(@POINT,Obj) AND ");
        } else if (params.getGeometry() != null && params.getGeometry() instanceof MultiPolygon) {
            b.append("MI_Intersects(@MULTIPOLYGON,Obj) AND ");
        }  else if (params.getGeometry() != null && params.getGeometry() instanceof MultiFeatureGeometry)
        {
            b.append("MI_Intersects(@MULTIFEATUREGEOMETRY,Obj) AND ");
        }

        b.append(QueryHelper.constructWhereConditions(queryMetadata, boundParams));
        if (!getCount) {
            b.append(QueryHelper.constructOrderByClause(params));
        }
        log.debug("query sent to MI Feature WebService :" + b.toString());
        return b.toString();

    }

    private String constructSqlAttributeList(SearchByQueryParams params, String geometryAttribute) {
        List<String> attrs = params.getAttributes();
        if (attrs.isEmpty()) {
            return "*";
        }
        StringBuilder b = new StringBuilder();
        int idx = 0;
        for (String attr : attrs) {
            if (idx++ > 0) {
                b.append(", ");
            }
            String escapedAndQuotedAttr = QueryHelper.wrapWithQuotes(
                    StringEscapeUtils.escapeSql(attr));
            b.append(escapedAndQuotedAttr);
        }
        if (params.isIncludeGeometry()) {
            b.append(", ");
            b.append("\"");
            b.append(geometryAttribute);
            b.append("\"");
        }
        /*Commenting out below code to add MIPRINX attribute as MI_PRINX is no longer going to be used for identifying unique query result rows.
        MI_KEY would be used instead on both OnPrem and Saas as per CONN-17926 & CONN-18087 .Code for that added below. */

        //add MI_PRINX - CONN15846 fix
        /*b.append(", ");
        b.append("\"");
        b.append(getMIPRINXAttributeFromTable(params.getTableName()));
        b.append("\"");
        */
        b.append(", ");
        b.append("\"");
        b.append("MI_KEY");
        b.append("\"");
        return b.toString();
    }

    @Override
    public boolean isCustomQuery(SearchByQueryParams params) {
        // CONN-18690: set if a user defined query or not
        String queryName = StringUtils.trimToEmpty(params.getQueryName());
        return queryName.equals(Constants.CUSTOM_FILTER) ? true : false;
    }
}
