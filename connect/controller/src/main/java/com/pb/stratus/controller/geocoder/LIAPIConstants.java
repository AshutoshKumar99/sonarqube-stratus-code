package com.pb.stratus.controller.geocoder;

/**
 * @author ekiras
 */
public final class LIAPIConstants {

    //Constants for LIAPI XML elements;
    public final static String NAME = "name";
    public final static String URL = "url";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DATABASE = "database";
    public static final String COUNTRY = "country";
    public static final String FALLBACKTO_POSTAL = "fallbackToPostal";
    public static final String FALLBACKTO_GEOGRAPHIC = "fallbackToGeographic";
    public static final String MATCHMODE = "matchMode";


    // TOKEN params
    public static final String TOKEN_ATTRIBUTE = "AnalystOAuthToken";


    // Request Payload params
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_TYPE_VALUE = "ADDRESS";
    public static final String PARAM_PREFERENCES = "preferences";
    public static final String PARAM_ADDRESSESS = "addresses";
    public static final String PARAM_ADDRESS_LINE = "mainAddressLine";
    public static final String PARAM_COUNTRY = "country";
    public static final String PARAM_MAXCANDIDATES = "maxReturnedCandidates";

    public static final String RESPONSE = "responses";
    public static final String RESPONSE_CANDIDATES = "candidates";

    // Response Keys
    public static final String RESPONSE_ADDRESS = "address";

    public static final String RESPONSE_ADDRESS_MAINLINE = "mainAddressLine";
    public static final String RESPONSE_ADDRESS_LASTLINE = "addressLastLine";
    public static final String RESPONSE_ADDRESS_PLACENAME = "placeName";
    public static final String RESPONSE_ADDRESS_AREANAME_1 = "areaName1";
    public static final String RESPONSE_ADDRESS_AREANAME_2 = "areaName2";
    public static final String RESPONSE_ADDRESS_AREANAME_3 = "areaName3";
    public static final String RESPONSE_ADDRESS_AREANAME_4 = "areaName4";
    public static final String RESPONSE_ADDRESS_POSTCODE_1 = "postCode1";
    public static final String RESPONSE_ADDRESS_POSTCODE_2 = "postCode2";
    public static final String RESPONSE_ADDRESS_COUNTRY = "country";
    public static final String RESPONSE_ADDRESS_ADDRESSNUMBER = "addressNumber";
    public static final String RESPONSE_ADDRESS_STREETNAME = "streetName";

    public static final String RESPONSE_GEOMETRY = "geometry";
    public static final String RESPONSE_GEOMETRY_COORDINATES = "coordinates";
    public static final String RESPONSE_GEOMETRY_CRS = "crs";
    public static final String RESPONSE_GEOMETRY_CRS_PROPERTIES = "properties";
    public static final String RESPONSE_GEOMETRY_CRS_PROPERTIES_NAME = "name";//actual projection;



}
