package com.pb.stratus.controller.service;

import com.pb.stratus.core.common.Preconditions;

import java.util.ArrayList;
import java.util.List;

/**
 * Class will split a SearchParams based on the page length as Midev has a
 * maximum cap on the maximum features that can be requested. So if it is
 * required to get more features we must split the request into multiple
 * requests varying just in page length and page number.
 */
public final class SearchParamsSplitStrategy
{
    private SearchParamsSplitStrategy(){}

    /**
     * Split the request param into multiple request params if the page
     * length is more than maximum allowed.
     * If the page length is in permissible limit then the original param is
     * added to the list and returned.
     * @param searchParams SearchParams the original request.
     * @param maxPageLength int The maximum length a request can have.
     * @param newPageLength int The page length of the new split requests.
     * @return List<SearchParams>
     */
    public static List<SearchParams> splitRequestOnPageSize(SearchParams
            searchParams, int maxPageLength, int newPageLength)
    {
        Preconditions.checkNotNull(searchParams,
                "SearchParams should not be null");
        List<SearchParams> searchParamsList = new ArrayList<SearchParams>();
        if(searchParams.getPageLength() >= maxPageLength)
        {
            splitParamsInMultiplePages(searchParamsList, searchParams,
                    newPageLength);
        }
        else
        {
            searchParamsList.add(searchParams);
        }
        return searchParamsList;
    }

    private static void splitParamsInMultiplePages(List<SearchParams>
            searchParamsList, SearchParams searchParams, int newPageLength)
    {
        int totalPages = searchParams.getPageLength() / newPageLength;
        int recordsInLastPage = searchParams.getPageLength() % newPageLength;
        if(recordsInLastPage > 0)
        {
            totalPages++;
        }
        for(int pageNum=1;pageNum<=totalPages;pageNum++)
        {
            SearchParams params = searchParams.getCopy();
            params.setPageNumber(pageNum);
            if(pageNum == totalPages)
            {
                params.setPageLength(recordsInLastPage);
            }
            else
            {
                params.setPageLength(newPageLength);
            }
            searchParamsList.add(params);
        }
    }
}
