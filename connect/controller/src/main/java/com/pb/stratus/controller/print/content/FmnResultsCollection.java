/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pb.stratus.controller.print.content;

import java.util.List;



/**
 * This class represents a collection of FmnResults with an associated title.
 */
public class FmnResultsCollection
{

    private String title;
    
    private List<FmnResult> fmnResults;
    
    public FmnResultsCollection(String title, List<FmnResult> fmnresults)
    {
        this.title = title;
        this.fmnResults = fmnresults;
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * @return the fmnResults
     */
    public List<FmnResult> getFmnResults()
    {
        return fmnResults;
    }

    /**
     * @param fmnResults the fmnResults to set
     */
    public void setFmnResults(List<FmnResult> fmnResults)
    {
        this.fmnResults = fmnResults;
    }


}
