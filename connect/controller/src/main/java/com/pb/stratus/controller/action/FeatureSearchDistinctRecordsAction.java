package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.service.SearchByExpressionParams;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for providing the list of features information for accessing
 * the Midev Feature WebService method listFeatures, from a web request.
 * 
 * listFeatures is a function that finds a list of features for the
 * specified table.
 * 
 * This class uses an Midev webservice Artifact.
 */
public class FeatureSearchDistinctRecordsAction extends FeatureSearchAction
{

    public FeatureSearchDistinctRecordsAction(FeatureService featureService)
    {
        super(featureService);
    }

    protected Object createObject(HttpServletRequest request)
    {
        SearchByExpressionParams params = new SearchByExpressionParams();
      super.populateParams(params, request);

      String tableName = request.getParameter("tableName");
      params.setTable(tableName);

      String attributes = request.getParameter("attributes");
      List<String> attributeList = new ArrayList<String>();
      attributeList.add(attributes);
      params.setAttributes(attributeList);
        try
        {
            return getFeatureService().distinctRecords(params);
        }
        catch (ServiceException sx)
        {
            throw new Error("Unhandled ServiceException", sx);
        }
    }

}
