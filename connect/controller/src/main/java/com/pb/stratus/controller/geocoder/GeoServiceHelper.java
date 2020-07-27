package com.pb.stratus.controller.geocoder;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Provides template for helper methods which are encapsulated for GCM and EGM modules
 * Created by ar009sh on 08-09-2015.
 */
public interface GeoServiceHelper {

    String buildURL(GeoSearchParams params, SearchParameters searchParams)
            throws UnsupportedEncodingException;

    Address parseAddress(JSONObject resource);
}
