package com.pb.stratus.core.configuration;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.common.application.SpringApplicationContextLocator;
import com.pb.stratus.core.exception.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import uk.co.graphdata.utilities.resource.ResourceResolver;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.pb.stratus.core.configuration.FileListModificationObserverFactory.ObserverType;

/**
 * This class implements a controller configuration backed by a Java properties
 * file. By default, the file is expected to reside on the classpath. Its name
 * is <code>controller.properties</code>.
 */
public class PropertiesFileControllerConfiguration implements
        ControllerConfiguration {

    public static final String SINGLE_LINE_SEARCH_SERVICE_URL = "indexsearch.singleLineSearch.wsdl";
    public static final String ACCESS_AUTHENTICATED_ENABLED = "access.authenticated.enabled";
    public static final String ACCESS_PUBLIC_ENABLED = "access.public.enabled";
    public static final String LEGEND_CACHE_TIMEOUT = "legendImageCacheTimeout";
    public static final String MAX_NUM_FEATURES = "maxNumberOfFeatures";
    public static final String ADMINCONSOLE_URL = "adminconsole.externalUrl";
    public static final String TENANT_THEME_URL = "tenant.theme.url";
    public static final String TILE_SERVICE_URL = "spatialserver.tileserver.url";
    public static final String SPATIAL_SERVICE_BASE_URL = "spatialserver.rest.baseurl";
    public static final String PROJECT_SERVICE_URL = "spatialserver.rest.project.url";
    public static final String PROJECT_SERVICE_API_KEY = "application.id";
    public static final String SSA_MAP_PROJECT_BASE_URL = "ssa.mapproject.rest.baseurl";
    public static final String LOCATOR_IMAGE_PATH = "locatorImagePath";
    public static final String CALLOUT_INFO_IMAGE_PATH = "callOutInfoImagePath";
    public static final String MAPPING_SERVICE_WSDL_FOR_BASE_MAPS_URL = "spatialserver.mapping-tiles.wsdl";
    public static final String OAUTH2_CLIENT_ID = "auth.oauth2.clientId";
    public static final String OAUTH2_CLIENT_SECRET = "auth.oauth2.clientSecret";
    public static final String OAUTH2_AUTHORIZATION_SERVER_URL = "auth.oauth2.authorizationServer.tokenEndpoint";
    public static final String BING_API_PRIVATE_KEY = "bingAPIPrivateKey";
    public static final String BING_API_PUBLIC_KEY = "bingAPIPublicKey";
    public static final String APPLICATION_LINKING_ENABLED = "applicationLinkingEnabled";
    public static final String APPLICATION_LINKING_HOST_IP = "applicationLinkingHostIP";
    public static final String APPLICATION_LINKING_HOST_PORT = "applicationLinkingHostPort";
    public static final String EXTENSIBILITY_ENABLED = "extensibilityEnabled";
    public static final String SHORT_CIRCUIT_APPLICATION_LINKING_REGISTRATION = "shortCircuitApplicationLinkingRegistration";
    public static final String USAGE_DATA_SOURCE = "usage.data.source";
    public static final String USAGE_DATA_SOURCE_FILE = "usage.data.source.file";
    public static final String DB_URL = "database.url";
    public static final String DB_USERNAME = "database.username";
    public static final String DB_PASSWORD = "database.password";
    public static final String DB_DRIVER_CLASS_NAME = "database.driver.class.name";
    public static final String DB_POOL_SIZE = "database.pool.size";
    public static final String DB_BUFFER_SIZE = "database.buffer.size";
    public static final String MAX_NUM_PRINTABLE_TILES = "maxNumberOfPrintableTiles";
    static final String TOKEN_TENANT_NAME = "@{tenant}"; // CONN-12991
    static final String TOKEN_TENANT_NAME_REPLACE = "\\@\\{tenant\\}"; // CONN-12991
    private static final Logger logger = Logger
            .getLogger(PropertiesFileControllerConfiguration.class.getName());
    private static final String CONNECT_CONFIG_FILE = "controller.properties";
    private static final String SHARED_CONFIG_FILE = "shared.properties";
    private static final String LEGEND_CACHE_ENABLED = "legend.cache.enabled";
    private static final String CAPTURE_USAGE_DATA = "capture.usage.data";
    private static final int DEFAULT_MAX_NUM_PRINTABLE_TILES = 300;

    private static final String BOTH = "both";
    private static final String SECURED = "secured";
    private static final String PUBLIC = "public";

    private static final String NONE = "none";
    private static final String DB = "mysql";
    private static final String csv = "csv";
    private static final String SPATIAL_SERVER_PLACEHOLDER = "@spectrum_server";
    private static final String PATH_GET_INFO = "/controller/authentication/getinfo";
    private static final String PATH_SECURITY_CHECK = "/security-check";
    private static final String PATH_GET_INFO_ENCODED = "controller%2Fauthentication%2Fgetinfo%3FnoCache";
    private static final String MOBILE = "mobile";

    private static final int DEFAULT_CACHE_TIMEOUT = 1;

    private ResourceResolver tenantResourceResolver;

    private ResourceResolver globalResourceResolver;

    private Properties connectProps;

    private Properties sharedProps;

    private boolean loaded = false;

    private URL tileServiceUrl;

    private URL spatialBaseUrl;

    private URL projectServiceUrl;

    private String projectServiceAPIKey;

    private URL ssaMapProjectUrl;

    private URL mappingServiceUrl;

    private URL featureServiceUrl;

    private URL adminConsoleUrl;

    private URL tenantThemeUrl;

    private URL singleLineSearchServiceUrl;

    private int legendCacheTimeout;

    private int maxFeatures;

    private String locatorImagePath;

    private String callOutInfoImagePath;

    private URL mappingServiceWsdlForBaseMapsUrl;

    private String bingAPIPrivateKey;

    private String bingAPIPublicKey;

    private String ssoStartUrl;

    private String sloStartUrl;

    private String tenantName;

    private String anonymousUsername;

    private String anonymousPassword;

    private String oauth2ClientId;

    private String oauth2ClientSecret;

    private String usageDataSource;

    private String dbPoolSize;

    private String dbBufferSize;

    private String dbUrl;

    private String dbUsername;

    private String dbPassword;

    private String dbDriverClassName;

    private String usageDataSourceFile;

    private URL oauth2AuthorizationServerUrl;

    private int maximumNumberOfPrintableTiles;

    private String authType;

    private String legendCacheEnabled;

    private String captureUsageData;

    private String applicationLinkingEnabled;

    private String extensibilityEnabled;

    private String shortCircuitApplicationLinkingRegistration;

    private String applicationLinkingHostPort;

    private String applicationLinkingHostIP;

    private Map<String, PropertyEvaluator> propertyEvaluators = new HashMap<String, PropertyEvaluator>();
    private PropertyEvaluator defaultPropertyEvaluator = new SimplePropertyEvaluator();

    private FileListModificationObserverFactory fileListModificationObserverFactory;

    public PropertiesFileControllerConfiguration(
            ResourceResolver tenantResourceResolver,
            ResourceResolver globalResourceResolver,
            String tenantName,
            FileListModificationObserverFactory fileListModificationObserverFactory) {

        this.tenantResourceResolver = tenantResourceResolver;
        this.globalResourceResolver = globalResourceResolver;
        this.tenantName = tenantName;
        this.fileListModificationObserverFactory = fileListModificationObserverFactory;
    }

    public void addPropertyEvaluator(PropertyEvaluator propertyEvaluator,
                                     boolean isDefault) {
        propertyEvaluators.put(propertyEvaluator.getType(), propertyEvaluator);

        if (isDefault) {
            defaultPropertyEvaluator = propertyEvaluator;
        }
    }

    public URL getAdminConsoleUrl() {
        checkLoaded();
        return adminConsoleUrl;
    }

    public URL getTenantThemeUrl() {
        checkLoaded();
        return tenantThemeUrl;
    }

    public int getMaximumNumberOfPrintableTiles() {
        checkLoaded();
        return this.maximumNumberOfPrintableTiles;
    }

    public URL getSingleLineSearchServiceWsdlUrl() {
        checkLoaded();
        return singleLineSearchServiceUrl;
    }

    public int getLegendCacheTimeoutInMinutes() {
        checkLoaded();
        return legendCacheTimeout;
    }

    public int getMaxFeatureSearchResults() {
        checkLoaded();
        return maxFeatures;
    }

    public boolean isLegendCacheEnabled() {
        checkLoaded();
        boolean legendCacheEnabled = false;
        if ("true".equalsIgnoreCase(this.legendCacheEnabled)) {
            legendCacheEnabled = true;
        }
        return legendCacheEnabled;
    }


    public boolean isCaptureUsageData() {
        checkLoaded();

        this.captureUsageData = getSharedProperty(CAPTURE_USAGE_DATA, false);

        boolean captureUsageData = false;

        if ("true".equalsIgnoreCase(this.captureUsageData)) {
            captureUsageData = true;
        }
        return captureUsageData;
    }

    public String getApplicationLinkingEnabled() {
        checkLoaded();
        return this.applicationLinkingEnabled;
    }

    public String getExtensibilityEnabled() {
        checkLoaded();
        return this.extensibilityEnabled;
    }

    @Override
    public boolean isApplicationLinkingRegistrationShortCircuited() {
        checkLoaded();
        boolean shortCircuited = false;
        if ("true".equalsIgnoreCase(this.shortCircuitApplicationLinkingRegistration)) {
            shortCircuited = true;
        }

        return shortCircuited;
    }

    @Override
    public String getApplicationLinkingHostIP() {
        checkLoaded();
        return this.applicationLinkingHostIP;
    }

    @Override
    public String getApplicationLinkingHostPort() {
        checkLoaded();
        return this.applicationLinkingHostPort;
    }

    public URL getTileServiceUrl() {
        checkLoaded();
        return tileServiceUrl;
    }

    @Override
    public URL getSpatialServiceBaseUrl() {
        checkLoaded();
        return spatialBaseUrl;
    }

    @Override
    public URL getProjectServiceUrl() {
        checkLoaded();
        return projectServiceUrl;
    }

    public String getLocatorImageForPrint() {
        checkLoaded();
        return this.locatorImagePath;
    }

    public String getCallOutInfoImageForPrint() {
        checkLoaded();
        return this.callOutInfoImagePath;
    }

    public String getProjectServiceApiKey() {
        checkLoaded();
        return this.projectServiceAPIKey;
    }


    @Override
    public URL getSSAMapProjectBaseUrl() {
        checkLoaded();
        return ssaMapProjectUrl;
    }

    public URL getMappingServiceWsdlForBaseMapsUrl() {
        checkLoaded();
        return mappingServiceWsdlForBaseMapsUrl;
    }

    private void checkLoaded() {
        if (!loaded) {
            throw new IllegalStateException("Properties not loaded yet");
        }
    }

    private void reload(Properties props, ResourceResolver resourceResolver,
                        String configFile) {
        // load Connect-specific properties
        InputStream propsInputStream = null;
        try {
            propsInputStream = resourceResolver.getResourceAsStream(configFile);
            if (propsInputStream == null) {
                throw new ConfigurationException("Configuration file '"
                        + configFile + "' not found");
            }

            props.load(propsInputStream);
        } catch (IOException iox) {
            throw new ConfigurationException(
                    "Failed to load existing config file '" + configFile + "'",
                    iox);
        } finally {
            if (propsInputStream != null) {
                try {
                    propsInputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

    }

    public void reload() {
        connectProps = new Properties();
        sharedProps = new Properties();

        // load global Connect-specific properties
        reload(connectProps, globalResourceResolver, CONNECT_CONFIG_FILE);

        // load tenant-specific properties
        reload(connectProps, tenantResourceResolver, CONNECT_CONFIG_FILE);

        // load shared properties
        reload(sharedProps, globalResourceResolver, SHARED_CONFIG_FILE);

        reload(sharedProps, tenantResourceResolver, SHARED_CONFIG_FILE);

        loadProperties();
        loaded = true;
    }

    private void loadProperties() {
        this.tileServiceUrl = getSharedUrlProperty(TILE_SERVICE_URL, true);
        this.spatialBaseUrl = getSharedUrlProperty(SPATIAL_SERVICE_BASE_URL, true);
        this.projectServiceUrl = getSharedUrlProperty(PROJECT_SERVICE_URL, true);
        this.adminConsoleUrl = getSharedUrlProperty(ADMINCONSOLE_URL, false);
        this.tenantThemeUrl = getSharedUrlProperty(TENANT_THEME_URL, false);

        this.singleLineSearchServiceUrl = getSharedUrlProperty(
                SINGLE_LINE_SEARCH_SERVICE_URL, false);

        this.legendCacheEnabled = getSharedProperty(LEGEND_CACHE_ENABLED, false);
        this.captureUsageData = getSharedProperty(CAPTURE_USAGE_DATA, false);
        this.legendCacheTimeout = getLegendCacheTimeoutProperty();
        this.maxFeatures = getPositiveIntegerProperty(MAX_NUM_FEATURES, true);
        this.locatorImagePath = getProperty(LOCATOR_IMAGE_PATH, true);
        this.callOutInfoImagePath = getProperty(CALLOUT_INFO_IMAGE_PATH, true);
        this.mappingServiceWsdlForBaseMapsUrl = getSharedUrlProperty(
                MAPPING_SERVICE_WSDL_FOR_BASE_MAPS_URL, true);
        this.bingAPIPrivateKey = getProperty(BING_API_PRIVATE_KEY, true);
        this.bingAPIPublicKey = getProperty(BING_API_PUBLIC_KEY, true);
        this.applicationLinkingEnabled = getProperty(APPLICATION_LINKING_ENABLED, false);
        this.extensibilityEnabled = getSharedProperty(EXTENSIBILITY_ENABLED, false);
        this.projectServiceAPIKey = getSharedProperty(PROJECT_SERVICE_API_KEY, true);

        this.shortCircuitApplicationLinkingRegistration = getProperty(SHORT_CIRCUIT_APPLICATION_LINKING_REGISTRATION, false);
        this.applicationLinkingHostIP = getProperty(APPLICATION_LINKING_HOST_IP, false);
        this.applicationLinkingHostPort = getProperty(APPLICATION_LINKING_HOST_PORT, false);
        this.ssoStartUrl = getProperty(Constants.SSO_START_URL, true);
        this.sloStartUrl = getProperty(Constants.SLO_START_URL, true);
        this.anonymousUsername = getSharedProperty(Constants.ANONYMOUS_USER_NAME,
                true);
        this.anonymousPassword = getSharedProperty(Constants.ANONYMOUS_PASSWORD,
                true);

        this.authType = getAuthTypeProperty();
        this.oauth2ClientId = getSharedProperty(OAUTH2_CLIENT_ID, true);
        this.oauth2ClientSecret = getSharedProperty(OAUTH2_CLIENT_SECRET, true);
        this.usageDataSource = getSharedProperty(USAGE_DATA_SOURCE, false);
        this.ssaMapProjectUrl = getSharedUrlProperty(SSA_MAP_PROJECT_BASE_URL, true);

        if (DB.equalsIgnoreCase(this.usageDataSource)) {
            this.dbDriverClassName = getSharedProperty(DB_DRIVER_CLASS_NAME, false);
            this.dbUrl = getSharedProperty(DB_URL, false);
            this.dbUsername = getSharedProperty(DB_USERNAME, false);
            this.dbPassword = getSharedProperty(DB_PASSWORD, false);
            this.dbPoolSize = getSharedProperty(DB_POOL_SIZE, false);
            this.dbBufferSize = getSharedProperty(DB_BUFFER_SIZE, false);
        }
        if (csv.equalsIgnoreCase(this.usageDataSource))
            this.usageDataSourceFile = getSharedProperty(USAGE_DATA_SOURCE_FILE, false);

        this.oauth2AuthorizationServerUrl = getSharedUrlProperty(
                OAUTH2_AUTHORIZATION_SERVER_URL, true);
        String maxTiles = getProperty(MAX_NUM_PRINTABLE_TILES, false);
        if (StringUtils.isBlank(maxTiles)) {
            maximumNumberOfPrintableTiles = DEFAULT_MAX_NUM_PRINTABLE_TILES;
        } else {
            try {
                maximumNumberOfPrintableTiles = Integer.parseInt(maxTiles);
            } catch (NumberFormatException nfx) {
                throw new ConfigurationException("The property " +
                        "'maximumNumberOfPrintableTiles' is not an integer");
            }
        }
    }

    private String getAuthTypeProperty() {
        Boolean publicAccess = getSharedBooleanProperty(ACCESS_PUBLIC_ENABLED,
                true);
        Boolean authAccess = getSharedBooleanProperty(
                ACCESS_AUTHENTICATED_ENABLED, true);

        if (publicAccess) {
            if (authAccess)
                return BOTH;
            else
                return PUBLIC;
        } else {
            if (authAccess)
                return SECURED;
            else
                throw new ConfigurationException("'" + ACCESS_PUBLIC_ENABLED
                        + "' and '" + ACCESS_AUTHENTICATED_ENABLED
                        + "' are both false or undefined");
        }
    }

    private URL getUrlProperty(String propertyName, boolean required) {
        String url = getProperty(propertyName, required);
        if (url == null) {
            return null;
        }
        try {
            return new URL(url);
        } catch (MalformedURLException mux) {
            throw new ConfigurationException("The value '" + url
                    + "' of the configuration property '" + propertyName
                    + "' is not a valid URL");
        }
    }

    private URL getSharedUrlProperty(String propertyName, boolean required) {
        String url = getSharedProperty(propertyName, required);
        if (url == null) {
            return null;
        }
        try {
            URL property = null;
            if (url.startsWith("classpath")) {
                Resource resource = SpringApplicationContextLocator
                        .getApplicationContext().getResource(url);
                if (resource != null) {
                    property = resource.getURL();
                } else {
                    logger.warn("Failed to load configuration property " + propertyName);
                }
            } else {
                property = new URL(url);
            }

            return property;
        } catch (MalformedURLException mux) {
            throw new ConfigurationException("The value '" + url
                    + "' of the configuration property '" + propertyName
                    + "' is not a valid URL");
        } catch (IOException e) {
            throw new ConfigurationException("The value '" + url
                    + "' of the configuration property '" + propertyName
                    + "' is not a valid URL");
        }
    }

    private String getProperty(String propertyName, boolean required) {
        String value = connectProps.getProperty(propertyName);
        if (StringUtils.isBlank(value)) {
            if (required) {
                throw new ConfigurationException("The configuration property '"
                        + propertyName + "' could not be found");

            } else {
                return null;
            }
        }
        value = replaceParameterValues(value);
        return value.trim();
    }

    private String evaluateProperty(String value) {
        if (value == null) {
            return null;
        }

        int hashAnchorIndex = value.indexOf('#');
        int openParenAnchorIndex = value.indexOf('(');
        int closeParenAnchorIndex = value.lastIndexOf(')');

        if (hashAnchorIndex == 0 && openParenAnchorIndex > 0
                && openParenAnchorIndex < closeParenAnchorIndex) {
            String propertyEvaluatorType = value.substring(1,
                    openParenAnchorIndex);
            String expression = value.substring(openParenAnchorIndex + 1,
                    closeParenAnchorIndex);

            PropertyEvaluator evaluator = propertyEvaluators
                    .get(propertyEvaluatorType);

            if (evaluator == null) {
                throw new ConfigurationException("Property evaluator type \""
                        + propertyEvaluatorType + "\" is not supported.");
            }

            return evaluator.evaluate(expression);
        }

        return defaultPropertyEvaluator.evaluate(value);
    }

    private String getSharedProperty(String propertyName, boolean required) {
        String value = sharedProps.getProperty(propertyName);
        if (StringUtils.isBlank(value)) {
            if (required) {
                throw new ConfigurationException("The configuration property '"
                        + propertyName + "' could not be found");

            } else {
                return null;
            }
        }
        value = replaceParameterValues(value);
        return value.trim();
    }

    private String replaceParameterValues(String url) {
        String newUrl = url;
        if (url.contains(TOKEN_TENANT_NAME)) {
            newUrl = url.replaceAll(TOKEN_TENANT_NAME_REPLACE, tenantName);
        }
        return newUrl;
    }

    private int getLegendCacheTimeoutProperty() {
        String strValue = getProperty(LEGEND_CACHE_TIMEOUT, false);
        if (strValue == null) {
            return DEFAULT_CACHE_TIMEOUT;
        }
        try {
            return Integer.parseInt(strValue);
        } catch (NumberFormatException nfx) {
            throw new ConfigurationException("The property '"
                    + LEGEND_CACHE_TIMEOUT + "' is not an integer value");
        }
    }

    private boolean getSharedBooleanProperty(String propertyName,
                                             boolean required) {
        String strValue = getSharedProperty(propertyName, required);
        if (strValue == null) {
            return false;
        }
        return Boolean.parseBoolean(strValue);
    }

    private int getPositiveIntegerProperty(String propertyName, boolean required) {
        String strValue = getProperty(propertyName, required);
        if (strValue == null) {
            return -1;
        }
        int value;
        try {
            value = Integer.parseInt(strValue);
        } catch (NumberFormatException nfx) {
            throw new ConfigurationException("The property '" + propertyName
                    + "' is not an integer value");
        }
        if (value <= 0) {
            throw new ConfigurationException("The property '" + propertyName
                    + "' must be a positive number");
        }
        return value;
    }

    public String getBingServicesPrivateApiKey() {
        checkLoaded();
        return bingAPIPrivateKey;
    }


    public String getBingServicesPublicApiKey() {
        checkLoaded();
        return bingAPIPublicKey;
    }

    public String getSsoStartUrl() {
        checkLoaded();
        // In case of SSO just do some additional processing
        String ssoUrl = evaluateProperty(ssoStartUrl);
        if (ssoUrl.contains(SPATIAL_SERVER_PLACEHOLDER)) {
            ssoUrl = ssoUrl.replaceAll(SPATIAL_SERVER_PLACEHOLDER, getSpatialServiceBaseUrl().toString());
            String replace1 = ssoUrl.substring(ssoUrl.indexOf(PATH_GET_INFO), ssoUrl.indexOf(PATH_SECURITY_CHECK));
            ssoUrl = ssoUrl.replace(replace1, "/" + MOBILE);
            int index = ssoUrl.indexOf(PATH_GET_INFO_ENCODED);
            ssoUrl = ssoUrl.substring(0, index) + MOBILE + "/";
        }
        return ssoUrl;
    }

    public String getSloStartUrl() {
        checkLoaded();
        return evaluateProperty(sloStartUrl);
    }

    @Override
    public String getAnonymousUserName() {
        checkLoaded();
        return this.anonymousUsername;
    }

    @Override
    public String getAnonymousPassword() {
        checkLoaded();
        return this.anonymousPassword;
    }

    @Override
    public String getAuthType() {
        checkLoaded();
        boolean isModified = hasPropertyFileBeenModified(SHARED_CONFIG_FILE,
                ObserverType.TENANT_LOCAL);
        if (isModified) {
            reload(sharedProps, tenantResourceResolver, SHARED_CONFIG_FILE);
            logger.debug("We have reloaded the access mode propertes of file : "
                    + SHARED_CONFIG_FILE);
            this.authType = getAuthTypeProperty();
        }
        return this.authType;
    }

    @Override
    public String getOauth2ClientId() {
        checkLoaded();
        return this.oauth2ClientId;
    }

    @Override
    public String getOauth2ClientSecret() {
        checkLoaded();
        return this.oauth2ClientSecret;
    }

    @Override
    public URL getOauth2AuthorizationServerUrl() {
        checkLoaded();
        return this.oauth2AuthorizationServerUrl;
    }

    @Override
    public String getUsageDataSource() {
        checkLoaded();
        return this.usageDataSource == null ? NONE : this.usageDataSource;
    }

    @Override
    public String getUsageDataSourceFile() {
        checkLoaded();
        return this.usageDataSourceFile == null ? NONE : this.usageDataSourceFile;
    }

    /**
     * default my sql pool size is 10
     *
     * @return
     */
    public String getPoolSize() {
        checkLoaded();
        if (DB.equalsIgnoreCase(this.usageDataSource)) {
            try {
                Integer.parseInt(this.dbPoolSize);
            } catch (NumberFormatException ex) {
                logger.debug("db pool size not an integer");
                this.dbPoolSize = "10";
            }
        }
        return this.dbPoolSize;
    }

    /**
     * default buffer size is 64
     *
     * @return
     */
    public String getDBBufferSize() {
        checkLoaded();
        if (DB.equalsIgnoreCase(this.usageDataSource)) {
            try {
                Integer.parseInt(this.dbBufferSize);
            } catch (NumberFormatException ex) {
                logger.debug("db buffer size not an integer");
                this.dbBufferSize = "128";
            }
        }
        return this.dbBufferSize;
    }

    @Override
    public String getDBUserName() {
        checkLoaded();
        return this.dbUsername;
    }

    @Override
    public String getDBUrl() {
        checkLoaded();
        return this.dbUrl;
    }

    @Override
    public String getDBPassword() {
        checkLoaded();
        return this.dbPassword;
    }

    @Override
    public String getDBDriverClassName() {
        checkLoaded();
        return this.dbDriverClassName;
    }

    /**
     * This method will check whether the given file is modified or not.
     *
     * @param fileName
     * @param observerType
     * @return isModified
     */
    private boolean hasPropertyFileBeenModified(String fileName,
                                                ObserverType observerType) {
        FileModificationObserver fileModificationObserver = fileListModificationObserverFactory
                .getFileModificationObserver(observerType);
        boolean isModified;
        try {
            isModified = fileModificationObserver.isModified(fileName);
            logger.debug("The access mode propertes in file : " + fileName
                    + " was modified by the user");
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            isModified = false;
        }
        return isModified;
    }

}
