package com.pb.gazetteer.rest;

import com.pb.gazetteer.*;
import com.pb.gazetteer.lucene.LuceneIndexGenerator;
import com.pb.gazetteer.lucene.LuceneInstance;
import com.pb.gazetteer.search.SearchLogic;
import com.pb.gazetteer.webservice.*;

import static org.mockito.Matchers.*;

import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import junit.framework.Assert;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.*;

import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.test.context.ActiveProfiles;


import javax.activation.DataHandler;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by JU002AH on 4/15/2019.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RESTLocateService.class, LuceneIndexGenerator.class, LocateService.class})
@PowerMockIgnore("javax.management.*")
public class RESTLocateAPITest {

    private static final String TENANT1 = "Tenant1";
    private static final String TENANT2 = "Tenant2";
    private static final String GAZ1 = "Gaz1";
    private static final String GAZ2 = "Gaz2";
    private static final String PROJECTION = "epsg:27700";
    private static final int ADDRESS_COLUMN = 0;
    private static final int X_COLUMN = 1;
    private static final int Y_COLUMN = 2;
    private static final String DELIMITER = ",";
    private static final String DEFAULT_LOGIC = "DEFAULT_LOGIC";

    private List<Address> m_testTenant1TestGaz1ExpectedResult = new ArrayList<Address>();
    private List<Address> m_testTenant1TestGaz2ExpectedResult = new ArrayList<Address>();
    private List<Address> m_testTenant2TestGaz1ExpectedResult = new ArrayList<Address>();
    private List<Address> m_testTenant2TestGaz2ExpectedResult = new ArrayList<Address>();

    private LuceneInstance locateInstance1;
    LocateConfigProvider provider;
    private RESTLocateService restLocateService = new RESTLocateService();
    LocateService mockLocateService;

    private LocateService locateService;

    @Context
    private MessageContextImpl messageContext;

    @Before
    public void setup() throws Exception {
        Map<String, LocateConfig> tenantConfigMap = new HashMap<String, LocateConfig>();
        mockLocateService = mock(LocateService.class);
        restLocateService.setLocateService(mockLocateService);

        //tenant 1 setup
        LocateConfig locConfig1 = new LocateConfig();
        List<LocateInstance> locInstances1 = new ArrayList<LocateInstance>();
        locateInstance1 = createMockLocateInstance(GAZ1, m_testTenant1TestGaz1ExpectedResult);
        locInstances1.add(locateInstance1);
        locInstances1.add(createMockLocateInstance(GAZ2, m_testTenant1TestGaz2ExpectedResult));
        locConfig1.setLocateInstances(locInstances1);
        locConfig1.setDefaultInstance(locInstances1.get(1));
        tenantConfigMap.put(TENANT1, locConfig1);

        //tenant 2 setup
        LocateConfig locConfig2 = new LocateConfig();
        List<LocateInstance> locInstances2 = new ArrayList<LocateInstance>();
        locInstances2.add(createMockLocateInstance(GAZ1, m_testTenant2TestGaz1ExpectedResult));
        locInstances2.add(createMockLocateInstance(GAZ2, m_testTenant2TestGaz2ExpectedResult));
        locConfig2.setLocateInstances(locInstances2);
        locConfig2.setDefaultInstance(locInstances2.get(1));
        tenantConfigMap.put(TENANT2, locConfig2);

        provider = createMockConfigProvider(tenantConfigMap);

        MessageContextImpl mockWebServiceContext = mock(MessageContextImpl.class, Mockito.RETURNS_MOCKS);
        ServletContext mockServletContext = mock(ServletContext.class);
        when(mockWebServiceContext.getServletContext()).thenReturn(mockServletContext);
        when(mockServletContext.getAttribute(ApplicationContextListener.PROVIDER_KEY)).thenReturn(provider);
        Whitebox.setInternalState(restLocateService, "messageContext", mockWebServiceContext);
    }

    @Test
    public void getGazetteerNames() throws Exception {
        //assert tenant 1
        GazetteerNames result1 = restLocateService.getGazetteerNames(TENANT1);
        assertEquals(GAZ2, result1.getDefaultGazetteerName());
        List<GazetteerInstance> gazetteerInstances = result1.getGazetteerInstances();
        assertEquals(2, gazetteerInstances.size());
        assertEquals(GAZ1, gazetteerInstances.get(0).getGazetteerName());
        assertEquals(GAZ2, gazetteerInstances.get(1).getGazetteerName());
    }

    @Test
    public void testPopulateGazetteer() throws Exception {
        Attachment mockAttachment = mock(Attachment.class);
        PopulateParameters pp = new PopulateParameters();
        pp.setTenantName(TENANT1);
        pp.setGazetteerName(GAZ1);
        pp.setProjection(PROJECTION);
        pp.setSearchLogic(SearchLogic.DEFAULT_LOGIC);
        PopulateResponse populateResponse = new PopulateResponse();
        populateResponse.setSuccess(true);
        populateResponse.setFailureCode(null);
        populateResponse.setRowAddedCnt(100);
        doReturn(populateResponse).when(mockLocateService).populateGazetteer(anyString(), anyString(), anyObject(), anyString(),
                anyInt(), anyInt(), anyInt(), anyString(), anyString(), any());
        PopulateResponse response = restLocateService.populateGazetteer(GAZ1, TENANT1, mockAttachment, PROJECTION,
                ADDRESS_COLUMN, X_COLUMN, Y_COLUMN, DELIMITER, DEFAULT_LOGIC);
        assertEquals(response.getSuccess(), true);
    }


    private static LocateConfigProvider createMockConfigProvider(Map<String, LocateConfig> tenantConfigMap) throws Exception {
        LocateConfigProvider mockProvider = mock(LocateConfigProvider.class);
        PopulateParameters pp = new PopulateParameters();
        pp.setTenantName(TENANT1);
        pp.setGazetteerName(GAZ1);
        pp.setProjection(PROJECTION);
        pp.setSearchLogic(SearchLogic.DEFAULT_LOGIC);
        for (Map.Entry<String, LocateConfig> entry : tenantConfigMap.entrySet()) {
            when(mockProvider.getConfig(entry.getKey())).thenReturn(entry.getValue());
        }
        return mockProvider;
    }

    private static LuceneInstance createMockLocateInstance(String gazName, List<Address> searchReturn) {
        LuceneInstance result = new LuceneInstance();
        LocateEngine locateEngine = new LocateEngine();
        locateEngine.setName("Lucene");
        result.setName(gazName);
        result.setEngine(locateEngine);

        IndexSearchFactory mockIndexSearchFactory = mock(IndexSearchFactory.class);
        IndexSearch mockIndexSearch = mock(IndexSearch.class);
        when(mockIndexSearchFactory.getIndex(result)).thenReturn(mockIndexSearch);
        when(mockIndexSearch.search(anyString(), any(SearchLogic.class), anyInt())).thenReturn(searchReturn);

        result.setIndexFactory(mockIndexSearchFactory);
        return result;
    }
}
