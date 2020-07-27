package com.pb.stratus.controller.service.invocationhandler;

import com.mapinfo.midev.service.feature.v1.DescribeTableRequest;
import com.mapinfo.midev.service.feature.v1.SearchBySQLRequest;
import com.mapinfo.midev.service.feature.v1.SpatialSearchRequest;
import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometries.v1.GeometryList;
import com.mapinfo.midev.service.mapping.v1.*;
import com.mapinfo.midev.service.table.v1.NamedTable;
import com.pb.stratus.controller.featuresearch.Target;
import com.pb.stratus.controller.util.MiDevNamedLayerRepository;
import com.pb.stratus.controller.util.MiDevNamedMapRepository;
import com.pb.stratus.controller.util.MiDevNamedTableRepository;
import com.pb.stratus.controller.util.MiDevRepository;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NamedResourceTranslatorInvocationHandlerTest
{
    

    
    private Target mockTarget;
    
    private Target proxy;
    
    @Before
    public void setUp()
    {
        mockTarget = mock(Target.class);
        MiDevRepository mockNamedTableRepository = mock(MiDevNamedTableRepository.class);
        MiDevRepository mockNamedMapRepository = mock(MiDevNamedMapRepository.class);
        MiDevRepository mockNamedLayerRepository = mock(MiDevNamedLayerRepository.class);
        when(mockNamedLayerRepository.getInternalResourceName(any(String.class)))
                .thenReturn("internalName");
        when(mockNamedMapRepository.getInternalResourceName(any(String.class)))
                .thenReturn("internalName");
        when(mockNamedTableRepository.getInternalResourceName(any(String.class)))
                .thenReturn("internalName");
        Map<String, MiDevRepository> repoMap = new HashMap<String, MiDevRepository>();
        repoMap.put("NAMED_MAP", mockNamedMapRepository);
        repoMap.put("NAMED_LAYER", mockNamedLayerRepository);
        repoMap.put("NAMED_TABLE", mockNamedTableRepository);
        //when(mockRepository.getNamedMapInternalResourceName(any(String.class)))
        //        .thenReturn("/NamedTiles/NamedMaps/internalName");
        NamedResourceTranslatorInvocationHandler handler
                = new NamedResourceTranslatorInvocationHandler(
                        repoMap, mockTarget);
        proxy = (Target) Proxy.newProxyInstance(getClass().getClassLoader(), 
                new Class<?>[] {Target.class}, handler);
    }
    
    @Test
    public void shouldTranslateSpatialSearchRequests() throws Throwable
    {
        SpatialSearchRequest req = new SpatialSearchRequest()
        {
        };
        NamedTable table = new NamedTable();
        table.setName("externalName");
        req.setTable(table);
        proxy.invoke(req);
        assertEquals("internalName", 
                ((NamedTable) req.getTable()).getName());
    }
    
    @Test
    public void shouldTranslateRenderNamedMapRequests() throws Throwable
    {
        RenderNamedMapRequest req = new RenderNamedMapRequest();
        req.setNamedMap("externalName");
        proxy.invoke(req);
        //assertEquals("/NamedTiles/NamedMaps/internalName", req.getNamedMap());
        assertEquals("internalName", req.getNamedMap());
    }

    @Test
    public void shouldNotTranslateRenderNamedMapRequestsHavingRepositoryPath() throws Throwable
    {
        RenderNamedMapRequest req = new RenderNamedMapRequest();
        req.setNamedMap("/OnPrim/Map1/externalName");
        proxy.invoke(req);
        //assertEquals("/NamedTiles/NamedMaps/internalName", req.getNamedMap());
        assertEquals("/OnPrim/Map1/externalName", req.getNamedMap());
    }
    
    @Test
    public void shouldPassThroughParamsAndReturnValue() throws Exception
    {
        Object mockRetVal = new Object();
        Object mockParam = new Object();
        when(mockTarget.invoke(mockParam)).thenReturn(mockRetVal);
        Object actualRetVal = proxy.invoke(mockParam);
        assertEquals(mockRetVal, actualRetVal);
    }
    
    @Test
    public void shouldTranslateSqlQueries() throws Exception
    {
        SearchBySQLRequest request = new SearchBySQLRequest();
        request.setSQL("select * from \"someTable\" where \"attribute1\" > 1");
        proxy.invoke(request);
        assertEquals("select * from \"internalName\" " 
                + "where \"attribute1\" > 1", request.getSQL());
    }

    @Test
    public void shouldNotTranslateSqlQueriesForTablesHavingRepositoryPath() throws Throwable
    {
        SearchBySQLRequest request = new SearchBySQLRequest();
        request.setSQL("select * from \"/MyTables/someTable\" where \"attribute1\" > 1");
        proxy.invoke(request);
        assertEquals("select * from \"/MyTables/someTable\" "
                + "where \"attribute1\" > 1", request.getSQL());
    }
    
    @Test
    public void shouldTranslateLegendRequests() throws Exception
    {
        GetNamedMapLegendsRequest request = new GetNamedMapLegendsRequest();
        request.setNamedMap("externalName");
        proxy.invoke(request);
        assertEquals("internalName", request.getNamedMap());
    }

    @Test
    public void shouldNotTranslateLegendRequestsHavingRepositoryPath() throws Throwable
    {
        GetNamedMapLegendsRequest request = new GetNamedMapLegendsRequest();
        request.setNamedMap("/OnPrim/Map1/mapName");
        proxy.invoke(request);
        assertEquals("/OnPrim/Map1/mapName", request.getNamedMap());
    }
    
    @Test
    public void shouldTranslateDescribeTableRequest() throws Exception
    {
        DescribeTableRequest request = new DescribeTableRequest();
        request.setTable("externalName");
        proxy.invoke(request);
        assertEquals("internalName", request.getTable());
    }

    @Test
    public void shouldNotTranslateDescribeTableRequestHavingRepositoryPath() throws Exception
    {
        DescribeTableRequest request = new DescribeTableRequest();
        request.setTable("/OnPrem/Tables/abc");
        proxy.invoke(request);
        assertEquals("/OnPrem/Tables/abc", request.getTable());
    }
    
    @Test
    public void shouldTranslateDescribeNamedMapRequest() throws Exception
    {
        DescribeNamedMapRequest request = new DescribeNamedMapRequest();
        request.setNamedMap("externalName");
        proxy.invoke(request);
        assertEquals("internalName", request.getNamedMap());
    }

    @Test
    public void shouldNotTranslateDescribeNamedMapRequestHavingRepositoryPath() throws Exception
    {
        DescribeNamedMapRequest request = new DescribeNamedMapRequest();
        request.setNamedMap("/OnPrem/Maps/map1");
        proxy.invoke(request);
        assertEquals("/OnPrem/Maps/map1", request.getNamedMap());
    }

    @Test
    public void shouldTranslateRenderMapRequest() throws Exception
    {
        RenderMapRequest request = new RenderMapRequest();
        com.mapinfo.midev.service.mapping.v1.Map map = new com.mapinfo.midev.service.mapping.v1.Map();
        request.setMap(map);
        NamedLayer layer = new NamedLayer();
        layer.setName("externalName1");
        request.getMap().getLayer().add(layer);
        layer.setName("externalName2");
        request.getMap().getLayer().add(layer);
        proxy.invoke(request);
        assertEquals("internalName", ((NamedLayer)request.getMap().getLayer().get(0)).getName());
        assertEquals("internalName", ((NamedLayer)request.getMap().getLayer().get(0)).getName());
    }


    @Test
    public void shouldNotTranslateRenderMapRequestHavingRepositoryPath() throws Exception
    {
        RenderMapRequest request = new RenderMapRequest();
        com.mapinfo.midev.service.mapping.v1.Map map = new com.mapinfo.midev.service.mapping.v1.Map();
        request.setMap(map);
        NamedLayer layer1 = new NamedLayer();
        layer1.setName("/OnPrem/Layers1/layer1");
        request.getMap().getLayer().add(layer1);
        NamedLayer layer2 = new NamedLayer();
        layer2.setName("/OnPrem/Layers2/layer2");
        request.getMap().getLayer().add(layer2);
        proxy.invoke(request);
        assertEquals("/OnPrem/Layers1/layer1", ((NamedLayer)request.getMap().getLayer().get(0)).getName());
        assertEquals("/OnPrem/Layers2/layer2", ((NamedLayer)request.getMap().getLayer().get(1)).getName());
    }

    @Test
    public void shouldNotTranslateRenderMapRequestIfGeometryLayerPassed() throws Exception
    {
        RenderMapRequest request = new RenderMapRequest();
        com.mapinfo.midev.service.mapping.v1.Map map = new com.mapinfo.midev.service.mapping.v1.Map();
        request.setMap(map);

        GeometryLayer geometryLayer = new GeometryLayer();
        geometryLayer.setRenderable(true);
        geometryLayer.setDescription("Geometry layer for Features");
        GeometryList geometryList = new GeometryList();
        geometryLayer.setGeometryList(geometryList);
        Geometry mockGeometry = mock(Geometry.class);
        geometryList.getGeometry().add(mockGeometry);

        request.getMap().getLayer().add(geometryLayer);
        proxy.invoke(request);
        assertEquals("Geometry layer for Features", ((GeometryLayer)request.getMap().getLayer().get(0)).getDescription());
    }

    @Test
    public void shouldTranslateDescribeNamedlayerRequest() throws Exception
    {
        DescribeNamedLayerRequest request = new DescribeNamedLayerRequest();
        request.setNamedLayer("externalName");
        proxy.invoke(request);
        assertEquals("internalName", request.getNamedLayer());
    }

    @Test
    public void shouldNotTranslateDescribeNamedLayerRequestHavingRepositoryPath() throws Exception
    {
        DescribeNamedLayerRequest request = new DescribeNamedLayerRequest();
        request.setNamedLayer("/OnPrem/Layers/layer1");
        proxy.invoke(request);
        assertEquals("/OnPrem/Layers/layer1", request.getNamedLayer());
    }

}
