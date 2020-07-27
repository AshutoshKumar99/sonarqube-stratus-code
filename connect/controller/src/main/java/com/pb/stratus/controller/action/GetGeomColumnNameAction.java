package com.pb.stratus.controller.action;


/*This class will return the geometry column name since it differs in analyst & Stratus.
* For analyst it is "Obj" and for Saas it is "GEOM" */

import com.mapinfo.midev.service.feature.v1.DescribeTableResponse;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinition;
import com.mapinfo.midev.service.featurecollection.v1.GeometryAttributeDefinition;
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
 * Created by vi021ch on 4/15/2015.
 */
public class GetGeomColumnNameAction extends DataInterchangeFormatControllerAction {

    private FeatureService featureService;

    public GetGeomColumnNameAction(FeatureService featureService) {
        this.featureService = featureService;
    }

    protected Object createObject(HttpServletRequest request) {
        String tableName = request.getParameter("tableName");
        if (StringUtils.isBlank(tableName)) {
            throw new IllegalRequestException("tableName cannot be blank or null");
        }
        DescribeTableResponse response = null;
        try {
            response = this.featureService.describeTable(tableName);
        } catch (ServiceException sx) {
            throw new Error("Unhandled ServiceException", sx);
        }

        return prepareResult(response.getTableMetadata());
    }

    private DescribeTableResult prepareResult(TableMetadata metadata) {
        filterAttributes(metadata.getAttributeDefinitionList().getAttributeDefinition());
        DescribeTableResult describeTableResult = new DescribeTableResult();
        describeTableResult.addAttributionDefinitionList(metadata.getAttributeDefinitionList().getAttributeDefinition());
        describeTableResult.setSupportsInsert(metadata.isSupportsInsert());
        describeTableResult.setKeyDefinition(metadata.getKeyDefinition());
        return describeTableResult;
    }

    private void filterAttributes(List<AttributeDefinition>
                                          attributeDefinitions) {
        Iterator<AttributeDefinition> i = attributeDefinitions.iterator();
        while (i.hasNext()) {
            AttributeDefinition attributeDefinition = i.next();
            if (!(attributeDefinition instanceof GeometryAttributeDefinition)) {
                i.remove();
            }
        }
    }
}

