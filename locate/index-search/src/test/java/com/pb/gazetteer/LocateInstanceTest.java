package com.pb.gazetteer;

import com.pb.gazetteer.search.SearchLogic;
import com.pb.gazetteer.webservice.LocateException;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class LocateInstanceTest
{
    @Test(expected = LocateException.class)
    public void testSearch_noFactory() throws Exception
    {
        LocateInstance li = new LocateInstance();
        li.search(null);
    }

    @Test
    public void testSearch() throws LocateException
    {
        LocateInstance li = new LocateInstance();
        li.setName("testLiName");

        IndexSearchFactory mockFactory = mock(IndexSearchFactory.class);
        IndexSearch mockSearch = mock(IndexSearch.class);
        when(mockFactory.getIndex(li)).thenReturn(mockSearch);
        ArrayList<Address> expectedResult = new ArrayList<Address>();
        when(mockSearch.search("testQuery",SearchLogic.DEFAULT_LOGIC, 3)).thenReturn(expectedResult);
        li.setIndexFactory(mockFactory);

        SearchParameters sp = new SearchParameters();
        sp.setMaxRecords(3);
        sp.setSearchString("testQuery");
        sp.setSearchLogic(SearchLogic.DEFAULT_LOGIC);
        List<Address> actualResult = li.search(sp);
        assertSame(expectedResult, actualResult);
    }
}
