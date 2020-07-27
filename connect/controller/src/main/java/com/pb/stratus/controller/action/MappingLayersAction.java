package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.mapping.v1.DescribeNamedMapRequest;
import com.mapinfo.midev.service.mapping.v1.DescribeNamedMapResponse;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.MapNotFoundException;
import com.pb.stratus.controller.service.MappingService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author ma050si
 */
public class MappingLayersAction extends DataInterchangeFormatControllerAction {

    private static final Logger logger = LogManager.getLogger(MappingLayersAction.class);

    private static final String MAPNAME_PARAM_NAME = "namedMap";

    private static final String REPO_PATH_PARAM_NAME = "repositoryPath";

    private static final String NOT_FOUND = "map not found";

    private static final String WHITESPACE = " ";

    private MappingService mappingService;

    public MappingLayersAction(MappingService mappingService) {
        this.mappingService = mappingService;
    }

    private MappingService getMappingService() {
        return mappingService;
    }

    protected Object createObject(HttpServletRequest request) throws ServletException, IOException, InvalidGazetteerException {
        DescribeNamedMapResponse describeNamedMapResponse = getNamedMapLayer(request);
        if (null == describeNamedMapResponse) {
            throw new MapNotFoundException(request.getParameter(MAPNAME_PARAM_NAME) + WHITESPACE + NOT_FOUND);
        }
        return describeNamedMapResponse;
    }

    private DescribeNamedMapResponse getNamedMapLayer(HttpServletRequest request) {
        DescribeNamedMapRequest describeNamedMap = new DescribeNamedMapRequest();
        DescribeNamedMapResponse describeNamedMapResponse = null;
        if(request.getParameter(REPO_PATH_PARAM_NAME)!=null && request.getParameter(REPO_PATH_PARAM_NAME).trim()!=""){
            describeNamedMap.setNamedMap(request.getParameter(REPO_PATH_PARAM_NAME));
        }else{
            describeNamedMap.setNamedMap(request.getParameter(MAPNAME_PARAM_NAME));
        }
        try {
            describeNamedMapResponse = getMappingService().describeNamedMap(describeNamedMap);
        } catch (Exception e) {
            logger.error(e);
        }
        return describeNamedMapResponse;
    }

}
