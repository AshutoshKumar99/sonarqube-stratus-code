package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.mapping.v1.*;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.MapNotFoundException;
import com.pb.stratus.controller.service.MappingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: VI021CH
 * Date: 1/29/15
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class DescribeNamedMapsAction extends DataInterchangeFormatControllerAction {

    private static final Logger logger = LogManager.getLogger(MappingLayersAction.class);

    private static final String MAPNAME_PARAM_NAME = "namedMap";

    private static final String REPO_PATH_PARAM_NAME = "repositoryPath";

    private static final String NOT_FOUND = "map not found";

    private static final String WHITESPACE = " ";

    private MappingService mappingService;

    public DescribeNamedMapsAction(MappingService mappingService) {
        this.mappingService = mappingService;
    }

    private MappingService getMappingService() {
        return mappingService;
    }

    protected Object createObject(HttpServletRequest request) throws ServletException, IOException, InvalidGazetteerException {
        DescribeNamedMapsResponse describeNamedMapsResponse = getNamedMapLayer(request);
        if (null == describeNamedMapsResponse) {
            throw new MapNotFoundException(request.getParameter(MAPNAME_PARAM_NAME) + WHITESPACE + NOT_FOUND);
        }
        return describeNamedMapsResponse;
    }

    private DescribeNamedMapsResponse getNamedMapLayer(HttpServletRequest request) {
        DescribeNamedMapsRequest describeNamedMaps = new DescribeNamedMapsRequest();
        DescribeNamedMapsResponse describeNamedMapsResponse = null;

        describeNamedMaps.setNamedMapList(getNamedList(request));

        try {
            describeNamedMapsResponse = getMappingService().describeNamedMaps(describeNamedMaps);
        } catch (Exception e) {
            logger.error(e);
        }
        return describeNamedMapsResponse;
    }

    private NamedMapList getNamedList(HttpServletRequest request) {
        NamedMapList namedMapList = new NamedMapList();

        if (request.getParameterValues(REPO_PATH_PARAM_NAME) != null) {
            String[] repoPathArray = request.getParameterValues(REPO_PATH_PARAM_NAME);
            for (String repoPath : repoPathArray) {
                namedMapList.getNamedMap().add(repoPath);
            }

        } else {
            String[] mapNameParams = request.getParameterValues(MAPNAME_PARAM_NAME);
            for (String mapName : mapNameParams) {
                namedMapList.getNamedMap().add(mapName);
            }
        }


        return namedMapList;
    }

}
