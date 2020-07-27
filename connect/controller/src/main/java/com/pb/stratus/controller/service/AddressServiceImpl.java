package com.pb.stratus.controller.service;

import com.pb.gazetteer.webservice.*;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.RemoteAccessException;
import com.pb.stratus.controller.geocoder.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.graphdata.utilities.contract.Contract;

import java.util.List;

public class AddressServiceImpl implements AddressService {
    static final String INTERNATIONAL_GEOCODER_KEY = "BING"; //old value- InternationalGeocoder
    static final String INTERNATIONAL_GEOCODER_KEY_OLD = "InternationalGeocoder";
    static final String LOCATE_GEOCODER_KEY = "LOCATE";
    static final String EGM_GEOCODER_KEY = "EGM";
    static final String GGM_GEOCODER_KEY = "GGM";
    static final String LIAPI_GEOCODER_KEY = "LIAPI";

    private final static Logger logger = LogManager.getLogger(AddressServiceImpl.class);

    private SingleLineAddress singleLineAddressWebService;
    private BingAddressService bingAddressService;
    private GeoService geoSrvc;
    private GGMService ggmService;
    private LIAPIService liapiService;
    private ProjectService projectService;

    public AddressServiceImpl(SingleLineAddress singleLineAddressWebService, BingAddressService bingAddressService,
                              GeoService geoService, GGMService ggmService,
                              LIAPIService liapiService, ProjectService projectService) {
        this.singleLineAddressWebService = singleLineAddressWebService;
        this.bingAddressService = bingAddressService;
        this.geoSrvc = geoService;
        this.ggmService = ggmService;
        this.liapiService = liapiService;
        this.projectService = projectService;
    }

    /**
     * Performs a search using given search input data.
     *
     * @param searchParameters, search parameters.
     * @return a <List>Address</List> List of Addresses.
     */
    public List<Address> findAddresses(SearchParameters searchParameters) throws InvalidGazetteerException {
        Contract.pre(!StringUtils.isBlank(searchParameters.getSearchString()),
                "Search expression required");
        Contract.pre(searchParameters.getMaxRecords() > 0,
                "Positive maxResults required");
        List<Address> address;
        try {
            if (INTERNATIONAL_GEOCODER_KEY.equals(searchParameters.getGazetteerService())) {
                address = bingAddressService.search(searchParameters);
            } else if (EGM_GEOCODER_KEY.equalsIgnoreCase(searchParameters.getGazetteerService())) {
                GeoSearchParams searchParams = projectService.describeGeocodingConfig(searchParameters.getGazetteerName(),
                        new GeoSearchParams());
                geoSrvc.setGeoHelper(EGMHelper.getInstance());
                address = geoSrvc.search(searchParams, searchParameters);
            } else if (GGM_GEOCODER_KEY.equalsIgnoreCase(searchParameters.getGazetteerService())) {
                GeoSearchParams searchParams = projectService.describeGeocodingConfig(searchParameters.getGazetteerName(),
                        new GeoSearchParams());
                ggmService.setGeoHelper(GGMHelper.getInstance());
                address = ggmService.search(searchParams, searchParameters);
            } else if (LIAPI_GEOCODER_KEY.equalsIgnoreCase(searchParameters.getGazetteerService())) {
                GeoSearchParams searchParams = projectService.describeGeocodingConfig(searchParameters.getGazetteerName(),
                        new GeoSearchParams());
                liapiService.setGeoHelper(LIAPIHelper.getInstance());
                address = liapiService.search(searchParams, searchParameters);
            } else if (LOCATE_GEOCODER_KEY.equalsIgnoreCase(searchParameters.getGazetteerService())) {
                address = singleLineAddressWebService.search(searchParameters);
            }
            // this is done to provision for old geocoder tag <gazetteer-name>InternationalGeocoder</gazetteer-name>
            else if (INTERNATIONAL_GEOCODER_KEY_OLD.equals(searchParameters.getGazetteerName())) {
                address = bingAddressService.search(searchParameters);
            } else {
                // searchParameters.setGazetteerName(searchParameters.getGazetteerService());//to set actual name of 'gazetteer' in default scenario;
                if (!isValidGazetteerName(searchParameters.getGazetteerName(), searchParameters.getTenantName())) {
                    logger.debug("Gazetteer name is invalid. Throwing InvalidGazetteerException.");
                    throw new InvalidGazetteerException("Invalid Gazetteer Name!");
                }
                address = singleLineAddressWebService.search(searchParameters);
            }
        } catch (Exception e) {
            if (e instanceof InvalidGazetteerException) {
                logger.error("InvalidGazetteerException: ", e);
                throw (InvalidGazetteerException) e;
            } else {
                logger.error("RemoteAccessException: ", e);
                throw new RemoteAccessException(e);
            }
        }
        return address;
    }

    /**
     * Performs geocoding action on input address data.
     *
     * @param ggmRequestParams GGMRequestParams
     * @return a JSONObject containing geocoded data
     */
    public JSONObject geocodeAddresses(GGMRequestParams ggmRequestParams) throws InvalidGazetteerException {
        JSONObject geocodedAddressList = new JSONObject();
        try {
            if (LIAPI_GEOCODER_KEY.equalsIgnoreCase(ggmRequestParams.getGazetteerService())) {
                GeoSearchParams searchParams = projectService.describeGeocodingConfig(ggmRequestParams.getGazetteerName(),
                        new GeoSearchParams());
                liapiService.setGeoHelper(LIAPIHelper.getInstance());
                geocodedAddressList = liapiService.geocodeAddresses(searchParams, ggmRequestParams);
            } else if (GGM_GEOCODER_KEY.equalsIgnoreCase(ggmRequestParams.getGazetteerService())) {
                GeoSearchParams searchParams = projectService.describeGeocodingConfig(ggmRequestParams.getGazetteerName(),
                        new GeoSearchParams());
                ggmService.setGeoHelper(GGMHelper.getInstance());
                geocodedAddressList = ggmService.geocodeAddresses(searchParams, ggmRequestParams);
            } else if (!isValidGazetteerName(ggmRequestParams.getGazetteerName(), ggmRequestParams.getTenantName())) {
                logger.debug("Gazetteer name is invalid. Throwing InvalidGazetteerException.");
                throw new InvalidGazetteerException("Invalid Gazetteer Name!");
            }
        } catch (Exception e) {
            if (e instanceof InvalidGazetteerException) {
                logger.debug("InvalidGazetteerException: ", e);
                throw (InvalidGazetteerException) e;
            } else {
                logger.debug("RemoteAccessException: ", e);
                throw new RemoteAccessException(e);
            }
        }
        return geocodedAddressList;
    }

    /**
     * Determines if the Gazetteer name is a valid gazetteer name or not.
     *
     * @param gazetteerName, tenantName.
     * @return a boolean value
     */
    public boolean isValidGazetteerName(String gazetteerName, String tenantName) {
        try {
            for (GazetteerInstance gazetteerInstance : this.singleLineAddressWebService.getGazetteerNames(tenantName).getGazetteerInstances()) {
                if (gazetteerInstance.getGazetteerName().equals(gazetteerName)) {
                    return true;
                }
            }
        } catch (LocateException_Exception e) {
            logger.debug("Error retrieving the list of Gazetteer Names. LocateException_Exception thrown.");
        }
        return false;
    }
}
