package com.pb.stratus.controller;

/**
 * Constants
 * User: gu003du
 * Date: 6/10/14
 * Time: 7:03 PM
 */
public class Constants {

    // Constants related to SQL based searches
    public static final String QUERY_COLUMN_NAME = "columnName";
    public static final String QUERY_COLUMN_TYPE = "columnType";
    public static final String QUERY_COLUMN_VAL = "value";
    public static final String QUERY_OPERATOR = "operator";
    public static final String CUSTOM_FILTER = "Custom Filter";

    // Constants related to Geometry and its parsing
    public static final String GEOMETRY_TYPE = "type";
    public static final String COORDINATES = "coordinates";
    public static final String GEOMETRY_COLLECTION = "GeometryCollection";
    public static final String GEOMETRIES = "geometries";

    public static final String RESPONSE_FORMAT_HEADER = "RESPONSE-FORMAT";
    public static final String ACCEPT_HEADER = "Accept";
    public static final String ACCEPT_HEADER_VALUE_IMAGE = "image";
    public static final String ACCEPT_HEADER_VALUE_XML = "application/xml";
    public static final String ACCEPT_HEADER_VALUE_JSON = "text/json-comment-filtered";

    // Constants for custom template code generation.
    public static final String TEMPLATE_NAME = "templateName";
    public static final String TABLE_REF = "tableRef";
    public static final String TEMPLATE_CONFIG = "templateConfig";
    public static final String TEMPLATE_ELEMENTS = "templateElements";
    public static final String FILE_EXT_HTML = ".html";
    public static final String FILE_EXT_TYPESCRIPT = ".ts";
    public static final String FILE_EXT_JSON = ".json";
    public static final String FILE_EXT_XML = ".xml";
    public static final String TEMPLATE_ALREADY_EXISTS_ERROR = "TemplateAlreadyExists";
    public static final String FAILED_TO_CREATE_TEMPLATE = "FailedToCreateTemplate";
    public static final String TEMPLATE_FILE_NOT_FOUND = "TemplateFileNotFound";
    public static final String TEMPLATE_PATH = "templatePath";
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String IS_DEFAULT = "isDefault";
    public static final String OVERRITE = "overrite";
    public static final String TABLE_TEMPLATE_MAPPING_FILENAME = "tableTemplateMappings";
    public static final int ERROR_UNPROCESSABLE_ENTITY = 422;

    public static final String NOT_AUTHORIZED = "You are not authorized, ensure you are an Administrator.";
    public static final String TABLE_TEMPLATE_MAPPING_NODE = "TableTemplateMappings";
    public static final String MAPPING = "Mapping";
    public static final String TABLE = "Table";
    public static final String TEMPLATE = "Template";
    public static final String CALLOUT_MAPPING_XML = "CalloutInfoTemplateDefinitions.xml";
    public static final String FAILED_TO_UPDATE_XML = "FailedToUpdateTemplateMappingXML";
    public static final String SUCCESS = "Success";
    public static final String YES = "Yes";
    public static final String XML_INDENT = "{http://xml.apache.org/xslt}indent-amount";

    public  static final String REQUEST_TYPE_POST = "POST";
    public  static final String REQUEST_TYPE_GET = "GET";
    public  static final String REQUEST_TYPE_PUT = "PUT";
    public  static final String REQUEST_TYPE_DELETE = "DELETE";

    public static String ADMIN = "admin";
    public static String VALID_ANALYST_ROLE_PREFIX="Analyst";

    public static String MAP_PROJECT_LIST_URI = "/mapProjectProxy/mapProjectList";

    public static String DATABIND_LIST_URI = "/databindList";
    public static final String MAP_CONFIG_TABLETEMPLATE_MAPPINGS = "MapConfigTableTemplateMappings";
    public static final String MAPCONFIG = "MapConfig";
    public static final String MAP_CONFIG_TABLETEMPLATE_MAPPING = "MapConfigTableTemplateMapping";
    public static final String MAPPINGS = "Mappings";
    public static final String MAPCONFIG_TABLE_TEMP_MAPPING_PARAM = "mapConfigMapping";
    public static final String FEATURE_EDIT_MAPPING_XML = "FeatureEditTemplateDefinitions.xml";
    public static final String FEATURE_EDIT_MAPPING_PARAM = "featureEditConfig";
    public static final String STRING_UNDEFINED = "undefined";

}
