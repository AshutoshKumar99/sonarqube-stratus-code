package com.pb.stratus.controller.infrastructure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple object cache with a timeout strategy. Cache entries become outdated
 * after the timeout period specified when they were added to the cache. 
 * Outdated object are not removed from the cache before {@link #get(Object)} 
 * is called, to spare the overhead of a cleaner thread.
 * @deprecated
 */
public class Cache
{
    
    private Map<Object, CacheEntry> entries 
            = new HashMap<Object, CacheEntry>();
    
    public synchronized void add(Object key, Object value, int timeout)
    {
        CacheEntry entry = new CacheEntry(value, timeout);
        entries.put(key, entry);
    }
    
    public synchronized Object get(Object key)
    {
        clean();
        CacheEntry entry = entries.get(key);
        if (entry != null)
        {
            return entry.getValue();
        }
        else
        {
            return null;
        }
    }
    
    public synchronized void removeAll()
    {
        entries = new HashMap<Object, CacheEntry>();
    }
    
    private void clean()
    {
        for (Iterator<Map.Entry<Object, CacheEntry>> it 
                = entries.entrySet().iterator(); it.hasNext(); )
        {
            Map.Entry<Object, CacheEntry> entry = it.next();
            if (entry.getValue().isOutdated())
            {
                it.remove();
            }
        }
    }

    private class CacheEntry
    {
        
        private long timeout;
        
        private Object value;
        
        public CacheEntry(Object value, int timeout)
        {
            if (timeout < 0)
            {
                this.timeout = timeout;
            }
            else
            {
            this.timeout = System.currentTimeMillis() + timeout;
            }
            this.value = value;
        }
        
        public Object getValue()
        {
            return value;
        }
        
        public boolean isOutdated()
        {
            if (timeout < 0)
            {
                return false;
            }
            return System.currentTimeMillis() >= timeout;
        }
        
    }
    
}
