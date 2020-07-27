package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ConfigReader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ImageActionTest extends ControllerActionTestBase
{
    
    private ImageAction action;

    @Before
    public void setUpAction() throws Exception
    {
        ConfigReader mockConfigReader = mock(ConfigReader.class);
        action = new ImageAction(mockConfigReader);
    }
    
    @Test
    public void shouldGetFileNameFromNameParameter()
    {
        getRequest().addParameter("name", "someName");
        assertEquals("someName", action.getFileName(getRequest()));
    }

    @Test
    public void shouldUseImageGifAsMimeType()
    {
        assertEquals("image/gif", action.getMimeType());
    }

}
