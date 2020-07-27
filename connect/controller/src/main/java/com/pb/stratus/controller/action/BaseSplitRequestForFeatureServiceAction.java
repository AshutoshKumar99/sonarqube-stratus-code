package com.pb.stratus.controller.action;


import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.info.FeatureSearchResultCombiningStrategy;
import com.pb.stratus.controller.service.SearchParams;
import com.pb.stratus.controller.service.SearchParamsSplitStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseSplitRequestForFeatureServiceAction extends
        FeatureSearchAction {
    public BaseSplitRequestForFeatureServiceAction(FeatureService featureService) {
        super(featureService);
    }

    /**
     * Make serial split request by creating a list of parameters with
     * different page lengths and page numbers and then combine the results.
     *
     * @param request the request of the caller that the response will be
     *                sent to.
     * @return
     * @throws ServletException
     * @throws IOException
     */
    protected Object createObject(HttpServletRequest request)
            throws ServletException, IOException {
        SearchParams params = constructParams(request);
        List<SearchParams> searchParams = SearchParamsSplitStrategy
                .splitRequestOnPageSize(params, MAX_PAGE_LENGTH, NEW_PAGE_LENGTH);
        return getCombinedResult(searchParams);
    }

    /**
     * Subclasses must construct parameter of their respective
     * implementations in this method.
     *
     * @param request
     * @return
     */
    protected abstract SearchParams constructParams(HttpServletRequest
                                                            request);

    /**
     * Combine the results of the split requests into a single object.
     *
     * @param searchParams
     * @return
     */
    protected FeatureSearchResult getCombinedResult(
            List<SearchParams> searchParams) {
        List<FeatureSearchResult> featureSearchResults = new
                ArrayList<FeatureSearchResult>();
        for (SearchParams searchParam : searchParams) {
            FeatureSearchResult searchResult =
                    getResultFromFeatureService(searchParam);
            featureSearchResults.add(searchResult);
        }
        return FeatureSearchResultCombiningStrategy.combine(featureSearchResults);
    }

    /**
     * Subclasses must claa the respective method of feature service in this
     * method.
     * Subclasses must handle their exceptions as they are not generic.
     *
     * @param searchParam
     * @return
     */
    protected abstract FeatureSearchResult getResultFromFeatureService(
            SearchParams searchParam);
}
