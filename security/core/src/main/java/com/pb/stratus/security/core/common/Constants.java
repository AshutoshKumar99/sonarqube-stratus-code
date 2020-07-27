package com.pb.stratus.security.core.common;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/25/12
 * Time: 1:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class Constants {

    // query params
    public static final String KEY_TENANT = "tenant";
    public static final String KEY_GAZETTEER = "loccfg";
    public static final String KEY_GAZETTEER_NAME = "gazetteername";
    public static final String KEY_QUERY = "q";
    public static final String KEY_REP = "rep";
    public static final String KEY_SRS = "srs";
    public static final String KEY_MAX_RESULT_COUNT = "count";

    // header
    public static final String KEY_AUTHORIZATION = "Authorization";
    public static final String HEADER_TENANTNAME = "TenantName";

    public static final String EMPTY_TENANT_NAME = "Either a null or nil string  was specified for Tenant " +
            "name. Tenant name is a required parameter. Specify a valid Tenant name.";
    public static final String INVALID_TENANT_NAME = "Could not find a Tenant with specified name.  Specify  a " +
            "valid Tenant name.";
    public static final String EMPTY_MAP_NAME = "Either a null or nil string  was specified for Map" +
            " name.  Specify a valid  Map name.";
    public static final String CLEAR_CACHE_SUCCESS = "Legend cache cleared.";
    public static final String CLEAR_CACHE_FAILURE = "Clear-Cache Operation Failed!";
    public static final String UPLOADER_VERSION_NOT_SUPPORTED = "Analyst supports Uploader version {0} or higher. Previous versions are not supported.";
}
