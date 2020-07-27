package com.pb.stratus.controller.action;

import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchWithinGeometryParams;
import com.pb.stratus.controller.service.SearchWithinGeometryParams.SpatialOperation;
import com.pb.stratus.controller.util.helper.SpatialServicesHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This class is responsible for providing the feature information for accessing
 * the Midev Feature WebService method searchByPolygon, from a web request.
 * 
 * searchByPolygon is a function that finds a list of features within the
 * specified geometry(circle,ellipse,rectangle,square,line string).
 * 
 * This class uses an Midev webservice Artifact.
 */
public class FeatureSearchByGeometryAction extends FeatureSearchAction
{

    public FeatureSearchByGeometryAction(FeatureService featureService, SpatialServicesHelper sph)
    {
        super(featureService);
        /**
         * Adding a cross service helper class - In particular I need it to call geometry service from feature service
         * to pass the Union geometry.
         */
        setSph(sph);
    }

    protected Object createObject(HttpServletRequest request)
            throws ServletException, IOException
    {
    	SearchWithinGeometryParams params = new SearchWithinGeometryParams();
        populateParams(params, request);
             
        FeatureSearchResult searchResult = getFeatureService().searchByGeometry(
                params);
        return searchResult;
    }

    protected void populateParams(SearchWithinGeometryParams params,
            HttpServletRequest request)
    {
        super.populateParams(params, request);
        String tableName = request.getParameter("tableName");
        String spatialOperation = request.getParameter("spatialOperation");
        params.setSourceSrs(getSourceSrs(request));
        params.setTable(tableName);
        SpatialOperation operation = SpatialOperation.valueOf(spatialOperation);
        params.setSpatialOperation(operation);
    }
}
