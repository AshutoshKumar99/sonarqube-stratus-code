package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.service.SearchWithinGeometryParams;
import com.pb.stratus.controller.util.helper.SpatialServicesHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * <p>
 * This class is responsible for providing the feature information for accessing
 * the Midev Feature WebService method searchByPolygon, from a web request.
 * </p>
 * <p>
 * searchByPolygon is a function that finds a list of features within the
 * specified geometry(circle,ellipse,rectangle,square,line string).
 * </p>
 * Created by GU003DU on 3/10/2016.
 */
public class FeatureSearchByGeometryInTablesActions extends FeatureSearchByGeometryAction {

    public FeatureSearchByGeometryInTablesActions(FeatureService featureService, SpatialServicesHelper sph) {
        super(featureService, sph);
    }

    protected void populateParams(SearchWithinGeometryParams params,
                                  HttpServletRequest request) {
        params.setSearchInTables(true);
        params.setReturnTotalCount(true);
        super.populateParams(params, request);
    }
}
