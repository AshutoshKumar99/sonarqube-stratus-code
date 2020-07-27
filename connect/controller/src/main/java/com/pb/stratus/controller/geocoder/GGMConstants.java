package com.pb.stratus.controller.geocoder;

/**
 * File to hold constants required for GGM Service and related work.
 */
public final class GGMConstants {

    //Constants for GGM XML elements;
    public final static String NAME = "name";
    public final static String URL = "url";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DATABASE = "database";
    public static final String COUNTRY = "country";
    public static final String FALLBACKTO_POSTAL = "fallbackToPostal";
    public static final String FALLBACKTO_GEOGRAPHIC = "fallbackToGeographic";
    public static final String MATCHMODE = "matchMode";

    public static final String REQUEST_TYPE = "POST";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String AUTHORIZATION = "Authorization";

    //Set of parameters which are going to be appended in GGM URL.
    public static final String PARAM_ADDRESS_LINE = "mainAddressLine";
    public static final String PARAM_ADDRESSES = "addresses";
    public static final String PARAM_FALLBACKTO_POSTAL = "fallbackToPostal";
    public static final String PARAM_FALLBACKTO_GEOGRAPHIC = "fallbackToGeographic";
    public static final String PARAM_MATCHMODE = "matchMode";
    public static final String PARAM_MAXCANDIDATES = "maxReturnedCandidates";
    public static final String PARAM_PREFERENCES = "preferences";
    public static final String PARAM_COUNTRY = "country";
    public static final String PARAM_MAXCANDIDATES_DEFAULT_VALUE = "5";
    public static final String DEFAULT_PROJECTION = "epsg:4326";

    //Set of Attributes that are to be picked form Response.
    //Basic attributes
    public static final String RESPONSE_CANDIDATES = "candidates";

    //For Address.
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

    //For Geometry
    public static final String RESPONSE_GEOMETRY = "geometry";
    public static final String RESPONSE_GEOMETRY_TYPE = "type";
    public static final String RESPONSE_GEOMETRY_COORDINATES_LONGITUDE = "coordinates"; //Longitude
    public static final String RESPONSE_GEOMETRY_COORDINATES_LATITUDE = "coordinates"; //Latitude
    public static final String RESPONSE_GEOMETRY_CRS = "crs";
    public static final String RESPONSE_GEOMETRY_CRS_PROPERTIES = "properties";
    public static final String RESPONSE_GEOMETRY_CRS_PROPERTIES_NAME = "name";//actual projection;

}