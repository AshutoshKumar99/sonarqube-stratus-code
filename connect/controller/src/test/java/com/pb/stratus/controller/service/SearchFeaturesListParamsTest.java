package com.pb.stratus.controller.service;


import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class SearchFeaturesListParamsTest
{
    SearchFeaturesListParams searchFeaturesListParams;

    @Before
    public void setUp()
    {
        searchFeaturesListParams = new SearchFeaturesListParams();
        fillParamObject(searchFeaturesListParams);
    }

    @Test
    public void testGetCopyFunction()
    {
        SearchFeaturesListParams expectedResult = new
                SearchFeaturesListParams();
        fillParamObject(expectedResult);
        SearchFeaturesListParams actualResult = searchFeaturesListParams
                .getCopy();
        assertTrue(expectedResult.equals(actualResult));
    }

     public static void fillParamObject(SearchFeaturesListParams searchParams)
     {
         SearchParamsTest.fillParamObject(searchParams);
         searchParams.setTable("table1");
     }
}
