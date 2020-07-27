package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.BoundParameter;
import com.mapinfo.midev.service.geometries.v1.*;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.service.SearchByQueryParams;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

import static com.pb.stratus.controller.service.SearchWithinGeometryParams.SpatialOperation;

/**
 * Builds the SQL query for summarizing numeric columns within the given table
 */
public class SummarizedQuery implements Query {

    private static final Logger log = LogManager.getLogger(SummarizedQuery.class);
    @Override
    public String createSQLQuery(QueryMetadata queryMetadata,
                                  SearchByQueryParams params, boolean getCount,
                                  List<BoundParameter> boundParams, String geometryAttribute) {
        /*
         * example> "select count(*), SUM(\"SALES_FIGURES\") from \"PlanningApplications\" where
         * MI_Intersects(@POLYGON]',Obj)
         */

        StringBuilder sqlQuery = new StringBuilder("SELECT ");
        sqlQuery.append("count(*) AS COUNT");
        sqlQuery.append(constructSqlFunctionalAttributeList(params));
        sqlQuery.append(constructAdditionalSqlAttribute(params, geometryAttribute));
        sqlQuery.append(" FROM ");
        String tableName = params.getTableName();
        String escapedAndQuotedTableName = QueryHelper.wrapWithQuotes(StringUtils.trim(tableName));
        sqlQuery.append(escapedAndQuotedTableName);
        sqlQuery.append(constructWhereClause(params));
        return sqlQuery.toString();
    }

    /**
     * Builds the SQL Select clause with all the functions that need to be applied on the given columns
     * @param params
     * @return select portion of the SQL
     */
    private String constructSqlFunctionalAttributeList(SearchByQueryParams params) {
        StringBuilder selectSqlPart1 = new StringBuilder();
        Map<String, List<String>> functionAttributesMap = params.getFunctionAttributes();
        for (Map.Entry<String, List<String>> entry : functionAttributesMap.entrySet()) {
            String attribute = entry.getKey();
            String escapedAndQuotedAttribute = QueryHelper.wrapWithQuotes(
                    StringEscapeUtils.escapeSql(attribute));

            List<String> functionsList = entry.getValue();
            for (String function : functionsList) {
                selectSqlPart1.append(", ");
                selectSqlPart1.append(function);
                selectSqlPart1.append(QueryHelper.wrapWithParentheses(escapedAndQuotedAttribute));
                selectSqlPart1.append(" AS ");
                selectSqlPart1.append(function + "_" + attribute);
            }
        }
        return selectSqlPart1.toString();
    }

    /**
     * Builds the SQL Select clause with the geometry attribute
     * @param params
     * @param geometryAttribute
     * @return select portion of the SQL
     */
    private String constructAdditionalSqlAttribute(SearchByQueryParams params, String geometryAttribute) {
        StringBuilder selectSqlPart2 = new StringBuilder();

        if (params.isIncludeGeometry()) {
            selectSqlPart2.append(", ");
            selectSqlPart2.append(QueryHelper.wrapWithQuotes(geometryAttribute));
        }
        return selectSqlPart2.toString();
    }

    /**
     * Builds the where clause of the SQL statement based on whether the features need to be queried lie within the
     * or intersects the given geometry
     * @param params
     * @return where portion of the SQL
     */
    private String constructWhereClause(SearchByQueryParams params) {
        StringBuilder whereString = new StringBuilder();
        whereString.append(" WHERE ");

        SpatialOperation spatialOperation = params.getSpatialOperation();
        if (SpatialOperation.ENTIRELYWITHIN == spatialOperation) {
            whereString.append("MI_Contains");
        } else {
            whereString.append("MI_Intersects");
        }

        Geometry geometryObject = params.getGeometry();
        if (geometryObject instanceof Polygon) {
            whereString.append("(@POLYGON,Obj)");
        } else if (geometryObject instanceof LineString) {
            whereString.append("(@MULTICURVE,Obj)");
        } else if (geometryObject instanceof Point) {
            whereString.append("(@POINT,Obj)");
        } else if (geometryObject instanceof MultiPolygon) {
            whereString.append("(@MULTIPOLYGON,Obj)");
        }  else if (geometryObject instanceof MultiFeatureGeometry) {
            whereString.append("(@MULTIFEATUREGEOMETRY,Obj)");
        }
        log.info("Summarization query sent to MI Feature WebService :" + whereString);
        return whereString.toString();
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
