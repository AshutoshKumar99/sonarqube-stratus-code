package com.pb.stratus.controller.geocoder;

import com.pb.gazetteer.webservice.Address;
import com.pb.gazetteer.webservice.SearchParameters;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author ekiras
 */
public class LIAPIHelper implements GeoServiceHelper {

    private static LIAPIHelper instance;


    private LIAPIHelper() {
    }


    @Override
    public String buildURL(GeoSearchParams params, SearchParameters searchParams) throws UnsupportedEncodingException {
        return params.getEndPoint();
    }

    @Override
    public Address parseAddress(JSONObject candidate) {
        Address address = new Address();

        if (candidate != null) {
            address.setAddress(getAddress(candidate.getJSONObject(LIAPIConstants.RESPONSE_ADDRESS)));
            address.setX(getX(candidate));
            address.setY(getY(candidate));
            address.setSrs(getSrs(candidate));
            address.setId(getId(candidate));
            address.setScore(getScore(candidate));

        }
        return address;
    }

    private float getScore(JSONObject candidate) {
        return 0;
    }

    private String getId(JSONObject candidate) {
        return null;
    }

    private String getSrs(JSONObject candidate) {
        return candidate.getJSONObject(LIAPIConstants.RESPONSE_GEOMETRY).getJSONObject(LIAPIConstants.RESPONSE_GEOMETRY_CRS).getJSONObject(LIAPIConstants.RESPONSE_GEOMETRY_CRS_PROPERTIES).getString(LIAPIConstants.RESPONSE_GEOMETRY_CRS_PROPERTIES_NAME);
    }

    private double getX(JSONObject candidate) {
        return candidate.getJSONObject(LIAPIConstants.RESPONSE_GEOMETRY).getJSONArray(LIAPIConstants.RESPONSE_GEOMETRY_COORDINATES).getDouble(0);
    }

    private double getY(JSONObject candidate) {
        return candidate.getJSONObject(LIAPIConstants.RESPONSE_GEOMETRY).getJSONArray(LIAPIConstants.RESPONSE_GEOMETRY_COORDINATES).getDouble(1);
    }

    private String getAddress(JSONObject address) {
        StringBuilder addressBuilder = new StringBuilder();
        int check = 0;

        //Here all address attributes are to be checked and merged into final Address attribute.
        try {
            if (address.getString(LIAPIConstants.RESPONSE_ADDRESS_MAINLINE) != null &&
                    !(address.getString(LIAPIConstants.RESPONSE_ADDRESS_MAINLINE) + "").trim().equals("")) {
                check++;
                addressBuilder.append(address.getString(LIAPIConstants.RESPONSE_ADDRESS_MAINLINE));
            }
        } catch (JSONException ex) {
            // Don't do anything
        }

        try {
            if (address.getString(LIAPIConstants.RESPONSE_ADDRESS_LASTLINE) != null &&
                    !(address.getString(LIAPIConstants.RESPONSE_ADDRESS_LASTLINE) + "").trim().equals("")) {
                check++;
                if (addressBuilder.toString().equals("")) {
                    addressBuilder.append(address.getString(LIAPIConstants.RESPONSE_ADDRESS_LASTLINE));
                } else {
                    addressBuilder.append(",").append(address.getString(LIAPIConstants.RESPONSE_ADDRESS_LASTLINE));
                }
            }
        } catch (JSONException ex) {
            // Don't do anything
        }

        if (check != 2) {
            //maintains the order of insertion and unique elements
            Set<String> secondaryAddress = new LinkedHashSet<>();

            try {
                if (address.getString(LIAPIConstants.RESPONSE_ADDRESS_AREANAME_4) != null &&
                        !(address.getString(LIAPIConstants.RESPONSE_ADDRESS_AREANAME_4) + "").trim().equals("")) {
                    secondaryAddress.add(address.getString(LIAPIConstants.RESPONSE_ADDRESS_AREANAME_4));
                }
            } catch (JSONException ex) {
                // Don't do anything
            }

            try {
                if (address.getString(LIAPIConstants.RESPONSE_ADDRESS_AREANAME_3) != null &&
                        !(address.getString(LIAPIConstants.RESPONSE_ADDRESS_AREANAME_3) + "").trim().equals("")) {
                    secondaryAddress.add(address.getString(LIAPIConstants.RESPONSE_ADDRESS_AREANAME_3));
                }
            } catch (JSONException ex) {
                // Don't do anything
            }

            try {
                if (address.getString(LIAPIConstants.RESPONSE_ADDRESS_AREANAME_2) != null &&
                        !(address.getString(LIAPIConstants.RESPONSE_ADDRESS_AREANAME_2) + "").trim().equals("")) {
                    secondaryAddress.add(address.getString(LIAPIConstants.RESPONSE_ADDRESS_AREANAME_2));
                }
            } catch (JSONException ex) {
                // Don't do anything
            }

            try {
                if (address.getString(LIAPIConstants.RESPONSE_ADDRESS_AREANAME_1) != null &&
                        !(address.getString(LIAPIConstants.RESPONSE_ADDRESS_AREANAME_1) + "").trim().equals("")) {
                    secondaryAddress.add(address.getString(LIAPIConstants.RESPONSE_ADDRESS_AREANAME_1));
                }
            } catch (JSONException ex) {
                // Don't do anything
            }

            try {
                if (address.getString(LIAPIConstants.RESPONSE_ADDRESS_POSTCODE_1) != null &&
                        !(address.getString(LIAPIConstants.RESPONSE_ADDRESS_POSTCODE_1) + "").trim().equals("")) {
                    secondaryAddress.add(address.getString(LIAPIConstants.RESPONSE_ADDRESS_POSTCODE_1));
                }
            } catch (JSONException ex) {
                // Don't do anything
            }

            try {
                if (address.getString(LIAPIConstants.RESPONSE_ADDRESS_POSTCODE_2) != null &&
                        !(address.getString(LIAPIConstants.RESPONSE_ADDRESS_POSTCODE_2) + "").trim().equals("")) {
                    secondaryAddress.add(address.getString(LIAPIConstants.RESPONSE_ADDRESS_POSTCODE_2));
                }
            } catch (JSONException ex) {
                // Don't do anything
            }

            try {
                if (address.getString(LIAPIConstants.RESPONSE_ADDRESS_COUNTRY) != null &&
                        !(address.getString(LIAPIConstants.RESPONSE_ADDRESS_COUNTRY) + "").trim().equals("")) {
                    secondaryAddress.add(address.getString(LIAPIConstants.RESPONSE_ADDRESS_COUNTRY));
                }
            } catch (JSONException ex) {
                // Don't do anything
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


    public static LIAPIHelper getInstance() {
        if (null == instance) {
            synchronized (LIAPIHelper.class) {
                if (null == instance) {
                    instance = new LIAPIHelper();
                }
            }
        }
        return instance;
    }
}
