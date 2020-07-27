package com.pb.gazetteer;

import com.pb.gazetteer.search.SearchLogic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class PopulateParameters
{
    @XmlElement(required=true)
    private String tenantName;
    @XmlElement(required=true)
    private String gazetteerName;
    @XmlElement(required=true)
    private String projection;

    /**
     * We do not need the search logic so making it optional
     */
    @XmlElement(required=false, defaultValue = "DEFAULT_LOGIC")
    private SearchLogic searchLogic;

    @XmlElement(required=true)
    private int addressColumn;
    @XmlElement(required=true)
    private int xColumn;
    @XmlElement(required=true)
    private int yColumn;
    @XmlElement(required=false)
    private Integer maxFailures;
    @XmlElement(required=false)
    private String delimiter;

    public String getTenantName()
    {
        return tenantName;
    }

    public void setTenantName(String tenantName)
    {
        this.tenantName = tenantName;
    }

    public String getGazetteerName()
    {
        return gazetteerName;
    }

    public void setGazetteerName(String gazetteerName)
    {
        this.gazetteerName = gazetteerName;
    }

    public String getProjection()
    {
        return projection;
    }

    public void setProjection(String projection)
    {
        this.projection = projection;
    }
    
    public SearchLogic getSearchLogic()
    {
        if(searchLogic == null)
            return SearchLogic.DEFAULT_LOGIC;
        return searchLogic;
    }

    public void setSearchLogic(SearchLogic searchLogic)
    {
        this.searchLogic = searchLogic;
    }

    public int getAddressColumn()
    {
        return addressColumn;
    }

    public void setAddressColumn(int addressColumn)
    {
        this.addressColumn = addressColumn;
    }

    public int getxColumn()
    {
        return xColumn;
    }

    public void setxColumn(int xColumn)
    {
        this.xColumn = xColumn;
    }

    public int getyColumn()
    {
        return yColumn;
    }

    public void setyColumn(int yColumn)
    {
        this.yColumn = yColumn;
    }

    public int getMaxFailures()
    {
        return maxFailures == null ? Integer.MAX_VALUE : maxFailures.intValue();
    }

    public void setMaxFailures(Integer maxFailures)
    {
        this.maxFailures = maxFailures;
    }

    public String getDelimiter()
    {
        return delimiter;
    }

    public void setDelimiter(String delimiter)
    {
        this.delimiter = delimiter;
    }
}
