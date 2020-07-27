package com.pb.stratus.controller.geocoder;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.action.ProxyUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * A GeoService that acts as a client to the EGM and GCM services configured for a mapConfig.
 * Created by ar009sh on 08-09-2015.
 */
public class GeoService {
    // Provides EGM specific helper objects
    private GeoServiceHelper geoHelper;

    private static final Logger logger = LogManager.getLogger(GeoService.class);

    /**
     * template to get the List of Addresses with respect to EGM or GCM Service
     *
     * @param geoParams
     * @param searchParameters
     * @return
     * @throws Exception
     */
    public final List<Address> search(GeoSearchParams geoParams, SearchParameters searchParameters)
            throws Exception {

        URL url = new URL(geoHelper.buildURL(geoParams, searchParameters));
        return parseResult(fireRequest(url, geoParams));
    }

    /**
     * Parses the response data
     *
     * @param jsonString
     * @return
     */
    List<Address> parseResult(String jsonString) throws Exception {

        JSONArray resources;
        JSONObject json = new JSONObject();
        try {
            json = JSONObject.fromObject(jsonString);
            resources = json.getJSONArray("output_port");
        } catch (JSONException ex) {
            Set<?> jsonSet = json.keySet();
            Iterator<?> jsonIterator = jsonSet.iterator();
            do {
                resources = json.getJSONArray(jsonIterator.next().toString());
            } while (jsonIterator.hasNext());
        }

        // when no result is returned.
        Object status = resources.getJSONObject(0).get("Status");

        if (status != null) {
            Object statusCode = resources.getJSONObject(0).get("Status.Code");
            // for Spectrum 11 Status.Code is replaced with Status_Code
            if (statusCode == null) {
                statusCode = resources.getJSONObject(0).get("Status_Code");
            }
            if (status.equals("F") && statusCode != null) {

                if (statusCode.equals("No Match Found")) {
                    return Collections.emptyList();
                } else {
                    logger.error("GeoCoding Search Failed Due to ::" + statusCode.toString());
                    throw new Exception(statusCode.toString());
                }
            }
        }

        List<Address> result = new ArrayList<>(resources.size());
        for (int n = 0; n < resources.size(); n++) {
            result.add(geoHelper.parseAddress(resources.getJSONObject(n)));
        }

        return result;
    }

    /**
     * hits egm/gcm service returns jsonString
     *
     * @param url
     * @param geoParams
     * @return String
     * @throws IOException
     */
    public String fireRequest(URL url, GeoSearchParams geoParams) throws IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("EGM service request: " + url);
        }

        CloseableHttpClient httpClient = ProxyUtils.getHttpClient(geoParams.getUsername(), geoParams.getPassword());
        HttpGet serviceRequest = new HttpGet(url.toString());
        HttpResponse serviceResponse = httpClient.execute(serviceRequest);

        if (logger.isDebugEnabled()) {
            logger.debug("EGM service response: " + serviceResponse);
        }

        if (serviceResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String errorResponse = EntityUtils.toString(serviceResponse.getEntity(), "UTF-8");
            String errorMsg = "There was a service error: ";
            if (!StringUtils.isEmpty(errorResponse)) {
                errorMsg = errorMsg + errorResponse;
            } else {
                errorMsg = errorMsg + serviceResponse.getStatusLine().getReasonPhrase();
            }
            logger.error("There was a service error. URL: " + url + ", Response: " + serviceResponse);
            throw new IOException(errorMsg);
        }
        return EntityUtils.toString(serviceResponse.getEntity(), "UTF-8");
    }

    public void setGeoHelper(GeoServiceHelper geoHelper) {
        this.geoHelper = geoHelper;
    }
}
