package com.pb.stratus.controller.json;

import org.apache.commons.lang.StringEscapeUtils;
import uk.co.graphdata.utilities.contract.Contract;

import java.util.Map;
import java.util.Set;

/**
 * A converter strategy that serialised an instance of {@link java.util.Map}
 * into a JSON string. The resulting string looks like this:
 * <pre>
 * {
 *     key1: value1,
 *     key2: value2,
 *     ...
 * }
 * </pre>
 * Keys will be escaped into JavaScript quoted strings. Values will be passed
 * back to the owning conversion strategy for serialisation.
 *
 * @author Volker Leidl
 */
public class MapStrategy extends OwnedConverterStrategy
{

    public void processValue(Object value, StringBuilder b)
    {
        Contract.pre(value != null, "Value required");
        Contract.pre(value instanceof Map, "Value must be a map");
        Map<?, ?> om = (Map<?, ?>) value;
        int i = 0;
        b.append("{");
        for (Map.Entry<?, ?> entry : om.entrySet())
        {
            String name = entry.getKey().toString();

            if (i++ > 0)
            {
                b.append(", ");
            }
            b.append("\"");
            b.append(StringEscapeUtils.escapeJavaScript(name.toString()));
            b.append("\": ");
            Object propValue = entry.getValue();

            processByOwner(propValue, b);
        }
        b.append("}");
    }

}
