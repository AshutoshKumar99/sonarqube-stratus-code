package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ConfigReader;
import com.pb.stratus.core.configuration.CustomerConfigDirHolder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListTemplatesActionTest extends ControllerActionTestBase
{
    
    private ListTemplatesAction action;

    @Before
    public void setUpAction() throws Exception
    {
		CustomerConfigDirHolder mockCustomerConfigDirHolder = mock(CustomerConfigDirHolder.class);
		when(mockCustomerConfigDirHolder.getCustomerConfigDir()).thenReturn(new File("src\\test\\resources\\com\\pb\\stratus\\controller\\action"));
        action = new ListTemplatesAction(mockCustomerConfigDirHolder, "stratus");
    }
    
    @Test
    public void shouldGetListOfEditTemplates()
    {
        getRequest().addParameter("type", "edit");
		assertEquals(2, ((ArrayList) action.createObject(getRequest())).size());
		assertEquals("tpl1xml", ((ArrayList)action.createObject(getRequest())).get(0));
    }

	@Test
	public void shouldGetListOfInfoTemplates()
	{
		getRequest().addParameter("type", "info");
		assertEquals(2, ((ArrayList) action.createObject(getRequest())).size());
		assertEquals("tpl1ts", ((ArrayList)action.createObject(getRequest())).get(0));
	}

    @Test
    public void shouldGetEmptyListIfNoType()
    {
		Object x = action.createObject(getRequest());
		assertTrue(action.createObject(getRequest()) instanceof ArrayList);
		assertEquals(0, ((ArrayList) action.createObject(getRequest())).size());
	}

}
