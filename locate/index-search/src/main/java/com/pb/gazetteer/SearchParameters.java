package com.pb.gazetteer;

import com.pb.gazetteer.search.SearchLogic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * This class encapsulates the input parameters required for Single Line Address
 * search
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchParameters {
    @XmlElement(required = true)
    private String searchString;
    @XmlElement(required = true)
    private int maxRecords;
    @XmlElement(required = false)
    private String gazetteerName;

    @XmlElement(required = true)
    private String tenantName;

    @XmlElement(required = false)
    private String culture;

    @XmlElement(required = false)
    private String gazetteerService;

    /**
     * Introducing an option of specifying the searching capability.
     */
    @XmlElement(required = false, defaultValue = "DEFAULT_LOGIC")
    private SearchLogic searchLogic;

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * @param searchString the searchString to set
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    /**
     * @return the maxRecords
     */
    public int getMaxRecords() {
        return maxRecords;
    }

    /**
     * @param maxRecords the maxRecords to set
     */
    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

    /**
     * @return the gazetteerName
     */
    public String getGazetteerName() {
        return gazetteerName;
    }

    /**
     * @param gazetteerName the gazetteerName to set
     */
    public void setGazetteerName(String gazetteerName) {
        this.gazetteerName = gazetteerName;
    }

    /**
     * @return The tenant name for the request.
     */
    public String getTenantName() {
        return tenantName;
    }

    /**
     * @param tenantName The tenant name for the request.
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public SearchLogic getSearchLogic() {
        if (this.searchLogic == null) {
            return SearchLogic.DEFAULT_LOGIC;
        }
        return searchLogic;
    }

    /**
     * Set the searchLogic that needs to be used.
     *
     * @param searchLogic
     */
    public void setSearchLogic(SearchLogic searchLogic) {
        this.searchLogic = searchLogic;
    }

    /**
     * @return the culture
     */
    public String getCulture() {
        return culture;
    }

    /**
     * @param culture the culture to set
     */
    public void setCulture(String culture) {
        this.culture = culture;
    }

    public String getGazetteerService() {
        return gazetteerService;
    }

    public void setGazetteerService(String gazetteerService) {
        this.gazetteerService = gazetteerService;
    }
}
