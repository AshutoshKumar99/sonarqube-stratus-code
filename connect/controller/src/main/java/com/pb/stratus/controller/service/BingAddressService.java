/**
 * **************************************************************************
 * Copyright © 2011, Pitney Bowes Software Inc. All rights reserved.
 * Confidential Property of Pitney Bowes Software Inc. $Author: AL021CH $
 * $Revision: 20401 $ $LastChangedDate: 2011-09-11 11:47:27 +0530 (Sun, 11 Sep
 * 2011) $ $HeadURL:
 * http://noisvnmsprod/svn/stratus-connect/stratus/trunk/connect/controller/src/main/java/com/pb/stratus/controller/service/BingAddressService.java $
 * ***************************************************************************
 */
package com.pb.stratus.controller.service;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.action.ProxyUtils;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BingAddressService {

    private static final Logger logger = LogManager
            .getLogger(BingAddressService.class);

    private ControllerConfiguration config;
    private AuthorizationUtils authorizationUtils;

    public BingAddressService(ControllerConfiguration config, AuthorizationUtils authorizationUtils) {
        this.config = config;
        this.authorizationUtils = authorizationUtils;
    }

    public List<Address> search(SearchParameters searchParameters)
            throws IOException {
        List<Address> result;
        try {
            URL url = new URL(buildURL(searchParameters));
            String s = makeCallout(url);
            result = parseResult(s);

            int maxRecords = searchParameters.getMaxRecords();
            // we already know that maxRecords > 0
            if (result.size() > maxRecords) {
                result = result.subList(0, maxRecords);
            }
        } catch (IOException e) {
            logger.error("Error making callout to Bing", e);
            throw e;
        }
        return result;
    }

    String makeCallout(URL url) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("Bing request: " + url);
        }

        CloseableHttpClient httpClient = ProxyUtils.getHttpClient();
        HttpGet serviceRequest = new HttpGet(url.toString());
        HttpResponse serviceResponse = httpClient.execute(serviceRequest);

        if (logger.isDebugEnabled()) {
            logger.debug("Bing response: " + serviceResponse);
        }
        return EntityUtils.toString(serviceResponse.getEntity(), "UTF-8");
    }

    String buildURL(SearchParameters searchParameters) throws UnsupportedEncodingException {
        String searchString = searchParameters.getSearchString();
        StringBuilder result = new StringBuilder();
        result.append("https://dev.virtualearth.net/REST/v1/Locations");
        result.append("?key=");
        // CONN-13923
        if (authorizationUtils.isAnonymousUser()) {
            result.append(config.getBingServicesPublicApiKey());
        } else {
            result.append(config.getBingServicesPrivateApiKey());
        }

        result.append("&maxResults=" + searchParameters.getMaxRecords());//CONN-21679
        result.append("&culture=" + searchParameters.getCulture());
        result.append("&q=");
        result.append(URLEncoder.encode(searchString, "UTF-8"));
        return result.toString();
    }

    static List<Address> parseResult(String jsonString) {
        JSONObject json = JSONObject.fromObject(jsonString);

        int statusCode = json.getInt("statusCode");
        if (statusCode != 200) {
            return Collections.emptyList();
        }

        JSONArray resourceSets = json.getJSONArray("resourceSets");
        JSONArray resources = resourceSets.getJSONObject(0).getJSONArray(
                "resources");

        // sort by confidence
        Collections.sort(resources, AddressConfidenceComparator.INSTANCE);

        List<Address> result = new ArrayList<Address>(resources.size());
        for (int n = 0; n < resources.size(); n++) {
            result.add(parseAddress(resources.getJSONObject(n)));
        }
        return result;
    }

    static Address parseAddress(JSONObject resource) {
        Address result = new Address();

        JSONObject address = resource.getJSONObject("address");
        result.setAddress(address.getString("formattedAddress"));

        JSONObject point = resource.getJSONObject("point");
        JSONArray coordinates = point.getJSONArray("coordinates");
        result.setY(coordinates.getDouble(0));
        result.setX(coordinates.getDouble(1));
        result.setSrs("epsg:4326");
        return result;
    }

    private static class AddressConfidenceComparator implements
            Comparator<JSONObject> {

        private static enum Confidence {
            High, Medium, Low, Unknown
        }

        private static AddressConfidenceComparator INSTANCE = new AddressConfidenceComparator();

        public int compare(JSONObject o1, JSONObject o2) {
            String confidenceString1 = o1.getString("confidence");
            String confidenceString2 = o2.getString("confidence");
            Confidence c1 = Confidence.valueOf(confidenceString1);
            Confidence c2 = Confidence.valueOf(confidenceString2);
            return c1.compareTo(c2);
        }
    }
}
