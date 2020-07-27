package com.pb.gazetteer.rest;

import com.pb.gazetteer.*;
import com.pb.gazetteer.lucene.LuceneIndexGenerator;
import com.pb.gazetteer.lucene.LuceneInstance;
import com.pb.gazetteer.search.SearchLogic;
import com.pb.gazetteer.webservice.ApplicationContextListener;
import com.pb.gazetteer.webservice.JaxwsBugFixInputStream;
import com.pb.gazetteer.webservice.LocateConfigProvider;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.MessageContextImpl;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.activation.DataHandler;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.*;
import static org.powermock.api.mockito.PowerMockito.*;

import org.apache.cxf.jaxrs.ext.MessageContextImpl;

/**
 * Created by JU002AH on 4/22/2019.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({RESTLocateService.class, LuceneIndexGenerator.class, ByteArrayInputStream.class,JaxwsBugFixInputStream.class, IOUtils.class})
@PowerMockIgnore("javax.management.*")
public class LocateServiceTest {

    private static final String TENANT1 = "Tenant1";
    private static final String GAZ1 = "Gaz1";
    private static final String GAZ2 = "Gaz2";
    private static final String PROJECTION = "epsg:27700";
    private static final int ADDRESS_COLUMN = 0;
    private static final int X_COLUMN = 1;
    private static final int Y_COLUMN = 2;
    private static final String DELIMITER = ",";
    private static final String DEFAULT_LOGIC = "DEFAULT_LOGIC";
    @Mock
    private ByteArrayInputStream mockByteArrayClass;

    @Context
    private MessageContextImpl messageContext;

    private List<Address> m_testTenant1TestGaz1ExpectedResult = new ArrayList<Address>();
    private List<Address> m_testTenant1TestGaz2ExpectedResult = new ArrayList<Address>();

    private LuceneInstance locateInstance1;
    LocateConfigProvider provider;
    LocateService locateService = new LocateService();
    MessageContextImpl mockWebServiceContext;



    @Before
    public void setup() throws Exception {
        Map<String, LocateConfig> tenantConfigMap = new HashMap<String, LocateConfig>();

        //tenant 1 setup
        //tenant 1 setup
        LocateConfig locConfig1 = new LocateConfig();
        List<LocateInstance> locInstances1 = new ArrayList<LocateInstance>();
        locateInstance1 = createMockLocateInstance(GAZ1, m_testTenant1TestGaz1ExpectedResult);
        locInstances1.add(locateInstance1);
        locInstances1.add(createMockLocateInstance(GAZ2, m_testTenant1TestGaz2ExpectedResult));
        locConfig1.setLocateInstances(locInstances1);
        locConfig1.setDefaultInstance(locInstances1.get(1));
        tenantConfigMap.put(TENANT1, locConfig1);
        provider = createMockConfigProvider(tenantConfigMap);
        mockWebServiceContext = mock(MessageContextImpl.class, Mockito.RETURNS_MOCKS);
        ServletContext mockServletContext = mock(ServletContext.class);
        when(mockWebServiceContext.getServletContext()).thenReturn(mockServletContext);
        when(mockServletContext.getAttribute(ApplicationContextListener.PROVIDER_KEY)).thenReturn(provider);


    }

    @Test
    public void populateGazetteer() throws Exception {

        byte[] mockByteArray = new byte[] { 1, 2, 23, 33 };
        Attachment mockAttachment = mock(Attachment.class);
        DataHandler mockdh = mock(DataHandler.class);
        InputStream inputMock = mock(InputStream.class);
        mockStatic(LuceneIndexGenerator.class);
        when(mockAttachment.getDataHandler()).thenReturn(mockdh);
        when(mockdh.getInputStream()).thenReturn(inputMock);
        mockStatic(IOUtils.class);
        when(IOUtils.toByteArray(any(InputStream.class))).thenReturn(mockByteArray);
        String configFile = "src\\test\\resources\\com\\pb\\gazetteer\\testConfiguration.xml";
        when(provider.getLocateConfigPath(TENANT1)).thenReturn(configFile);
        PopulateParameters pp = new PopulateParameters();
        pp.setTenantName(TENANT1);
        pp.setGazetteerName(GAZ1);
        pp.setProjection("epsg:27700");
        pp.setSearchLogic(SearchLogic.DEFAULT_LOGIC);
        PopulateResponse populateResponse = new PopulateResponse();

        when(LuceneIndexGenerator.generate(any(), any(), any(InputStream.class))).thenReturn(populateResponse);

        PopulateResponse result = locateService.populateGazetteer(GAZ1, TENANT1, mockAttachment, PROJECTION,
                ADDRESS_COLUMN, X_COLUMN, Y_COLUMN, DELIMITER, DEFAULT_LOGIC, mockWebServiceContext);
        assertSame(result, populateResponse);
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
