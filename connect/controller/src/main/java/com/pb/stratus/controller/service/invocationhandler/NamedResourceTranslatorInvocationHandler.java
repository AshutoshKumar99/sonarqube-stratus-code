package com.pb.stratus.controller.service.invocationhandler;

import com.mapinfo.midev.service.feature.v1.DescribeTableRequest;
import com.mapinfo.midev.service.feature.v1.SearchBySQLRequest;
import com.mapinfo.midev.service.feature.v1.SpatialSearchRequest;
import com.mapinfo.midev.service.mapping.v1.*;
import com.mapinfo.midev.service.table.v1.NamedTable;
import com.pb.stratus.controller.util.MiDevRepository;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamedResourceTranslatorInvocationHandler implements InvocationHandler
{
    private String NAMED_MAP = "NAMED_MAP";
    private String NAMED_TABLE = "NAMED_TABLE";
    private String NAMED_LAYER = "NAMED_LAYER";

    private Map<String, MiDevRepository> repositoryMap;

    private Object target;

    public NamedResourceTranslatorInvocationHandler(
            Map repository, Object target)
    {
        this.repositoryMap = repository;
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable
    {
        if (args[0] instanceof SpatialSearchRequest)
        {
            translateTableNames((SpatialSearchRequest) args[0]);
        }
        else if (args[0] instanceof RenderNamedMapRequest)
        {
            translateMapName((RenderNamedMapRequest) args[0]);
        }
        else if (args[0] instanceof SearchBySQLRequest)
        {
            translateTableNameInQuery((SearchBySQLRequest) args[0]);
        }
        else if (args[0] instanceof GetNamedMapLegendsRequest)
        {
            translateMapName((GetNamedMapLegendsRequest) args[0]);
        }
        else if (args[0] instanceof DescribeTableRequest)
        {
            translateTableName((DescribeTableRequest) args[0]);
        }
        else if (args[0] instanceof DescribeNamedMapRequest)
        {
            translateMapName((DescribeNamedMapRequest) args[0]);
        }
        else if (args[0] instanceof DescribeNamedLayerRequest)
        {
            translateNamedLayerName((DescribeNamedLayerRequest) args[0]);
        }
        else if (args[0] instanceof RenderMapRequest)
        {
            translateLayerName((RenderMapRequest) args[0]);
        }
        try
        {
            return method.invoke(target, args);
        }
        catch (InvocationTargetException itx)
        {
            throw itx.getCause();
        }
    }

    private void translateLayerName(RenderMapRequest renderMapRequest)
    {
        List<Layer> list = renderMapRequest.getMap().getLayer();
        Iterator<Layer> it = list.iterator();
        while(it.hasNext())
        {
            Layer layer = (Layer) it.next();
            if(layer instanceof NamedLayer) {
                NamedLayer namedLayer = (NamedLayer)layer;
                String externalName = namedLayer.getName();
                if(externalName.startsWith("/")){
                    namedLayer.setName(externalName);
                }else{
                    namedLayer.setName(repositoryMap.get(NAMED_LAYER).getInternalResourceName(externalName));
                }
            }
        }
    }

    private void translateTableNames(SpatialSearchRequest spatialSearchRequest)
    {
        if (spatialSearchRequest.getTable() instanceof NamedTable){
            NamedTable table = (NamedTable) spatialSearchRequest.getTable();

            String externalName = table.getName();
            if(externalName.startsWith("/")){
            table.setName(externalName);
            }else{
            String internalName = repositoryMap.get(NAMED_TABLE).getInternalResourceName(externalName);
            table.setName(internalName);
            }
        }

    }

    private void translateMapName(RenderNamedMapRequest renderNamedMapRequest)
    {
        String externalName = renderNamedMapRequest.getNamedMap();
        if(externalName.startsWith("/")){
            renderNamedMapRequest.setNamedMap(externalName);
        }else{
            String internalName = repositoryMap.get(NAMED_MAP).getInternalResourceName(externalName);
            renderNamedMapRequest.setNamedMap(internalName);
        }

    }

    private void translateMapName(GetNamedMapLegendsRequest getNamedMapLegendsRequest)
    {
        String externalName = getNamedMapLegendsRequest.getNamedMap();
        if(externalName.startsWith("/")){
            getNamedMapLegendsRequest.setNamedMap(externalName);
        }else{
            String internalName = repositoryMap.get(NAMED_MAP).getInternalResourceName(externalName);
            getNamedMapLegendsRequest.setNamedMap(internalName);
        }
    }

    private void translateTableNameInQuery(SearchBySQLRequest searchBySQLRequest)
    {
        String query = searchBySQLRequest.getSQL();
        Pattern pattern = Pattern.compile("select .* from \"(.*?)\".*");
        Matcher matcher = pattern.matcher(query);
        if (!matcher.matches())
        {
            return;
        }
        int start = matcher.start(1);
        int end = matcher.end(1);
        StringBuilder newQuery = new StringBuilder();
        newQuery.append(query.substring(0, start));
        String externalName = matcher.group(1);
        if(externalName.startsWith("/")){
            newQuery.append(externalName);
        } else{
            newQuery.append(repositoryMap.get(NAMED_TABLE).getInternalResourceName(externalName));
        }
        newQuery.append(query.substring(end));
        searchBySQLRequest.setSQL(newQuery.toString());
    }

    private void translateTableName(DescribeTableRequest describeTableRequest)
    {
        String externalName = describeTableRequest.getTable();
        if(externalName.startsWith("/")){
            describeTableRequest.setTable(externalName);
        }else{
            String internalName = repositoryMap.get(NAMED_TABLE).getInternalResourceName(externalName);
            describeTableRequest.setTable(internalName);
        }
    }

    private void translateMapName(
            DescribeNamedMapRequest describeNamedMapRequest)
    {
        String externalName = describeNamedMapRequest.getNamedMap();
        if(externalName.startsWith("/")){
            describeNamedMapRequest.setNamedMap(externalName);
        }else{
            String internalName = repositoryMap.get(NAMED_MAP).getInternalResourceName(externalName);
            describeNamedMapRequest.setNamedMap(internalName);
        }
    }

    private void translateNamedLayerName(
            DescribeNamedLayerRequest describeNamedLayerRequest)
    {
        String externalName = describeNamedLayerRequest.getNamedLayer();
        if(externalName.startsWith("/")){
            describeNamedLayerRequest.setNamedLayer(externalName);
        }else{
            String internalName = repositoryMap.get(NAMED_LAYER).getInternalResourceName(externalName);
            describeNamedLayerRequest.setNamedLayer(internalName);
        }

    }

}
