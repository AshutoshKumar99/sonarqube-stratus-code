package com.pb.stratus.core.util;

import com.mapinfo.midev.service.feature.v1.DescribeTableRequest;
import com.mapinfo.midev.service.feature.v1.SearchAtPointRequest;
import com.mapinfo.midev.service.feature.v1.SearchIntersectsRequest;
import com.mapinfo.midev.service.feature.v1.SearchNearestRequest;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.mapping.v1.*;
import com.mapinfo.midev.service.table.v1.NamedTable;
import com.mapinfo.midev.service.table.v1.Table;
import junit.framework.Assert;
import org.apache.log4j.MDC;
import org.apache.logging.log4j.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class ServiceLoggingUtilsTest
{

    @Before
    public void setUp() throws Exception
    {}

    @After
    public void tearDown() throws Exception
    {
        ThreadContext.clearAll();
    }

    @Test
    public void testCaptureResourcesForRenderNamedMap()
    {
        RenderNamedMapRequest renderNamedMapRequest = new RenderNamedMapRequest();
        renderNamedMapRequest.setNamedMap("TestMap1");
        ServiceLoggingUtil.captureResourcesForMappingService(renderNamedMapRequest);
        Assert.assertEquals("TestMap1", ThreadContext.get(ServiceLoggingUtil.SS_OP_ARGS));

    }

    @Test
    public void testCaptureResourcesForRenderMap()
    {
        RenderMapRequest renderMapRequest = new RenderMapRequest();
        Map map = new Map();
        NamedLayer layer = new NamedLayer();
        layer.setName("NamedLayer1");
        map.getLayer().add(layer);
        layer = new NamedLayer();
        layer.setName("NamedLayer2");
        map.getLayer().add(layer);
        renderMapRequest.setMap(map);
        ServiceLoggingUtil.captureResourcesForMappingService(renderMapRequest);
        Assert.assertEquals("NamedLayer1,NamedLayer2", ThreadContext.get(ServiceLoggingUtil.SS_OP_ARGS));
    }

    @Test
    public void testCaptureResourcesForDescribeNamedMap()
    {
        DescribeNamedMapRequest describeNamedMapRequest = new DescribeNamedMapRequest();
        describeNamedMapRequest.setNamedMap("TestMap2");
        ServiceLoggingUtil.captureResourcesForMappingService(describeNamedMapRequest);
        Assert.assertEquals("TestMap2", ThreadContext.get(ServiceLoggingUtil.SS_OP_ARGS));
    }

    @Test
    public void testCaptureResourcesForGetNamedMapLegend()
    {
        GetNamedMapLegendsRequest getNamedMapLegendsRequest = new GetNamedMapLegendsRequest();
        getNamedMapLegendsRequest.setNamedMap("TestMap2");
        ServiceLoggingUtil.captureResourcesForMappingService(getNamedMapLegendsRequest);
        Assert.assertEquals("TestMap2", ThreadContext.get(ServiceLoggingUtil.SS_OP_ARGS));
    }

    @Test
    public void testCaptureResourcesForDescribeNamedLayer()
    {
        DescribeNamedLayerRequest describeNamedLayerRequest = new DescribeNamedLayerRequest();
        describeNamedLayerRequest.setNamedLayer("NamedLayer3");
        ServiceLoggingUtil.captureResourcesForMappingService(describeNamedLayerRequest);
        Assert.assertEquals("NamedLayer3", ThreadContext.get(ServiceLoggingUtil.SS_OP_ARGS));
    }

    @Test
    public void testCaptureResourcesForDescribeTable(){
        DescribeTableRequest request = new DescribeTableRequest();
        request.setTable("TestTable1");

        ServiceLoggingUtil.captureResourcesForFeatureService(request);
        Assert.assertEquals("TestTable1", ThreadContext.get(ServiceLoggingUtil.SS_OP_ARGS));
    }

    @Test
    public void testCaptureResourcesForSearchNearest(){
        SearchNearestRequest request = new SearchNearestRequest();
        Table table = new NamedTable();
        table.setName("TestTable2");
        request.setTable(table);

        ServiceLoggingUtil.captureResourcesForFeatureService(request);
        Assert.assertEquals("TestTable2", ThreadContext.get(ServiceLoggingUtil.SS_OP_ARGS));
    }

    @Test
    public void testCaptureResourcesForSearchIntersect(){
        SearchIntersectsRequest request = new SearchIntersectsRequest();
        Table table = new NamedTable();
        table.setName("TestTable3");
        request.setTable(table);

        ServiceLoggingUtil.captureResourcesForFeatureService(request);
        Assert.assertEquals("TestTable3", ThreadContext.get(ServiceLoggingUtil.SS_OP_ARGS));
    }

    @Test
    public void testCaptureResourcesForSearchAtPoint(){
        SearchAtPointRequest request = new SearchAtPointRequest();
        Table table = new NamedTable();
        table.setName("TestTable4");
        request.setTable(table);

        ServiceLoggingUtil.captureResourcesForFeatureService(request);
        Assert.assertEquals("TestTable4", ThreadContext.get(ServiceLoggingUtil.SS_OP_ARGS));

    }
}
