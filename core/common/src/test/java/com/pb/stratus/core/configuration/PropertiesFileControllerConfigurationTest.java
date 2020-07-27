package com.pb.stratus.core.configuration;

import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.configuration.FileListModificationObserverFactory.ObserverType;
import com.pb.stratus.core.exception.ConfigurationException;
import com.pb.stratus.core.util.MockSupport;
import org.junit.Before;
import org.junit.Test;
import uk.co.graphdata.utilities.resource.ResourceResolver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import static com.pb.stratus.core.configuration.PropertiesFileControllerConfiguration.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PropertiesFileControllerConfigurationTest {

    private MockSupport mockSupport;

    private URL expectedAdminconsoleUrl;
    private URL expectedGlobalAdminconsoleUrl;

    private URL expectedTenantThemeUrl;
    private URL expectedGlobalTenantThemeUrl;

    private URL replaceTenantThemeUrl;

    private URL expectedSingleLineServiceUrl;
    private URL expectedGlobalSingleLineServiceUrl;

    private int legendCacheTimeout;
    private int globalLegendCacheTimeout;

    private int maxFeatures;
    private int globalMaxFeatures;

    private String expectedLocatorImagePath;
    private String expectedCallOutInfoImagePath;
    private String expectedGlobalLocatorImagePath;

    private String expectedUsageDataSource;
    private String expectedUsageDataSourceFile;

    private String expectedDBUrl;
    private String expectedDBUsername;
    private String expectedDBPassword;
    private String expectedDBDriverClassName;
    private String expectedDBPoolSize;

    private URL expectedMappingServiceForBaseMapsUrl;
    private URL expectedGlobalMappingServiceForBaseMapsUrl;

    private String expectedBingApiPrivateKey;
    private String expectedGlobalBingApiPrivateKey;

    private String expectedBingApiPublicKey;
    private String expectedGlobalBingApiPublicKey;

    private String expectedSsoStartUrl;
    private String expectedGlobalSsoStartUrl;

    private String expectedSloStartUrl;
    private String expectedGlobalSloStartUrl;

    private String tenantName = "stratus";

    private String expectedAnonymousUserName;

    private String expectedAnonymousPassword;

    private String expectedAuthType;
    private String expectedGlobalAuthType;

    private String expectedOauthClientId;
    private String expectedGlobalOauthClientId;

    private String expectedOauthClientSecret;
    private String expectedGlobalOauthClientSecret;

    private String expectedAauthAuthserverEndpoint;
    private String expectedGlobalAauthAuthserverEndpoint;

    private URL expectedTileServiceUrl;
    private URL expectedGlobalTileServiceUrl;
    private URL expectedSpatialServiceBaseUrl;
    private URL expectedGlobalSpatialServiceBaseUrl;
    private URL expectedSSAMapProjectBaseUrl;

    private boolean expectedPublicAccess;
    private boolean expectedGlobalPublicAccess;

    private boolean expectedAuthAccess;
    private boolean expectedGlobalAuthAccess;

    private URL expectedProjectServiceUrl;
    private String expectedProjectServiceApiKey;

    private static final String CAPTURE_USAGE_DATA = "capture.usage.data";

    public static final String USAGE_DATA_SOURCE = "usage.data.source";
    public static final String USAGE_DATA_SOURCE_FILE = "usage.data.source.file";
    public static final String DB_URL="database.url";
    public static final String DB_USERNAME="database.username";
    public static final String DB_PASSWORD="database.password";
    public static final String DB_DRIVER_CLASS_NAME="database.driver.class.name";
    public static final String DB_POOL_SIZE ="database.pool.size";

    Properties props;
    Properties globalProps;

    Properties sharedProps;
    Properties globalSharedProps;

    @Before
    public void setUp() throws Exception {

        expectedAdminconsoleUrl = new URL("http://admin/console/url");
        expectedGlobalAdminconsoleUrl = new URL(
                "http://globaladmin/console/url");

        expectedTenantThemeUrl = new URL("http://tenant/theme/url");
        expectedGlobalTenantThemeUrl = new URL("http://globaltenant/theme/url");

        replaceTenantThemeUrl = new URL(
                "http://localhost/connect/@{tenant}/theme");

        expectedSingleLineServiceUrl = new URL("http://singleline/console/url");
        expectedGlobalSingleLineServiceUrl = new URL(
                "http://singleline/console/url");

        expectedUsageDataSource= "mysql";
        expectedUsageDataSourceFile="none";
        expectedDBUrl="http://mysql:3361";
        expectedDBUsername="admin";
        expectedDBPassword="password";
        expectedDBDriverClassName="com.java.mysql";
        expectedDBPoolSize="15";


        legendCacheTimeout = 123;
        globalLegendCacheTimeout = 102;

        maxFeatures = 456;
        globalMaxFeatures = 654;

        expectedLocatorImagePath = "images/home.png";
        expectedCallOutInfoImagePath = "images/black-information.png";
        expectedGlobalLocatorImagePath = "images/globalhome.png";
        expectedMappingServiceForBaseMapsUrl = new URL(
                "http://mappingFromTile/service/url");
        expectedGlobalMappingServiceForBaseMapsUrl = new URL(
                "http://mappingFromTile/globalservice/url");

        expectedBingApiPrivateKey = "BingAPIPrivateKeyForUseInStratus";
        expectedGlobalBingApiPrivateKey = "GlobalBingAPIPrivateKeyForUseInStratus";

        expectedBingApiPublicKey = "BingAPIPublicKeyForUseInStratus";
        expectedGlobalBingApiPublicKey = "GlobalBingAPIPublicKeyForUseInStratus";


        expectedSsoStartUrl = "http://devsso.g1.com:9029/sp/startSSO.ping?PartnerIdpId=pbbi-dev:default:entityId&action=sso&TargetResource=http%3A%2F%2Fyo003ba-lan%3A8090%2Fconnect/login%2F%3Ftenant%3Dstratus";
        expectedGlobalSsoStartUrl = "http://globaldevsso.g1.com:9029/sp/startSSO.ping?PartnerIdpId=pbbi-dev:default:entityId&action=sso&TargetResource=http%3A%2F%2Fyo003ba-lan%3A8090%2Fconnect/login%2F%3Ftenant%3Dstratus";
        expectedSloStartUrl = "http://devsso.g1.com:9029/sp/startSLO.ping?LogoutService=http%3A%2F%2Fyo003ba-lan%3A8090%2Fconnect%2Fslo%3Ftenant%3Dstratus&TargetResource=http%3A%2F%2Fyo003ba-lan%3A8090%2Fconnect%2F%3Ftenant%3Dstratus&InErrorResource=http%3A%2F%2Fyo003ba-lan%3A8090%2Fconnect%2Finerror%3Ftenant%3Dstratus";
        expectedGlobalSloStartUrl = "http://globaldevsso.g1.com:9029/sp/startSLO.ping?LogoutService=http%3A%2F%2Fyo003ba-lan%3A8090%2Fconnect%2Fslo%3Ftenant%3Dstratus&TargetResource=http%3A%2F%2Fyo003ba-lan%3A8090%2Fconnect%2F%3Ftenant%3Dstratus&InErrorResource=http%3A%2F%2Fyo003ba-lan%3A8090%2Fconnect%2Finerror%3Ftenant%3Dstratus";
        expectedAnonymousUserName = "customerstratustenant2_troy guest";
        expectedAnonymousPassword = "guest";
        expectedAuthType = "public";
        expectedGlobalAuthType = "both";

        expectedPublicAccess = true;
        expectedGlobalPublicAccess = true;

        expectedAuthAccess = false;
        expectedGlobalAuthAccess = true;

        expectedOauthClientId = "stratusClientId";
        expectedGlobalOauthClientId = "globalStratusClientId";

        expectedOauthClientSecret = "stratusClientPassword";
        expectedGlobalOauthClientSecret = "globalStratusClientPassword";

        expectedAauthAuthserverEndpoint = "http://authserver/oauth/endpoint";
        expectedGlobalAauthAuthserverEndpoint = "http://globalauthserver/oauth/endpoint";

        expectedTileServiceUrl = new URL("http://tile/service/url");
        expectedGlobalTileServiceUrl = new URL("http://globaltile/service/url");
        expectedSpatialServiceBaseUrl = new URL("http://host:8080");
        expectedGlobalSpatialServiceBaseUrl = new URL("http://domain");
        expectedSSAMapProjectBaseUrl = new URL("http://localhost:8080");

        expectedProjectServiceUrl = new URL("http://localhost:8080/rest/Spatial/ProjectService");
        expectedProjectServiceApiKey = "testApiKey";
        mockSupport = new MockSupport();
    }

    @Test
    public void testLoad() throws Exception {
        createAllProps();
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertPropertiesLoaded(conf);
    }

    @Test
    public void testCaptureUsageDataDefaultValue() throws Exception {
        createAllProps();
        removeSharedProp(CAPTURE_USAGE_DATA);
        ControllerConfiguration conf = createConfig();
        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals(false, conf.isCaptureUsageData());
    }

    @Test
    public void testCaptureUsageDataCaseInsensitive() throws Exception {
        createAllProps();
        globalSharedProps.setProperty(CAPTURE_USAGE_DATA, "TruE");
        ControllerConfiguration conf = createConfig();
        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals(true, conf.isCaptureUsageData());
    }

    @Test
    public void testAdminConsoleUrlOptional() throws Exception {
        createAllProps();
        removeSharedProp(ADMINCONSOLE_URL);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertNull(conf.getAdminConsoleUrl());
    }

    @Test
    public void testUsageDataSourceoptional() throws Exception{

        createAllProps();
        removeSharedProp(USAGE_DATA_SOURCE);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals("none", conf.getUsageDataSource());
    }

    @Test
    public void testUsageDataSourceFilePathOptional() throws Exception{
        createAllProps();
        removeSharedProp(USAGE_DATA_SOURCE_FILE);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals("none", conf.getUsageDataSourceFile());

    }


    @Test
    public void testUsageDataDBUrlOptional() throws Exception{
        createAllProps();
        removeSharedProp(DB_URL);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals(null, conf.getDBUrl());

    }


    @Test
    public void testUsageDataDBUsernameOptional() throws Exception{
        createAllProps();
        removeSharedProp(DB_USERNAME);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals(null, conf.getDBUserName());

    }

    @Test
    public void testUsageDataDBPasswordOptional() throws Exception{
        createAllProps();
        removeSharedProp(DB_PASSWORD);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals(null, conf.getDBPassword());

    }

    @Test
    public void testUsageDataDBDriverClassNameOptional() throws Exception{
        createAllProps();
        removeSharedProp(DB_DRIVER_CLASS_NAME);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals(null, conf.getDBDriverClassName());

    }

    @Test
    public void testUsageDataDBPoolSizeOptional() throws Exception{
        createAllProps();
        removeSharedProp(DB_POOL_SIZE);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals("10", conf.getPoolSize());

    }

    @Test
    public void testTenantThemeUrlOptional() throws Exception {
        createAllProps();
        removeSharedProp(TENANT_THEME_URL);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertNull(conf.getTenantThemeUrl());
    }

    @Test
    public void testTenantThemeUrlReplaceName() throws Exception {
        createAllProps();
        sharedProps.remove(TENANT_THEME_URL);
        sharedProps.setProperty(TENANT_THEME_URL,
                replaceTenantThemeUrl.toString());
        ControllerConfiguration conf = createConfig();
        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals(conf.getTenantThemeUrl().toString(),
                "http://localhost/connect/stratus/theme");
    }

    @Test
    public void testCacheTimeoutDefault() throws Exception {
        createAllProps();
        removeProp(LEGEND_CACHE_TIMEOUT);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals(1, conf.getLegendCacheTimeoutInMinutes());
    }

    @Test
    public void testLocatorImagePath() throws Exception {
        createAllProps();
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals(expectedLocatorImagePath, conf.getLocatorImageForPrint());
    }

    @Test
    public void testCallOutInfoImagePath() throws Exception {
        createAllProps();
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals(expectedCallOutInfoImagePath, conf.getCallOutInfoImageForPrint());
    }

    @Test
    public void testLocatorImagePathNotGiven() throws Exception {
        createAllProps();
        removeProp(LOCATOR_IMAGE_PATH);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        try {
            conf.reload();
            assertTrue(false);
        } catch (Exception e) {
            // excepected
        }
    }

    @Test
    public void testCallOutInfoImagePathNotGiven() throws Exception {
        createAllProps();
        removeProp(CALLOUT_INFO_IMAGE_PATH);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        try {
            conf.reload();
            assertTrue(false);
        } catch (Exception e) {
            // excepected
        }
    }

    @Test
    public void testMappingServiceForBaseMap() throws Exception {
        createAllProps();
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        conf.reload();
        assertEquals(expectedMappingServiceForBaseMapsUrl,
                conf.getMappingServiceWsdlForBaseMapsUrl());
    }

    @Test
    public void testMappingServiceForBaseMapNotGiven() throws Exception {
        createAllProps();
        removeSharedProp(MAPPING_SERVICE_WSDL_FOR_BASE_MAPS_URL);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();
        try {
            conf.reload();
            assertTrue(false);
        } catch (Exception e) {
            // excepected
        }
    }

    @Test
    public void testBingPrivateAPIKeyExistsInControllerDotProperties()
            throws IOException {
        createAllProps();
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();

        conf.reload();
        assertNotNull(conf.getBingServicesPrivateApiKey());
    }


    @Test
    public void testBingPublicAPIKeyExistsInControllerDotProperties()
            throws IOException {
        createAllProps();
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();

        conf.reload();
        assertNotNull(conf.getBingServicesPublicApiKey());
    }

    @Test
    public void testBingAPIPrivateKeyIsCorrect() throws IOException {
        createAllProps();
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();

        conf.reload();
        assertEquals(expectedBingApiPrivateKey, conf.getBingServicesPrivateApiKey());
    }

    @Test
    public void testBingAPIPublicKeyIsCorrect() throws IOException {
        createAllProps();
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();

        conf.reload();
        assertEquals(expectedBingApiPublicKey, conf.getBingServicesPublicApiKey());
    }


    @Test
    public void testBingAPIPrivateKeyIsSetInPropertiesFile() throws IOException {
        createAllProps();
        removeProp(BING_API_PRIVATE_KEY);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();

        try {
            conf.reload();
            assertTrue("bingAPIPrivateKey is mandatory", false);
        } catch (ConfigurationException ce) {
            // expected
        }
    }


    @Test
    public void testBingAPIPublicKeyIsSetInPropertiesFile() throws IOException {
        createAllProps();
        removeProp(BING_API_PUBLIC_KEY);
        ControllerConfiguration conf = createConfig();

        mockSupport.replayAllMocks();

        try {
            conf.reload();
            assertTrue("bingAPIPublicKey is mandatory", false);
        } catch (ConfigurationException ce) {
            // expected
        }
    }



    @Test
    public void testAllGlobalProperties() throws IOException {
        props = new Properties();
        globalProps = createConnectConfigGlobalProperties();

        sharedProps = new Properties();
        globalSharedProps = createGlobalSharedProperties();

        ControllerConfiguration conf = createConfig();
        mockSupport.replayAllMocks();
        conf.reload();
        assertGlobalPropertiesLoaded(conf);
    }

    @Test
    public void testUpdatedProperties() throws IOException {
        createAllProps();

        ControllerConfiguration conf = createReloadConfig();
        mockSupport.replayAllMocks();
        conf.reload();
        assertPropertiesLoaded(conf);
        assertPropertyReloaded(conf);
    }

    private ResourceResolver createTenantMockResolver(
            InputStream tenantInputStream, InputStream tenantSharedInputStream) {
        ResourceResolver mockResolver = mock(ResourceResolver.class);

        when(mockResolver.getResourceAsStream("controller.properties"))
                .thenReturn(tenantInputStream);
        when(mockResolver.getResourceAsStream("shared.properties")).thenReturn(
                tenantSharedInputStream);

        return mockResolver;

    }

    private ResourceResolver createGlobalMockResolver(InputStream globalStream,
                                                      InputStream sharedStream) {
        ResourceResolver mockResolver = mock(ResourceResolver.class);

        mockSupport.createMock(ResourceResolver.class);

        when(mockResolver.getResourceAsStream("controller.properties"))
                .thenReturn(globalStream);
        when(mockResolver.getResourceAsStream("shared.properties")).thenReturn(
                sharedStream);

        return mockResolver;
    }

    private InputStream createInputStreamFromProperties(Properties props)
            throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        props.store(bos, null);
        return new ByteArrayInputStream(bos.toByteArray());
    }

    private Properties createConnectConfigProperties() {
        Properties props = new Properties();
        props.setProperty(LEGEND_CACHE_TIMEOUT,
                Integer.toString(legendCacheTimeout));
        props.setProperty(MAX_NUM_FEATURES, Integer.toString(maxFeatures));
        props.setProperty(LOCATOR_IMAGE_PATH, expectedLocatorImagePath);
        props.setProperty(CALLOUT_INFO_IMAGE_PATH, expectedCallOutInfoImagePath);
        props.setProperty(BING_API_PRIVATE_KEY, expectedBingApiPrivateKey);
        props.setProperty(BING_API_PUBLIC_KEY, expectedBingApiPublicKey);
        props.setProperty(Constants.SSO_START_URL, expectedSsoStartUrl);
        props.setProperty(Constants.SLO_START_URL, expectedSloStartUrl);

        props.setProperty(Constants.SLO_START_URL, expectedSloStartUrl);
        props.setProperty(Constants.SLO_START_URL, expectedSloStartUrl);
        props.setProperty(Constants.SLO_START_URL, expectedSloStartUrl);

        props.setProperty(Constants.AUTH_TYPE, expectedAuthType);
        return props;
    }

    private Properties createConnectConfigGlobalProperties() {
        Properties props = new Properties();
        props.setProperty(LEGEND_CACHE_TIMEOUT,
                Integer.toString(globalLegendCacheTimeout));
        props.setProperty(MAX_NUM_FEATURES, Integer.toString(globalMaxFeatures));
        props.setProperty(LOCATOR_IMAGE_PATH, expectedGlobalLocatorImagePath);
        props.setProperty(CALLOUT_INFO_IMAGE_PATH, expectedCallOutInfoImagePath);
        props.setProperty(BING_API_PRIVATE_KEY, expectedGlobalBingApiPrivateKey);
        props.setProperty(BING_API_PUBLIC_KEY, expectedGlobalBingApiPublicKey);
        props.setProperty(Constants.SSO_START_URL, expectedGlobalSsoStartUrl);
        props.setProperty(Constants.SLO_START_URL, expectedGlobalSloStartUrl);

        props.setProperty(Constants.SLO_START_URL, expectedGlobalSloStartUrl);
        props.setProperty(Constants.SLO_START_URL, expectedGlobalSloStartUrl);
        props.setProperty(Constants.SLO_START_URL, expectedGlobalSloStartUrl);

        props.setProperty(Constants.AUTH_TYPE, expectedGlobalAuthType);
        return props;
    }

    private Properties createSharedProperties() {
        Properties props = new Properties();

        props.setProperty(TENANT_THEME_URL, expectedTenantThemeUrl.toString());
        props.setProperty(ADMINCONSOLE_URL, expectedAdminconsoleUrl.toString());
        props.setProperty(TILE_SERVICE_URL, expectedTileServiceUrl.toString());
        props.setProperty(SPATIAL_SERVICE_BASE_URL,expectedSpatialServiceBaseUrl.toString());
        props.setProperty(SSA_MAP_PROJECT_BASE_URL,expectedSSAMapProjectBaseUrl.toString());

        props.setProperty(OAUTH2_CLIENT_ID, expectedOauthClientId);
        props.setProperty(OAUTH2_CLIENT_SECRET,
                expectedOauthClientSecret);
        props.setProperty(OAUTH2_AUTHORIZATION_SERVER_URL,
                expectedAauthAuthserverEndpoint);
        props.setProperty(SINGLE_LINE_SEARCH_SERVICE_URL,
                expectedSingleLineServiceUrl.toString());
        props.setProperty(ACCESS_PUBLIC_ENABLED,
                Boolean.toString(expectedPublicAccess));
        props.setProperty(ACCESS_AUTHENTICATED_ENABLED,
                Boolean.toString(expectedAuthAccess));
        props.setProperty(MAPPING_SERVICE_WSDL_FOR_BASE_MAPS_URL,
                expectedMappingServiceForBaseMapsUrl.toString());
        props.setProperty(Constants.ANONYMOUS_USER_NAME,
                expectedAnonymousUserName);
        props.setProperty(Constants.ANONYMOUS_PASSWORD,
                expectedAnonymousPassword);

        return props;
    }

    private Properties createGlobalSharedProperties() {
        Properties props = new Properties();

        props.setProperty(TENANT_THEME_URL,
                expectedGlobalTenantThemeUrl.toString());
        props.setProperty(ADMINCONSOLE_URL,
                expectedGlobalAdminconsoleUrl.toString());
        props.setProperty(TILE_SERVICE_URL,
                expectedGlobalTileServiceUrl.toString());
        props.setProperty(SPATIAL_SERVICE_BASE_URL,
                expectedGlobalSpatialServiceBaseUrl.toString());
        props.setProperty(SSA_MAP_PROJECT_BASE_URL,expectedSSAMapProjectBaseUrl.toString());

        props.setProperty(OAUTH2_CLIENT_ID,
                expectedGlobalOauthClientId);
        props.setProperty(OAUTH2_CLIENT_SECRET,
                expectedGlobalOauthClientSecret);
        props.setProperty(OAUTH2_AUTHORIZATION_SERVER_URL,
                expectedGlobalAauthAuthserverEndpoint);
        props.setProperty(SINGLE_LINE_SEARCH_SERVICE_URL,
                expectedGlobalSingleLineServiceUrl.toString());
        props.setProperty(ACCESS_PUBLIC_ENABLED,
                Boolean.toString(expectedGlobalPublicAccess));
        props.setProperty(ACCESS_AUTHENTICATED_ENABLED,
                Boolean.toString(expectedGlobalAuthAccess));
        props.setProperty(MAPPING_SERVICE_WSDL_FOR_BASE_MAPS_URL,
                expectedGlobalMappingServiceForBaseMapsUrl.toString());
        props.setProperty(Constants.ANONYMOUS_USER_NAME,
                expectedAnonymousUserName);
        props.setProperty(Constants.ANONYMOUS_PASSWORD,
                expectedAnonymousPassword);
        props.setProperty(USAGE_DATA_SOURCE,
                expectedUsageDataSource);
        props.setProperty(USAGE_DATA_SOURCE_FILE,
                expectedUsageDataSourceFile);
        props.setProperty(DB_URL,
                expectedDBUrl);
        props.setProperty(DB_USERNAME,
                expectedDBUsername);
        props.setProperty(DB_PASSWORD,
                expectedDBPassword);
        props.setProperty(DB_DRIVER_CLASS_NAME,
                expectedDBDriverClassName);
        props.setProperty(DB_POOL_SIZE,
                expectedDBPoolSize);
        props.setProperty(CAPTURE_USAGE_DATA, "true");
        props.setProperty(PROJECT_SERVICE_URL, expectedProjectServiceUrl.toString());
        props.setProperty(PROJECT_SERVICE_API_KEY, expectedProjectServiceApiKey);

        return props;
    }

    private void createSharedProps() {
        sharedProps = createSharedProperties();
        globalSharedProps = createGlobalSharedProperties();
    }

    private void createProps() {
        props = createConnectConfigProperties();
        globalProps = createConnectConfigGlobalProperties();
    }

    private void createAllProps() {
        createProps();
        createSharedProps();
    }

    private void removeSharedProp(String propName) {
        sharedProps.remove(propName);
        globalSharedProps.remove(propName);
    }

    private void removeProp(String propName) {
        props.remove(propName);
        globalProps.remove(propName);
    }

    /**
     * Creates the configurations
     *
     * @return
     * @throws IOException
     */
    private ControllerConfiguration createConfig() throws IOException {
        InputStream tenantInputStream = createInputStreamFromProperties(props);
        InputStream globalInputStream = createInputStreamFromProperties(globalProps);
        InputStream tenantSharedInputStream = createInputStreamFromProperties(sharedProps);
        InputStream globalSharedInputStream = createInputStreamFromProperties(globalSharedProps);

        PropertiesFileControllerConfiguration config = new PropertiesFileControllerConfiguration(
                createTenantMockResolver(tenantInputStream,
                        tenantSharedInputStream), createGlobalMockResolver(
                globalInputStream, globalSharedInputStream),
                tenantName,
                getUnmodifiedMockFileListModificationObserverFactory());

        PropertyEvaluator ognl = new OgnlPropertyEvaluator();
        config.addPropertyEvaluator(ognl, false);
        PropertyEvaluator smpl = new SimplePropertyEvaluator();
        config.addPropertyEvaluator(smpl, true);
        return config;
    }

    /**
     * Creates the configurations
     *
     * @return
     * @throws IOException
     */
    private ControllerConfiguration createReloadConfig() throws IOException {
        InputStream tenantInputStream = createInputStreamFromProperties(props);
        InputStream globalInputStream = createInputStreamFromProperties(globalProps);
        InputStream tenantSharedInputStream = createInputStreamFromProperties(sharedProps);
        InputStream globalSharedInputStream = createInputStreamFromProperties(globalSharedProps);

        PropertiesFileControllerConfiguration config = new PropertiesFileControllerConfiguration(
                createTenantMockResolver(tenantInputStream,
                        tenantSharedInputStream), createGlobalMockResolver(
                globalInputStream, globalSharedInputStream),
                tenantName,
                getModifiedMockFileListModificationObserverFactory());

        PropertyEvaluator ognl = new OgnlPropertyEvaluator();
        config.addPropertyEvaluator(ognl, false);
        PropertyEvaluator smpl = new SimplePropertyEvaluator();
        config.addPropertyEvaluator(smpl, true);
        return config;
    }

    /**
     * This method will return the mock object of
     * FileListModificationObserverFactory
     *
     * @return FileListModificationObserverFactory
     */
    private FileListModificationObserverFactory getUnmodifiedMockFileListModificationObserverFactory() {

        FileListModificationObserverFactory fileListModificationObserverFactory = mock(FileListModificationObserverFactory.class);
        FileModificationObserver observerMocked = mock(FileModificationObserver.class);
        when(
                fileListModificationObserverFactory
                        .getFileModificationObserver(ObserverType.TENANT_LOCAL))
                .thenReturn(observerMocked);
        when(observerMocked.isModified("shared.properties")).thenReturn(false);
        return fileListModificationObserverFactory;
    }

    private FileListModificationObserverFactory getModifiedMockFileListModificationObserverFactory() {

        FileListModificationObserverFactory fileListModificationObserverFactory = mock(FileListModificationObserverFactory.class);
        FileModificationObserver observerMocked = mock(FileModificationObserver.class);
        when(
                fileListModificationObserverFactory
                        .getFileModificationObserver(ObserverType.TENANT_LOCAL))
                .thenReturn(observerMocked);
        when(observerMocked.isModified("shared.properties")).thenReturn(true);
        return fileListModificationObserverFactory;
    }

    private void assertPropertyReloaded(ControllerConfiguration config) {
        assertEquals(expectedAuthType, config.getAuthType());
    }

    private void assertGlobalPropertiesLoaded(ControllerConfiguration config) {
        assertEquals(expectedGlobalTileServiceUrl.toString(), config
                .getTileServiceUrl().toString());
        assertEquals(expectedGlobalAdminconsoleUrl.toString(), config
                .getAdminConsoleUrl().toString());
        assertEquals(expectedGlobalTenantThemeUrl.toString(), config
                .getTenantThemeUrl().toString());
        assertEquals(globalLegendCacheTimeout,
                config.getLegendCacheTimeoutInMinutes());
        assertEquals(expectedGlobalSingleLineServiceUrl.toString(), config
                .getSingleLineSearchServiceWsdlUrl().toString());
        assertEquals(globalMaxFeatures, config.getMaxFeatureSearchResults());
        assertEquals(expectedGlobalLocatorImagePath,
                config.getLocatorImageForPrint());
        assertEquals(expectedGlobalMappingServiceForBaseMapsUrl,
                config.getMappingServiceWsdlForBaseMapsUrl());
        assertEquals(expectedGlobalBingApiPrivateKey, config.getBingServicesPrivateApiKey());
        assertEquals(expectedGlobalBingApiPublicKey, config.getBingServicesPublicApiKey());
        assertEquals(expectedGlobalSsoStartUrl, config.getSsoStartUrl());
        assertEquals(expectedGlobalSloStartUrl, config.getSloStartUrl());

        assertEquals(expectedGlobalAuthType, config.getAuthType());
    }

    private void assertPropertiesLoaded(ControllerConfiguration config) {
        assertEquals(expectedTileServiceUrl.toString(), config
                .getTileServiceUrl().toString());
        assertEquals(expectedSpatialServiceBaseUrl.toString(), config
                .getSpatialServiceBaseUrl().toString());
        assertEquals(expectedAdminconsoleUrl.toString(), config
                .getAdminConsoleUrl().toString());
        assertEquals(expectedTenantThemeUrl.toString(), config
                .getTenantThemeUrl().toString());
        assertEquals(legendCacheTimeout,
                config.getLegendCacheTimeoutInMinutes());
        assertEquals(expectedSingleLineServiceUrl.toString(), config
                .getSingleLineSearchServiceWsdlUrl().toString());
        assertEquals(maxFeatures, config.getMaxFeatureSearchResults());
        assertEquals(expectedLocatorImagePath, config.getLocatorImageForPrint());
        assertEquals(expectedCallOutInfoImagePath, config.getCallOutInfoImageForPrint());
        assertEquals(expectedMappingServiceForBaseMapsUrl,
                config.getMappingServiceWsdlForBaseMapsUrl());
        assertEquals(expectedBingApiPrivateKey, config.getBingServicesPrivateApiKey());
        assertEquals(expectedBingApiPublicKey, config.getBingServicesPublicApiKey());
        assertEquals(expectedSsoStartUrl, config.getSsoStartUrl());
        assertEquals(expectedSloStartUrl, config.getSloStartUrl());

        assertEquals(expectedAnonymousUserName, config.getAnonymousUserName());
        assertEquals(expectedAnonymousPassword, config.getAnonymousPassword());
        assertEquals(expectedAuthType, config.getAuthType());
        assertEquals(expectedUsageDataSource, config.getUsageDataSource());
        assertEquals(expectedUsageDataSourceFile, config.getUsageDataSourceFile());
        assertEquals(expectedDBDriverClassName, config.getDBDriverClassName());
        assertEquals(expectedDBPassword, config.getDBPassword());
        assertEquals(expectedDBPoolSize, config.getPoolSize());
        assertEquals(expectedDBUrl, config.getDBUrl());
        assertEquals(expectedDBUsername, config.getDBUserName());
        assertEquals(expectedProjectServiceUrl, config.getProjectServiceUrl());
        assertEquals(expectedProjectServiceApiKey, config.getProjectServiceApiKey());
    }

    // FIXME test cases for illegal property values
}
