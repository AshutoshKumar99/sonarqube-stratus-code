package com.pb.stratus.controller.info;

import java.util.ArrayList;

/**
 * A very simple class to model Info Tables to be serialised into JSON.
 * 
 * @author vilam
 */
public class InfoTables
{

    ArrayList<InfoTable> tables;

    /**
     * Constructor for an Location object.
     */
    public InfoTables(ArrayList<InfoTable> tables)
    {
        this.tables = tables;
    }

    /**
     * Accessor method for this instance's tables attribute.
     * 
     * @return the value of the tables attribute.
     */
    public ArrayList<InfoTable> getTables()
    {
        return tables;
    }

    /**
     * Mutator method for this instance's tables attribute.
     * 
     * @param tables
     *            the tables for this instance.
     */
    public void setTables(ArrayList<InfoTable> tables)
    {
        this.tables = tables;
    }

}
