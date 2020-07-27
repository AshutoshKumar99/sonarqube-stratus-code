package com.pb.stratus.controller.catalog;

import com.mapinfo.midev.service.mapping.v1.*;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.mapinfo.midev.service.table.v1.NamedTable;
import com.pb.stratus.controller.infrastructure.CombiningExecutorFactory;
import com.pb.stratus.core.component.MappingServiceUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.same;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MappingServiceUtilities.class})
@PowerMockIgnore("javax.management.*")
public class CatalogServiceImplTest
{

	private CatalogServiceImpl catalogService;

	private MappingServiceInterface mockMappingWebService;

    private ExecutorService executorService;

	@Before
	public void setUp() throws Exception
	{
		mockMappingWebService = mock(MappingServiceInterface.class);
        executorService = Executors.newFixedThreadPool(1);
		CombiningExecutorFactory mockFactory = new CombiningExecutorFactory(executorService);
		catalogService = new CatalogServiceImpl(mockMappingWebService, mockFactory, "myTenantName");

        //thread storage expectations need to be set for test
        ServletRequestAttributes requestAttributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        SecurityContextHolder.setContext(mock(SecurityContext.class));
	}

	@After
	public void tearDown()
	{
		executorService.shutdown();

        //clean up thread storage used in test
        RequestContextHolder.resetRequestAttributes();
        SecurityContextHolder.clearContext();
	}

	@Test
	public void testGetTableNames() throws ServiceException
	{
		mockMappingServiceUtilities();

		Map<String, List<String>> result = catalogService.getTableNames(Arrays.asList("/myTenantName/Map1", "/myTenantName/Map2"));
		assertEquals(2, result.size());
		assertEquals(Arrays.asList("Table1", "Table2"), result.get("/myTenantName/Map1"));
		assertEquals(Arrays.asList("Table3", "Table4"), result.get("/myTenantName/Map2"));
	}

	@Test
	public void testUnsupportedLayerTypes() throws ServiceException
	{
		mockMappingServiceUtilities();

		Map<String, List<String>> result = catalogService.getTableNames(Arrays.asList("/myTenantName/Map3"));
		assertEquals(1, result.size());
		assertTrue(result.get("/myTenantName/Map3").isEmpty());
	}

	private void mockMappingServiceUtilities() throws ServiceException
	{
		mockStatic(MappingServiceUtilities.class);
		when(MappingServiceUtilities.getLayersForMap(same(mockMappingWebService), Matchers.anyString())).thenAnswer(new Answer()
		{
			public Object answer(InvocationOnMock invocation)
			{
				Object[] args = invocation.getArguments();
				String mapName = (String) args[1];
				if (mapName.equals("/myTenantName/Map1"))
				{
					FeatureLayer layer1 = new FeatureLayer();
					NamedTable table1 = new NamedTable();
					table1.setName("/myTenantName/Table1");
					layer1.setTable(table1);

					FeatureLayer layer2 = new FeatureLayer();
					NamedTable table2 = new NamedTable();
					table2.setName("/myTenantName/Table2");
					layer2.setTable(table2);

					return Arrays.asList(layer1, layer2);
				}
				else if (mapName.equals("/myTenantName/Map2"))
				{
					FeatureLayer layer3 = new FeatureLayer();
					NamedTable table3 = new NamedTable();
					table3.setName("/myTenantName/Table3");
					layer3.setTable(table3);

					FeatureLayer layer4 = new FeatureLayer();
					NamedTable table4 = new NamedTable();
					table4.setName("/myTenantName/Table4");
					layer4.setTable(table4);

					return Arrays.asList(layer3, layer4);
				}
				else if (mapName.equals("/myTenantName/Map3"))
				{
					PieLayer layer1 = new PieLayer();
					GraduatedSymbolLayer layer2 = new GraduatedSymbolLayer();
					GeometryLayer layer3 = new GeometryLayer();
					LabelLayer layer4 = new LabelLayer();
					GridLayer layer5 = new GridLayer();
					BarLayer layer6 = new BarLayer();
					return Arrays.asList(layer1, layer2, layer3, layer4, layer5, layer6);
				}
				return Collections.emptyList();
			}
		});
		when(MappingServiceUtilities.getTables(same(mockMappingWebService), Matchers.any(Layer.class))).thenAnswer(new Answer()
		{
			public Object answer(InvocationOnMock invocation)
			{
				try
				{
					return invocation.callRealMethod();
				}
				catch (Throwable e)
				{
					throw new RuntimeException(e);
				}
			}
		});
	}
}
