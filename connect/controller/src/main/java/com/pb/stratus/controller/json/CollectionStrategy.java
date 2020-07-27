package com.pb.stratus.controller.json;

import uk.co.graphdata.utilities.contract.Contract;

import java.util.Collection;

/**
 * A JSON converter strategy to serialise collections and arrays into JSON.
 *
 * @author Volker Leidl
 */
public class CollectionStrategy extends OwnedConverterStrategy
{

    public void processValue(Object value, StringBuilder b)
    {
        Contract.pre(value != null, "Value required");
        Contract.pre(value instanceof Collection || value.getClass().isArray(),
                "Value must be either a collection or an array");

        b.append("[");
        if (value instanceof Collection)
        {
            Collection<?> c = (Collection<?>) value;
            int i = 0;
            for (Object o : c)
            {
                if (i > 0)
                {
                    b.append(", ");
                }
                i++;
                processByOwner(o, b);
            }
        }
        else if (value.getClass().isArray())
        {
            int i = 0;
            for (Object o : (Object[]) value)
            {
                if (i > 0)
                {
                    b.append(", ");
                }
                i++;
                processByOwner(o, b);
            }

        }
        b.append("]");
    }

}
