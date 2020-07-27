package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.mapping.v1.DescribeNamedLayerRequest;
import com.mapinfo.midev.service.mapping.v1.DescribeNamedLayerResponse;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.MapNotFoundException;
import com.pb.stratus.controller.service.MappingService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ma050si
 * Date: 11/11/13
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class MappingNamedLayerAction extends DataInterchangeFormatControllerAction {

    private static final Logger logger = LogManager.getLogger(MappingLayersAction.class);

    private static final String LAYERNAME_PARAM_NAME = "namedLayer";

    private static final String NOT_FOUND = "layer not found";

    private MappingService mappingService;

    public MappingNamedLayerAction (MappingService mappingService) {
        this.mappingService = mappingService;
    }

    private MappingService getMappingService() {
        return mappingService;
    }
    @Override
    protected Object createObject(HttpServletRequest request) throws ServletException, IOException, InvalidGazetteerException {
        DescribeNamedLayerResponse describeNamedLayerResponse = getNamedLayerTable(request);
        if(null ==  describeNamedLayerResponse) {
            throw new MapNotFoundException(request.getParameter(LAYERNAME_PARAM_NAME) + NOT_FOUND);
        }
        return describeNamedLayerResponse;
    }

    private DescribeNamedLayerResponse getNamedLayerTable(HttpServletRequest request) {
        DescribeNamedLayerRequest describeNamedLayerRequest = new DescribeNamedLayerRequest();
        describeNamedLayerRequest.setNamedLayer(request.getParameter(LAYERNAME_PARAM_NAME));
        DescribeNamedLayerResponse describeNamedLayerResponse = null;
        try {
            describeNamedLayerResponse = getMappingService().describeNamedLayer(describeNamedLayerRequest);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return describeNamedLayerResponse;
    }
}
