package com.pb.stratus.controller.geocoder;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.action.ProxyUtils;
import com.pb.stratus.controller.model.Option;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GGMService acts as a client to the GGM service configured for a mapConfig.
 * Created by pr007ka on 08-09-2015.
 */
public class GGMService {

    // Provides GGM specific helper objects
    private GeoServiceHelper geoHelper;

    private static final Logger logger = LogManager
            .getLogger(GGMService.class);

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
        JSONObject requestObj = buildRequestPayload(geoParams, searchParameters);

        return parseResult(fireRequest(url, geoParams, requestObj));
    }

    /**
     * function to get the List of geocoded Addresses with GGM Service
     *
     * @param geoParams
     * @param ggmRequestParams
     * @return
     * @throws Exception
     */
    public JSONObject geocodeAddresses(GeoSearchParams geoParams, GGMRequestParams ggmRequestParams)
            throws Exception {

        URL url = new URL(geoHelper.buildURL(geoParams, null));
        JSONObject requestObj = buildGeocodeRequestPayload(geoParams, ggmRequestParams);

        return fireRequest(url, geoParams, requestObj);
    }

    /**
     * parses the webservice response
     *
     * @param json
     * @return
     */
    List<Address> parseResult(JSONObject json) {

        JSONArray responses = json.getJSONArray("responses");

        // when no result is returned.
        Object status = responses.getJSONObject(0).get("Status");
        if (status != null) {
            if (status.equals("F"))
                return Collections.emptyList();
        }

        JSONArray candidates = responses.getJSONObject(0).getJSONArray("candidates");
        List<Address> result;
        if (candidates != null && !candidates.isEmpty()) {
            result = new ArrayList<>(candidates.size());

            for (int n = 0; n < candidates.size(); n++) {
                result.add(geoHelper.parseAddress(candidates.getJSONObject(n)));
            }
        } else {
            return Collections.emptyList();
        }
        return result;
    }

    /**
     * hits ggm service and gets json string
     *
     * @param url
     * @param geoParams
     * @param requestObj
     * @return
     * @throws IOException
     */
    public JSONObject fireRequest(URL url, GeoSearchParams geoParams, JSONObject requestObj) throws IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("GGM service request: " + url);
        }

        CloseableHttpClient httpClient = ProxyUtils.getHttpClient(geoParams.getUsername(), geoParams.getPassword());
        HttpPost serviceRequest = new HttpPost(url.toString());
        StringEntity entity = new StringEntity(requestObj.toString(), "UTF-8");
        entity.setContentType("application/json; charset=UTF-8");
        serviceRequest.setEntity(entity);
        HttpResponse serviceResponse = httpClient.execute(serviceRequest);

        if (logger.isDebugEnabled()) {
            logger.debug("GGM service response: " + serviceResponse);
        }

        if (serviceResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String responseError = EntityUtils.toString(serviceResponse.getEntity(), "UTF-8");
            String errorMsg = "There was a service error: ";
            if (!StringUtils.isEmpty(responseError)) {
                JSONObject responseJson = JSONObject.fromObject(responseError);
                if (responseJson.has("errors")) {
                    errorMsg = errorMsg + responseJson.getJSONArray("errors").getJSONObject(0).getString("errorDescription");
                } else {
                    errorMsg = errorMsg + responseError;
                }
            } else if (serviceResponse.getFirstHeader("exceptionCode") != null) {
                errorMsg = errorMsg + serviceResponse.getFirstHeader("exceptionCode").getValue();
            } else {
                errorMsg = errorMsg + serviceResponse.getStatusLine().getReasonPhrase();
            }
            logger.error("There was a service error. URL: " + url + ", Response: " + serviceResponse);
            throw new IOException(errorMsg);
        }
        return JSONObject.fromObject(EntityUtils.toString(serviceResponse.getEntity(), "UTF-8"));
    }

    /**
     * builds POST data payload
     *
     * @param params       GeoSearchParams
     * @param searchParams SearchParameters
     * @return JSONObject
     * @throws UnsupportedEncodingException
     */
    private JSONObject buildRequestPayload(GeoSearchParams params, SearchParameters searchParams)
            throws UnsupportedEncodingException {

        //Preparing JSONObjects
        JSONObject requestObj = new JSONObject();
        JSONObject addressObj = new JSONObject();
        JSONArray addressArray = new JSONArray();
        JSONObject prefObject = new JSONObject();

        addressObj.put(GGMConstants.COUNTRY, params.getCountry());
        addressObj.put(GGMConstants.PARAM_ADDRESS_LINE, searchParams.getSearchString());
        addressArray.add(addressObj);

        requestObj.put(GGMConstants.PARAM_ADDRESSES, addressArray);

        if (searchParams.getMaxRecords() > 0) {
            prefObject.put(GGMConstants.PARAM_MAXCANDIDATES, searchParams.getMaxRecords());
        } else {
            prefObject.put(GGMConstants.PARAM_MAXCANDIDATES, GGMConstants.PARAM_MAXCANDIDATES_DEFAULT_VALUE);
        }

        for (Option option : params.getOptions()) {
            prefObject.put(option.getName(), option.getValue());
        }

        requestObj.put(GGMConstants.PARAM_PREFERENCES, prefObject);

        return requestObj;
    }

    /**
     * builds POST data payload for geocoding addresses
     *
     * @param params
     * @param ggmRequestParams
     * @return
     * @throws UnsupportedEncodingException
     */
    private JSONObject buildGeocodeRequestPayload(GeoSearchParams params, GGMRequestParams ggmRequestParams)
            throws UnsupportedEncodingException {

        JSONObject requestObj = new JSONObject();
        JSONArray addressArray = new JSONArray();
        JSONObject prefObject = new JSONObject();

        List<String> addressList = ggmRequestParams.getAddressList();
        for (String anAddressList : addressList) {
            JSONObject addressObj = new JSONObject();
            addressObj.put(GGMConstants.COUNTRY, params.getCountry());
            addressObj.put(GGMConstants.PARAM_ADDRESS_LINE, anAddressList);
            addressArray.add(addressObj);
        }
        requestObj.put(GGMConstants.PARAM_ADDRESSES, addressArray);

        if (ggmRequestParams.getMaxRecords() > 0) {
            prefObject.put(GGMConstants.PARAM_MAXCANDIDATES, ggmRequestParams.getMaxRecords());
        }

        for (Option option : params.getOptions()) {
            prefObject.put(option.getName(), option.getValue());
        }

        requestObj.put(GGMConstants.PARAM_PREFERENCES, prefObject);
        return requestObj;
    }

    public void setGeoHelper(GeoServiceHelper geoHelper) {
        this.geoHelper = geoHelper;
    }

}
