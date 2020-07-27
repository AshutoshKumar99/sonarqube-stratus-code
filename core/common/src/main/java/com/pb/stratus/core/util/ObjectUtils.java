package com.pb.stratus.core.util;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ObjectUtils
{

    public static final int SEED = 31;

    public static <T> T castToOrReturnNull(Class<T> type, Object o)
    {
        if (o == null)
        {
            return null;
        }
        if (o.getClass() != type)
        {
            return null;
        }
        return type.cast(o);
    }

    public static boolean equals(Object value1, Object value2)
    {
        if (value1 == null)
        {
            return value2 == null;
        }
        else
        {
            return value1.equals(value2);
        }
    }

    public static boolean equalTypes(Object value1, Object value2)
    {
        if (value1 == null)
        {
            return value2 == null;
        }
        else if (value2 == null)
        {
            return value1 == null;
        }
        else
        {
            return value1.getClass() == value2.getClass();
        }
    }

    public static boolean equals(Object[] value1, Object[] value2)
    {
        if (value1 == null)
        {
            return value2 == null;
        }
        return Arrays.equals(value1, value2);
    }

    public static int hash(int seed, Object obj)
    {
        int result = seed;
        if (obj == null)
        {
            result = hash(result, 0);
        }
        else if (obj.getClass().isArray())
        {
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++)
            {
                Object item = Array.get(obj, i);
                // XXX recursive call, what if we have a circular ref?
                result = hash(result, item);
            }
        }
        else
        {
            result = result * SEED + obj.hashCode();
        }
        return result;
    }

}
