package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.*;
import com.mapinfo.midev.service.feature.ws.v1.FeatureServiceInterface;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDataType;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinition;
import com.mapinfo.midev.service.table.v1.NamedTable;
import com.pb.stratus.controller.info.FeatureSearchResult;
import com.pb.stratus.controller.service.SearchParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

// XXX because of the structural difference between searchAtPoint and all
// the other searches (multiple tables) I can't write an implementation
// that enforces the usage of the methods in this class.
// However, I also don't want to litter unit tests with repetitive code.
// We should align all searches so that we can enforce a certain execution
// path in here by, e.g., using a template method pattern.
/**
 * Performs a feature search, following an arbitrary implementation strategy.
 */
public abstract class BaseSearchStrategy implements SearchStrategy
{

    private static final Logger logger = LogManager
            .getLogger(BaseSearchStrategy.class);

    protected FeatureServiceInterface featureWebService;

    private FeatureSearchResultConverterFactory converterFactory;

    protected int maxResults;

    protected BaseSearchStrategy(FeatureServiceInterface featureWebService,
            FeatureSearchResultConverterFactory converterFactory, int maxResults)
    {
        this.featureWebService = featureWebService;
        this.converterFactory = converterFactory;
        this.maxResults = maxResults;
    }

    protected void popuplateBaseValues(String tableName, SearchRequest request,
            SearchParams params)
    {
        request.setLocale(params.getLocale());
        if (request instanceof SpatialSearchRequest)
        {
            populateSpatialBaseValues(tableName,
                    (SpatialSearchRequest) request, params);
        }
        setPageAndNumResults(params, request);
    }

    private void populateSpatialBaseValues(String tableName,
            SpatialSearchRequest request, SearchParams params)
    {
        List<String> attrs = params.getAttributes();
        List<String> finalAttrs = new LinkedList<String>();
        if (attrs != null && !attrs.isEmpty())
        {
            finalAttrs.addAll(attrs);
        }
        if (params.isIncludeGeometry())
        {
            addGeometryColumnIfNecessary(tableName, finalAttrs);
        }
        // CONN-18087 - adding MI_KEY to the attribute list.
        addMI_KEYValue(tableName, finalAttrs);

        /*Commenting out below method addMIPRINXValue as MI_PRINX is no longer going to be used for identifying unique query result rows.
         MI_KEY would be used insteadon both OnPrem and Saas as per CONN-17926 & CONN-18087  */
        //CONN15513 - adding MI_PRINX to the attribute list.
        //addMIPRINXValue(tableName, finalAttrs);

        // CONN-13488 Don't set empty element. 
        if(!finalAttrs.isEmpty()){
            AttributeList attList = new AttributeList();
            attList.getAttributeName().addAll(finalAttrs);
            request.setAttributeList(attList);
        }
        NamedTable table = new NamedTable();
        table.setName(tableName);
        request.setTable(table);

        request.setReturnTotalCount(params.getReturnTotalCount());
        setOrderByParams(params, request);
    }

    private void setOrderByParams(SearchParams params,
            SpatialSearchRequest request)
    {
        OrderByDirection orderByDirection = OrderByDirection.ASCENDING;
        String directionValue = params.getOrderByDirection();
        if (directionValue != null && directionValue.equalsIgnoreCase("desc"))
        {
            orderByDirection = OrderByDirection.DESCENDING;
        }
        OrderByList orderByList = new OrderByList();
        for (String attr : params.getOrderByList())
        {
            OrderBy orderBy = new OrderBy();
            orderBy.setAttributeName(attr);
            orderBy.setOrderByDirection(orderByDirection);
            orderByList.getOrderBy().add(orderBy);
        }
        request.setOrderByList(orderByList);
    }

    private void addGeometryColumnIfNecessary(String table, List<String> attrs)
    {
        if (attrs.isEmpty())
        {
            return;
        }
        String geomAttr = getGeometryAttributeFromTable(table);
        if (geomAttr == null)
        {
            logger.warn("Table '" + table + "' doesn't contain a geometry " +
                    "attribute. No geometry will be included");
        }
        else
        {
            if (!attrs.contains(geomAttr))
            {
                attrs.add(geomAttr);
            }
        }
    }

    protected String getGeometryAttributeFromTable(String table)
    {
        DescribeTableRequest request = new DescribeTableRequest();
        request.setTable(table);
        DescribeTableResponse resp;
        try
        {
            resp = featureWebService.describeTable(request);
        }
        catch (ServiceException sx)
        {
            throw new Error("Unhandled ServiceException", sx);
        }
        List<AttributeDefinition> attrDefs =
                resp.getTableMetadata().getAttributeDefinitionList()
                        .getAttributeDefinition();
        for (AttributeDefinition attrDef : attrDefs)
        {

            if (attrDef.getDataType() == AttributeDataType.GEOMETRY)
            {
                return attrDef.getName();
            }
        }
        return null;
    }

    private void
            setPageAndNumResults(SearchParams params, SearchRequest request)
    {
        int pageNumber =
                (params.getPageNumber() > 1) ? params.getPageNumber() : 1;
        int pageLength =
                (params.getPageLength() > 0) ? params.getPageLength()
                        : maxResults;
        request.setPageNumber(pageNumber);
        request.setPageLength(pageLength);
    }

    protected FeatureSearchResult convertWebServiceResponse(String tableName,
            SearchResponse response)
    {
        return convertWebServiceResponse(tableName, response, null);
    }

    protected FeatureSearchResult convertWebServiceResponse(String tableName,
            SearchResponse response, SearchParams searchParams)
    {
        FeatureSearchResultConverter converter =
                converterFactory.createConverter();
        return converter.convert(tableName, response, searchParams);
    }

    /**
     * addMI_KEYvalue function
     *
     * CONN15513 - Adding the  MI_KEY value to attribute list.
     * to attribute list.
     *
     * @param table
     * @param attrs
     */
    protected void addMI_KEYValue(String table, List<String> attrs){
        if (attrs.isEmpty())
        {
            return;
        }
        attrs.add("MI_KEY");
    }


    /**
     * addMIPRINXValue function
     *
     * CONN15513 - Adding the retrieved MI_PRINX value to attribute list.
     * to attribute list.
     *
     * @param table
     * @param attrs
     */
    protected void addMIPRINXValue(String table, List<String> attrs){
        if (attrs.isEmpty())
        {
            return;
        }
        String miPrinxAttr = getMIPRINXAttributeFromTable(table);
        if (miPrinxAttr == null)
        {
            logger.warn("Table '" + table + "' doesn't contain a MI_PRINX " +
                    "attribute.");
        }
        else
        {
            if (!attrs.contains(miPrinxAttr))
            {
                attrs.add(miPrinxAttr);
            }
        }
    }

    /**
     * getMIPRINXAttributeFromTable function
     *
     * CONN15513 - retrieving MI_PRINX information which uniquely identifies each query record.
     *
     * @param table
     * @return
     */

    protected String getMIPRINXAttributeFromTable(String table) {
        DescribeTableRequest request = new DescribeTableRequest();
        request.setTable(table);
        DescribeTableResponse resp;
        try
        {
            resp = featureWebService.describeTable(request);
        }
        catch (ServiceException sx)
        {
            throw new Error("Unhandled ServiceException", sx);
        }
        List<AttributeDefinition> attrDefs =
                resp.getTableMetadata().getAttributeDefinitionList()
                        .getAttributeDefinition();
        if (attrDefs != null && !attrDefs.isEmpty()) {
        for (AttributeDefinition attrDef : attrDefs)
        {

            if (attrDef.getName().equalsIgnoreCase("MI_Prinx"))
            {
                return attrDef.getName();
            }
        }
        }
        return null;
    }

}
