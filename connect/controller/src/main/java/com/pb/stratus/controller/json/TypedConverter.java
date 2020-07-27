package com.pb.stratus.controller.json;

import uk.co.graphdata.utilities.contract.Contract;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * A typed converter is a JSON converter that converts objects based on their
 * Java class. The logic of how to convert a specific data type into a JSON
 * String is encapsulated in instances of (@link TypedConverterStrategy}. 
 * Some common types are recognised out of the box. Those types are: 
 * <ul>
 *     <li>java.util.Map</li>
 *     <li>java.util.Collection</li>
 *     <li>java.lang.String</li>
 *     <li>java.lang.Number</li>
 * </ul>
 * New strategies can be added by calling 
 * {@link #addStrategy(Class, TypedConverterStrategy)}.
 * 
 * <p>
 * For more details about the type lookup algorithm see 
 * {@link #getStrategy(Object)}. Before the type lookup algorithm starts, the
 * value is checked if it is <code>null</code>, an array, or a primitive value.
 * If one of those conditions applies, one of {@link NullStrategy}, 
 * {@link CollectionStrategy}, or {@link ScalarStrategy} will be used, 
 * respectively, to convert the value into a JSON string. 
 * </p>
 * 
 * @author Volker Leidl
 */
public class TypedConverter implements JsonConverter, TypedConverterStrategy
{
    
    private Map<Class<?>, TypedConverterStrategy> strats 
            = new HashMap<Class<?>, TypedConverterStrategy>();
    
    private TypedConverterStrategy nullStrat;
    
    private TypedConverterStrategy defaultStrat;
    
    private TypedConverterStrategy scalarStrat;
    
    private OwnedConverterStrategy collectionStrat;
    
    private OwnedConverterStrategy mapStrat;
    
    /**
     * Creates a new instance of this class. This also adds strategies for the 
     * types mentioned in the class documentation above. If that behaviour is 
     * not desired, the respective strategies can be replaced after creation 
     * by calling {@link #addStrategy(Class, TypedConverterStrategy)}.
     */
    public TypedConverter()
    {
        this(new JavaBeanStrategy());
    }
    
    public TypedConverter(TypedConverterStrategy defaultStrat)
    {
        nullStrat = new NullStrategy();
        this.defaultStrat = defaultStrat;
        if (defaultStrat instanceof OwnedConverterStrategy)
        {
            ((OwnedConverterStrategy) defaultStrat).setOwner(this);
        }
        scalarStrat = new ScalarStrategy();
        collectionStrat = new CollectionStrategy();
        collectionStrat.setOwner(this);
        mapStrat = new MapStrategy();
        mapStrat.setOwner(this);
        strats.put(String.class, scalarStrat);
        strats.put(Number.class, scalarStrat);
        strats.put(Collection.class, collectionStrat);
        strats.put(Map.class, mapStrat);
    }
    
    /**
     * Adds a new converter strategy for the given type. If the strategy is
     * an instance of {@link OwnedConverterStrategy}, this typed converter
     * is set as its owner.
     * 
     * @param cls the type the given converter strategy is responsible for
     * @param strat a convert strategy to use for objects of the given type
     */
    public final void addStrategy(Class<?> cls, TypedConverterStrategy strat)
    {
        Contract.pre(cls != null, "Class required");
        Contract.pre(strat != null, "Converter strategy required");
        strats.put(cls, strat);
        if (strat instanceof OwnedConverterStrategy)
        {
            ((OwnedConverterStrategy) strat).setOwner(this);
        }
    }
    
    /**
     * Iterates over all super types of <code>value</code> and finds the most
     * appropriate converter strategy for it. The inheritance tree is iterated
     * recursively starting with the class of value and continuing with its
     * super class and directly implemented interfaces, and so on until no
     * more super classes or interfaces can be found.
     * 
     * @param value the value to find a converter for
     * @return a conversion strategy. If no converter strategy can be found
     *         by type lookup, an instance of {@link JavaBeanStrategy} is 
     *         returned. This method never returns <code>null</code>.
     */
    protected TypedConverterStrategy getStrategy(Object value)
    {
        // A few cases we cannot catch with the lookup approach
        if (value == null)
        {
            return nullStrat;
        }
        else if (value.getClass().isArray())
        {
            return collectionStrat;
        }
        else if (value.getClass().isPrimitive())
        {
            return scalarStrat;
        }
        LinkedList<Class<?>> classes = new LinkedList<Class<?>>();
        classes.addFirst(value.getClass());
        while (!classes.isEmpty())
        {
            Class<?> c = classes.removeLast();
            TypedConverterStrategy strat = strats.get(c);
            if (strat != null)
            {
                return strat;
            }
            if (c.getSuperclass() != null)
            {
                classes.add(c.getSuperclass());
            }
            if (c.getInterfaces() != null)
            {
                for (Class<?> i : c.getInterfaces())
                {
                    classes.add(i);
                }
            }
        }
        return defaultStrat;
    }

    /**
     * Converts the given object to JSON.
     * 
     * @param o the object to be converted, which can also be <code>null</code>
     */
    public String toJson(Object o)
    {
        StringBuilder b = new StringBuilder();
        b.append("/*");
        processValue(o, b);
        b.append("*/");
        return b.toString();
    }
    
    /**
     * Processes the given value into a JSON string and appends the results
     * to the given string builder.
     * 
     * @param value an arbitrary object to be serialised
     * @param b a string builder that acts as a buffer for the serialised
     *          JSON
     */
    public void processValue(Object value, StringBuilder b)
    {
        getStrategy(value).processValue(value, b);
    }

}
