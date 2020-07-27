package com.pb.stratus.controller.json;

import uk.co.graphdata.utilities.contract.Contract;

/**
 * Instances of this class represent converter strategies that are &quote;owned&quote; by another conversion strategy.
 * The owned conversion strategy can then pass responsibility to convert certain objects back to the owner. This is
 * particularly useful if the overall conversion also includes the serialisation of other objects referenced by the
 * object passed into the original {@link #processValue(Object, StringBuilder)} call, but this conversion strategy
 * doesn't know how to serialise the referenced objects.
 *
 * @author Volker Leidl
 */
public abstract class OwnedConverterStrategy implements TypedConverterStrategy
{

    private TypedConverterStrategy owner;

    /**
     * Sets the owner of this converter strategy. We could have done this through a constructor as well, but then all
     * subclass would have to provide such a constructor themselves.
     * 
     * @param owner the owner of this converter strategy.
     */
    public void setOwner(TypedConverterStrategy owner)
    {
        this.owner = owner;
    }

    /**
     * Let's the owner of this conversion strategy process <code>o</code>.
     * 
     * @param o the object to be processed
     * @param b the StringBuilder to write the converted object into.
     */
    protected void processByOwner(Object o, StringBuilder b)
    {
        Contract.pre(owner != null, "Owner required");
        owner.processValue(o, b);
    }

}
