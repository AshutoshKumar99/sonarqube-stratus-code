package com.pb.gazetteer;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SearchParametersTest
{
    @Test
    public void testCreateSearchParameters()
    {
        SearchParameters searchParameters = new SearchParameters();
        searchParameters.setSearchString("testExpression");
        searchParameters.setMaxRecords(1234);
        searchParameters.setGazetteerName("testGazetteerName");
        searchParameters.setTenantName("testTenant");
        assertEquals("testExpression", searchParameters.getSearchString());
        assertEquals(1234, searchParameters.getMaxRecords());
        assertEquals("testGazetteerName", searchParameters.getGazetteerName());
        assertEquals("testTenant", searchParameters.getTenantName());
    }

    @Test
    public void testCreateSearchParametersNullValues()
    {
        SearchParameters searchParameters = new SearchParameters();
        assertNull(searchParameters.getSearchString());
        assertTrue(searchParameters.getMaxRecords() == 0);
        assertNull(searchParameters.getGazetteerName());
        assertNull(searchParameters.getTenantName());
    }
}
