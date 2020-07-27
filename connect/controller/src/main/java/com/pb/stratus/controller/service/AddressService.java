package com.pb.stratus.controller.service;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.geocoder.GGMRequestParams;
import net.sf.json.JSONObject;

import java.util.List;

public interface AddressService
{
    public List<Address> findAddresses(SearchParameters searchParameters) throws InvalidGazetteerException;
    
    public boolean isValidGazetteerName(String gazetteerName, String tenantName);

    public JSONObject geocodeAddresses(GGMRequestParams ggmRequestParams) throws InvalidGazetteerException;
}
