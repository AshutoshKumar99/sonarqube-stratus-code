package com.pb.stratus.controller.featuresearch;

import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchParams;

public class UnsupportedSearchStrategy implements SearchStrategy
{
    public FeatureSearchResult search(SearchParams searchParams)
    {
        throw new UnsupportedOperationException("The invoked search " 
                + "operation is not supported");
    }
}
