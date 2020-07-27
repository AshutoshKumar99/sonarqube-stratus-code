package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.feature.v1.DescribeTableResponse;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDataType;
import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinition;
import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.QueryType;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.service.SearchByQueryParams;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ar009sh
 * Date: 1/12/15
 * Time: 12:20 PMbindTableres a query based on the binding to return the
 * response.
 */
public class DataBindingAction extends FeatureSearchAction{

    //Constants for DataBindingXML
    public static final String BIND_TABLE_NAME= "bindTable";
    public static final String BIND_COLUMN= "bindColumn";
    public static final String BIND_VALUE="bindValue";

    public DataBindingAction(FeatureService featureService) {
        super(featureService);
    }

    @Override
    protected Object createObject(HttpServletRequest request) throws ServletException, IOException, InvalidGazetteerException {
        SearchByQueryParams params = new SearchByQueryParams();
        populateParams(params,request);
        return getFeatureService().searchByQuery(params);
    }
    protected void populateParams(SearchByQueryParams params, HttpServletRequest request) {

        String bindTable = request.getParameter(BIND_TABLE_NAME);
        String bindColumn = request.getParameter(BIND_COLUMN);
        params.setTableName(bindTable);
        params.setColumnName(bindColumn);

        // form a comma separated string of  values like 'c1value1','c1value2' for sql  query
        String bindValues = request.getParameter(BIND_VALUE);
        JSONArray array = JSONArray.fromObject(bindValues);
        Set<String> uniqueBindValues = new LinkedHashSet<String>();
        uniqueBindValues.addAll(array);

        boolean isBindColumnNumeric = isBindColumnNumeric(bindTable, bindColumn);
        StringBuilder sb = new StringBuilder();
        for (String s : uniqueBindValues) {
            if (!isBindColumnNumeric) {
                sb.append("'");
            }
            sb.append(s);
            if (!isBindColumnNumeric) {
                sb.append("'");
            }
            sb.append(",");
        }
        String paramsString = sb.toString();
        if(paramsString.endsWith(",")) {
            paramsString = paramsString.substring(0,sb.length()-1);
        }
        params.setParams(paramsString);

        params.setTotalCount(request.getParameter(TOTAL_COUNT));
        params.setSourceSrs(getSourceSrs(request));
        params.setQueryType(QueryType.DataBindingQuery);
    }

    private boolean isBindColumnNumeric(String bindTable, String bindColumn) {

        if(StringUtils.isBlank(bindTable) || StringUtils.isBlank(bindColumn)) {
            throw new IllegalRequestException("tableName or column name cannot be blank or null");
        }
        // First describe the table
        DescribeTableResponse response = null;
        try {
            response = getFeatureService().describeTable(bindTable);
        } catch (ServiceException sx) {
            throw new Error("Encountered ServiceException while describing the table " + bindTable, sx);
        }
        List<AttributeDefinition> attributeDefinitions=  response
                .getTableMetadata()
                .getAttributeDefinitionList()
                .getAttributeDefinition();

        boolean isBindColumnNumeric = false;

        // secondly, iterate over the column names and check its data type
        Iterator<AttributeDefinition> i = attributeDefinitions.iterator();
        while(i.hasNext()) {
            AttributeDefinition attributeDefinition = i.next();
            //columnNameTypeMap.put(attributeDefinition.getName(), attributeDefinition.getDataType());
            if (bindColumn.equals(attributeDefinition.getName())) {

                AttributeDataType attributeDataType = attributeDefinition.getDataType();

                if (AttributeDataType.INT.equals(attributeDataType)
                        || AttributeDataType.DECIMAL.equals(attributeDataType)
                        || AttributeDataType.DOUBLE.equals(attributeDataType)
                        || AttributeDataType.FLOAT.equals(attributeDataType)
                        || AttributeDataType.LONG.equals(attributeDataType)
                        || AttributeDataType.SHORT.equals(attributeDataType)
                        || AttributeDataType.BYTE.equals(attributeDataType)) {
                    isBindColumnNumeric = true;
                }
                break;
            }
        }
        return isBindColumnNumeric;
    }
}
