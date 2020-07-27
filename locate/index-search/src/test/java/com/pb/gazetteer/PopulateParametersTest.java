package com.pb.gazetteer;

import com.pb.gazetteer.search.SearchLogic;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PopulateParametersTest
{
    @Test
    public void testGetTenantName() throws Exception
    {
        PopulateParameters pp = new PopulateParameters();
        pp.setTenantName("testTenant");
        assertEquals("testTenant", pp.getTenantName());
    }

    @Test
    public void testGetGazetteerName() throws Exception
    {
        PopulateParameters pp = new PopulateParameters();
        pp.setGazetteerName("testGaz");
        assertEquals("testGaz", pp.getGazetteerName());
    }

    @Test
    public void testGetAddressColumn() throws Exception
    {
        PopulateParameters pp = new PopulateParameters();
        pp.setAddressColumn(3);
        assertEquals(3, pp.getAddressColumn());
    }

    @Test
    public void testGetxColumn() throws Exception
    {
        PopulateParameters pp = new PopulateParameters();
        pp.setxColumn(4);
        assertEquals(4, pp.getxColumn());
    }

    @Test
    public void testGetyColumn() throws Exception
    {
        PopulateParameters pp = new PopulateParameters();
        pp.setyColumn(5);
        assertEquals(5, pp.getyColumn());
    }

    @Test
    public void testGetMaxFailures() throws Exception
    {
        PopulateParameters pp = new PopulateParameters();
        pp.setMaxFailures(6);
        assertEquals(6, pp.getMaxFailures());

	      pp.setMaxFailures(null);
        assertEquals(Integer.MAX_VALUE, pp.getMaxFailures());
    }

    @Test
    public void testGetDelimiter() throws Exception
    {
        PopulateParameters pp = new PopulateParameters();
        pp.setDelimiter("#");
        assertEquals("#", pp.getDelimiter());
    }
    
    @Test
    public void testGetProjection() throws Exception
    {
        PopulateParameters pp = new PopulateParameters();
        pp.setProjection("EPSG:3857");
        assertEquals("EPSG:3857", pp.getProjection());
}
    
    @Test
    public void testGetSearchLogic() throws Exception
    {
        PopulateParameters pp = new PopulateParameters();
        pp.setSearchLogic(SearchLogic.DEFAULT_LOGIC);
        assertEquals(SearchLogic.DEFAULT_LOGIC, pp.getSearchLogic());
    }
}
