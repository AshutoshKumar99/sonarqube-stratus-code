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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MappingServiceUtilities
{
	private MappingServiceUtilities()
	{
	}

	public static List<Layer> getLayersForMap(MappingServiceInterface mappingService, String mapPath) throws ServiceException
	{
		DescribeNamedMapRequest req = new DescribeNamedMapRequest();
		req.setNamedMap(mapPath);
		DescribeNamedMapResponse res = mappingService.describeNamedMap(req);

		List<Layer> result = new ArrayList<Layer>();
		for (Layer layer : res.getMap().getLayer())
		{
			if (layer != null)
			{
				// if we have a named layer, get the real one
				if (layer instanceof NamedLayer)
				{
					layer = describeNamedLayer(mappingService, ((NamedLayer) layer).getName());
				}
				result.add(layer);
			}
		}
		return result;
	}

	public static List<String> getTablesForMap(MappingServiceInterface mappingService, String mapPath) throws ServiceException
	{
		List<Layer> layerList = getLayersForMap(mappingService, mapPath);

		List<String> result = new ArrayList<String>();
		for (Layer layer : layerList)
		{
			if (layer != null)
			{
				List<Table> tableList = getTables(mappingService, layer);
				for (Table table : tableList)
				{
					if (table instanceof NamedTable)
					{
						result.add(((NamedTable) table).getName());
					}
				}
			}
		}
		return result;
	}

	public static List<Table> getTables(MappingServiceInterface mappingService, Layer layer) throws ServiceException
	{
		if (layer instanceof FeatureLayer)
		{
			return Collections.singletonList(((FeatureLayer) layer).getTable());
		}
		else if (layer instanceof PieLayer)
		{
			return Collections.singletonList(((PieLayer) layer).getTable());
		}
		else if (layer instanceof GraduatedSymbolLayer)
		{
			return Collections.singletonList(((GraduatedSymbolLayer) layer).getTable());
		}
		else if (layer instanceof GeometryLayer)
		{
			return Collections.emptyList();
		}
		else if (layer instanceof LabelLayer)
		{
			List<Table> result = new ArrayList<Table>();
			LabelLayer labelLayer = (LabelLayer) layer;
			for (LabelSource labelSource : labelLayer.getLabelSource())
			{
				result.add(labelSource.getTable());
			}
			return result;
		}
		else if (layer instanceof GridLayer)
		{
			return Collections.singletonList(((GridLayer) layer).getTable());
		}
		else if (layer instanceof BarLayer)
		{
			return Collections.singletonList(((BarLayer) layer).getTable());
		}
		else if (layer instanceof NamedLayer)
		{
			NamedLayer namedLayer = (NamedLayer) layer;
			return getTables(mappingService, describeNamedLayer(mappingService, namedLayer.getName()));
		}
		else
		{
			throw new IllegalStateException("Invalid layer type: " + layer.getClass());
		}
	}

	public static Layer describeNamedLayer(MappingServiceInterface mappingService, String path) throws ServiceException
	{
		DescribeNamedLayerRequest req = new DescribeNamedLayerRequest();
		req.setNamedLayer(path);
		DescribeNamedLayerResponse res = mappingService.describeNamedLayer(req);
		return res.getLayer().get(0);
	}
}
