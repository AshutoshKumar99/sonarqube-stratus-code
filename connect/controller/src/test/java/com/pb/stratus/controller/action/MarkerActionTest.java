package com.pb.stratus.controller.action;

import com.pb.stratus.controller.marker.MarkerRepository;
import com.pb.stratus.controller.marker.MarkerType;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class MarkerActionTest extends ControllerActionTestBase
{
    
    private MarkerAction action;
    
    private byte[] mockData;

    private MarkerRepository mockRepo;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        mockRepo = mock(MarkerRepository.class);
        mockData = new byte[] {1, 5, 3, 7, 54, 35, 6, 56, 36, 19};
        InputStream is = new ByteArrayInputStream(mockData);
        when(mockRepo.getMarkerImage(any(String.class), 
                any(MarkerType.class))).thenReturn(is);
        getRequest().addParameter("name", "someMarker.jpg");
        getRequest().addParameter("type", "marker");
        action = new MarkerAction(mockRepo);
    }
    
    @Test
    public void shouldCopyMarkerToResponse() throws Exception
    {
        action.execute(getRequest(), getResponse());
        assertArrayEquals(mockData, getResponse().getContentAsByteArray());
    }
    
    @Test
    public void shouldSetContentType() throws Exception
    {
        action.execute(getRequest(), getResponse());
        assertEquals("image/jpeg", getResponse().getContentType());
    }
    
    @Test
    public void shouldPassNameAndTypeToMarkerRepo() throws Exception
    {
        action.execute(getRequest(), getResponse());
        verify(mockRepo).getMarkerImage("someMarker.jpg", MarkerType.MARKER);
    }
    
}
