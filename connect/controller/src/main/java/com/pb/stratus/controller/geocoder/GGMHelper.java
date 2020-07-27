package com.pb.stratus.controller.geocoder;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides helper methods to construct GGM specific client
 */
public class GGMHelper implements GeoServiceHelper {

    private static volatile GGMHelper instance;

    private GGMHelper() {
    }

    /**
     * This method builds the URL.
     *
     * @param params
     * @param searchParams
     * @return String
     * @throws java.io.UnsupportedEncodingException
     */
    public String buildURL(GeoSearchParams params, SearchParameters searchParams)
            throws UnsupportedEncodingException {
        String geourl = params.getEndPoint();
        if (geourl.charAt(geourl.length() - 1) != '/') {
            geourl = geourl + "/geocode.json";
        } else {
            geourl = geourl + "geocode.json";
        }
        return geourl;
    }

    /**
     * parses individual candidate responses to get the address,projection
     *
     * @param candidate
     * @return
     */
    public Address parseAddress(JSONObject candidate) {
        Address address = new Address();
        JSONObject candidate_address = candidate.getJSONObject(GGMConstants.RESPONSE_ADDRESS);

        if (candidate_address != null) {
            //prepare Address from JSON;
            address.setAddress(prepareAddress(candidate_address));
        }

        //To set coordinates
        JSONObject geometryObj = candidate.getJSONObject(GGMConstants.RESPONSE_GEOMETRY);
        if (geometryObj != null) {
            JSONArray coordinates = geometryObj.getJSONArray(GGMConstants.RESPONSE_GEOMETRY_COORDINATES_LONGITUDE);
            if (coordinates != null) {
                address.setX(Double.valueOf(coordinates.get(0) + ""));
                address.setY(Double.valueOf(coordinates.get(1) + ""));
            }
            //to get- ProjectionSystem;
            JSONObject crsObj = geometryObj.getJSONObject(GGMConstants.RESPONSE_GEOMETRY_CRS);
            if (crsObj != null) {
                JSONObject propertiesObj = crsObj.getJSONObject(GGMConstants.RESPONSE_GEOMETRY_CRS_PROPERTIES);

                if (propertiesObj != null) {
                    String projection = propertiesObj.getString(GGMConstants.RESPONSE_GEOMETRY_CRS_PROPERTIES_NAME);
                    address.setSrs(projection);
                }
            }
        }
        return address;
    }

    /**
     * Parse the response and Prepare Address.
     *
     * @param address
     * @return
     */
    private String prepareAddress(JSONObject address) {

        StringBuilder addressBuilder = new StringBuilder();
        int check = 0;

        //Here all address attributes are to be checked and merged into final Address attribute.
        if (address.getString(GGMConstants.RESPONSE_ADDRESS_MAINLINE) != null &&
                !(address.getString(GGMConstants.RESPONSE_ADDRESS_MAINLINE) + "").trim().equals("")) {
            check++;
            addressBuilder.append(address.getString(GGMConstants.RESPONSE_ADDRESS_MAINLINE));
        }

        if (address.getString(GGMConstants.RESPONSE_ADDRESS_LASTLINE) != null &&
                !(address.getString(GGMConstants.RESPONSE_ADDRESS_LASTLINE) + "").trim().equals("")) {
            check++;
            addressBuilder.append("," + address.getString(GGMConstants.RESPONSE_ADDRESS_LASTLINE));
        }

        if (check != 2) {
            //maintains the order of insertion and unique elements
            Set<String> secondaryAddress = new LinkedHashSet<>();

            if (address.getString(GGMConstants.RESPONSE_ADDRESS_AREANAME_4) != null &&
                    !(address.getString(GGMConstants.RESPONSE_ADDRESS_AREANAME_4) + "").trim().equals("")) {
                secondaryAddress.add(address.getString(GGMConstants.RESPONSE_ADDRESS_AREANAME_4));
            }

            if (address.getString(GGMConstants.RESPONSE_ADDRESS_AREANAME_3) != null &&
                    !(address.getString(GGMConstants.RESPONSE_ADDRESS_AREANAME_3) + "").trim().equals("")) {
                secondaryAddress.add(address.getString(GGMConstants.RESPONSE_ADDRESS_AREANAME_3));
            }

            if (address.getString(GGMConstants.RESPONSE_ADDRESS_AREANAME_2) != null &&
                    !(address.getString(GGMConstants.RESPONSE_ADDRESS_AREANAME_2) + "").trim().equals("")) {
                secondaryAddress.add(address.getString(GGMConstants.RESPONSE_ADDRESS_AREANAME_2));
            }

            if (address.getString(GGMConstants.RESPONSE_ADDRESS_AREANAME_1) != null &&
                    !(address.getString(GGMConstants.RESPONSE_ADDRESS_AREANAME_1) + "").trim().equals("")) {
                secondaryAddress.add(address.getString(GGMConstants.RESPONSE_ADDRESS_AREANAME_1));
            }

            if (address.getString(GGMConstants.RESPONSE_ADDRESS_POSTCODE_1) != null &&
                    !(address.getString(GGMConstants.RESPONSE_ADDRESS_POSTCODE_1) + "").trim().equals("")) {
                secondaryAddress.add(address.getString(GGMConstants.RESPONSE_ADDRESS_POSTCODE_1));
            }

            if (address.getString(GGMConstants.RESPONSE_ADDRESS_POSTCODE_2) != null &&
                    !(address.getString(GGMConstants.RESPONSE_ADDRESS_POSTCODE_2) + "").trim().equals("")) {
                secondaryAddress.add(address.getString(GGMConstants.RESPONSE_ADDRESS_POSTCODE_2));
            }

            if (address.getString(GGMConstants.RESPONSE_ADDRESS_COUNTRY) != null &&
                    !(address.getString(GGMConstants.RESPONSE_ADDRESS_COUNTRY) + "").trim().equals("")) {
                secondaryAddress.add(address.getString(GGMConstants.RESPONSE_ADDRESS_COUNTRY));
            }

            if (addressBuilder.length() > 0 && secondaryAddress.size() > 0) {
                addressBuilder.append(",");
            }

            // add elements to string builder
            Iterator<String> iterator = secondaryAddress.iterator();
            for (; ; ) {
                if (iterator.hasNext()) {
                    addressBuilder.append(iterator.next());
                }
                if (!iterator.hasNext())
                    break;
                addressBuilder.append(",");
            }
        }
        return addressBuilder.toString();
    }

    /**
     * Method to get an instance of GGMHelper.
     *
     * @return
     */
    public static GGMHelper getInstance() {
        if (null == instance) {
            synchronized (GGMHelper.class) {
                if (null == instance) {
                    instance = new GGMHelper();
                }
            }
        }
        return instance;
    }

}