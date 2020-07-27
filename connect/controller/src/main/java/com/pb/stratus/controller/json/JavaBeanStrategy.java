package com.pb.stratus.controller.json;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import uk.co.graphdata.utilities.contract.Contract;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Converts an object into a JSON string based on it's JavaBean properties.
 * The <code>class</code> property will always be ignored. Equally, all 
 * properties that don't provide a public getter will be excluded from the
 * serialisation process.
 * 
 * @author Volker Leidl
 */
public class JavaBeanStrategy extends OwnedConverterStrategy
{
    
    private static final Logger logger = LogManager
            .getLogger(JavaBeanStrategy.class);
    
    private Map<Class<?>, Set<String>> excludedProperty 
            = new HashMap<Class<?>, Set<String>>(); 
    
    public void excludeProperty(Class<?> type, String propertyName)
    {
        Set<String> excludes = excludedProperty.get(type);
        if (excludes == null)
        {
            excludes = new HashSet<String>();
            excludedProperty.put(type, excludes);
        }
        excludes.add(propertyName);
    }
    
    /**
     * Converts the given <code>value</code> into a JSON string by retrieving
     * all it's readable JavaBean properties through introspection. The 
     * resulting JSON string will look like this:
     * <p>
     * <code>{"<propertyName>": <propertyValue>, ...}</code>
     * </p>
     * Property values will be processed - potentially recursively - by passing 
     * them back to the owning TypedConverter instance.
     * 
     * @value the value to serialise into a JSON string
     * @b a StringBuilder to write the resulting JSON string to
     * @throws ConversionException if a property getter could not be called
     *         successfully.
     */
    public void processValue(Object value, StringBuilder b)
    {
        Contract.pre(value != null, "Value required");
        List<PropertyDescriptor> props = getPropertyDescriptors(value);
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
            Method readMethod = prop.getReadMethod();
            if (readMethod == null)
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("No getter for property '" + name 
                            + "' on class '" + value.getClass().getName() 
                            + "'. Property will not be serialized");
                }
                continue;
            }
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
                propValue = prop.getReadMethod().invoke(value);
            }
            catch (Exception x)
            {
                throw new ConversionException(x);
            }

            processByOwner(propValue, b);
        }
        b.append("}");
    }
    
    protected List<PropertyDescriptor> getPropertyDescriptors(Object value)
    {
        PropertyDescriptor[] props = PropertyUtils
                .getPropertyDescriptors(value); 
        Set<String> excludes = excludedProperty.get(value.getClass());
        if (excludes == null)
        {
            return Arrays.asList(props);
        }
        else
        {
            List<PropertyDescriptor> propDescs 
                    = new LinkedList<PropertyDescriptor>();
            for (PropertyDescriptor prop : props)
            {
                if (!excludes.contains(prop.getName()))
                {
                    propDescs.add(prop);
                }
            }
            return propDescs;
        }
    }

}
