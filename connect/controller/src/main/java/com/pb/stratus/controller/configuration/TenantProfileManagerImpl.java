package com.pb.stratus.controller.configuration;

import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.geometry.ws.v1.GeometryServiceInterface;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import com.pb.gazetteer.webservice.SingleLineAddress;
import com.pb.spectrum.platform.server.config.core.ws.productapi.impl.SecurityProductServiceImpl;
import com.pb.stratus.controller.ExceptionConverter;
import com.pb.stratus.controller.FeatureServiceExceptionConverter;
import com.pb.stratus.controller.UnknownTenantException;
import com.pb.stratus.controller.action.*;
import com.pb.stratus.controller.annotation.AnnotationParser;
import com.pb.stratus.controller.application.Application;
import com.pb.stratus.controller.application.ApplicationStartupFilter;
import com.pb.stratus.controller.catalog.CatalogService;
import com.pb.stratus.controller.catalog.CatalogServiceImpl;
import com.pb.stratus.controller.compositor.LayerCompositor;
import com.pb.stratus.controller.compositor.LayerCompositorImpl;
import com.pb.stratus.controller.compositor.MapCompositor;
import com.pb.stratus.controller.featuresearch.*;
import com.pb.stratus.controller.geocoder.GGMService;
import com.pb.stratus.controller.geocoder.GeoService;
import com.pb.stratus.controller.geocoder.LIAPIService;
import com.pb.stratus.controller.geometry.*;
import com.pb.stratus.controller.httpclient.WMSHttpClientFactoryImpl;
import com.pb.stratus.controller.httpclient.WMSMapConfigHelper;
import com.pb.stratus.controller.infrastructure.CombiningExecutorFactory;
import com.pb.stratus.controller.infrastructure.cache.CacheHub;
import com.pb.stratus.controller.infrastructure.cache.CacheManager;
import com.pb.stratus.controller.infrastructure.cache.TenantCacheable;
import com.pb.stratus.controller.infrastructure.cache.TenantCacheableImpl;
import com.pb.stratus.controller.legend.CachingLegendService;
import com.pb.stratus.controller.legend.LegendService;
import com.pb.stratus.controller.legend.LegendServiceImpl;
import com.pb.stratus.controller.legend.SpriteImageService;
import com.pb.stratus.controller.marker.MarkerFactory;
import com.pb.stratus.controller.marker.MarkerRepository;
import com.pb.stratus.controller.print.*;
import com.pb.stratus.controller.print.config.LayerServiceType;
import com.pb.stratus.controller.print.config.MapConfigParser;
import com.pb.stratus.controller.print.config.MapConfigRepository;
import com.pb.stratus.controller.print.config.MapConfigRepositoryImpl;
import com.pb.stratus.controller.print.content.WMSMapParser;
import com.pb.stratus.controller.print.image.ImageReader;
import com.pb.stratus.controller.print.render.*;
import com.pb.stratus.controller.print.template.ComponentFactory;
import com.pb.stratus.controller.print.template.StratusComponentFactory;
import com.pb.stratus.controller.print.template.TemplateRenderer;
import com.pb.stratus.controller.service.*;
import com.pb.stratus.controller.service.invocationhandler.ExceptionConvertingInvocationHandler;
import com.pb.stratus.controller.service.invocationhandler.LoggingInvocationHandler;
import com.pb.stratus.controller.service.invocationhandler.NamedResourceTranslatorInvocationHandler;
import com.pb.stratus.controller.tile.service.*;
import com.pb.stratus.controller.util.*;
import com.pb.stratus.controller.util.helper.SpatialServicesHelper;
import com.pb.stratus.controller.wmsprofile.WMSProfileParser;
import com.pb.stratus.core.common.Constants;
import com.pb.stratus.core.common.application.SpringApplicationContextLocator;
import com.pb.stratus.core.configuration.*;
import com.pb.stratus.core.configuration.FileListModificationObserverFactory.ObserverType;
import com.pb.stratus.onpremsecurity.analyst.auth.AnalystOAuthProvider;
import com.pb.stratus.onpremsecurity.http.HttpRequestAuthorizerFactory;
import com.pb.stratus.security.core.http.HttpRequestExecutorFactory;
import com.pb.stratus.security.core.http.IHttpRequestExecutor;
import com.pb.stratus.security.core.resourceauthorization.ResourceAuthorization;
import com.pb.stratus.security.core.resourceauthorization.ResourceException;
import com.pb.stratus.security.core.resourceauthorization.ResourceType;
import com.pb.stratus.security.core.util.AuthorizationUtils;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FopFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * A TenantProfileManagerImpl which does no caching. Every call to getProfile
 * causes a reload of the tenant configuration.
 */
public class TenantProfileManagerImpl implements TenantProfileManager {
    private static final String GLOBAL_PSEUDO_TENANT_NAME = "_global_";
    private static final String SHARED_PROPERTIES = "shared.properties";

    private static final Logger logger = LogManager
            .getLogger(TenantProfileManagerImpl.class);

    private TenantConfiguration tenantConfiguration;

    public TenantProfileManagerImpl(TenantConfiguration tenantConfiguration) {
        this.tenantConfiguration = tenantConfiguration;
    }

    /**
     * Used by TenantConfiguration class for loading tenant profile configuration dynamically
     *
     * @param request
     * @return ControllerConfiguration
     */
    public static ControllerConfiguration getTenantConfiguration(HttpServletRequest request) {
        return getRequestProfile(request).getConfiguration();
    }

    public static int getSessionTimeout(HttpServletRequest request) {
        return getRequestProfile(request).getSessionTimeout();
    }

    public TenantProfile getProfile(String tenantName)
            throws UnknownTenantException {
        tenantConfiguration.setTenantNameHolder(new TenantNameHolderImpl(tenantName));
        return new TenantProfileImpl(tenantConfiguration);
    }

    public static TenantProfile getRequestProfile(HttpServletRequest request) {
        TenantProfile tenantProfile;

        ServletContext servletContext = request.getSession(false)
                .getServletContext();
        Application application = (Application) servletContext
                .getAttribute(ApplicationStartupFilter.APPLICATION_ATTRIBUTE_NAME);
        TenantProfileManager tenantProfileManager = application
                .getTenantProfileManager();

        String tenantName = (String) request
                .getAttribute(Constants.TENANT_ATTRIBUTE_NAME);

        if (tenantName == null) {
            tenantProfile = new NullTenantProfile();
        } else {
            // enforce that all tenant names are used in lower case
            tenantProfile = tenantProfileManager.getProfile(tenantName.toLowerCase());
        }
        return tenantProfile;
    }

    private static class TenantProfileImpl implements TenantProfile {

        private CustomerConfigDirHolder configDirHolder;
        private TenantNameHolder tenantNameHolder;
        private TenantConfiguration tenantConfiguration;
        private ControllerActionFactory actionFactory;
        private ControllerConfiguration config;
        private AuthorizationUtils authorizationUtils;
        private Map<String, MiDevRepository> miDevRepositoryMap;
        private MappingServiceInterface mappingWebService;
        private MappingService mappingService;
        private FeatureServiceInterface featureWebService;
        private GeometryService geometryService;
        private GeometryServiceInterface geometryWebService;
        private FeatureService featureService;
        private CombiningExecutorFactory executorFactory;
        private LegendService legendService;
        private FileSystemConfigReader configReader;

        private AddressService addressService;
        private SingleLineAddress singleLineAddressWebService;
        private BingAddressService bingAddressService;

        private GeoService geoService;
        private GGMService ggmService;
        private LIAPIService liApiService;

        private AnalystOAuthProvider analystOAuthProvider;

        private CatalogService catalogService;
        private TenantSpecificFileSystemResourceResolver tenantResourceResolver;
        private TenantSpecificFileSystemResourceResolver globalResourceResolver;
        private SecurityProductServiceImpl securityProductService;
        private MapConfigRepositoryImpl mapConfigRepo;
        private TemplateRepository templateRepository;
        private TemplateRenderer templateRenderer;
        private MapCompositor mapCompositor;
        private MarkerRepository markerRepository;
        private Tenant tenant;
        private CacheHub cacheHub;
        private TenantCacheable tenantCacheable;
        private FileListModificationObserverFactory fileListModificationObserverFactory;
        private RESTLocatorProxy restLocateproxy;
        private RESTAnalystProxy restAnalystProxy;
        private WMSProfileParser wmsProfileParser;
        private com.pb.stratus.controller.httpclient.HttpClientFactory httpClientFactory;
        private WMSMapConfigHelper wmsConfigHelper;
        private CustomTemplateConfiguration customTemplateConfig;
        private CustomTemplateService customTemplateService;
        private CustomTemplateTableMappingService customTemplateTableMappingService;
        private FeatureEditTemplateMappingService featureEditTemplateMappingService;
        private HttpRequestAuthorizerFactory httpRequestAuthorizerFactory;
        private ProjectService projectService;
        private static final int SECONDS_IN_A_MINUTE = 60;
        private static final int DEFAULT_SESSION_TIMEOUT = 30;
        /**
         * A cross helper class - Ideally when I am introducing it to ensure
         * I can call geometry service from feature service.
         * TODO: Filter out dependency and find a nicer way.
         */
        private SpatialServicesHelper spatialHelper;

        private TenantProfileImpl(TenantConfiguration tenantConfiguration) {
            this.tenantConfiguration = tenantConfiguration;
            this.configDirHolder = tenantConfiguration.getConfigDirHolder();
            this.tenantNameHolder = tenantConfiguration.getTenantNameHolder();

            initTenant();
            initCacheHub();
            initResourceResolver();
            initFileListModificationObserverFactory();
            initConfig();
            initAuthorizationUtils();
            initMiDevRepository();
            initMappingWebService();
            initFeatureWebService();
            initGeometryWebService();
            initAddressWebService();
            initBingAddressService();
            initGeometryService();
            initExecutorFactory();
            initConfigReaders();
            initAnalystOAuthProvider();
            initGeoService();
            initGGMService();
            initLIApiService();
            initRestLocatorProxy();
            initRestAnalystProxy();
            try {
                initFeatureService();
            } catch (NoSuchMethodException nsmx) {
                throw new Error(nsmx);
            }
            initMappingService();
            initLegendService();
            initWMSProfileParser();
            initMapConfigRepository();
            initCatalogService();
            httpRequestAuthorizerFactory = new HttpRequestAuthorizerFactory();
            initProjectService();
            initAddressService();
            initTemplateRepository();
            initMarkerRepository();
            initTemplateRenderer();
            initWMSHttpClientFactory();
            initWMSMapConfigHelper();
            initCustomTemplateService();
            initCustomTemplateTableMappingService();
            initSpatialHelper();
            initSecurityProductService();
            initActionFactory();
            initTenantCacheable();
        }

        private void initAnalystOAuthProvider() {
            analystOAuthProvider = new AnalystOAuthProvider();
        }

        private void initAuthorizationUtils() {
            this.authorizationUtils = (AuthorizationUtils) SpringApplicationContextLocator
                    .getApplicationContext().getBean("authorizationUtils");
        }

        private void initRestLocatorProxy() {
            restLocateproxy = new RESTLocatorProxy(singleLineAddressWebService, geometryService, configDirHolder);
        }

        private void initRestAnalystProxy() {
            restAnalystProxy = new RESTAnalystProxy(configDirHolder);
        }


        private void initCacheHub() {
            cacheHub = CacheManager.getInstance();
        }

        private void initTenantCacheable() {
            tenantCacheable = new TenantCacheableImpl(tenant, cacheHub);
        }

        private void initTenant() {
            tenant = new TenantImpl(this.configDirHolder, this.tenantNameHolder);
        }

        private void initWMSMapConfigHelper() {
            wmsConfigHelper = new WMSMapConfigHelper(projectService);
        }

        private void initCustomTemplateService() {
            customTemplateConfig = new CustomTemplateConfiguration(configDirHolder);
            customTemplateService = new CustomTemplateService(customTemplateConfig);
        }

        private void initCustomTemplateTableMappingService() {
            customTemplateTableMappingService = new CustomTemplateTableMappingService(this.configReader, this.configDirHolder, this.tenantNameHolder.getTenantName());
            featureEditTemplateMappingService = new FeatureEditTemplateMappingService(this.configReader, this.configDirHolder, this.tenantNameHolder.getTenantName());
        }

        public ControllerActionFactory getActionFactory() {
            return actionFactory;
        }

        public String getTenantName() {
            return tenantNameHolder.getTenantName();
        }

        public MapConfigRepositoryImpl getMapConfigRepository() {
            return mapConfigRepo;
        }

        public TenantCacheable getTenantLegendCache() {
            return tenantCacheable;
        }

        public ResourceAuthorization getResourceAuthorization(ResourceType resourceType) {
            try {
                return authorizationUtils.getTenantResourceAuthorization(
                        configDirHolder.getCustomerConfigDir().getAbsolutePath(),
                        tenantNameHolder.getTenantName(),
                        resourceType);
            } catch (ResourceException e) {
                logger.error(e.getMessage());
            }
            return null;
        }

        @Override
        public RESTLocatorProxy getLocateRestProxy() {
            return restLocateproxy;
        }

        @Override
        public RESTAnalystProxy getAnalystRestProxy() {
            return restAnalystProxy;
        }

        @Override
        public int getSessionTimeout(){
            int timeout = DEFAULT_SESSION_TIMEOUT;
            try {
                timeout = projectService.getSessionTimeOut();
            } catch (Exception e) {
                logger.error(e.getMessage() + ":- setting default session timeout which is "+ DEFAULT_SESSION_TIMEOUT + " minutes");
            }

            timeout = timeout == 0 ? DEFAULT_SESSION_TIMEOUT : timeout;
            return timeout * SECONDS_IN_A_MINUTE ;
        }

        private void initResourceResolver() {
            tenantResourceResolver = new TenantSpecificFileSystemResourceResolver(
                    configDirHolder, tenantNameHolder);
            globalResourceResolver = new TenantSpecificFileSystemResourceResolver(
                    configDirHolder, new TenantNameHolderImpl(
                    GLOBAL_PSEUDO_TENANT_NAME));
        }

        /**
         * This method will add files for which any modification need to be
         * watched
         */
        private void initFileListModificationObserverFactory() {
            fileListModificationObserverFactory = new FileListModificationObserverFactory();
            Set<String> fileSet = new HashSet<>();
            fileSet.add(SHARED_PROPERTIES);
            ConfigurationFileListModificationObserver tenantLocalObserver = new ConfigurationFileListModificationObserver(
                    tenantResourceResolver, fileSet);
            fileListModificationObserverFactory.addObserver(ObserverType.TENANT_LOCAL, tenantLocalObserver);
        }

        private void initConfig() {
            PropertiesFileControllerConfiguration propertiesConfiguration = new PropertiesFileControllerConfiguration(
                    tenantResourceResolver, globalResourceResolver,
                    tenantNameHolder.getTenantName(),
                    fileListModificationObserverFactory);
            propertiesConfiguration.addPropertyEvaluator(
                    new SimplePropertyEvaluator(), true);
            propertiesConfiguration.addPropertyEvaluator(
                    new OgnlPropertyEvaluator(), false);
            config = propertiesConfiguration;
            config.reload();
        }

        private void initMiDevRepository() {
            miDevRepositoryMap = new HashMap<>();
            miDevRepositoryMap.put("NAMED_TABLE", new MiDevNamedTableRepository(tenantNameHolder.getTenantName()));
            miDevRepositoryMap.put("NAMED_MAP", new MiDevNamedMapRepository(tenantNameHolder.getTenantName()));
            miDevRepositoryMap.put("NAMED_LAYER", new MiDevNamedLayerRepository(tenantNameHolder.getTenantName()));

        }

        private void initGeometryWebService() {
            geometryWebService = (GeometryServiceInterface) LoggingInvocationHandler.newInstance(
                    WebServiceProxyFactory.getProxy(config).getGeometryInterface(),
                    new TransactionMonitor(TransactionMonitor.GEOMETRY_SERVICE_LOGGER));

        }

        private void initMappingWebService() {

            mappingWebService = WebServiceProxyFactory.getProxy(config)
                    .getMappingInterface();
            ExceptionConverter converter = new FeatureServiceExceptionConverter();
            InvocationHandler handler = new ExceptionConvertingInvocationHandler(
                    converter, mappingWebService);
            mappingWebService = (MappingServiceInterface) Proxy
                    .newProxyInstance(getClass().getClassLoader(),
                            new Class<?>[]{MappingServiceInterface.class},
                            handler);
            handler = new NamedResourceTranslatorInvocationHandler(
                    miDevRepositoryMap, mappingWebService);
            mappingWebService = (MappingServiceInterface) Proxy
                    .newProxyInstance(getClass().getClassLoader(),
                            new Class<?>[]{MappingServiceInterface.class},
                            handler);
            mappingWebService = (MappingServiceInterface) LoggingInvocationHandler.newInstance(
                    mappingWebService, new TransactionMonitor(TransactionMonitor.MAPPING_SERVICE_LOGGER));
        }

        private void initFeatureWebService() {

            featureWebService = WebServiceProxyFactory.getProxy(config)
                    .getFeatureInterface();
            ExceptionConverter converter = new FeatureServiceExceptionConverter();
            InvocationHandler handler = new ExceptionConvertingInvocationHandler(
                    converter, featureWebService);
            featureWebService = (FeatureServiceInterface) Proxy
                    .newProxyInstance(getClass().getClassLoader(),
                            new Class<?>[]{FeatureServiceInterface.class},
                            handler);
            handler = new NamedResourceTranslatorInvocationHandler(
                    miDevRepositoryMap, featureWebService);
            featureWebService = (FeatureServiceInterface) Proxy
                    .newProxyInstance(getClass().getClassLoader(),
                            new Class<?>[]{FeatureServiceInterface.class},
                            handler);
            featureWebService = (FeatureServiceInterface) LoggingInvocationHandler.newInstance(
                    featureWebService, new TransactionMonitor(TransactionMonitor.FEATURE_SERVICE_LOGGER));
        }

        private void initAddressWebService() {
            singleLineAddressWebService = WebServiceProxyFactory.getProxy(
                    config).getSingleLineSearchInterface();
        }

        private void initBingAddressService() {
            bingAddressService = new BingAddressService(config, authorizationUtils);
        }

        // initialise EGM service
        private void initGeoService() {
            geoService = new GeoService();
        }

        // initialize LIAPI Service
        private void initLIApiService() {
            liApiService = new LIAPIService(analystOAuthProvider);
        }

        // initialize Project Service
        private void initProjectService() {
            projectService = new ProjectService(
                    config.getProjectServiceUrl().toString(),
                    config.getProjectServiceApiKey(),
                    new RestUrlExecutorImpl(httpRequestAuthorizerFactory.getJWTAuthorizer())
            );
        }

        // initialise GGM service
        private void initGGMService() {
            ggmService = new GGMService();
        }

        private void initExecutorFactory() {
            executorFactory = new CombiningExecutorFactory(
                    Executors.newFixedThreadPool(20));
        }

        private void initGeometryService() {
            geometryService = new GeometryServiceImpl(geometryWebService, config);
        }

        private void initMappingService() {
            mappingService = new MappingServiceImpl(mappingWebService, tenantNameHolder.getTenantName());
        }

        private void initFeatureService() throws NoSuchMethodException {
            SearchStrategyRepository searchStratRepo = new SearchStrategyRepository();
            FeatureSearchResultConverterFactory converterFactory = new FeatureSearchResultConverterFactory();
            SearchStrategy strat = new SearchAtPointStrategy(featureWebService,
                    converterFactory, config.getMaxFeatureSearchResults());
            searchStratRepo.addSearchStrategy("searchAtPoint", strat);

            strat = new SearchNearestStrategy(featureWebService,
                    converterFactory, config.getMaxFeatureSearchResults());
            searchStratRepo.addSearchStrategy("searchNearest", strat);

            strat = new SearchByExpressionStrategy(featureWebService,
                    converterFactory, config.getMaxFeatureSearchResults());
            searchStratRepo.addSearchStrategy("searchByExpression", strat);

            // This strategy is to get Distinct data for a column from a table.
            strat = new SearchByExpressionGroupByStrategy(featureWebService,
                    converterFactory, config.getMaxFeatureSearchResults());
            searchStratRepo.addSearchStrategy("searchByExpressionGroupBy",
                    strat);

            strat = new SearchWithinGeometryStrategy(featureWebService,
                    converterFactory, config.getMaxFeatureSearchResults());
            searchStratRepo.addSearchStrategy("searchByGeometry", strat);

            strat = new SearchByGeometryInTablesStrategy(featureWebService,
                    converterFactory, config.getMaxFeatureSearchResults());
            searchStratRepo.addSearchStrategy("searchByGeometryInTables", strat);

            strat = new SearchFeaturesListStrategy(featureWebService,
                    converterFactory, config.getMaxFeatureSearchResults());
            searchStratRepo.addSearchStrategy("listFeatures", strat);

            strat = new SearchByQueryStrategy(featureWebService,
                    converterFactory, config.getMaxFeatureSearchResults());
            searchStratRepo.addSearchStrategy("searchByQuery", strat);

            SrsTransformer transformer = new SrsTransformer(geometryService);
            featureService = new FeatureServiceImpl(featureWebService,
                    searchStratRepo, transformer);
        }

        private void initLegendService() {
            legendService = new LegendServiceImpl(mappingWebService,
                    executorFactory);
            if (this.config != null && this.config.isLegendCacheEnabled()) {
                legendService = new CachingLegendService(legendService, tenant, cacheHub);
            }
        }

        private void initConfigReaders() {
            configReader = new FileSystemConfigReader(configDirHolder
                    .getCustomerConfigDir().getAbsolutePath(),
                    tenantNameHolder.getTenantName());
        }

        private void initMapConfigRepository() {
            MapConfigParser mapConfigParser = new MapConfigParser();
            mapConfigRepo = new MapConfigRepositoryImpl(configReader, mapConfigParser, wmsProfileParser);
            // XXX should constant go somewhere else?
            // TODO need to make this variable thread-bound.
            // applicationScope.setAttribute(
            // ScriptInserter.MAP_CONFIG_REPOSITORY_ATTR, mapConfigRepo);
        }

        private void initWMSProfileParser() {
            this.wmsProfileParser = new WMSProfileParser();
        }

        private void initWMSHttpClientFactory() {
            this.httpClientFactory = new WMSHttpClientFactoryImpl(this.mapConfigRepo);
        }

        private void initCatalogService() {
            catalogService = new CatalogServiceImpl(mappingWebService,
                    executorFactory, tenantNameHolder.getTenantName());
        }

        private void initSecurityProductService() {
            securityProductService = (SecurityProductServiceImpl) SpringApplicationContextLocator.
                    getApplicationContext().getBean("securityProductServiceInterface");
        }

        private void initAddressService() {
            addressService = new AddressServiceImpl(
                    singleLineAddressWebService, bingAddressService, geoService, ggmService, liApiService,
                    projectService);
        }

        private void initTemplateRepository() {
            templateRepository = new TemplateRepository(configReader);
        }

        private void initMarkerRepository() {
            markerRepository = new MarkerRepository(configReader);
        }

        private void initTemplateRenderer() {
            Map<LayerServiceType, LayerRenderer> layerRenderers = new HashMap<>();
            LayerRenderer renderer = new BingRenderer(new TileCompositor(),
                    new BingTileService(config, new ImageReader(),
                            new BingUrlBuilder(), this.authorizationUtils));
            layerRenderers.put(LayerServiceType.BING, renderer);

            /**
             * Added Open street map renderer
             */
            renderer = new OpenStreetRenderer(new TileCompositor(),
                    new OpenStreetTileService(config));
            layerRenderers.put(LayerServiceType.OSM, renderer);
            renderer = new XYZRenderer(new TileCompositor(),
                    new XYZTileService(config));
            layerRenderers.put(LayerServiceType.XYZ, renderer);
            renderer = new TMSRenderer(new TileCompositor(),
                    new TMSTileService(config));
            layerRenderers.put(LayerServiceType.TMS, renderer);

            renderer = new MidevRenderer(mappingService);
            layerRenderers.put(LayerServiceType.MAPPING, renderer);
            renderer = createTileLayerRenderer();
            layerRenderers.put(LayerServiceType.TILE, renderer);
            renderer = new ThematicRenderer(mappingService);
            layerRenderers.put(LayerServiceType.THEMATIC, renderer);
            renderer = new QueryResultRenderer(mappingService);
            layerRenderers.put(LayerServiceType.QUERYRESULT, renderer);
            LayerCompositor layerCompositor = new LayerCompositorImpl(
                    layerRenderers);
            WatermarkRenderer watermarkRenderer = new WatermarkRenderer();
            MarkerRenderer markerRenderer = new MarkerRenderer();
            CopyrightRenderer copyrightRenderer = new CopyrightRenderer();
            BingAttributionRenderer bingAttributionRenderer = new BingAttributionRenderer(
                    geometryService);
            OpenStreetMapAttributionRenderer openStreetMapAttributionRenderer = new OpenStreetMapAttributionRenderer(
                    geometryService);
            MapConfigParser mapConfigParser = new MapConfigParser();
            MapConfigRepository mapConfigRepo = new MapConfigRepositoryImpl(
                    configReader, mapConfigParser, wmsProfileParser);

            //Sikhar: This is a new renderer created to support printing of geometries like Line Polygon etc
            AnnotationRenderer annotationsRenderer = new AnnotationRendererImpl(mappingService, markerRenderer);

            mapCompositor = new MapCompositor(layerCompositor,
                    watermarkRenderer, markerRenderer, copyrightRenderer,
                    bingAttributionRenderer, openStreetMapAttributionRenderer,
                    mapConfigRepo, configReader, annotationsRenderer);
            ScaleBarFactory scaleBarFactory = new ScaleBarFactory(
                    geometryService);
            BoundingBoxCalculator calculator = new BoundingBoxCalculator();
            ComponentFactory componentFactory = new StratusComponentFactory(
                    mapCompositor, legendService, scaleBarFactory, calculator,
                    mapConfigRepo);
            FopFactory fopFactory = FopFactory.newInstance();
            try {

                String fontsDirPath = configDirHolder.getCustomerConfigDir()
                        .getAbsolutePath() + File.separator + "_global_" +
                        File.separator + "fonts";

                if (new File(fontsDirPath).exists()) {
                    fopFactory.getFontManager().setFontBaseURL(fontsDirPath);
                    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("fop.xml");
                    fopFactory.setUserConfig(stream2file(inputStream));
                }

            } catch (Exception ex) {
                logger.error(ex.getMessage());
            }

            TemplateContentHandlerFactory templateContentHandlerFactory = new TemplateContentHandlerFactory(
                    componentFactory, fopFactory);
            templateRenderer = new TemplateRenderer(
                    templateContentHandlerFactory);
        }

        /**
         * Converts Input Stream to File Object
         *
         * @param in input stream
         * @return fileobject from
         * @throws IOException
         */
        private static File stream2file(InputStream in) throws IOException {
            final File tempFile = File.createTempFile("fop", ".xml");
            tempFile.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                IOUtils.copy(in, out);
            }
            return tempFile;
        }

        private void initSpatialHelper() {
            spatialHelper = new SpatialServicesHelper(geometryWebService, mappingWebService, featureWebService);
        }

        private void initActionFactory() {
            /**
             * Annotation parser defined.
             */
            AnnotationParser annotationParser = new AnnotationParser();

            WMSMapParser wmsMapParser = new WMSMapParser(new WMSHttpClientFactoryImpl(this.mapConfigRepo), config);

            actionFactory = new ControllerActionFactory(config);
            addControllerAction("/getCsvForSearchBySql",
                    new SplitRequestForSearchBySqlAction(featureService,
                            configReader));
            addControllerAction("/getCsvForSearchByPolygon",
                    new SplitRequestForSearchByPolygonAction(featureService, spatialHelper));
            addControllerAction("/getCsvForListFeatures",
                    new SplitRequestForListFeatureAction(featureService));
            addControllerAction("/describeTable", new DescribeTableAction(
                    featureService));
            addControllerAction("/getSpatialColumnName", new GetGeomColumnNameAction(
                    featureService));
            addControllerAction("/queryList", new QueryListAction(authorizationUtils));
            addControllerAction("/queryFilters", new QueryFiltersAction(authorizationUtils));
            addControllerAction(
                    "/featureSearchByQuery",
                    new FeatureSearchByQueryAction(featureService, configReader, authorizationUtils, spatialHelper));
            addControllerAction("/queryDataLoad", new QueryDataLoadAction(configReader));
            addControllerAction("/listFeatures",
                    new FeatureSearchListFeaturesAction(featureService));
            addControllerAction("/featureSearch",
                    new FeatureSearchAtPointAction(featureService));
            addControllerAction("/featureSearchNearest",
                    new FeatureSearchNearestAction(featureService));
            addControllerAction("/distinctRecords",
                    new FeatureSearchDistinctRecordsAction(featureService));
            addControllerAction("/featureSearchByExpression",
                    new FeatureSearchByExpressionAction(featureService));
            addControllerAction("/featureSearchByGeometry",
                    new FeatureSearchByGeometryAction(featureService, spatialHelper));
            addControllerAction("/featureSearchByGeometryInTables",
                    new FeatureSearchByGeometryInTablesActions(featureService, spatialHelper));
            addControllerAction("/summarizeData",
                    new SummarizedFeatureSearchAction(featureService));

            addControllerAction("/featureCountByGeometry",
                    new FeatureCountByGeometryAction(featureService));
            String legendImageActionBasePath = "/mapping/legend";
            addControllerAction("/legend", new LegendAction(legendService,
                    legendImageActionBasePath));
            SpriteImageGeneratorFactory imageGenFactory = new SpriteImageGeneratorFactory();
            SpriteImageService imageService = new SpriteImageService(
                    legendService, imageGenFactory, tenant, cacheHub);
            addControllerAction(legendImageActionBasePath,
                    new CompositeLegendImageAction(imageService));
            addControllerAction("/mapping/getmap", new MappingAction(
                    mappingService));
            addControllerAction("/getMapLayers", new MappingLayersAction(
                    mappingService));
            addControllerAction("/describeNamedMaps", new DescribeNamedMapsAction(
                    mappingService));
            addControllerAction("/requestNamedLayers", new MappingNamedLayerAction(
                    mappingService));

            addControllerAction("/mapImage", new MapImageAction(mapCompositor));
            FmnResultsCollectionParser fmnParser = new FmnResultsCollectionParser(
                    new MarkerFactory(), markerRepository, new ImageReader());
            LocatorMarkerParser locatorParser = new LocatorMarkerParser(configReader, config);
            addControllerAction("/mapping/print", new PrintAction(templateRepository,
                    templateRenderer, fmnParser, locatorParser, annotationParser,
                    featureService, wmsMapParser));
            addControllerAction("/printTemplateAction", new PrintTemplateAction(templateRepository));
            addControllerAction("/getImage", new ImageAction(configReader));
            addControllerAction("/getmarker",
                    new MarkerAction(markerRepository));
            addControllerAction("/locator/singleLine",
                    new SingleLineSearchAction(addressService, geometryService,
                            mapConfigRepo, tenantNameHolder));
            addControllerAction("/geocodeAddresses",
                    new GeocodeAddressesAction(addressService, tenantNameHolder));

            addControllerAction("/listRoles", new ListRolesAction(securityProductService));
            addControllerAction(
                    "/tiling/gettile",
                    new TileServiceProxyAction(
                            (HttpRequestExecutorFactory) SpringApplicationContextLocator
                                    .getApplicationContext().getBean(
                                            "httpRequestExecutorFactory"),
                            new SimpleClientHttpRequestFactory()));

            addControllerAction("/connectProxy", new ConnectSpatialRestProxyAction(
                    (HttpRequestExecutorFactory) SpringApplicationContextLocator
                            .getApplicationContext().getBean(
                                    "httpRequestExecutorFactory"),
                    new SimpleClientHttpRequestFactory()));


            addControllerAction("/geometry",
                    new GeometryAction(geometryService));
            addControllerAction("/config", new ConfigAction(configReader, config, authorizationUtils));
            addControllerAction("/infoTemplate", new CalloutInfoTemplateAction(configReader, config, authorizationUtils));
            addControllerAction("/getFeatureEditTemplates", new FeatureEditTemplateAction(configReader, config, authorizationUtils));
            addControllerAction("/getOverlayTables",
                    new OverlaysNamedTablesAction(catalogService));
            addControllerAction("/authentication", new AuthenticationAction(
                    config));
            addControllerAction("/theme", new ThemeAction(tenantResourceResolver));
            addControllerAction("/project", new ThemeAction(tenantResourceResolver));
            addControllerAction("/logout", new LogoutControllerAction());
            addControllerAction("/logout/", new LogoutControllerAction());
            addControllerAction("/getAuthMapConfigs",
                    new GetAuthorizedMapConfigAction(authorizationUtils));
            addControllerAction("/getAuthProjects",
                    new GetAuthorizedMapConfigAction(authorizationUtils, true));
            addControllerAction("/measure", new GeometryMeasureAction(
                    new GeometryMeasureExecutorAdapter(config, geometryWebService),
                    new GeometryRequestFactory()));
            addControllerAction("/bufferAnnotation",
                    new GeometryBufferAnnotationAction(geometryService));
            addControllerAction("/exportAnnotationAsKML",
                    new ExportKMLAction());
            addControllerAction("/importKMLAsAnnotation",
                    new ImportKMLAction());
            addControllerAction("/geometryTransformation",
                    new TransformGeometryAction(geometryService));
            addControllerAction("/geometryDistance",
                    new GeometryDistanceAction(geometryService));
            addControllerAction("/connectHelp", new ConnectHelpAction());
            addControllerAction("/buildRevisionDetails", new BuildRevisionDetailsAction());
            addControllerAction("/wms/getwms", new WMSProxyAction(httpClientFactory, wmsConfigHelper));
            addControllerAction("/externalTileServiceProxy", new ExternalTileServiceProxyAction(projectService));
            addControllerAction("/exportCsvData", new ExportCsvDataAction());
            addControllerAction("/getThematicMap", new ThematicMappingAction(mappingService));
            addControllerAction("/getThematicMapLegend", new ThematicLegendAction(legendService));
            addControllerAction("/getColumnRecordCount", new RecordCountQueryAction(featureService));
            addControllerAction("/getBoundaryValueforColumn", new GetBoundaryColumnValueAction(featureService));
            addControllerAction("/dataBinding", new DataBindingAction(featureService));
            addControllerAction("/getQueryMap", new QueryMappingAction(mappingService));
            addControllerAction("/applicationLinkingMapping", new ExternalAppLinkingMappingAction(featureService));
            addControllerAction("/checkApplicationLinking", new ExternalAppLinkingAction(this.configReader, tenantNameHolder.getTenantName()));
            addControllerAction("/checkExtensibility", new AnalystExtensionEnabledAction(this.configReader, tenantNameHolder.getTenantName()));

            addControllerAction("/getCustomAnalystModuleConfig", new CustomAnalystModuleConfigAction(this.configReader, tenantNameHolder.getTenantName()));
            addControllerAction("/registerApplicationLinkingUser", new RegisterAppLinkingUserAction(this.configReader, this.configDirHolder, tenantNameHolder.getTenantName()));
            addControllerAction("/getDeployedProfile", new DeploymentIdentificationAction());
            addControllerAction("/getBrands", new BrandAction(this.configDirHolder, tenantNameHolder.getTenantName()));

            //GetTravelBoundary

            addControllerAction("/getTravelBoundary", new GetTravelBoundaryAction(httpRequestAuthorizerFactory,
                    new RestUrlExecutorImpl(), projectService, new LIAPIService(new AnalystOAuthProvider())));
            addControllerAction("/customTemplateDesigner", new CustomTemplateGeneratorAction(customTemplateService, authorizationUtils));
            addControllerAction("/isTemplateManuallyEdited", new CustomTemplateTimestampAction(customTemplateService, authorizationUtils));
            addControllerAction("/customTemplateTableMapping", new CustomTemplateTableMappingAction(customTemplateService, authorizationUtils, customTemplateTableMappingService, featureEditTemplateMappingService));
            addControllerAction("/listTemplates", new ListTemplatesAction(this.configDirHolder, tenantNameHolder.getTenantName()));
            addControllerAction("/mapProjectProxy", new MapProjectProxyAction(authorizationUtils, (HttpRequestExecutorFactory) SpringApplicationContextLocator
                    .getApplicationContext().getBean(
                            "httpRequestExecutorFactory"),
                    new SimpleClientHttpRequestFactory()));
        }

        private void addControllerAction(String path, ControllerAction action) {
            actionFactory.mapControllerAction(action, path);
        }

        private LayerRenderer createTileLayerRenderer() {
            TileCompositor tileCompositor = new TileCompositor();
            HttpRequestExecutorFactory requestExecutorFactory = (HttpRequestExecutorFactory) SpringApplicationContextLocator
                    .getApplicationContext().getBean(
                            "httpRequestExecutorFactory");

            IHttpRequestExecutor iHttpRequestExecutor = requestExecutorFactory.create(config);
            TileRepository repo = new TileRepository(
                    tenantNameHolder.getTenantName(), iHttpRequestExecutor,
                    new ImageReader(), config.getTileServiceUrl(),
                    new SimpleClientHttpRequestFactory());
            //XXX read maxTiles from configuration
            MiDevTileService tileService = new MiDevTileService(repo,
                    config.getMaximumNumberOfPrintableTiles());
            return new MiDevTileLayerRenderer(tileCompositor, tileService);
        }

        public ControllerConfiguration getConfiguration() {
            return this.config;
        }
    }
}
