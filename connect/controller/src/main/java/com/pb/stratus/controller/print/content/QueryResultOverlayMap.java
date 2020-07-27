package com.pb.stratus.controller.print.content;

import com.mapinfo.midev.service.mapping.v1.Map;

/**
 * Created by vi012gu on 2/19/2015.
 */
public class QueryResultOverlayMap extends RuntimeMap {

    private String querySql;
    private String queryTable;
    private String fillColor;
    private String lineColor;
    private String pointFontColor;

    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }

    public String getQueryTable() {
        return queryTable;
    }

    public void setQueryTable(String queryTable) {
        this.queryTable = queryTable;
    }

    public String getFillColor() { return fillColor; }

    public void setFillColor(String fillColor) { this.fillColor = fillColor; }

    public String getLineColor() { return lineColor; }

    public void setLineColor(String lineColor) { this.lineColor = lineColor; }

    public String getPointFontColor() { return pointFontColor; }

    public void setPointFontColor(String pointFontColor) { this.pointFontColor = pointFontColor; }
}
