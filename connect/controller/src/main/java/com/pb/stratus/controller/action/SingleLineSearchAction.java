package com.pb.stratus.controller.action;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.RemoteAccessException;
import com.pb.stratus.controller.exception.AnalystCreditsExpiredException;
import com.pb.stratus.controller.geometry.GeometryService;
import com.pb.stratus.controller.locator.Location;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfigRepository;
import com.pb.stratus.controller.service.AddressService;
import com.pb.stratus.controller.util.LocationTransformer;
import com.pb.stratus.core.configuration.TenantNameHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.pb.stratus.controller.util.TypeConversionUtils.getIntegerValue;
import static com.pb.stratus.controller.util.TypeConversionUtils.getStringValue;

/**
 * This class provides the interface to access the gazetteer single line
 * Address webService. SingleLineAddress service provides search method to search
 * for single line queries. The search method
 *
 * @author Kavitha Yeruva
 */
public class SingleLineSearchAction extends DataInterchangeFormatControllerAction {

    private static final int DEFAULT_COUNT_VALUE = 50;

    static final String INTERNATIONAL_GEOCODER_OLD = "InternationalGeocoder";

    private final static Logger logger = LogManager.getLogger(SingleLineSearchAction.class);

    private AddressService addressService;

    private MapConfigRepository mapConfigRepository;

    private TenantNameHolder m_tenantNameHolder;

    private LocationTransformer transformer;

    public SingleLineSearchAction(AddressService addressService,
                                  GeometryService geometryService,
                                  MapConfigRepository mapConfigRepository,
                                  TenantNameHolder tenantNameHolder) {
        this.addressService = addressService;
        this.mapConfigRepository = mapConfigRepository;
        //temporary code for while multi-tenant locate service, but connect isn't yet.
        m_tenantNameHolder = tenantNameHolder;
        transformer = new LocationTransformer(geometryService);
    }

    protected Object createObject(HttpServletRequest request)
            throws ServletException, IOException, InvalidGazetteerException {

        Map parameters = request.getParameterMap();
        String query = getStringValue("query", parameters, null);
        String culture = getStringValue("culture", parameters, null);

        if (StringUtils.isBlank(query)) {
            throw new InvalidParameterException(
                    "query is a mandatory parameter for Locator.");
        }

        int count = getIntegerValue("count", parameters, DEFAULT_COUNT_VALUE);
        //Locate Service will bring 5 results, in case of autoSearch functionality.
        String[] isAutoSearchEnabled = (String[]) parameters.get("autoSearch");
        if (null != isAutoSearchEnabled) {
            count = 5;
        }

        String mapSrs = getStringValue("srs", parameters, null);
        String gazetteerName = getStringValue("gazetteerName", parameters, null);
        String gazetteerService = getStringValue("gazetteerService", parameters, null);
        String mapConfigName = getStringValue("mapcfg", parameters, null);
        int searchResultCount = 0;

        MapConfig mapConfig = null;
        if (mapConfigName != null) {
            if (!StringUtils.isBlank(mapConfigName)) {
                if (mapConfigName.toLowerCase().endsWith(".xml")) {
                    mapConfigName = mapConfigName.substring(0, mapConfigName.length() - 4);
                }
                mapConfig = mapConfigRepository.getMapConfig(mapConfigName);
                if (mapConfig == null) {
                    logger.debug("MapConfig not found");
                    throw new IllegalArgumentException();

                }

                //to get SearchResultCount from XML;
                if(mapConfig.getMapConfigDefinition() != null) {
                    searchResultCount = mapConfig.getMapConfigDefinition().getAddressSearchResultsCount();
                }

                if (StringUtils.isBlank(gazetteerName)) {
                    gazetteerName = mapConfig.getDefaultGazetteerName();
                }

                gazetteerService = mapConfig.getDefaultGazetteerService();

            } else {
                logger.debug("MapConfig name cannot be blank");
                throw new IllegalArgumentException();
            }
        }else{
            //For requests coming from mobile, count variable has searchResultCount value;
            searchResultCount = count;
        }

        if (gazetteerName == null) {
            logger.info("Gazetteer name still not found. Throwing exception");
            throw new IllegalArgumentException();
        }

        if(gazetteerName.equalsIgnoreCase(INTERNATIONAL_GEOCODER_OLD)) {
            String countryName = "";
            if (mapConfig != null) {
                countryName = readCountryNameFromMapConfig(mapConfig);
            } else // For connect-mobile
            {
                countryName = " " + getStringValue("country", parameters, null);
            }
            if (countryName != null) {
                query = query + countryName;
            }
        }

        SearchParameters searchParameters = new SearchParameters();
        populateSearchParameters(searchParameters, query, searchResultCount, gazetteerName, culture, gazetteerService);
        try {
            List<Location> result = search(mapSrs, searchParameters);
            return result;
        }
        catch (AnalystCreditsExpiredException e){
            Map<String,String> errors = new HashMap<>();
            errors.put("error",e.getCause().getMessage());
            return errors;
        }
        catch (RemoteAccessException e){
            Map<String,String> errors = new HashMap<>();
            errors.put("error",e.getCause().getMessage());
            return errors;
        }
    }

    /**
     * Read country name to restrict the search to that country itself when gazetteerName
     * is InternationalGeocoder.
     *
     * @param mapConfig
     * @return
     */
    private String readCountryNameFromMapConfig(MapConfig mapConfig) {
        String internationalGeocoderTargetCountry = null;
        try {
            internationalGeocoderTargetCountry = mapConfig.getInternationalGeocoderTargetCountry();
            if (internationalGeocoderTargetCountry == null) {
                logger.debug("internationalGeocoderTargetCountry tag is not present in the mapconfig");
                return "";
            }
            return " " + internationalGeocoderTargetCountry;
        } catch (IllegalArgumentException e) {
            logger.debug("Exception thrown while retrieving internationalGeocoderTargetCountry tag");
            return "";
        }
    }

    /**
     * Method to populates SearchParameters Object.
     *
     * @param searchParameters
     * @param query
     * @param searchResultCount
     * @param gazetteerName
     * @param culture
     * @param gazetteerService
     */
    private void populateSearchParameters(SearchParameters searchParameters,
                                          String query, int searchResultCount, String gazetteerName, String culture, String gazetteerService) {
        searchParameters.setSearchString(query);
        searchParameters.setMaxRecords(searchResultCount);
        searchParameters.setGazetteerName(gazetteerName);
        searchParameters.setTenantName(m_tenantNameHolder.getTenantName());
        searchParameters.setCulture(culture);
        searchParameters.setGazetteerService(gazetteerService);
    }

    private List<Location> search(String mapSrs,
                                  SearchParameters searchParameters) throws InvalidGazetteerException {
        List<Address> addresses = addressService.findAddresses(searchParameters);
        List<Location> locations = new LinkedList<Location>();
        // expecting data projection on all points is equal.Not sure
        // how this works with multiple source concept.
        String dataProjection = mapSrs;
        for (Address a : addresses) {
            Location loc = new Location(a.getId(), 0.0f, a.getAddress(),
                    a.getX(), a.getY(), a.getSrs());
            dataProjection = a.getSrs();
            locations.add(loc);
        }
        // transform the locations if the projection is not same.
        if (mapSrs != null && !dataProjection.equals(mapSrs)) {
            return transformer.transformLocations(locations, mapSrs);
        } else {
            return locations;
        }
    }
}
