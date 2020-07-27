package com.pb.stratus.controller.geocoder;

import java.util.List;

/**
 * File created to hold GGM Search Request Parameters.
 */
public class GGMRequestParams {

    private List<String> addressList;
    private int maxRecords;
    private String gazetteerName;
    private String tenantName;
    private String gazetteerService;

    public List<String> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<String> addressList) {
        this.addressList = addressList;
    }

    public int getMaxRecords() {
        return maxRecords;
    }

    public void setMaxRecords(int maxRecords) {
        this.maxRecords = maxRecords;
    }

    public String getGazetteerName() {
        return gazetteerName;
    }

    public void setGazetteerName(String gazetteerName) {
        this.gazetteerName = gazetteerName;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getGazetteerService() {
        return gazetteerService;
    }

    public void setGazetteerService(String gazetteerService) {
        this.gazetteerService = gazetteerService;
    }
}