package com.pb.stratus.controller.action;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.json.geojson.GeoJsonParser;
import com.pb.stratus.controller.service.SearchParams;
import com.pb.stratus.controller.service.SearchWithinGeometryParams;
import com.pb.stratus.controller.util.helper.SpatialServicesHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * This class is responsible for splitting the MIDEV "searchByPolygon" request
 * into multiple
 * serial requests of smaller page lengths as there is a cap of 1000 records
 * at MIdev side.
 * The responses of each split request is then combined into one
 * FeatureSearchResult.
 */
public class SplitRequestForSearchByPolygonAction extends
        BaseSplitRequestForFeatureServiceAction
{
    public SplitRequestForSearchByPolygonAction(FeatureService featureService, SpatialServicesHelper spatialServicesHelper)
    {
        super(featureService);
        setSph(spatialServicesHelper);
    }

    @Override
    protected SearchParams constructParams(HttpServletRequest request)
    {
    	SearchWithinGeometryParams params = new SearchWithinGeometryParams();
        populateParams(params, request);
        return params;
    }

    @Override
    protected FeatureSearchResult getResultFromFeatureService(SearchParams searchParam)
    {
        return getFeatureService().searchByGeometry(
                (SearchWithinGeometryParams)searchParam);
    }

    protected void populateParams(SearchWithinGeometryParams params,
            HttpServletRequest request)
    {
        super.populateParams(params, request);
        String tableName = request.getParameter("tableName");
        String geoJson = request.getParameter("geometry");
        String spatialOperation = request.getParameter("spatialOperation");
        params.setSourceSrs(getSourceSrs(request));
        GeoJsonParser parser = new GeoJsonParser(getSourceSrs(request));
        params.setTable(tableName);
//        params.setMultiPolygon((MultiPolygon) parser
//                .parseGeometry(geoJson));
        Geometry geom = parser.parseGeometry(geoJson);

        //params.setMultiPolygon((MultiPolygon) parser.parseGeometry(geometry));
        params.setGeometry(geom);
        SearchWithinGeometryParams.SpatialOperation operation =
        		SearchWithinGeometryParams.SpatialOperation.valueOf(spatialOperation);
        params.setSpatialOperation(operation);
    }
}
