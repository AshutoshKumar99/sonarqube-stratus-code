package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.feature.v1.SearchBySQLResponse;
import com.mapinfo.midev.service.feature.ws.v1.ServiceException;
import com.mapinfo.midev.service.featurecollection.v1.AttributeValue;
import com.mapinfo.midev.service.featurecollection.v1.DoubleValue;
import com.mapinfo.midev.service.featurecollection.v1.Feature;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.featuresearch.FeatureSearchResultConverterFactory;
import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.util.TypeConversionUtils;
import com.pb.stratus.core.common.Preconditions;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 12/30/14
 * Time: 2:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetBoundaryColumnValueAction extends DataInterchangeFormatControllerAction{
    private FeatureService featureService;
    private static final String COLUMN_NAME = "columnName";
    private static final String TABLE_NAME = "tableName";
    private static final String BOUNDARY_TYPE = "boundaryType";
    public GetBoundaryColumnValueAction(FeatureService featureService) {
        this.featureService = featureService;
    }

    @Override
    protected Object createObject(HttpServletRequest request){
        String sql = createSQLString(request);
        SearchBySQLResponse response = null;
        try{
            response = featureService.executeSql(sql);
        }catch (ServiceException ex){

        }
        if (response == null)
            return "";
        return getValue(response);  //To change body of implemented methods use File | Settings | File Templates.
    }

    private String getValue(SearchBySQLResponse response){
        Feature feature = response.getFeatureCollection().getFeatureList().getFeature().get(0);
         String valueAsString=TypeConversionUtils.convertAttributeValueToString(feature.getAttributeValue().get(0));
        return valueAsString ;
    }

    private String createSQLString(HttpServletRequest request){
        StringBuilder sql = new StringBuilder("select ");
        sql.append(getBoundaryType(request).toLowerCase() + "(\"" + getColumnName(request) + "\") from \"" + getTableName(request) + "\"");
        return sql.toString();
    }

    private String getColumnName(HttpServletRequest request){
        String columnName = request.getParameter(COLUMN_NAME);
        Preconditions.checkNotNull(columnName, "columnName required");
        Preconditions.checkState(!StringUtils.isBlank(columnName), "columnName cannot be blank");
        return columnName;
    }

    private String getTableName(HttpServletRequest request){
        String tableName = request.getParameter(TABLE_NAME);
        Preconditions.checkNotNull(tableName, "tableName required");
        Preconditions.checkState(!StringUtils.isBlank(tableName), "tableName cannot be blank");
        return tableName;
    }
    private String getBoundaryType(HttpServletRequest request){
        String type = request.getParameter(BOUNDARY_TYPE);
        Preconditions.checkNotNull(type, "boundaryType required");
        Preconditions.checkState(!StringUtils.isBlank(type), "boundaryType cannot be blank");
        return type;
    }
}
