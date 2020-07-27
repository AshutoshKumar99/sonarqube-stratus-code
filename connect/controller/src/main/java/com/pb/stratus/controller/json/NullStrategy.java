package com.pb.stratus.controller.json;

/**
 * This class simply writes &quot;null&quot; into the StringBuilder passed
 * into {@link #processValue(Object, StringBuilder)}.
 *
 * @author Volker Leidl
 */
public class NullStrategy implements TypedConverterStrategy
{

    /**
     * Writes &quot;null&quot; into <code>b</code>.
     *
     * @param value a null value. This is not enforced, though.
     * @param b     a StringBuilder to write &quot;null&quot; to.
     */
    public void processValue(Object value, StringBuilder b)
    {
        b.append("null");
    }

}
