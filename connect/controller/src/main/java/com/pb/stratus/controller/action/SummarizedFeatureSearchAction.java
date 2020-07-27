package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.feature.v1.DescribeTableResponse;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDataType;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinition;
import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.QueryType;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.info.DescribeTableResult;
import com.pb.stratus.controller.service.SearchByQueryParams;
import com.pb.stratus.controller.service.SearchWithinGeometryParams.SpatialOperation;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Provides a summarized view of numeric data of the underlying tables within or intersecting the annotations
 * drawn on the UI.
 */
public class SummarizedFeatureSearchAction extends FeatureSearchAction {

    public static final String TABLE_NAME = "tableName";

    public SummarizedFeatureSearchAction(FeatureService featureService) {
        super(featureService);
    }

    @Override
    protected Object createObject(HttpServletRequest request) {
        SearchByQueryParams params = new SearchByQueryParams();
        populateParams(params, request);
        String tableName = request.getParameter(TABLE_NAME);
        // get all columns for the given table, then return only numeric subset of all columns
        DescribeTableResult result = getTableDescription(tableName);
        // update the params with numeric columns and the functions that need to be executed on these.
        populateColumnsRequiredForSummarization(params, result);
        // build query with count, max, min and sum
        // include geometries (with in or intersection), invoke feature service and return object
        return getFeatureService().searchByQuery(params);
    }

    private DescribeTableResult getTableDescription (String tableName) {
        if(StringUtils.isBlank(tableName)) {
            throw new IllegalRequestException("tableName cannot be blank or null");
        }
        DescribeTableResponse response = null;
        try {
            response = getFeatureService().describeTable(tableName);
        } catch (ServiceException sx) {
            throw new Error("Encountered ServiceException while describing the table " + tableName, sx);
        }
        List<AttributeDefinition> attributeDefinitions=  response
                .getTableMetadata()
                .getAttributeDefinitionList()
                .getAttributeDefinition();
        return prepareResult(attributeDefinitions);
    }

    private DescribeTableResult prepareResult(List<AttributeDefinition>
                                                      attributeDefinitions)
    {
        filterNumericAttributes(attributeDefinitions);
        DescribeTableResult describeTableResult = new DescribeTableResult();
        describeTableResult.addAttributionDefinitionList(attributeDefinitions);
        return describeTableResult;
    }

    private void filterNumericAttributes(List<AttributeDefinition> attributeDefinitions) {
        Iterator<AttributeDefinition> i = attributeDefinitions.iterator();
        while(i.hasNext()) {
            AttributeDefinition attributeDefinition = i.next();
            AttributeDataType attributeDataType = attributeDefinition.getDataType();

            if (AttributeDataType.INT.equals(attributeDataType)
                    || AttributeDataType.DECIMAL.equals(attributeDataType)
                    || AttributeDataType.DOUBLE.equals(attributeDataType)
                    || AttributeDataType.FLOAT.equals(attributeDataType)
                    || AttributeDataType.LONG.equals(attributeDataType)
                    || AttributeDataType.SHORT.equals(attributeDataType)
                    || AttributeDataType.BYTE.equals(attributeDataType)) {
                // do nothing, remove the object in the else condition
            } else {
                i.remove();
            }
        }
    }

    protected void populateParams(SearchByQueryParams params, HttpServletRequest request) {
        super.populateParams(params, request);
        String tableName = request.getParameter(TABLE_NAME);
        String dynamicParams = request.getParameter(PARAMS);
        String spatialOperation = request.getParameter(SPATIAL_OPERATION);
        params.setTableName(tableName);
        params.setParams(dynamicParams);
        params.setTotalCount(request.getParameter(TOTAL_COUNT));
        params.setSourceSrs(getSourceSrs(request));
        SpatialOperation operation = SpatialOperation.valueOf(spatialOperation);
        params.setSpatialOperation(operation);
        params.setQueryType(QueryType.SummarizedQuery);
    }

    /**
     * Returns a map of all the numeric columns as keys and all of the summarized functions e.g. Sum, Avg, Min and Max as values.
     * @param params
     * @param describeTableResult
     */
    private void populateColumnsRequiredForSummarization (SearchByQueryParams params,
                                                          DescribeTableResult describeTableResult) {
        Map<String, List<String>> functionAttributesMap = new LinkedHashMap<>();

        // e.g. in SELECT clause usage is SUM(colA), AVG(colA), MIN(colA), MAX(colA), SUM(colB), AVG(colB), MIN(colB), MAX(colB)
        List<String> summarizeFunctionList = Arrays.asList("SUM", "AVG", "MIN", "MAX");
        List<AttributeDefinition> attributionDefinitionList = describeTableResult.getDefinitionList();

        for (AttributeDefinition attributeDefinition : attributionDefinitionList) {
            functionAttributesMap.put(attributeDefinition.getName(), summarizeFunctionList);
        }
        params.setFunctionAttributes(functionAttributesMap);
    }
}
