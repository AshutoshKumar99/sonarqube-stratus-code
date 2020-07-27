package com.pb.stratus.controller.featuresearch;

import java.util.HashMap;
import java.util.Map;

public class SearchStrategyRepository
{

    private Map<String, SearchStrategy> strategies =
            new HashMap<String, SearchStrategy>();

    private UnsupportedSearchStrategy unsupportedStrategy =
            new UnsupportedSearchStrategy();

    public void addSearchStrategy(String searchType, SearchStrategy strat)
    {
        strategies.put(searchType, strat);
    }

    public SearchStrategy getSearchStrategy(String searchType)
    {
        SearchStrategy strat = strategies.get(searchType);
        if (strat != null)
        {
            return strat;
        }
        else
        {
            return unsupportedStrategy;
        }
    }

}
