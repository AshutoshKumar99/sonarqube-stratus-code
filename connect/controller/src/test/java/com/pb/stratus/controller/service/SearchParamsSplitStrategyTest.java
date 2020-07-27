package com.pb.stratus.controller.service;


import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchParamsSplitStrategyTest
{
    @Test
    public void testForPageLengthLessThanMax()
    {
        SearchParams searchParams = new SearchParams();
        searchParams.setPageLength(999);
        searchParams.setPageNumber(2);
        List<SearchParams> actualResult =  SearchParamsSplitStrategy
            .splitRequestOnPageSize(searchParams, 1000, 999);
        assertTrue(actualResult.size() == 1);
    }

    @Test
    public void testForPageLengthGreaterThanMax()
    {
        SearchParams searchParams = new SearchParams();
        searchParams.setPageLength(3658);
        searchParams.setPageNumber(1);
        List<SearchParams> actualResult =  SearchParamsSplitStrategy
            .splitRequestOnPageSize(searchParams, 1000, 999);
        assertTrue(actualResult.size() == 4);
    }

    @Test
    public void testForPageLengthAfterSplitting()
    {
        SearchParams searchParams = new SearchParams();
        searchParams.setPageLength(3658);
        searchParams.setPageNumber(1);
        List<SearchParams> actualResult =  SearchParamsSplitStrategy
            .splitRequestOnPageSize(searchParams, 1000, 999);
        assertEquals(999, actualResult.get(0).getPageLength());
        assertEquals(999, actualResult.get(1).getPageLength());
        assertEquals(999, actualResult.get(2).getPageLength());
        assertEquals(661, actualResult.get(3).getPageLength());
    }

    @Test
    public void testForPageNumberAfterSplitting()
    {
        SearchParams searchParams = new SearchParams();
        searchParams.setPageLength(3658);
        searchParams.setPageNumber(1);
        List<SearchParams> actualResult =  SearchParamsSplitStrategy
            .splitRequestOnPageSize(searchParams, 1000, 999);
        assertEquals(1, actualResult.get(0).getPageNumber());
        assertEquals(2, actualResult.get(1).getPageNumber());
        assertEquals(3, actualResult.get(2).getPageNumber());
        assertEquals(4, actualResult.get(3).getPageNumber());
    }
}
