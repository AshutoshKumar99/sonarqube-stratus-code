package com.pb.stratus.controller.service;

import com.pb.stratus.controller.AuthType;
import com.pb.stratus.controller.DeploymentType;
import com.pb.stratus.controller.GeocodingServiceType;
import com.pb.stratus.controller.geocoder.GeoSearchParams;
import com.pb.stratus.controller.geocoder.LIAPIConstants;
import com.pb.stratus.controller.httpclient.Authentication;
import com.pb.stratus.controller.model.Option;
import com.pb.stratus.controller.model.RoutingConfig;
import com.pb.stratus.controller.model.RoutingDB;
import com.pb.stratus.controller.model.TravelBoundary;
import com.pb.stratus.controller.util.RestUrlExecutor;
import com.pb.stratus.controller.wmsprofile.WMSProfile;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GU003DU on 8/7/2019.
 */
public class ProjectService {

    private static final Logger logger = LogManager
            .getLogger(ProjectService.class);
    private static final String FAILED_TO_PROCESS_REQUEST = "Failed to process request, check logs for detailed info";

    private static final String ROUTING_CONFIG_JSON = "routingConfigs.json";
    private static final String GEOCODING_CONFIG_JSON = "geocodingConfigs.json";
    private static final String WMS_CONFIG_JSON = "externalWMSConfigs.json";
    private static final String TILING_CONFIG_JSON = "externalTilingConfigs.json";
    private static final String FUNCTIONALITY_PROFILES = "functionalityProfiles";
    private static final String ANALYST_CONFIGURATION_JSON = "analystConfig.json";
    private static final String JSON = ".json";
    private final String apiKey;
    private final RestUrlExecutor restUrlExecutor;
    private String serviceEndpointUrl;

    public ProjectService(String endpointUrl, String apiKey, RestUrlExecutor restUrlExecutor) {
        if (endpointUrl.charAt(endpointUrl.length() - 1) != '/') {
            serviceEndpointUrl = endpointUrl + "/";
        } else {
            serviceEndpointUrl = endpointUrl;
        }
        this.apiKey = apiKey;
        this.restUrlExecutor = restUrlExecutor;
    }

    public RoutingConfig describeRoutingConfig(String resourceId) throws Exception {
        URI projectUri = new URI(serviceEndpointUrl + ROUTING_CONFIG_JSON);
        return populateRoutingConfig(describe(resourceId, projectUri.toString()));
    }

    public GeoSearchParams describeGeocodingConfig(String resourceId, GeoSearchParams geoSearchParams) throws Exception {
        URI projectUri = new URI(serviceEndpointUrl + GEOCODING_CONFIG_JSON);
        return populateGeocodingConfig(describe(resourceId, projectUri.toString()), geoSearchParams);
    }

    public WMSProfile getWMSProfile(String wmsProfile) throws Exception {
        URI projectUri = new URI(serviceEndpointUrl + WMS_CONFIG_JSON);
        return populateWMSProfile(describe(wmsProfile, projectUri.toString()));
    }

    public String getThirdPartyProfile(String tileServiceProfile) throws Exception {
        URI projectUri = new URI(serviceEndpointUrl + TILING_CONFIG_JSON);
        return describe(tileServiceProfile, projectUri.toString());
    }

    public String getFunctionalityProfile(String resourceId) throws Exception {
        URI projectUri = new URI(serviceEndpointUrl + FUNCTIONALITY_PROFILES + resourceId + JSON);
        return restUrlExecutor.get(projectUri.toString());
    }

    public int getSessionTimeOut() throws Exception {
        URI projectUri = new URI(serviceEndpointUrl + ANALYST_CONFIGURATION_JSON);
        JSONObject config = JSONObject.fromObject(restUrlExecutor.get(projectUri.toString()));
        return config.getInt("sessionTimeout");
    }

    private String describe(String resourceId, String endpoint) throws Exception {
        // execute request
        JSONObject json = new JSONObject();
        json.put("apiKey", apiKey);
        JSONArray resources = new JSONArray();
        resources.add(resourceId);
        json.put("resources", resources);
        try {
            return restUrlExecutor.post(json.toString(), endpoint);
        } catch (Exception e) {
            logger.error(FAILED_TO_PROCESS_REQUEST, e);
            throw new Exception(FAILED_TO_PROCESS_REQUEST);
        }
    }

    private WMSProfile populateWMSProfile(String wmsConfig) throws Exception {
        JSONArray array = JSONArray.fromObject(wmsConfig);
        if (array.isEmpty()) {
            logger.error("Did not find the WMS configuration. " + wmsConfig);
            throw new Exception("Did not find the WMS configuration");
        }
        JSONObject jsonObject = array.getJSONObject(0);
        JSONObject jsonDefObj = jsonObject.getJSONObject("definition");
        JSONObject authType = jsonDefObj.getJSONObject("auth");
        WMSProfile wmsProfile = new WMSProfile();

        if (jsonDefObj.has("auth") && authType.getString("type").equals(AuthType.basicAuth.name())) {
            if (StringUtils.isEmpty(authType.getString("user")) || StringUtils.isEmpty(authType.getString("password"))) {
                logger.error("user and password required. " + wmsConfig);
                throw new Exception("user and password required");
            } else {
                wmsProfile.setAuthn(Authentication.basic);
                Map<String, String> credentials = new HashMap<>();
                credentials.put("username", authType.getString("user"));
                credentials.put("password", authType.getString("password"));
                wmsProfile.setCredentials(credentials);
            }
        } else if (jsonDefObj.has("auth") && authType.getString("type").equals(AuthType.uriAuth.name())) {
            if (StringUtils.isEmpty(authType.getString("user")) || StringUtils.isEmpty(authType.getString("userQueryParam"))) {
                logger.error("user and userQueryParam required. " + wmsConfig);
                throw new Exception("user and userQueryParam required");
            } else {
                wmsProfile.setAuthn(Authentication.uri_param);
                Map<String, String> credentials = new HashMap<>();
                credentials.put("username", authType.getString("user"));
                credentials.put("usernameQueryParam", authType.getString("userQueryParam"));
                if (!StringUtils.isEmpty(authType.getString("password"))) {
                    credentials.put("password", authType.getString("password"));
                }
                if (!StringUtils.isEmpty(authType.getString("passwordQueryParam"))) {
                    credentials.put("passwordQueryParam", authType.getString("passwordQueryParam"));
                }
                wmsProfile.setCredentials(credentials);
            }
        }
        JSONArray layers = jsonDefObj.getJSONArray("layers");
        List<String> legendUrls = new ArrayList<>();
        for (int i = 0; i < layers.size(); i++) {
            if (layers.getJSONObject(i).has("legendUrl")) {
                legendUrls.add(layers.getJSONObject(i).getString("legendUrl"));
            }
        }
        wmsProfile.setLegendURLs(legendUrls);
        wmsProfile.setUrl(jsonDefObj.getJSONObject("capabilities").getString("url"));
        return wmsProfile;
    }

    private GeoSearchParams populateGeocodingConfig(String config, GeoSearchParams searchParams) throws Exception {
        JSONArray array = JSONArray.fromObject(config);
        if (array.isEmpty()) {
            logger.error("Did not find any geocoding configuration. " + config);
            throw new Exception("Did not find any geocoding configuration");
        }
        JSONObject jsonObject = array.getJSONObject(0);
        JSONObject jsonDefObj = jsonObject.getJSONObject("definition");
        searchParams.setName(jsonObject.getString("name"));

        JSONObject authType = jsonDefObj.getJSONObject("auth");

        if (authType == null || StringUtils.isEmpty(authType.getString("type"))) {
            logger.error("AuthType is required. " + config);
            throw new Exception("AuthType is required");
        }

        if (authType.getString("type").equals(AuthType.basicAuth.name())) {
            if (StringUtils.isEmpty(authType.getString("user")) || StringUtils.isEmpty(authType.getString("password"))) {
                logger.error("user and password required. " + config);
                throw new Exception("user and password required");
            } else {
                searchParams.setAuthType(AuthType.basicAuth);
                searchParams.setUsername(authType.getString("user"));
                searchParams.setPassword(authType.getString("password"));
            }
        } else if (authType.getString("type").equals(AuthType.oAuth2.name())) {
            if (StringUtils.isEmpty(authType.getString("apiKey")) || StringUtils.isEmpty(authType.getString("secret"))) {
                logger.error("apiKey and secret required. " + config);
                throw new Exception("apiKey and secret required");
            } else {
                searchParams.setAuthType(AuthType.oAuth2);
                searchParams.setUsername(authType.getString("apiKey"));
                searchParams.setPassword(authType.getString("secret"));
            }
        }
        searchParams.setEndPoint(jsonDefObj.getString("url"));
        searchParams.setDeploymentType(DeploymentType.valueOf(jsonDefObj.getString("deployment")));
        searchParams.setServiceType(GeocodingServiceType.valueOf(jsonDefObj.getString("serviceType")));

        List<Option> options = new ArrayList<>();
        if (jsonDefObj.has("options")) {
            JSONArray optionsJSONArray = jsonDefObj.getJSONArray("options");
            for (int i = 0; i < optionsJSONArray.size(); i++) {
                JSONObject optionJsonObj = optionsJSONArray.getJSONObject(i);
                if (optionJsonObj.getString("name").equals(LIAPIConstants.COUNTRY)) {
                    searchParams.setCountry(optionJsonObj.getString("value"));
                } else {
                    Option option = new Option(optionJsonObj.getString("name"), optionJsonObj.getString("value"));
                    options.add(option);
                }
            }
        }
        searchParams.setOptions(options);
        return searchParams;
    }

    private RoutingConfig populateRoutingConfig(String routingConfig) throws Exception {
        RoutingConfig config = new RoutingConfig();
        JSONArray array = JSONArray.fromObject(routingConfig);
        if (array.isEmpty()) {
            logger.error("Did not find any routing configuration. " + routingConfig);
            throw new Exception("Did not find any routing configuration");
        }
        JSONObject jsonObject = array.getJSONObject(0);
        JSONObject jsonDefObj = jsonObject.getJSONObject("definition");

        config.setName(jsonObject.getString("name"));

        JSONObject authType = jsonDefObj.getJSONObject("auth");

        if (authType == null || StringUtils.isEmpty(authType.getString("type"))) {
            logger.error("AuthType is required. " + routingConfig);
            throw new Exception("AuthType is required");
        }

        if (authType.getString("type").equals(AuthType.basicAuth.name())) {
            if (StringUtils.isEmpty(authType.getString("user")) || StringUtils.isEmpty(authType.getString("password"))) {
                logger.error("user and password required. " + routingConfig);
                throw new Exception("user and password required");
            } else {
                config.setAuthType(AuthType.basicAuth);
                config.setUser(authType.getString("user"));
                config.setPassword(authType.getString("password"));
            }
        } else if (authType.getString("type").equals(AuthType.oAuth2.name())) {
            if (StringUtils.isEmpty(authType.getString("apiKey")) || StringUtils.isEmpty(authType.getString("secret"))) {
                logger.error("apiKey and secret required. " + routingConfig);
                throw new Exception("apiKey and secret required");
            } else {
                config.setAuthType(AuthType.oAuth2);
                config.setUser(authType.getString("apiKey"));
                config.setPassword(authType.getString("secret"));
            }
        }

        config.setUrl(jsonDefObj.getString("url"));
        config.setDeployment(DeploymentType.valueOf(jsonDefObj.getString("deployment")));

        if (jsonDefObj.has("databases")) {
            JSONArray dbJSONArray = jsonDefObj.getJSONArray("databases");
            List<RoutingDB> databases = new ArrayList<>();
            for (int i = 0; i < dbJSONArray.size(); i++) {
                JSONObject dbJsonObj = dbJSONArray.getJSONObject(i);
                RoutingDB routingDB = new RoutingDB(dbJsonObj.getString("name"), dbJsonObj.getBoolean("enable"));
                databases.add(routingDB);
            }
            config.setDatabases(databases);
        }
        if (jsonDefObj.has("defaultDatabase")) {
            config.setDefaultDatabase(jsonDefObj.getString("defaultDatabase"));
        }

        if (jsonDefObj.has("defaultTravelDistanceBoundary")) {
            JSONObject dtdbJsonObj = jsonDefObj.getJSONObject("defaultTravelDistanceBoundary");
            TravelBoundary defaultDistanceBoundary = new TravelBoundary(dtdbJsonObj.getString("unit"),
                    dtdbJsonObj.getString("cost"));
            config.setDefaultTravelDistanceBoundary(defaultDistanceBoundary);
        }

        if (jsonDefObj.has("defaultTravelTimeBoundary")) {
            JSONObject dttbJsonObj = jsonObject.getJSONObject("definition")
                    .getJSONObject("defaultTravelTimeBoundary");
            TravelBoundary defaultTimeBoundary = new TravelBoundary(dttbJsonObj.getString("unit"),
                    dttbJsonObj.getString("cost"));
            config.setDefaultTravelTimeBoundary(defaultTimeBoundary);
        }

        config.setIsHistoricTrafficTimeBucket(jsonDefObj.getBoolean("historicTrafficTimeBucket"));

        if (jsonDefObj.has("options")) {
            JSONArray optionsJSONArray = jsonDefObj.getJSONArray("options");
            List<Option> options = new ArrayList<>();
            for (int i = 0; i < optionsJSONArray.size(); i++) {
                JSONObject optionJsonObj = optionsJSONArray.getJSONObject(i);
                Option option = new Option(
                        optionJsonObj.getString("name"), optionJsonObj.getString("value"));
                options.add(option);
            }
            config.setOptions(options);
        }
        return config;
    }
}
