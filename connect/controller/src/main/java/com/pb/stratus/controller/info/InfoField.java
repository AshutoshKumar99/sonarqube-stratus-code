package com.pb.stratus.controller.info;

/**
 * A very simple class to model an Additional fields in the feature table so
 * that it can be serialised into JSON.
 * 
 * @author kavitha yeruva.
 */
public class InfoField
{
    private String key;
    private String value;

    /**
     * Constructor for an InfoField object.
     */
    public InfoField(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public InfoField()
    {
    }

    /**
     * Accessor method for this instance's key(column name) attribute.
     * 
     * @return the value of the title attribute.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Mutator method for this instance's key attribute.
     * 
     * @param title
     *            the title for this instance.
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Accessor method for this instance's value attribute.
     * 
     * @return the value of the title attribute.
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Mutator method for this instance's value attribute.
     * 
     * @param title
     *            the title for this instance.
     */
    public void setValue(String value)
    {
        this.value = value;
    }
}