package com.pb.gazetteer.webservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.InputStream;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import com.pb.gazetteer.search.SearchLogic;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.pb.gazetteer.Address;
import com.pb.gazetteer.IndexSearch;
import com.pb.gazetteer.IndexSearchFactory;
import com.pb.gazetteer.LocateConfig;
import com.pb.gazetteer.LocateInstance;
import com.pb.gazetteer.PopulateParameters;
import com.pb.gazetteer.PopulateResponse;
import com.pb.gazetteer.SearchParameters;
import com.pb.gazetteer.lucene.LuceneIndexGenerator;
import com.pb.gazetteer.lucene.LuceneInstance;
import com.sun.xml.ws.developer.StreamingDataHandler;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SingleLineAddress.class, JaxwsBugFixInputStream.class, LuceneIndexGenerator.class})
@PowerMockIgnore("javax.management.*")
public class SingleLineAddressTest {
    private static final String TENANT1 = "Tenant1";
    private static final String TENANT2 = "Tenant2";
    private static final String GAZ1 = "Gaz1";
    private static final String GAZ2 = "Gaz2";

    private List<Address> m_testTenant1TestGaz1ExpectedResult = new ArrayList<Address>();
    private List<Address> m_testTenant1TestGaz2ExpectedResult = new ArrayList<Address>();
    private List<Address> m_testTenant2TestGaz1ExpectedResult = new ArrayList<Address>();
    private List<Address> m_testTenant2TestGaz2ExpectedResult = new ArrayList<Address>();
    private LuceneInstance locateInstance1;
    LocateConfigProvider provider;

    private SingleLineAddress m_slaUnderTest = new SingleLineAddress();

    @Before
    public void setup() throws Exception {
        Map<String, LocateConfig> tenantConfigMap = new HashMap<String, LocateConfig>();

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

        WebServiceContext mockWebServiceContext = mock(WebServiceContext.class, Mockito.RETURNS_DEEP_STUBS);
        ServletContext mockServletContext = mock(ServletContext.class);
        when(mockWebServiceContext.getMessageContext().get(MessageContext.SERVLET_CONTEXT)).thenReturn(mockServletContext);
        when(mockServletContext.getAttribute(ApplicationContextListener.PROVIDER_KEY)).thenReturn(provider);
        Whitebox.setInternalState(m_slaUnderTest, "m_context", mockWebServiceContext);
    }

    @Test
    public void testSearch_noGazName() throws Exception {
        SearchParameters sp = new SearchParameters();
        sp.setTenantName(TENANT1);
        sp.setGazetteerName(null);
        sp.setMaxRecords(5);
        sp.setSearchString("testQuery");

        List<Address> result = m_slaUnderTest.search(sp);
        assertSame(result, m_testTenant1TestGaz2ExpectedResult);
    }

    @Test
    public void testSearch_withGazName() throws Exception {
        //assert tenant 1
        SearchParameters sp1 = new SearchParameters();
        sp1.setTenantName(TENANT1);
        sp1.setGazetteerName(GAZ1);
        sp1.setMaxRecords(5);
        sp1.setSearchString("testQuery");
        sp1.setSearchLogic(SearchLogic.DEFAULT_LOGIC);
        List<Address> result1 = m_slaUnderTest.search(sp1);
        assertSame(result1, m_testTenant1TestGaz1ExpectedResult);

        //assert tenant 2
        SearchParameters sp2 = new SearchParameters();
        sp2.setTenantName(TENANT2);
        sp2.setGazetteerName(GAZ1);
        sp2.setMaxRecords(5);
        sp2.setSearchString("testQuery");
        List<Address> result2 = m_slaUnderTest.search(sp2);
        assertSame(result2, m_testTenant2TestGaz1ExpectedResult);
    }

    @Test
    public void testPopulateGazetteer_success() throws Exception {

        mockStatic(LuceneIndexGenerator.class);
        StreamingDataHandler mockSDH = mock(StreamingDataHandler.class);
        InputStream mockIS = mock(InputStream.class);
        when(mockSDH.readOnce()).thenReturn(mockIS);

        String configFile = "src\\test\\resources\\com\\pb\\gazetteer\\testConfiguration.xml";
        when(provider.getLocateConfigPath(TENANT1)).thenReturn(configFile);

        PopulateParameters pp = new PopulateParameters();
        pp.setTenantName(TENANT1);
        pp.setGazetteerName(GAZ1);
        pp.setProjection("epsg:27700");
        pp.setSearchLogic(SearchLogic.DEFAULT_LOGIC);


        PopulateResponse populateResponse = new PopulateResponse();
        when(LuceneIndexGenerator.generate(same(locateInstance1), same(pp), any(JaxwsBugFixInputStream.class))).thenReturn(populateResponse);
        JaxwsBugFixInputStream mockFixedIS = mock(JaxwsBugFixInputStream.class);
        whenNew(JaxwsBugFixInputStream.class).withArguments(mockIS).thenReturn(mockFixedIS);

        PopulateResponse result = m_slaUnderTest.populateGazetteer(pp, mockSDH);


        assertSame(result, populateResponse);
        verifyNew(JaxwsBugFixInputStream.class).withArguments(mockIS);
        verify(mockSDH).close();
        verify(mockFixedIS).close();
    }

    @Test(expected = ServerException.class)
    public void testPopulateGazetteer_error() throws Exception {
        mockStatic(LuceneIndexGenerator.class);
        StreamingDataHandler mockSDH = mock(StreamingDataHandler.class);
        InputStream mockIS = mock(InputStream.class);
        when(mockSDH.readOnce()).thenReturn(mockIS);

        PopulateParameters pp = new PopulateParameters();
        pp.setTenantName(TENANT1);
        pp.setGazetteerName(GAZ1);


        when(LuceneIndexGenerator.generate(same(locateInstance1), same(pp), any(JaxwsBugFixInputStream.class))).thenThrow(new LocateException("something went wrong"));
        JaxwsBugFixInputStream mockFixedIS = mock(JaxwsBugFixInputStream.class);
        whenNew(JaxwsBugFixInputStream.class).withArguments(mockIS).thenReturn(mockFixedIS);

        m_slaUnderTest.populateGazetteer(pp, mockSDH);
    }

    @Test
    public void testGetGazetteerNames() throws Exception {
        //assert tenant 1
        GazetteerNames result1 = m_slaUnderTest.getGazetteerNames(TENANT1);
        assertEquals(GAZ2, result1.getDefaultGazetteerName());
        List<GazetteerInstance> gazetteerInstances = result1.getGazetteerInstances();
        assertEquals(2, gazetteerInstances.size());
        assertEquals(GAZ1, gazetteerInstances.get(0).getGazetteerName());
        assertEquals(GAZ2, gazetteerInstances.get(1).getGazetteerName());

        //assert tenant 2
        GazetteerNames result2 = m_slaUnderTest.getGazetteerNames(TENANT2);
        List<GazetteerInstance> gazetteerInstances2 = result2.getGazetteerInstances();
        assertEquals(2, gazetteerInstances2.size());
        assertEquals(GAZ1, gazetteerInstances2.get(0).getGazetteerName());
        assertEquals(GAZ2, gazetteerInstances2.get(1).getGazetteerName());
    }

    private static LocateConfigProvider createMockConfigProvider(Map<String, LocateConfig> tenantConfigMap) throws Exception {
        LocateConfigProvider mockProvider = mock(LocateConfigProvider.class);
        PopulateParameters pp = new PopulateParameters();
        pp.setTenantName(TENANT1);
        pp.setGazetteerName(GAZ1);
        pp.setProjection("epsg:27700");
        pp.setSearchLogic(SearchLogic.DEFAULT_LOGIC);
        for (Map.Entry<String, LocateConfig> entry : tenantConfigMap.entrySet()) {
            when(mockProvider.getConfig(entry.getKey())).thenReturn(entry.getValue());
            //when(mockProvider.getModifiedLocateConfig(pp).thenReturn(entry.getValue());
        }
        return mockProvider;
    }

    private static LuceneInstance createMockLocateInstance(String gazName, List<Address> searchReturn) {
        LuceneInstance result = new LuceneInstance();
        result.setName(gazName);

        IndexSearchFactory mockIndexSearchFactory = mock(IndexSearchFactory.class);
        IndexSearch mockIndexSearch = mock(IndexSearch.class);
        when(mockIndexSearchFactory.getIndex(result)).thenReturn(mockIndexSearch);
        when(mockIndexSearch.search(anyString(), any(SearchLogic.class), anyInt())).thenReturn(searchReturn);

        result.setIndexFactory(mockIndexSearchFactory);
        return result;
    }
}
