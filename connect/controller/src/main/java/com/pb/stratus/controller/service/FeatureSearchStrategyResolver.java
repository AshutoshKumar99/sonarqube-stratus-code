package com.pb.stratus.controller.service;

import com.mapinfo.midev.service.geometries.v1.*;

/**
 * Created with IntelliJ IDEA.
 * User: AR009SH
 * Date: 6/13/12
 * Time: 6:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class FeatureSearchStrategyResolver {

    public static final String SEARCH_BY_GEOMETRY = "searchByGeometry";
    //public static final String SEARCH_AT_POINT ="searchAtPoint";
    public static final String SEARCH_BY_GEOMETRY_IN_TABLES = "searchByGeometryInTables";

    public static String getSearchType(SearchParams params) {

        Geometry geom = params.getGeometry();
        if (null == geom) {

            throw new RuntimeException("geometry not found");
        }
        if (geom instanceof Polygon || geom instanceof LineString
                || geom instanceof Point || geom instanceof MultiPolygon
                || geom instanceof MultiFeatureGeometry) {
            if (params instanceof SearchWithinGeometryParams &&
                    ((SearchWithinGeometryParams) params).isSearchInTables()) {
                return SEARCH_BY_GEOMETRY_IN_TABLES;
            } else {
                return SEARCH_BY_GEOMETRY;
            }

        } else {
            //TODO Handle other cases
            return null;
        }

    }

}
