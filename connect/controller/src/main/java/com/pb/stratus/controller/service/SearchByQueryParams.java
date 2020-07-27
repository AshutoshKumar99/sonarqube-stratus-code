package com.pb.stratus.controller.service;

import com.pb.stratus.controller.Constants;
import com.pb.stratus.controller.QueryType;
import com.pb.stratus.controller.service.SearchWithinGeometryParams.SpatialOperation;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This holds the Parameters for the SearchByQuery
 *
 * @author ar009sh
 */
public class SearchByQueryParams extends SearchParams {
    private String tableName;
    private String columnName ;
    private String queryName;
    private String path;
    // this string comprises of the idValue pairs of dynamic values
    private String params;
    private SpatialOperation spatialOperation;
    private String sourceSrs;
    private String totalCount;
    private Map<String, List<String>> idValueMap = null;
    private String miPrinx;
    // Setting the default query mechanism as Filtered Query
    private QueryType queryType = QueryType.FilteredQuery;

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public String getMiPrinx() {
        return miPrinx;
    }

    public void setMiPrinx(String miPrinx) {
        this.miPrinx = miPrinx;
    }

    public SearchByQueryParams() {
        super();
    }

    public SearchByQueryParams(SearchParams searchParams) {
        this.setLocale(searchParams.getLocale());
        this.setPageLength(searchParams.getPageLength());
        this.setPageNumber(searchParams.getPageNumber());
        this.setOrderByList(searchParams.getOrderByList());
        this.setOrderByDirection(searchParams.getOrderByDirection());
        this.setReturnTotalCount(searchParams.getReturnTotalCount());
        this.setAttributes(searchParams.getAttributes());
        this.setIncludeGeometry(searchParams.isIncludeGeometry());
        this.setTargetSrs(searchParams.getTargetSrs());
        this.setGeometry(searchParams.getGeometry());
    }

    public SearchByQueryParams getCopy() {
        SearchByQueryParams searchByQueryParams = new SearchByQueryParams(super.getCopy());
        searchByQueryParams.setTableName(this.getTableName());
        searchByQueryParams.setQueryName(this.getQueryName());
        searchByQueryParams.setPath(this.getPath());
        searchByQueryParams.setParams(this.getParams());
        searchByQueryParams.setSpatialOperation(this.getSpatialOperation());
        searchByQueryParams.setSourceSrs(this.getSourceSrs());
        searchByQueryParams.setTotalCount(this.getTotalCount());
        searchByQueryParams.setIdValueMap(this.getIdValueMap());
        return searchByQueryParams;
    }

    public void setIdValueMap(Map<String, List<String>> idValueMap) {
        this.idValueMap = idValueMap;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public SpatialOperation getSpatialOperation() {
        return spatialOperation;
    }

    public void setSpatialOperation(SpatialOperation spatialOperation) {
        this.spatialOperation = spatialOperation;
    }

    public String getSourceSrs() {
        return sourceSrs;
    }

    public void setSourceSrs(String sourceSrs) {
        this.sourceSrs = sourceSrs;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    /**
     * This method converts the input Parameter String into a Map<String,String>
     * which is used for storing the dynamic value for the columns
     *
     * @return Map<String, String>
     */
    public Map<String, List<String>> getIdValueMap() {
        idValueMap = new HashMap<String, List<String>>();
        if (!StringUtils.isBlank(params)) {
            params = params.trim();
            try {
                idValueMap = new ObjectMapper().readValue(params, HashMap.class);
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return idValueMap;
    }

    public List<Map<String, Object>> getCustomQueryParams() {
        List<Map<String, Object>> customQueryParams = new ArrayList<>();
        if (!StringUtils.isBlank(params)) {
            params = params.trim();
            try {
                customQueryParams = new ObjectMapper().readValue(params, ArrayList.class);
            } catch (IOException e) {
                ///TODO:
                e.printStackTrace();
            }
        }
        return customQueryParams;
    }
}
