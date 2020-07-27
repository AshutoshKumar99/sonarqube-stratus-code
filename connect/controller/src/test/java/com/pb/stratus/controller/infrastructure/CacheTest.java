package com.pb.stratus.controller.infrastructure;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CacheTest
{
    
    private Cache cache;

    @Before
    public void setUp()
    {
        cache = new Cache();
    }
    
    @Test
    public void shouldReturnNullIfNotPresent()
    {
        Object key = new Object();
        assertNull(cache.get(key));
    }
    
    @Test
    public void shouldReturnExpectedEntryForGivenKey()
    {
        Object key = new Object();
        Object value = new Object();
        int longTimeout = 100000000;
        cache.add(key, value, longTimeout);
        assertEquals(value, cache.get(key));
    }
    
    @Test
    public void shouldDiscardTimedOutEntries() throws Exception
    {
        Object key = new Object();
        Object value = new Object();
        int shortTimeout = 100;
        cache.add(key, value, shortTimeout);
        //XXX Thread.sleep() might sleep less than the given amount of time.
        //    that's why we sleep for twice the necessary time. Still not 
        //    entirely sure if that will succeed all the time.
        Thread.sleep(200);
        assertNull(cache.get(key));
    }

    @Test
    public void shouldReturnExpectedEntriesForInfiniteTime() throws Exception
    {
        List<String> key = new ArrayList<String>();
        key.add("test");
        Object value = new Object();
        int shortTimeout = -1;
        cache.add(key, value, shortTimeout);
        key = new ArrayList<String>();
        key.add("test");
        assertEquals(value, cache.get(key));
}

}
