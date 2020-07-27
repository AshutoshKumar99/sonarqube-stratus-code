package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.mapping.v1.GetMapLegendsRequest;
import com.mapinfo.midev.service.mapping.v1.GetMapLegendsResponse;
import com.mapinfo.midev.service.mapping.v1.Map;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.MapNotFoundException;
import com.pb.stratus.controller.json.LegendDataHolder;
import com.pb.stratus.controller.legend.*;
import com.pb.stratus.controller.service.MappingService;
import com.pb.stratus.controller.thematic.ThematicMapBuilderFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: ra007gi
 * Date: 9/30/14
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThematicLegendAction extends DataInterchangeFormatControllerAction{

    private LegendService legendService;
    private ThematicMapBuilderFactory thematicMapBuilderFactory;

    private static final String MAP_NAME = "thematicMapName";
    public final static String INVALID_PARAMETER_STRING = "The following parameter was not found in request: ";

    private static final Logger logger = LogManager.getLogger(ThematicLegendAction.class);

    public ThematicLegendAction(LegendService legendService) {
        this.legendService = legendService;
        this.thematicMapBuilderFactory = new ThematicMapBuilderFactory();
    }

    private LegendService getLegendService() {
        return legendService;
    }

    @Override
    protected Object createObject(HttpServletRequest request) throws ServletException, IOException, InvalidGazetteerException {
        String mapName =  request.getParameter(MAP_NAME);
        if (StringUtils.isEmpty(mapName))
        {
            throw new IllegalRequestException(INVALID_PARAMETER_STRING + MAP_NAME);
        }
        LegendData legendData;
        Map mapObj = thematicMapBuilderFactory.createThematicMap(request);
        if(mapObj ==null){
            return null;
        }
        try{
            legendData = getLegendService().getThematicLegendData(mapName,mapObj);
        }  catch (ServiceException sx)
        {
            throw new Error("Encountered ServiceException while retrieving legend ", sx);
        }
        String iconUrl = ""; // we do not need iconUrl for thematic maps
        return new LegendDataHolder(legendData, iconUrl);
    }



}
