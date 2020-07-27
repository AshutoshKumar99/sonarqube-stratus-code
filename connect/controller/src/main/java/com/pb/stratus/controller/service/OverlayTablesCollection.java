
package com.pb.stratus.controller.service;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to store an overlay and its midev tables 
 * which is returned as a json object containing an overlay name and its tables
 * object containing a overlay name and its tables
 */
public class OverlayTablesCollection
{
    String overlay;

    List<String> tables = new ArrayList<String>();

    public OverlayTablesCollection(String overlay, List<String> tables)
    {
        this.overlay = overlay;
        this.tables = tables;
    }

    public String getOverlay()
    {
        return overlay;
    }

    public List<String> getTables()
    {
        return tables;
    }
}
