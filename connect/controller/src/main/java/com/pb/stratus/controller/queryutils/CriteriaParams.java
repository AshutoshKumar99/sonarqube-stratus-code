package com.pb.stratus.controller.queryutils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds the DS for the column element of the xml Each instance of
 * this class denotes one where clause
 *
 * @author ar009sh
 */
public final class CriteriaParams {

    private String columnName;
    private String ColumnType;
    private String operator;
    private String displayType;
    private String label;
    private String id;

    // for DOM
    public CriteriaParams(String operator, String displayType, String label,
                          String id, String columnName, String columnType) {
        super();
        this.operator = operator;
        this.displayType = displayType;
        this.label = label;
        this.id = id;
        this.columnName = columnName;
        this.ColumnType = columnType;
        // avoid nulls
        this.values = new ArrayList<String>();
        this.values.add("");
    }

    public CriteriaParams(String columnName, String columnType, String operator, List<String> values) {
        super();
        this.operator = operator;
        this.columnName = columnName;
        this.ColumnType = columnType;
        this.values = values;
    }

    // this will come from the connect UI dynamically for each where clause say
    // column >= "value";
    private List<String> values;

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColumnType() {
        return ColumnType;
    }

    public void setColumnType(String columnType) {
        ColumnType = columnType;
    }

    public String toString() {
        return XMlConfigConstants.COLUMN + " " + XMlConfigConstants.COLUMN_ID
                + " : " + this.getId() + ", " + XMlConfigConstants.COLUMN_NAME
                + " : " + this.getColumnName() + ", "
                + XMlConfigConstants.COLUMN_TYPE + " : " + this.getColumnType()
                + ", " + XMlConfigConstants.OPERATOR + " : " + this.getOperator()
                + ", " + XMlConfigConstants.DISPLAY_TYPE + " : "
                + this.getDisplayType() + ", " + XMlConfigConstants.LABEL + " : "
                + this.getLabel() + ", " + "Value is >>" + " : " + this.getValues();

    }

}
