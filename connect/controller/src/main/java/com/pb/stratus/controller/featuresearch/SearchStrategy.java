package com.pb.stratus.controller.featuresearch;

import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchParams;

/**
 * Performs a feature search, following an arbitrary implementation strategy.
 */
public interface SearchStrategy
{
    FeatureSearchResult search(SearchParams searchParams);
}
