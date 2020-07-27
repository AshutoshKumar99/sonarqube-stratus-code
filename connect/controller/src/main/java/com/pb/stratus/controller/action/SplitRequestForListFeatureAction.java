package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchFeaturesListParams;
import com.pb.stratus.controller.service.SearchParams;

import javax.servlet.http.HttpServletRequest;

/**
 * This class is responsible for splitting the MIDEV "listFeatures" request
 * into multiple
 * serial requests of smaller page lengths as there is a cap of 1000 records
 * at MIdev side.
 * The responses of each split request is then combined into one
 * FeatureSearchResult.
 */
public class SplitRequestForListFeatureAction extends
        BaseSplitRequestForFeatureServiceAction
{

    public SplitRequestForListFeatureAction(FeatureService featureService)
    {
        super(featureService);
    }

    @Override
    protected SearchParams constructParams(HttpServletRequest request)
    {
        SearchFeaturesListParams params = new SearchFeaturesListParams();
        super.populateParams(params, request);
        params.setTable(request.getParameter("tableName"));
        return params;
    }

    @Override
    protected FeatureSearchResult getResultFromFeatureService(
            SearchParams searchParam)
    {
        try
        {
            return getFeatureService()
                    .listFeatures((SearchFeaturesListParams) searchParam);
        } catch (ServiceException sx)
        {
            throw new Error("Unhandled ServiceException", sx);
        }
    }
}
