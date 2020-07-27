package com.pb.stratus.controller.service;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;

public class SearchParamsTest
{
    SearchParams searchParams;

    @Before
    public void setUp()
    {
        searchParams = new SearchParams();
        fillParamObject(searchParams);
    }

    @Test
    public void checkCopyFunction()
    {
        SearchParams expectedResult = new SearchParams();
        fillParamObject(expectedResult);
        SearchParams actualResult = searchParams.getCopy();
        assertTrue(expectedResult.equals(actualResult));
    }

    public static void fillParamObject(SearchParams searchParams)
    {
        searchParams.setLocale("en-US");
        searchParams.setPageLength(25);
        searchParams.setPageNumber(2);
        List<String> orderByList = new ArrayList<String>();
        orderByList.add("col1");
        searchParams.setOrderByList(orderByList);
        searchParams.setOrderByDirection("Asc");
        searchParams.setReturnTotalCount(true);
        List<String> attributes = new ArrayList<String>();
        attributes.add("col1");
        attributes.add("col2");
        attributes.add("col3");
        searchParams.setAttributes(attributes);
        searchParams.setIncludeGeometry(true);
        searchParams.setTargetSrs("espg:27700");
    }
}
