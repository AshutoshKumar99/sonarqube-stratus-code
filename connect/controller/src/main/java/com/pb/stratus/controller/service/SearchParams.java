package com.pb.stratus.controller.service;

import com.mapinfo.midev.service.geometries.v1.Geometry;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parameter for Search - Encapsulation.
 */
public class SearchParams
{

    private Geometry geometry;

    private String locale;

    private int pageLength;

    private int pageNumber;

    private List<String> orderByList = new ArrayList<String>();

    private String orderByDirection;

    protected boolean returnTotalCount;

    private List<String> attributes = new ArrayList<String>();

    private Map<String, List<String>> functionAttributes = new HashMap<>();

    private boolean includeGeometry;

    private String targetSrs;

    /**
     * Default constructor. Providing some default values to avoid any null
     * pointer exception.
     */
    public SearchParams()
    {
        super();
        this.locale = "";
        this.orderByDirection = "";
        this.targetSrs = "";
        this.geometry=null;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public int getPageLength()
    {
        return pageLength;
    }

    public void setPageLength(int pageLength)
    {
        this.pageLength = pageLength;
    }

    public int getPageNumber()
    {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber)
    {
        this.pageNumber = pageNumber;
    }

    public List<String> getOrderByList()
    {
        return orderByList;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public void setOrderByList(List<String> orderByList)
    {
        this.orderByList = orderByList;
    }

    public String getOrderByDirection()
    {
        return orderByDirection;
    }

    public void setOrderByDirection(String orderByDirection)
    {
        this.orderByDirection = orderByDirection;
    }

    public boolean getReturnTotalCount()
    {
        return returnTotalCount;
    }

    public void setReturnTotalCount(boolean returnTotalCount)
    {
        this.returnTotalCount = returnTotalCount;
    }

    public List<String> getAttributes()
    {
        return attributes;
    }

    public void setAttributes(List<String> attributes)
    {
        this.attributes = attributes;
    }

    public Map<String, List<String>> getFunctionAttributes() {
        return functionAttributes;
    }

    public void setFunctionAttributes(Map<String, List<String>> functionAttributes) {
        this.functionAttributes = functionAttributes;
    }

    public boolean isIncludeGeometry()
    {
        return includeGeometry;
    }

    public void setIncludeGeometry(boolean includeGeometry)
    {
        this.includeGeometry = includeGeometry;
    }

    public String getTargetSrs()
    {
        return targetSrs;
    }

    public void setTargetSrs(String targetSrs)
    {
        this.targetSrs = targetSrs;
    }

    /**
     * Return a copy of the object. Avoiding cloning for any unexpected
     * surprises.
     * @return SearchParams
     */
    protected SearchParams getCopy()
    {
        SearchParams searchParams = new SearchParams();
        searchParams.setLocale(this.getLocale());
        searchParams.setPageLength(this.getPageLength());
        searchParams.setPageNumber(this.getPageNumber());
        searchParams.setOrderByList(this.getOrderByList());
        searchParams.setOrderByDirection(this.getOrderByDirection());
        searchParams.setReturnTotalCount(this.getReturnTotalCount());
        searchParams.setAttributes(this.getAttributes());
        searchParams.setIncludeGeometry(this.isIncludeGeometry());
        searchParams.setTargetSrs(this.getTargetSrs());
        searchParams.setGeometry(this.getGeometry());
        return searchParams;
    }

    public boolean equals(Object searchParams)
    {
        if(!(searchParams instanceof SearchParams))
        {
            return false;
        }
        if(searchParams ==  this)
        {
            return true;
        }
        SearchParams that = (SearchParams)searchParams;
        return this.getLocale().equals(that.getLocale()) &&
                this.getPageLength() == that.getPageLength() &&
                this.getPageNumber() == that.getPageNumber() &&
                this.getOrderByList().equals(that.getOrderByList()) &&
                this.getOrderByDirection().equals(that.getOrderByDirection())
                && this.getReturnTotalCount() == that.getReturnTotalCount()
                && this.getAttributes().equals(that.getAttributes()) &&
                this.isIncludeGeometry() == that.isIncludeGeometry() &&
                this.getTargetSrs().equals(that.getTargetSrs());
    }

    public int hashCode()
    {
         return new HashCodeBuilder(17, 37).append(locale).append(pageLength).
                 append(pageNumber).append(orderByList).append(orderByDirection).
                 append(returnTotalCount).append(attributes).append(includeGeometry).
                 append(targetSrs).toHashCode();
    }
}