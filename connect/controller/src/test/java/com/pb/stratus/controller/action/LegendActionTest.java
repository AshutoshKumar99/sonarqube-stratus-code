package com.pb.stratus.controller.action;

import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.controller.json.LegendDataHolder;
import com.pb.stratus.controller.legend.LegendData;
import com.pb.stratus.controller.legend.LegendService;
import com.pb.stratus.controller.legend.OverlayLegend;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LegendActionTest extends ControllerActionTestBase
{
    
    private Locale mockLocale;
    private LegendAction legendAction;
    private LegendData mockLegendData;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        mockLocale = Locale.JAPANESE;
        LocaleResolver.setLocale(mockLocale);
        mockLegendData = mock(LegendData.class);
        LegendService mockLegendService = mock(LegendService.class);
        getRequest().addParameter("overlays", "overlay1,overlay2");
        when(mockLegendService.getLegendData(mockLocale, null,null,
                "overlay1", "overlay2")).thenReturn(mockLegendData);
        legendAction = new LegendAction(mockLegendService, 
                "/someBasePath");
    }
    
    @Test
    public void shouldCreateLegendDataFromLegendService() throws Exception
    {
        LegendDataHolder legendDataHolder = (LegendDataHolder) 
                legendAction.createObject(getRequest());
        assertEquals(mockLegendData, legendDataHolder.getLegendData());
    }
    
    @Test
    public void shouldCreateCorrectIconUrl() throws Exception
    {
        getRequest().setContextPath("/someContext");
        LegendDataHolder legendDataHolder = (LegendDataHolder) 
                legendAction.createObject(getRequest());
        //XXX /controller is essentially a server configuration setting
        //    we shouldn't hard-code it.
        assertEquals("/controller/someBasePath?" 
                + "overlays=overlay1,overlay2", legendDataHolder.getIconUrl());
    }

    @Test
    public void shouldReturnLegendsInTheSameOrderAsInTheRequest() throws
            IOException, ServletException
    {
        // need a new request object, can't use the one used in setup.
        MockHttpServletRequest request =  new MockHttpServletRequest();
        request.setContextPath("/someContext");
        // overlay order in request.
        request.addParameter("overlays", "overlay2,overlay1,overlay3");
        // overlay order after merging from cache and non-cache data
        LegendData data  = getLegendData("overlay1", "overlay2", "overlay3");
        LegendService mockLegendService = mock(LegendService.class);
        when(mockLegendService.getLegendData(mockLocale, null,null,
                new String[]{ "overlay2", "overlay1", "overlay3"})).thenReturn(data);
        legendAction = new LegendAction(mockLegendService, "/someBasePath");
        LegendDataHolder legendDataHolder =
                (LegendDataHolder)legendAction.createObject(request);
        LegendData actual = legendDataHolder.getLegendData();
        // check the order of the overlays
        assertEquals("overlay2", actual.getOverlayLegends().get(0).getTitle());
        assertEquals("overlay1", actual.getOverlayLegends().get(1).getTitle());
        assertEquals("overlay3", actual.getOverlayLegends().get(2).getTitle());

    }
    
    private LegendData getLegendData(String...legendOrder) 
    {
        List<OverlayLegend> overlayLegends = new ArrayList<OverlayLegend>();
        for(String overlay : legendOrder) {
            OverlayLegend mockLegend = mock(OverlayLegend.class);
            when(mockLegend.getTitle()).thenReturn(overlay);
            overlayLegends.add(mockLegend);
        }
        return new LegendData(overlayLegends);
    }
    
}
