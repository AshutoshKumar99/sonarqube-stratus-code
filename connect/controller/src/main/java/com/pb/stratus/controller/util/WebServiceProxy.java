package com.pb.stratus.controller.util;

import com.g1.dcg.managers.access.AclManagerService;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.geometry.ws.v1.GeometryServiceInterface;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import com.pb.gazetteer.webservice.SingleLineAddress;
import com.pb.gazetteer.webservice.SingleLineAddressService;
import com.pb.stratus.core.common.Preconditions;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;
import java.net.URL;

/**
 * The WebServiceProxy class allows a single object to retrieve the wsdls over
 * JAX-WS once and once only. It should not be instantiated directly, rather a
 * call to WebServiceProxyFactory.getProxy() should be used, passing the
 * relevant configuration.
 *
 * @author co003ki
 */
public class WebServiceProxy
{
    private final static String MAPPING_NS = "http://www.mapinfo.com/midev/service/mapping/ws/v1";
    private final static String MAPPING_NAME = "MappingService";
    private final static String SINGLE_LINE_SEARCH_NS = "http://webservice.gazetteer.pb.com/";
    private final static String SINGLE_LINE_SEARCH_NAME = "SingleLineAddressService";
    private final static Logger logger = LogManager.getLogger(WebServiceProxy.class);
    
    private SingleLineAddressService singleLineSearchService = null;
    private ControllerConfiguration configuration = null;
    //private MappingService mappingServiceForBaseMap;
    private ServletContext applicationScope;

    protected WebServiceProxy(ControllerConfiguration configuration,
                              ServletContext applicationScope)
    {
        Preconditions.checkNotNull(applicationScope,
                "applicationScope cannot be null");
        logger.debug("Instantiating WebServiceProxy object");
        this.configuration = configuration;
        this.applicationScope = applicationScope;
        generateSingleLineSearchService();
        //generateMappingServiceForBaseMap();
    }

    /*
     * Get a MappingServiceInterface instance to speak directly to the
     * MappingService.
     * @return a MappingServiceInterface object
     */
    public MappingServiceInterface getMappingInterface()
    {
        MappingServiceInterface mappingServiceInterface =
        (MappingServiceInterface) this.applicationScope.
                getAttribute(MappingServiceInterface.class.getName());
        Preconditions.checkState(mappingServiceInterface != null,
                "mappingServiceInterface cannot be null");
        return mappingServiceInterface;
    }

    /*
     * Get a MappingServiceInterface instance to speak directly to the
     * MappingService.
     * @return a MappingServiceInterface object
     */
    //public MappingServiceInterface getMappingInterfaceForBaseMap()
    //{
    //    return mappingServiceForBaseMap.getMappingServiceInterface();
    //}
    
    /**
     * Get a FeatureServiceInterface instance to speak directly to the
     * FeatureService.
     *
     * @return a FeatureServiceInterface object
     */
    public FeatureServiceInterface getFeatureInterface()
    {
        FeatureServiceInterface featureServiceInterface =
        (FeatureServiceInterface) applicationScope.
                getAttribute(FeatureServiceInterface.class.getName());
        Preconditions.checkState(featureServiceInterface != null,
                "featureServiceInterface cannot be null");
        return featureServiceInterface;
    }

    /**
     * Get a GeometryServiceInterface instance to speak directly to the
     * GeometryService.
     *
     * @return a GeometryServiceInterface object
     */
    public GeometryServiceInterface getGeometryInterface()
    {
        GeometryServiceInterface geometryServiceInterface =
        (GeometryServiceInterface) applicationScope.
                getAttribute(GeometryServiceInterface.class.getName());
        Preconditions.checkState(geometryServiceInterface != null,
                "geometryServiceInterface cannot be null");
        return geometryServiceInterface;
    }

    /*
     * Get an AclManagerServiceInterface instance to speak directly to the
     * AclManagerService.
     * @return a AclManagerService object
     */
    public AclManagerService getAclManagerServiceInterface()
    {
        AclManagerService aclManagerServiceInterface =
                (AclManagerService) this.applicationScope.
                        getAttribute(AclManagerService.class.getName());
        Preconditions.checkState(aclManagerServiceInterface != null,
                "aclManagerServiceInterface cannot be null");
        return aclManagerServiceInterface;
    }

    /**
     * Get a SingleLineSearcSoap instance to speak directly to the
     * SingleLineSearchService.
     *
     * @return a tSingleLineSearchSoap object
     */
    public SingleLineAddress getSingleLineSearchInterface()
    {
        return singleLineSearchService.getSingleLineAddressPort();
    }


    // Create a single instance of the MappingService object
    //private void generateMappingServiceForBaseMap()
    //{
     //   QName serviceName = new QName(MAPPING_NS, MAPPING_NAME);
     //   assert configuration != null : "Null configuration passed to generateMappingService()";
     //   URL wsdl = configuration.getMappingServiceWsdlForBaseMapsUrl();
     //   logger.debug("mappingWSDLLocation: " + wsdl);
     //mappingServiceForBaseMap =  new MappingService(wsdl, serviceName);
   // }

    // Create a single instance of the AddressService object
    private void generateSingleLineSearchService()
    {
        try
        {
            QName serviceName = new QName(SINGLE_LINE_SEARCH_NS, SINGLE_LINE_SEARCH_NAME);
            assert configuration != null : "Null configuration passed to generateSingleLineSearchService()";
            URL wsdl = configuration.getSingleLineSearchServiceWsdlUrl();
            logger.debug("singleLineSearchServiceWSDLLocation: " + wsdl);
            singleLineSearchService = new SingleLineAddressService(wsdl, serviceName);
        } catch (WebServiceException e)
        {
        	logger.error("Webservice Exception for SingleLine WSDLLocation", e);
        }
    }
}
