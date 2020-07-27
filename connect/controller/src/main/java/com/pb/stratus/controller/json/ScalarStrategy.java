package com.pb.stratus.controller.json;

import org.apache.commons.lang.StringEscapeUtils;
import uk.co.graphdata.utilities.contract.Contract;

/**
 * Converts a scalar value (in the sense of a non-structured, singular value)
 * into a JSON string. This class distinguishes between numbers, booleans, and
 * &quot;everything else&quot;. A number or a boolean is converted into its
 * default String representation (e.g. 'true', 'false', '123.456', etc).
 * Everything else is converted into a quoted JavaScript string. Since there
 * is no dedicated char data type in JavaScript, characters are treated as
 * if they were strings.
 *
 * @author Volker Leidl
 */
public class ScalarStrategy implements TypedConverterStrategy
{

    public void processValue(Object value, StringBuilder b)
    {
        Contract.pre(value != null, "Value required");
        if (value instanceof Number || value instanceof Boolean
                || value.getClass() == Boolean.TYPE
                || (value.getClass().isPrimitive()
                && value.getClass() != Character.TYPE))
        {
            // The string representation of numbers and booleans corresponds
            // to their JavaScript equivalents. So we don't need to do anything
            // special
            b.append(value.toString());
        }
        else
        {
            // In all other cases we convert the value into a JavaScript quoted
            // string. There is no character data type, so a string is as
            // close as we can get.
            b.append("\"");
            b.append(StringEscapeUtils.escapeJavaScript(value.toString()));
            b.append("\"");
        }
    }

}
