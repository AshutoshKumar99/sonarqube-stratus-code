package com.pb.stratus.controller.info;

import java.util.ArrayList;

/**
 * A very simple class to model an Info Table (name and features) so that it can
 * be serialised into JSON.
 * 
 * @author vilam
 */
public class InfoTable
{

    private String tablename;
    private ArrayList<InfoRow> features= new ArrayList<InfoRow> ();

    /**
     * Constructor for an Info Table object.
     */
    public InfoTable(String tablename, ArrayList<InfoRow> features)
    {
        this.tablename = tablename;
        this.features = features;
    }

    /**
     * Constructor for an Info Table object.
     */
    public InfoTable(String tablename)
    {
        this.tablename = tablename;
    }

    /**
     * Accessor method for this instance's tablename attribute.
     * 
     * @return the value of the tablename attribute.
     */
    public String getTablename()
    {
        return tablename;
    }

    /**
     * Accessor method for this instance's features attribute.
     * 
     * @return the value of the features attribute.
     */
    public ArrayList<InfoRow> getFeatures()
    {
        return features;
    }

}
