package com.pb.stratus.controller.service;


import com.pb.stratus.core.common.Preconditions;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Class to hold the parameters for FeatureService listFeatures operation
 * 
 */
public class SearchFeaturesListParams extends SearchParams
{
    
    private String table;

    public SearchFeaturesListParams()
    {
        super();
    }

    public SearchFeaturesListParams(SearchParams searchParams)
    {
        this.setLocale(searchParams.getLocale());
        this.setPageLength(searchParams.getPageLength());
        this.setPageNumber(searchParams.getPageNumber());
        this.setOrderByList(searchParams.getOrderByList());
        this.setOrderByDirection(searchParams.getOrderByDirection());
        this.setReturnTotalCount(searchParams.getReturnTotalCount());
        this.setAttributes(searchParams.getAttributes());
        this.setIncludeGeometry(searchParams.isIncludeGeometry());
        this.setTargetSrs(searchParams.getTargetSrs());
    }

    /**
     * @return the table
     */
    public String getTable()
    {
        return table;
    }

    /**
     * @param table
     *  the table to set
     */
    public void setTable(String table)
    {
        this.table = table;
    }

    /**
     * Return a new instance as a copy of the class. Avoiding cloning to
     * circumvent any unexpected results.
     * @return SearchFeaturesListParams
     */
    public SearchFeaturesListParams getCopy()
    {
        SearchFeaturesListParams searchFeaturesListParams =
                new SearchFeaturesListParams(super.getCopy());
        searchFeaturesListParams.setTable(this.getTable());
        return searchFeaturesListParams;
}

    public boolean equals(Object o)
    {
        if(!(o instanceof SearchFeaturesListParams))
        {
            return false;
        }
        if(o == this)
        {
            return true;
        }
        SearchFeaturesListParams that = (SearchFeaturesListParams)o;
        Preconditions.checkNotNull(that.getTable(), "tableName cannot be null");
        return super.equals(that) && this.getTable().equals(that.getTable());
    }

    public int hashCode()
    {
        return new HashCodeBuilder(5, 37).append(table).appendSuper(super
                .hashCode()).toHashCode();
    }
}
