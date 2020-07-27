package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.feature.v1.DescribeTableResponse;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinition;
import com.mapinfo.midev.service.featurecollection.v1.GeometryAttributeDefinition;
import com.mapinfo.midev.service.featurecollection.v1.KeyDefinition;
import com.mapinfo.midev.service.featurecollection.v1.StyleAttributeDefinition;
import com.mapinfo.midev.service.table.v1.TableMetadata;
import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.DescribeTableResult;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;

/**
 * This class is responsible to provide the accessing
 * the Midev Feature WebService method describeTable, from a web request.
 * 
 * describeTable is a function that finds a list of columns within the
 * specified table.
 * 
 * This class uses an Midev webservice Artifact.
 */
public class DescribeTableAction extends DataInterchangeFormatControllerAction
{

    private FeatureService featureService;

    public DescribeTableAction(FeatureService featureService)
    {
        this.featureService = featureService;
    }

    protected Object createObject(HttpServletRequest request)
    {
        String tableName = request.getParameter("tableName");
        if(StringUtils.isBlank(tableName))
        {
            throw new IllegalRequestException("tableName cannot be blank or null");
        }
        DescribeTableResponse response = null;
        try
        {
            response = this.featureService.describeTable(tableName);
        } catch (ServiceException sx)
        {
            throw new Error("Unhandled ServiceException", sx);
        }
        ;
        return prepareResult(response.getTableMetadata());
    }

    private DescribeTableResult prepareResult(TableMetadata tableMetadata)
    {
        List<AttributeDefinition>  attributeDefinitions= tableMetadata.getAttributeDefinitionList().getAttributeDefinition();
        filterGeometryAndStyleAttributes(attributeDefinitions);
        DescribeTableResult describeTableResult = new DescribeTableResult();
        describeTableResult.addAttributionDefinitionList(attributeDefinitions);
        describeTableResult.setSupportsInsert(tableMetadata.isSupportsInsert());
        describeTableResult.setKeyDefinition(tableMetadata.getKeyDefinition());

        return describeTableResult;
    }

    private void filterGeometryAndStyleAttributes(List<AttributeDefinition>
            attributeDefinitions)
    {
        Iterator<AttributeDefinition> i = attributeDefinitions.iterator();
        while(i.hasNext())
        {
            AttributeDefinition attributeDefinition = i.next();
            if(attributeDefinition instanceof GeometryAttributeDefinition ||
                    attributeDefinition instanceof StyleAttributeDefinition)
            {
                i.remove();
            }
        }
    }
}
