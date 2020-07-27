package com.pb.stratus.controller.legend;

import com.mapinfo.midev.service.mapping.v1.GetMapLegendsRequest;
import com.mapinfo.midev.service.mapping.v1.GetMapLegendsResponse;
import com.mapinfo.midev.service.mapping.v1.Map;
import com.mapinfo.midev.service.mapping.v1.RenderMapResponse;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.infrastructure.CombiningExecutor;
import com.pb.stratus.controller.infrastructure.CombiningExecutorFactory;
import com.pb.stratus.controller.print.content.WMSMap;
import com.pb.stratus.controller.service.MappingService;
import com.pb.stratus.controller.util.MockSupport;
import com.pb.stratus.util.JaxbUtil;
import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import static org.easymock.EasyMock.capture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;
import static org.easymock.EasyMock.*;


public class LegendServiceImplTest
{
    
    private LegendServiceImpl legendService;
    
    private MappingServiceInterface mockMappingWebService;

    private CombiningExecutorFactory mockCombiningExecutorFactory;

    private CombiningExecutor<OverlayLegend> mockCombiningExecutor;
    
    private SecurityContext mockSecurityContext;
    
    private Locale mockLocale;
    
    private ServletRequestAttributes mockRequestAttributes;
    
    @Before
    @SuppressWarnings("unchecked")
    public void setUp()
    {
        mockMappingWebService = mock(MappingServiceInterface.class);
        mockCombiningExecutorFactory = mock(CombiningExecutorFactory.class);
        mockCombiningExecutor = mock(CombiningExecutor.class);
        when(mockCombiningExecutorFactory.createCombiningExecutor(
                OverlayLegend.class)).thenReturn(mockCombiningExecutor);
        legendService = new LegendServiceImpl(mockMappingWebService, 
                mockCombiningExecutorFactory);
        mockLocale = Locale.ENGLISH;
        mockSecurityContext = mock(SecurityContext.class);
        mockRequestAttributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(mockRequestAttributes);
    }
    
    @Test
    public void shouldRequestIndividualOverlaysThroughCombiningExecutor()
    {
        legendService.getLegendData(mockLocale, null, null,"overlay1", "overlay2");
        GetLegendTask expectedTask1 = new GetLegendTask("overlay1", mockLocale,
                mockMappingWebService, new LegendConverter(),mockSecurityContext);
        GetLegendTask expectedTask2 = new GetLegendTask("overlay2", mockLocale, 
                mockMappingWebService, new LegendConverter(),mockSecurityContext);
        verify(mockCombiningExecutor).addTask(argThat(new MatchesDelegate(expectedTask1)));
        verify(mockCombiningExecutor).addTask(argThat(new MatchesDelegate(expectedTask2)));
    }
    // assumes that the actual task added to the CombiningExecutor
    // is a TokenStoreCallableWrapper which delegates to the expected GetLegendTask.
    private static class MatchesDelegate extends ArgumentMatcher<Callable<OverlayLegend>> {
        private GetLegendTask expected;
        public MatchesDelegate(Object expected) {
            this.expected = (GetLegendTask)expected;
        }
        @Override
        public boolean matches(Object o) {
            GetLegendTask legendTask = (GetLegendTask) o;
            return expected.equals(legendTask);
        }
        @Override
        public void describeTo(org.hamcrest.Description description) {
            description.appendText("GetLegendTask: " + expected.getOverlayName());
        }
    }
    
    private List<OverlayLegend> createDummyOverlayLegend(String legendItemNames[])
    {
        List<LegendItem> legendList = new ArrayList<LegendItem>();
        for(int i=0; i<legendItemNames.length;i++)
        {
            LegendItem item = mock(LegendItem.class);
            when(item.getTitle()).thenReturn(legendItemNames[i]);
            legendList.add(item);
        }
        List<OverlayLegend> overlayLegendList = new ArrayList<OverlayLegend>();
        OverlayLegend overlayLegend = new OverlayLegend("overlay", legendList);
        overlayLegendList.add(overlayLegend);
        return overlayLegendList;
    }
    
    @Test
    public void shouldGetOrderedOverlays() throws Exception
    {
        String legendItemNames[] = {"OpenSpace", "ConservationArea", "ListedBuildings"};
        List<OverlayLegend> mockOverlayLegend = createDummyOverlayLegend(legendItemNames);
        when(mockCombiningExecutor.getResults()).thenReturn(mockOverlayLegend);
        LegendData legendData = legendService.getLegendData(mockLocale, null,null, "boverlay");
        OverlayLegend legendItemsData = legendData.getOverlayLegends().get(0);        
        assertEquals("OpenSpace", legendItemsData.getLegendItems().get(0).getTitle());
        assertEquals("ConservationArea", legendItemsData.getLegendItems().get(1).getTitle());
        assertEquals("ListedBuildings", legendItemsData.getLegendItems().get(2).getTitle());       
    }
    
    @Test
    public void shouldReturnLegendsReturnedFromCombiningExecutor() 
            throws Exception
    {
        List<OverlayLegend> mockLegends = Collections.singletonList(
                mock(OverlayLegend.class));
        
        when(mockCombiningExecutor.getResults()).thenReturn(mockLegends);
        LegendData actualLegendData = legendService.getLegendData(mockLocale, null,null,
                "overlay1");
        assertEquals(new LegendData(mockLegends), actualLegendData);
    }

    @Test
    public void shouldReturnWmsLegendsData() throws Exception
    {
        List<WMSMap> wmsMapList = new ArrayList<WMSMap>();
        WMSMap wmsMock1 = mock(WMSMap.class);
        WMSMap wmsMock2 = mock(WMSMap.class);
        wmsMapList.add(wmsMock1);
        wmsMapList.add(wmsMock2);
        LegendData wmsLegendData = legendService.getWMSLegendData(wmsMapList);
        assertEquals(true, wmsLegendData.getOverlayLegends().size()>0);
    }

    @Test
    public void shouldReturnThematicLegendData() throws Exception
    {
        Map mockMap = mock(Map.class);
        MockSupport mockSupport = new MockSupport();
        MappingServiceInterface mockMappingService = mockSupport.createMock(
                MappingServiceInterface.class);
        GetMapLegendsResponse getMapLegendsResponse = createMapLegendsResponse();
        Capture<GetMapLegendsRequest> capture = new Capture<GetMapLegendsRequest>();
        expect(mockMappingService.getMapLegends(capture(capture))).andReturn(
                getMapLegendsResponse);
        replay(mockMappingService);
        legendService = new LegendServiceImpl(mockMappingService,
            mockCombiningExecutorFactory);
        LegendData thematicLegendData = legendService.getThematicLegendData("mockThematicMap",mockMap);
        assertTrue(thematicLegendData.getOverlayLegends().size()>0);
        assertEquals("mockThematicMap",thematicLegendData.getOverlayLegends().get(0).getOverlayName());
        assertEquals("RangedTheme",thematicLegendData.getOverlayLegends().get(0).getLegendItems().get(0).getLegendType());
    }

    private GetMapLegendsResponse createMapLegendsResponse() throws Exception{

        GetMapLegendsResponse response = JaxbUtil.createObject(
                "getMapLegendsResponse.xml",
                GetMapLegendsResponse.class, LegendServiceImplTest.class);

        return response;
    }



}
