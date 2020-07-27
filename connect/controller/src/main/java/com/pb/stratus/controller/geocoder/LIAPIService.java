package com.pb.stratus.controller.geocoder;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.action.ProxyUtils;
import com.pb.stratus.controller.model.Option;
import com.pb.stratus.onpremsecurity.analyst.auth.AnalystOAuthAuthentication;
import com.pb.stratus.onpremsecurity.analyst.auth.AnalystOAuthProvider;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author ekiras
 */
public class LIAPIService {
    // Provides LIAPI specific helper objects
    private GeoServiceHelper geoHelper;
    private AnalystOAuthProvider analystOAuthProvider;

    private static final Logger logger = LogManager.getLogger(LIAPIService.class);

    public LIAPIService(AnalystOAuthProvider analystOAuthProvider) {
        assert analystOAuthProvider != null : "AnalystOAuthProvider cannot be null";
        this.analystOAuthProvider = analystOAuthProvider;
    }

    public final List<Address> search(GeoSearchParams geoParams, SearchParameters searchParameters) throws Exception {

        URL url = new URL(geoHelper.buildURL(geoParams, searchParameters));
        logger.debug("Geocoder url ", url.getPath());

        String token = getTokenFromSession(searchParameters.getGazetteerName());
        if (token == null) {
            token = getOAuthToken(geoParams, searchParameters.getGazetteerName());
        }
        String basicAuth = buildBasicAuthorizationString(token);
        JSONObject requestObj = buildRequestPayload(geoParams, searchParameters);
        logger.debug("Request Payload for geocoding", requestObj.toString());
        JSONObject responseObj = fireRequest(url, basicAuth, requestObj);
        logger.debug("Response from geocoder", responseObj);
        return parseResult(responseObj);
    }

    public HttpResponse findTravelBoundary(URI uri, String apiKey, String secret) throws IOException {
        String tokenUrl = uri.resolve("/oauth/token").toString();
        logger.debug("Get OAuth token from LI-API Geo-zone");
        String token = getTokenFromSession(apiKey);
        if (token == null) {
            AnalystOAuthAuthentication authentication = analystOAuthProvider.getAuthToken(tokenUrl, apiKey, secret);
            // Set oauth token in session
            ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
            requestAttributes.getRequest().getSession().setAttribute(LIAPIConstants.TOKEN_ATTRIBUTE + "_" + apiKey, authentication);
            token = authentication.getAccess_token();
        }
        CloseableHttpClient httpClient = ProxyUtils.getHttpClient();
        HttpGet serviceRequest = new HttpGet(uri.toURL().toString());
        serviceRequest.setHeader("Content-Type", "application/json");
        serviceRequest.setHeader("Authorization", "Bearer " + token);
        return httpClient.execute(serviceRequest);
    }

    public JSONObject geocodeAddresses(GeoSearchParams geoParams, GGMRequestParams searchParameters) throws Exception {
        URL url = new URL(geoHelper.buildURL(geoParams, null));
        logger.debug(url.getPath());

        String token = getTokenFromSession(searchParameters.getGazetteerName());
        if (token == null) {
            token = getOAuthToken(geoParams, searchParameters.getGazetteerName());
        }
        String basicAuth = buildBasicAuthorizationString(token);
        JSONObject requestObj = buildRequestPayload(geoParams, searchParameters);

        return fireRequest(url, basicAuth, requestObj);
    }

    public JSONObject fireRequest(URL url, String basicAuth, JSONObject requestObj) throws IOException {

        if (logger.isDebugEnabled()) {
            logger.debug("LIAPI service request: " + url);
        }

        CloseableHttpClient httpClient = ProxyUtils.getHttpClient();
        HttpPost serviceRequest = new HttpPost(url.toString());
        serviceRequest.setHeader("Content-Type", "application/json");
        serviceRequest.setHeader("Authorization", basicAuth);
        StringEntity entity = new StringEntity(requestObj.toString());
        serviceRequest.setEntity(entity);
        HttpResponse serviceResponse = httpClient.execute(serviceRequest);

        if (logger.isDebugEnabled()) {
            logger.debug("LIAPI service response: " + serviceResponse);
        }

        JSONObject responseJson = JSONObject.fromObject(EntityUtils.toString(serviceResponse.getEntity(), "UTF-8"));
        if (serviceResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            String errorMsg = "There was a service error: ";
            if (responseJson.has("errors")) {
                errorMsg = errorMsg + responseJson.getJSONArray("errors").getJSONObject(0).getString("errorDescription");
            } else {
                errorMsg = errorMsg + serviceResponse.getStatusLine().getReasonPhrase();
            }
            logger.error("There was a service error. URL: " + url + ", Response: " + serviceResponse);
            throw new IOException(errorMsg);
        }
        return responseJson;
    }

    public final String buildBasicAuthorizationString(String token) {
        return "Bearer " + token;
    }

    public final JSONObject buildRequestPayload(GeoSearchParams geoParams, GGMRequestParams ggmRequestParams) {

        // complete request payload
        JSONObject requestObj = new JSONObject();
        JSONArray addresses = new JSONArray();
        JSONObject preferencesObj = new JSONObject();

        if (ggmRequestParams.getMaxRecords() > 0) {
            preferencesObj.put(LIAPIConstants.PARAM_MAXCANDIDATES, ggmRequestParams.getMaxRecords());
        }

        for (Option option : geoParams.getOptions()) {
            preferencesObj.put(option.getName(), option.getValue());
        }

        // address to be searched
        List<String> addressList = ggmRequestParams.getAddressList();
        for (String anAddressList : addressList) {
            JSONObject addressObj = new JSONObject();
            addressObj.put(LIAPIConstants.PARAM_COUNTRY, geoParams.getCountry());
            addressObj.put(LIAPIConstants.PARAM_ADDRESS_LINE, anAddressList);
            addresses.add(addressObj);
        }

        // array of addresses to be searched
        requestObj.put(LIAPIConstants.PARAM_TYPE, LIAPIConstants.PARAM_TYPE_VALUE);
        requestObj.put(LIAPIConstants.PARAM_PREFERENCES, preferencesObj);
        requestObj.put(LIAPIConstants.PARAM_ADDRESSESS, addresses);
        return requestObj;
    }

    public final JSONObject buildRequestPayload(GeoSearchParams geoParams, SearchParameters searchParameters) {

        // complete request payload
        JSONObject requestObj = new JSONObject();
        JSONObject preferencesObj = new JSONObject();
        JSONObject searchAddressObject = new JSONObject();

        if (searchParameters.getMaxRecords() > 0) {
            preferencesObj.put(GGMConstants.PARAM_MAXCANDIDATES, searchParameters.getMaxRecords());
        } else {
            preferencesObj.put(GGMConstants.PARAM_MAXCANDIDATES, GGMConstants.PARAM_MAXCANDIDATES_DEFAULT_VALUE);
        }

        for (Option option : geoParams.getOptions()) {
            preferencesObj.put(option.getName(), option.getValue());
        }

        // address to be searched
        searchAddressObject.put(LIAPIConstants.PARAM_ADDRESS_LINE, searchParameters.getSearchString());
        searchAddressObject.put(LIAPIConstants.PARAM_COUNTRY, geoParams.getCountry());

        // array of addresses to be searched
        JSONArray addresses = new JSONArray();
        addresses.add(searchAddressObject);

        requestObj.put(LIAPIConstants.PARAM_TYPE, LIAPIConstants.PARAM_TYPE_VALUE);
        requestObj.put(LIAPIConstants.PARAM_PREFERENCES, preferencesObj);
        requestObj.put(LIAPIConstants.PARAM_ADDRESSESS, addresses);
        return requestObj;
    }

    public String getTokenFromSession(String gazetteerName) {
        logger.debug("Getting LI Geocoder OAuth Token from Session");
        ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        AnalystOAuthAuthentication authentication = (AnalystOAuthAuthentication) requestAttributes.getRequest().getSession().getAttribute(LIAPIConstants.TOKEN_ATTRIBUTE + "_" + gazetteerName);
        if (authentication != null) {
            Date issuedAt = new Date(Long.valueOf(authentication.getIssuedAt()));
            // convert expiry time in seconds
            Long expiresAt = Long.valueOf(authentication.getExpiresIn()) * 60;

            // check if new token is required
            if ((new Date()).compareTo(new Date(issuedAt.getTime() + expiresAt)) < 0) {
                logger.debug("Token found in session", authentication.toString());
                return authentication.getAccess_token();
            }
        }
        return null;
    }

    public String getOAuthToken(GeoSearchParams geoParams, String gazetteerName) {
        String regex = "\\/location-intelligence\\/geocode-service\\/v[\\d]\\/transient\\/(basic|premium)\\/geocode";
        String authUrl = geoParams.getEndPoint().replaceAll(regex, "/oauth/token");

        logger.debug("Get OAuth token from LI Geocoder");
        AnalystOAuthAuthentication authentication = analystOAuthProvider.getAuthToken(authUrl, geoParams.getUsername(),
                geoParams.getPassword());

        //set o auth token in session
        ServletRequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        requestAttributes.getRequest().getSession().setAttribute(LIAPIConstants.TOKEN_ATTRIBUTE + "_" + gazetteerName, authentication);
        logger.debug("Authentication from LI Geocoder ", authentication);

        return authentication.getAccess_token();
    }

    List<Address> parseResult(JSONObject json) {
        List<Address> result = null;
        if (json != null) {
            JSONArray responses = json.getJSONArray(LIAPIConstants.RESPONSE);

            if (responses != null) {
                JSONArray candidates = responses.getJSONObject(0).getJSONArray(LIAPIConstants.RESPONSE_CANDIDATES);
                if (candidates != null && candidates.size() > 0) {
                    result = new ArrayList<>(candidates.size());
                    for (int n = 0; n < candidates.size(); n++) {
                        result.add(geoHelper.parseAddress(candidates.getJSONObject(n)));
                    }
                } else {
                    return Collections.emptyList();
                }
            }
        }
        return result;
    }

    public void setGeoHelper(GeoServiceHelper geoHelper) {
        this.geoHelper = geoHelper;
    }
}
