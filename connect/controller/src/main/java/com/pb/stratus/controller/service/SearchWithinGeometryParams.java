package com.pb.stratus.controller.service;


/**
 * Class to hold the parameters for FeatureService serachByGeometry operation.
 * This will cater for circle,rectangle,square,ellipse,polygon and lineString
 *
 * @author pa016wa
 */
public class SearchWithinGeometryParams extends SearchParams {
    /**
     * Default constructor. Providing default values to avoid any
     * NullPointerExceptions.
     */
    public SearchWithinGeometryParams() {
        super();
        this.table = "";
        this.spatialOperation = SpatialOperation.INTERSECTS;
        this.sourceSrs = "";
    }

    public SearchWithinGeometryParams(SearchParams searchParams) {
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

    /*
     * Enum for list of spatial operations that can be performed in SearchByGeometry.
     */
    public enum SpatialOperation {
        INTERSECTS,
        ENTIRELYWITHIN
    }

    private String table;

    private SpatialOperation spatialOperation;

    private String sourceSrs;
    private boolean searchInTables;

    /**
     * Return a new instance as a copy of the class. Avoiding cloning to
     * circumvent any unexpected results.
     *
     * @return SearchWithinGeometryParams
     */
    public SearchWithinGeometryParams getCopy() {
        SearchWithinGeometryParams searchWithinGeometryParams = new
                SearchWithinGeometryParams(super.getCopy());
        searchWithinGeometryParams.setSpatialOperation(this.getSpatialOperation());
        searchWithinGeometryParams.setTable(this.getTable());
        searchWithinGeometryParams.setSourceSrs(this.getSourceSrs());
        return searchWithinGeometryParams;
    }

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return the spatialOperation
     */
    public SpatialOperation getSpatialOperation() {
        return spatialOperation;
    }

    /**
     * @param spatialOperation the spatialOperation to set
     */
    public void setSpatialOperation(SpatialOperation spatialOperation) {
        this.spatialOperation = spatialOperation;
    }


    /**
     * @return the sourceSrs
     */
    public String getSourceSrs() {
        return sourceSrs;
    }

    /**
     * @param sourceSrs the sourceSrs to set
     */
    public void setSourceSrs(String sourceSrs) {
        this.sourceSrs = sourceSrs;
    }

    public boolean isSearchInTables() {
        return searchInTables;
    }

    public void setSearchInTables(boolean searchInTables) {
        this.searchInTables = searchInTables;
    }
}
