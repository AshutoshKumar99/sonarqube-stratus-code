package com.pb.stratus.controller.legend;

import org.apache.commons.lang.ArrayUtils;

import java.util.Locale;

/**
 * @deprecated
 */
public class CacheKey
{
    
    private String[] mapNames;
    
    private Locale locale;
    
    public CacheKey(String[] mapNames, Locale locale)
    {
        this.mapNames = mapNames;
        this.locale = locale;
    }
    
    public int hashCode()
    {
        int hc = 31 * ArrayUtils.hashCode(mapNames);
        hc = hc * locale.hashCode();
        return hc;
    }
    
    public boolean equals(Object o)
    {
        if (o == null)
        {
            return false;
        }
        if (o.getClass() != this.getClass())
        {
            return false;
        }
        CacheKey that = (CacheKey) o;
        if (!ArrayUtils.isEquals(mapNames, that.mapNames))
        {
            return false;
        }
        if (!locale.equals(that.locale))
        {
            return false;
        }
        return true;
    }
    
    public String toString()
    {
        return getClass().getName() + "[" + ArrayUtils.toString(mapNames) 
                + ", " + locale + "]"; 
    }
    

}
