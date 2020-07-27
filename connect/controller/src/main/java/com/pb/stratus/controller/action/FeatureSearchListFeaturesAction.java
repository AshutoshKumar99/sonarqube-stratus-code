package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.service.SearchFeaturesListParams;

import javax.servlet.http.HttpServletRequest;

/**
 * This class is responsible for providing the list of features information for accessing
 * the Midev Feature WebService method listFeatures, from a web request.
 * 
 * listFeatures is a function that finds a list of features for the
 * specified table.
 * Used for All - Data - No Filter Query
 * This class uses an Midev webservice Artifact.
 */
public class FeatureSearchListFeaturesAction extends FeatureSearchAction
{

    public FeatureSearchListFeaturesAction(FeatureService featureService)
    {
        super(featureService);
    }

    protected Object createObject(HttpServletRequest request)
    {
        SearchFeaturesListParams params = new SearchFeaturesListParams();
        super.populateParams(params, request);
        params.setTable(request.getParameter("tableName"));
        
        try
        {
            return getFeatureService().listFeatures(params);
        }
        catch (ServiceException sx)
        {
            throw new Error("Unhandled ServiceException", sx);
        }
    }

}
