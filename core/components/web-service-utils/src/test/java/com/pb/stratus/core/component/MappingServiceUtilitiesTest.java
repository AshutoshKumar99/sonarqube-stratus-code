/*****************************************************************************
 *       Copyright © 2011, Pitney Bowes Software Inc.
 *       All  rights reserved.
 *       Confidential Property of Pitney Bowes Software Inc.
 *
 * $Author$
 * $Revision$
 * $LastChangedDate$
 *
 * $HeadURL$
 *****************************************************************************/
package com.pb.stratus.core.component;

import com.mapinfo.midev.service.mapping.v1.*;
import com.mapinfo.midev.service.mapping.ws.v1.MappingServiceInterface;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.mapinfo.midev.service.table.v1.NamedTable;
import com.mapinfo.midev.service.table.v1.Table;
import com.pb.stratus.core.component.MappingServiceUtilities;
import junit.framework.TestCase;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public final class MappingServiceUtilitiesTest extends TestCase
{
	public void testGetTable_PieLayer() throws ServiceException
	{
		MappingServiceInterface mappingService = mock(MappingServiceInterface.class);
		PieLayer layer = new PieLayer();
		layer.setTable(mock(Table.class));

		List<Table> tableList = MappingServiceUtilities.getTables(mappingService, layer);
		assertEquals(1, tableList.size());
		assertEquals(layer.getTable(), tableList.get(0));
	}

	public void testGetTable_GraduatedSymbolLayer() throws ServiceException
	{
		MappingServiceInterface mappingService = mock(MappingServiceInterface.class);
		GraduatedSymbolLayer layer = new GraduatedSymbolLayer();
		layer.setTable(mock(Table.class));

		List<Table> tableList = MappingServiceUtilities.getTables(mappingService, layer);
		assertEquals(1, tableList.size());
		assertEquals(layer.getTable(), tableList.get(0));
	}

	public void testGetTable_GeometryLayer() throws ServiceException
	{
		MappingServiceInterface mappingService = mock(MappingServiceInterface.class);
		GeometryLayer layer = new GeometryLayer();

		List<Table> tableList = MappingServiceUtilities.getTables(mappingService, layer);
		assertEquals(0, tableList.size());
	}

	public void testGetTable_GridLayer() throws ServiceException
	{
		MappingServiceInterface mappingService = mock(MappingServiceInterface.class);
		GridLayer layer = new GridLayer();
		layer.setTable(mock(Table.class));

		List<Table> tableList = MappingServiceUtilities.getTables(mappingService, layer);
		assertEquals(1, tableList.size());
		assertEquals(layer.getTable(), tableList.get(0));
	}

	public void testGetTable_FeatureLayer() throws ServiceException
	{
		MappingServiceInterface mappingService = mock(MappingServiceInterface.class);
		FeatureLayer layer = new FeatureLayer();
		layer.setTable(mock(Table.class));

		List<Table> tableList = MappingServiceUtilities.getTables(mappingService, layer);
		assertEquals(1, tableList.size());
		assertEquals(layer.getTable(), tableList.get(0));
	}

	public void testGetTable_BarLayer() throws ServiceException
	{
		MappingServiceInterface mappingService = mock(MappingServiceInterface.class);
		BarLayer layer = new BarLayer();
		layer.setTable(mock(Table.class));

		List<Table> tableList = MappingServiceUtilities.getTables(mappingService, layer);
		assertEquals(1, tableList.size());
		assertEquals(layer.getTable(), tableList.get(0));
	}

	public void testGetTable_LabelLayer_singleLabelSource() throws ServiceException
	{
		MappingServiceInterface mappingService = mock(MappingServiceInterface.class);
		LabelLayer layer = new LabelLayer();
		LabelSource labelSource = new LabelSource();
		labelSource.setTable(mock(Table.class));
		layer.getLabelSource().add(labelSource);

		List<Table> tableList = MappingServiceUtilities.getTables(mappingService, layer);
		assertEquals(1, tableList.size());
		assertEquals(labelSource.getTable(), tableList.get(0));
	}

	public void testGetTable_LabelLayer_noLabelSource() throws ServiceException
	{
		MappingServiceInterface mappingService = mock(MappingServiceInterface.class);
		LabelLayer layer = new LabelLayer();
		List<Table> tableList = MappingServiceUtilities.getTables(mappingService, layer);
		assertEquals(0, tableList.size());
	}

	public void testGetTable_LabelLayer_multipleLabelSource() throws ServiceException
	{
		MappingServiceInterface mappingService = mock(MappingServiceInterface.class);
		LabelLayer layer = new LabelLayer();

		LabelSource labelSource1 = new LabelSource();
		labelSource1.setTable(mock(Table.class));
		layer.getLabelSource().add(labelSource1);

		LabelSource labelSource2 = new LabelSource();
		labelSource2.setTable(mock(Table.class));
		layer.getLabelSource().add(labelSource2);

		List<Table> tableList = MappingServiceUtilities.getTables(mappingService, layer);
		assertEquals(2, tableList.size());
		assertEquals(labelSource1.getTable(), tableList.get(0));
		assertEquals(labelSource2.getTable(), tableList.get(1));
	}

	public void testGetTable_NamedLayer() throws ServiceException
	{
		MappingServiceInterface mockMappingService = mock(MappingServiceInterface.class);
		DescribeNamedLayerResponse res = new DescribeNamedLayerResponse();
		FeatureLayer featureLayer = new FeatureLayer();
		featureLayer.setTable(mock(Table.class));
		res.getLayer().add(featureLayer);
		when(mockMappingService.describeNamedLayer(any(DescribeNamedLayerRequest.class))).thenReturn(res);

		NamedLayer layer = new NamedLayer();
		layer.setName("/MyLayers/MyLayer");
		List<Table> tableList = MappingServiceUtilities.getTables(mockMappingService, layer);
		assertEquals(1, tableList.size());
		assertEquals(featureLayer.getTable(), tableList.get(0));

		ArgumentCaptor<DescribeNamedLayerRequest> requestCaptor = ArgumentCaptor.forClass(DescribeNamedLayerRequest.class);
		verify(mockMappingService).describeNamedLayer(requestCaptor.capture());

		assertEquals("/MyLayers/MyLayer", requestCaptor.getValue().getNamedLayer());
	}

	public void testGetTablesForMap() throws ServiceException
	{
		MappingServiceInterface mockMappingService = mock(MappingServiceInterface.class);

		DescribeNamedMapResponse dnmr = new DescribeNamedMapResponse();
		Map map = new Map();
		FeatureLayer layer1 = new FeatureLayer();
		NamedTable table1 = new NamedTable();
		table1.setName("/NamedTables/Table1");
		layer1.setTable(table1);
		FeatureLayer layer2 = new FeatureLayer();
		NamedTable table2 = new NamedTable();
		table2.setName("/NamedTables/Table2");
		layer2.setTable(table2);
		map.getLayer().add(layer1);
		map.getLayer().add(layer2);
		dnmr.setMap(map);

		when(mockMappingService.describeNamedMap(any(DescribeNamedMapRequest.class))).thenReturn(dnmr);

		List<String> tableList = MappingServiceUtilities.getTablesForMap(mockMappingService, "/NamedMaps/MyMap");
		assertEquals(2, tableList.size());
		assertEquals("/NamedTables/Table1", tableList.get(0));
		assertEquals("/NamedTables/Table2", tableList.get(1));

		ArgumentCaptor<DescribeNamedMapRequest> requestCaptor = ArgumentCaptor.forClass(DescribeNamedMapRequest.class);
		verify(mockMappingService).describeNamedMap(requestCaptor.capture());

		assertEquals("/NamedMaps/MyMap", requestCaptor.getValue().getNamedMap());
	}

	public void testGetLayersForMap_NamedLayer() throws ServiceException
	{
		MappingServiceInterface mockMappingService = mock(MappingServiceInterface.class);
		DescribeNamedLayerResponse res = new DescribeNamedLayerResponse();
		FeatureLayer featureLayer = new FeatureLayer();
		featureLayer.setTable(mock(Table.class));
		res.getLayer().add(featureLayer);
		when(mockMappingService.describeNamedLayer(any(DescribeNamedLayerRequest.class))).thenReturn(res);

		NamedLayer layer = new NamedLayer();
		layer.setName("/MyLayers/MyLayer");

		DescribeNamedMapResponse dnmr = new DescribeNamedMapResponse();
		Map map = new Map();
		map.getLayer().add(layer);
		dnmr.setMap(map);
		when(mockMappingService.describeNamedMap(any(DescribeNamedMapRequest.class))).thenReturn(dnmr);

		List<Layer> layerList = MappingServiceUtilities.getLayersForMap(mockMappingService, "/MyMaps/MyMap");
		assertEquals(1, layerList.size());
		assertEquals(featureLayer, layerList.get(0));

		ArgumentCaptor<DescribeNamedLayerRequest> requestCaptor1 = ArgumentCaptor.forClass(DescribeNamedLayerRequest.class);
		verify(mockMappingService).describeNamedLayer(requestCaptor1.capture());

		assertEquals("/MyLayers/MyLayer", requestCaptor1.getValue().getNamedLayer());

		ArgumentCaptor<DescribeNamedMapRequest> requestCaptor2 = ArgumentCaptor.forClass(DescribeNamedMapRequest.class);
		verify(mockMappingService).describeNamedMap(requestCaptor2.capture());

		assertEquals("/MyMaps/MyMap", requestCaptor2.getValue().getNamedMap());
	}
}
