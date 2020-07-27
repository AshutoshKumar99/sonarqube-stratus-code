package com.pb.stratus.controller.json;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CustomJsonConverter implements JsonConverter
{

    public String toJson(Object o)
    {
        StringBuilder b = new StringBuilder();
        b.append("/*");
        try
        {
            process(o, b);
        }
        catch (Exception x)
        {
            throw new ConversionException(x);
        }
        b.append("*/");
        return b.toString();
    }

    private void process(Object bean, StringBuilder b) throws Exception
    {
        processValue(bean, b);
    }

    public void processValue(Object value, StringBuilder b)
    {
        if (value == null)
        {
            b.append("null");
        }
        else if (value instanceof String)
        {
            b.append("\"");
            b.append(StringEscapeUtils.escapeJavaScript(value.toString()));
            b.append("\"");
        }
        else if (value.getClass().isPrimitive() || isWrapper(value.getClass()))
        {
            b.append(value.toString());
        }
        else if (value.getClass().isArray())
        {
            Object[] os = (Object[]) value;
            b.append("[");
            int j = 0;
            for (Object o : os)
            {
                if (j++ > 0)
                {
                    b.append(", ");
                }
                processValue(o, b);
            }
            b.append("]");
        }
        else if (value instanceof List)
        {
            // XXX copy-and-paste
            List os = (List) value;
            b.append("[");
            int j = 0;
            for (Object o : os)
            {
                if (j++ > 0)
                {
                    b.append(", ");
                }
                processValue(o, b);
            }
            b.append("]");
        }
        else if (value instanceof Map)
        {
            Map om = (Map) value;
            int i = 0;
            b.append("{");
            Set keyset = om.keySet();
            for (Object key : keyset)
            {
                String name = key.toString();

                if (i++ > 0)
                {
                    b.append(", ");
                }
                b.append("\"");
                b.append(StringEscapeUtils.escapeJavaScript(name.toString()));
                b.append("\": ");
                Object propValue = om.get(key);

                processValue(propValue, b);
            }
            b.append("}");
        }
        else
        {
            PropertyDescriptor[] props = PropertyUtils
                    .getPropertyDescriptors(value);
            int i = 0;
            b.append("{");
            for (PropertyDescriptor prop : props)
            {
                String name = prop.getName();
                if (name.equals("class"))
                {
                    continue;
                }
                // If a property doesn't have a getter method then it won't be
                // serialized.
                // TODO: This should be logged as a warning.
                Method readMethod = prop.getReadMethod();
                if (readMethod != null)
                {
                    if (i++ > 0)
                    {
                        b.append(", ");
                    }

                    b.append("\"");
                    b.append(StringEscapeUtils
                            .escapeJavaScript(name.toString()));
                    b.append("\": ");
                    Object propValue;
                    try
                    {
                    	propValue = prop.getReadMethod().invoke(value, null);
                    }
                    catch (Exception x)
                    {
                    	throw new ConversionException(x);
                    }

                    processValue(propValue, b);
                }
            }
            b.append("}");

        }
    }

    @SuppressWarnings("unchecked")
    private boolean isWrapper(Class c)
    {
        return c.isAssignableFrom(Boolean.class)
                || c.isAssignableFrom(Byte.class)
                || c.isAssignableFrom(Integer.class)
                || c.isAssignableFrom(Float.class)
                || c.isAssignableFrom(Double.class)
                || c.isAssignableFrom(Long.class);
    }

}
