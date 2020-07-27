package com.pb.stratus.controller.thematic;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.*;
import com.mapinfo.midev.service.mapping.v1.Map;
import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.util.EncodingUtil;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: ra007gi
 * Date: 9/29/14
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */

public class ThematicMapBuilderFactory {

    private static final String THEMATIC_MAP_TYPE = "thematicMapType";
    private static final String THEMATIC_MAP_JSON = "thematicMapJson";
    public final static String INVALID_PARAMETER_STRING = "The following parameter was not found in request: ";
    ObjectMapper mapper;

    public ThematicMapBuilderFactory() {
        this.mapper = new ObjectMapper();
    }

    public Map createThematicMap(HttpServletRequest request) throws IOException {

        //read json from request
        String thematicJson =  request.getParameter(THEMATIC_MAP_JSON);
        if (StringUtils.isEmpty(thematicJson))
        {
            throw new IllegalRequestException(INVALID_PARAMETER_STRING + THEMATIC_MAP_JSON);
        }
        String thematicMapJson = EncodingUtil.decodeURIComponent(thematicJson);
        return createThematicMapFromJson(thematicMapJson);
    }

    public Map createThematicMapFromJson(String thematicMapJson) throws IOException {
        //get thematicMapType from Json
        JsonNode treeNode = mapper.readTree(thematicMapJson);
        String thematicMapTypeString = treeNode.get(THEMATIC_MAP_TYPE).asText();

        // get Map object according to type of thematic
        ThematicTypeEnum thematicMapType = ThematicTypeEnum.getThematicType(thematicMapTypeString);
        switch (thematicMapType) {
            case RANGE:
                return new RangeThematicBuilder().getMap(treeNode);
            case INDIVIDUAL_VALUE:
                return new IndividualValueThematicBuilder().getMap(treeNode);
            case GRID:
                break;
            case GRADUATED_SYMBOL:
                return  new GraduatedSymbolThematicBuilder().getMap(treeNode);
            case DOT_DENSITY:
                break;
            case BAR_CHART:
                break;
            case PIE_CHART:
                break;
            case NONE:
                break;
            default:
                return null;
        }
        return null;
    }


}
