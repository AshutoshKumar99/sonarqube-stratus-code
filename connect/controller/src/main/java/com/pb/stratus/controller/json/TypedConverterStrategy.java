package com.pb.stratus.controller.json;


/**
 * A JSON converter strategy encapsulates the conversion logic for a specific
 * type. They can be registered with instances of {@link TypedConverter}.
 *
 * @author Volker Leidl
 */
public interface TypedConverterStrategy
{

    /**
     * Processes the given object into a JSON string.
     *
     * @param value the object to be converter
     * @param b     a StringBuilder to write the converted string into
     */
    public void processValue(Object value, StringBuilder b);

}
