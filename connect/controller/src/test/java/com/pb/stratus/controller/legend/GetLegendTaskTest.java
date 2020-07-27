package com.pb.stratus.controller.legend;

import com.mapinfo.midev.service.mapping.v1.GetNamedMapLegendsRequest;
import com.mapinfo.midev.service.mapping.v1.GetNamedMapLegendsResponse;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class GetLegendTaskTest
{
    
    private MappingServiceInterface mockMappingWebservice;
    
    private GetLegendTask getLegendTask;

    private LegendConverter mockLegendConverter;

    private SecurityContext mockSecurityContext;
    
    private ServletRequestAttributes mockRequestAttributes;

    @Before
    public void setUp()
    {
        mockMappingWebservice = mock(MappingServiceInterface.class);
        mockLegendConverter = mock(LegendConverter.class);
        mockSecurityContext = mock(SecurityContext.class);
        mockRequestAttributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(mockRequestAttributes);
        getLegendTask = new GetLegendTask("overlay1", Locale.ENGLISH, 
                mockMappingWebservice, mockLegendConverter,mockSecurityContext);
    }
    
    @Test
    public void shouldRequestExpectedOverlay() throws Exception
    {
        GetNamedMapLegendsRequest actualRequest 
                = invokeCallAndCaptureWebserviceRequest();
        assertEquals("overlay1", actualRequest.getNamedMap());
    }

     @Ignore
    @Test
    public void shouldRequestExpectedIconSize() throws Exception
    {
        GetNamedMapLegendsRequest actualRequest = invokeCallAndCaptureWebserviceRequest();
       assertEquals(16,actualRequest.getLegendImageHeight());
        assertEquals(16, actualRequest.getLegendImageWidth());

    }


    @Test
    public void shouldRequestExpectedIconType() throws Exception
    {
        GetNamedMapLegendsRequest actualRequest 
                = invokeCallAndCaptureWebserviceRequest();
        if(actualRequest.getImageMimeType()!=null){
            assertEquals("image/png", actualRequest.getImageMimeType());
        }
    }
    
    private GetNamedMapLegendsRequest invokeCallAndCaptureWebserviceRequest() 
            throws Exception
    {
        ArgumentCaptor<GetNamedMapLegendsRequest> arg = ArgumentCaptor
                .forClass(GetNamedMapLegendsRequest.class);
        getLegendTask.call();
        verify(mockMappingWebservice).getNamedMapLegends(arg.capture());
        return arg.getValue();
    }
    
    @Test
    public void shouldUseLegendConverter() throws Exception
    {
        OverlayLegend mockLegend = mock(OverlayLegend.class);
        GetNamedMapLegendsResponse mockResponse 
                = mock(GetNamedMapLegendsResponse.class);
        when(mockLegendConverter.convert(any(String.class),
                any(GetNamedMapLegendsResponse.class))).thenReturn(mockLegend);
        when(mockMappingWebservice.getNamedMapLegends(
                any(GetNamedMapLegendsRequest.class))).thenReturn(mockResponse);
        OverlayLegend actualLegend = getLegendTask.call();
        verify(mockLegendConverter).convert("overlay1", mockResponse);
        assertEquals(mockLegend, actualLegend);
    }
    
}
