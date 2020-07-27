package com.pb.stratus.controller.featuresearch;

import com.mapinfo.midev.service.feature.v1.BoundParameter;
import com.mapinfo.midev.service.featurecollection.v1.DateTimeValue;
import com.mapinfo.midev.service.featurecollection.v1.TimeValue;
import com.pb.stratus.controller.exception.QueryConfigException;
import com.pb.stratus.controller.queryutils.CriteriaParams;
import com.pb.stratus.controller.queryutils.QueryMetadata;
import com.pb.stratus.controller.service.SearchByQueryParams;
import com.pb.stratus.controller.util.EncodingUtil;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

/**
 * Defines helper methods for creating SQL query.
 *
 */
public class QueryHelper {

    public static final Object constructOrderByClause(SearchByQueryParams params) {
        StringBuilder b = new StringBuilder();
        String orderByColumn = getOrderByColumn(params);
        String orderByDirection = params.getOrderByDirection();
        if (null != orderByColumn) {

            b.append(" ORDER BY ");
            b.append(wrapWithQuotes(orderByColumn.trim()));

            if (null != orderByDirection) {
                b.append(" " + orderByDirection);
            }

        }
        return b.toString();
    }

    public static final String constructWhereConditions(QueryMetadata queryMetadata,
                                            List<BoundParameter> boundParams) {
        StringBuilder b = new StringBuilder();
        List<CriteriaParams> list = queryMetadata.getCriteriaParams();
        for (int i = 0; i < list.size(); i++) {
            CriteriaParams node = list.get(i);
            String columnType = node.getColumnType().trim();
            String operator = node.getOperator().trim();
            List<String> values = node.getValues();

            if (values.size() > 1) {
                b.append("(");
            }
            for (int j = 0; j < values.size(); j++) {
                String value = values.get(j);

                if (j != 0) {
                    if (operator.equals("<>")) {
                        b.append(" AND ");
                    } else {
                        b.append(" OR ");
                    }
                }

                // For case insensitive search in case of strings type.
                if (columnType.equalsIgnoreCase("STRING")) {
                    b.append("Lower(" + wrapWithQuotes(node.getColumnName().trim()) + ")");
                } else {
                    b.append(wrapWithQuotes(node.getColumnName().trim()));
                }
                b.append(" ");
                b.append(operator);
                b.append(" ");

                if (operator.equalsIgnoreCase("LIKE")) {
                    if (columnType.equalsIgnoreCase("STRING")) {
                        b.append("Lower('%" + EncodingUtil.decodeURIComponent(value.trim().replace("'", "''")) + "%')");
                    } else {
                        b.append("'%" + value.trim() + "%'");
                    }

                } else if (columnType.equalsIgnoreCase("DATE_TIME")) {
                    if (null != boundParams) {

                        BoundParameter boundParameter = new BoundParameter();
                        boundParameter.setName("DATETIME" + i + "" + j);
                        DateTimeValue datetimeValue = new DateTimeValue();

                        // TimeZone zone =
                        XMLGregorianCalendar xmlCal = null;
                        try {
                            xmlCal = DatatypeFactory.newInstance().
                                    newXMLGregorianCalendar(value.trim());
                        } catch (DatatypeConfigurationException e) {
                            throw new QueryConfigException(
                                    "Exception in creating XMLGregorianCalendar");

                        }
                        datetimeValue.setValue(xmlCal);
                        boundParameter.setValue(datetimeValue);
                        boundParams.add(boundParameter);
                    }
                    b.append("@DATETIME" + i + "" + j);

                } else if (columnType.equalsIgnoreCase("DATE")) {
                    b.append("StringToDate(" + "'" + value + "'"
                            + ", 'yyyy-mm-dd')");

                } else if (columnType.equalsIgnoreCase("STRING")) {
                    b.append("Lower('" + EncodingUtil.decodeURIComponent(value.trim().replace("'", "''")) + "')");
                } else if (columnType.equalsIgnoreCase("TIME")) {
                    if (null != boundParams) {

                        BoundParameter boundParameter = new BoundParameter();
                        boundParameter.setName("TIME" + i + "" + j);
                        TimeValue timeValue = new TimeValue();

                        // TimeZone zone =

                        XMLGregorianCalendar xmlCal = null;
                        try {
                            xmlCal = DatatypeFactory.newInstance()
                                    .newXMLGregorianCalendar();
                        } catch (DatatypeConfigurationException e) {
                            throw new QueryConfigException(
                                    "Exception in creating XMLGregorianCalendar");

                        }
                        String[] array = value.trim().split(":");
                        xmlCal.setTime(Integer.parseInt(array[0]), Integer
                                .parseInt(array[1]), Integer.parseInt(array[2]));
                        timeValue.setValue(xmlCal);
                        boundParameter.setValue(timeValue);
                        boundParams.add(boundParameter);
                    }
                    b.append("@TIME" + i + "" + j);

                } else {
                    b.append(value);
                }
            }
            if (values.size() > 1) {
                b.append(")");
            }
            if (i == list.size() - 1) {
                break;
            }
            b.append(" AND ");
        }
        return b.toString();
    }

    public static final String getOrderByColumn(SearchByQueryParams params) {

        String orderByColumn = null;
        for (String x : params.getOrderByList()) {
            orderByColumn = x;
        }

        return orderByColumn;
    }

    /**
     * Wrap the columns and table name in quotes as this fixes the problem with
     * spaces in columns or table names
     *
     * @param value to have quotes applied
     * @return a quoted string value
     */
    public static final String wrapWithQuotes(String value) {
        return "\"" + value + "\"";
    }


    public static Object wrapWithParentheses(String value) {
        return "(" + value + ")";
    }
}
