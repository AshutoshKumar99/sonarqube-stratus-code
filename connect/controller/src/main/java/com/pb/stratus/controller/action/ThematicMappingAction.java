package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.mapping.v1.*;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.MapNotFoundException;
import com.pb.stratus.controller.service.MappingService;
import com.pb.stratus.controller.service.RenderMapParams;
import com.pb.stratus.controller.thematic.ThematicMapBuilderFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ra007gi
 * Date: 9/29/14
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class ThematicMappingAction extends BaseControllerAction{

    private MappingService mappingService;
    private ThematicMapBuilderFactory thematicMapBuilderFactory;

    private static final Logger logger = LogManager.getLogger(ThematicMappingAction.class);
    private static final String ERROR_MSG = "Unable to render requested thematic map";
    private static final String SRS_PARAM_NAME = "srs";
    private static final String X_PARAM_NAME = "x";
    private static final String Y_PARAM_NAME = "y";
    private static final String ZOOM_PARAM_NAME = "zoom";
    private static final String WIDTH_PARAM_NAME = "width";
    private static final String HEIGHT_PARAM_NAME = "height";
    private static final String OUTPUT_PARAM_NAME = "output";

    public ThematicMappingAction(MappingService mappingService) {
        this.mappingService = mappingService;
        this.thematicMapBuilderFactory = new ThematicMapBuilderFactory();
    }

    private MappingService getMappingService() {
        return mappingService;
    }

    public void execute(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException, IOException
    {
        RenderMapResponse renderMapResponse = getThematicMap(request);
        String output = request.getParameter(OUTPUT_PARAM_NAME);
        if (null == renderMapResponse) {
            throw new MapNotFoundException(ERROR_MSG );
        }
        byte[] image = renderMapResponse.getMapImage().getImage();
        response.setContentType(output);
        response.setHeader("Cache-Control", "no-cache;max-age=0");   // setting no-cache as otherwise mozilla showing incorrect cached response
        response.setContentLength(image.length);
        response.getOutputStream().write(image);
        response.flushBuffer();
    }

    private RenderMapResponse getThematicMap(HttpServletRequest request) throws IOException {
        RenderMapParams renderMapParams = createRenderMapParams(request);
        Map mapObj = thematicMapBuilderFactory.createThematicMap(request);
        RenderMapResponse renderMapResponse = null;
        if(mapObj != null){
            try {
                renderMapResponse = getMappingService().renderMap(renderMapParams, mapObj);
            } catch (ServiceException e) {
                logger.error("Encountered ServiceException while rendering end-user thematic map ",e);
                throw new Error("Encountered ServiceException while rendering end-user thematic map " , e);
            }
        }
        return renderMapResponse;
    }

    private RenderMapParams createRenderMapParams(HttpServletRequest request) {
        RenderMapParams params = new RenderMapParams();
        params.setSrs(request.getParameter(SRS_PARAM_NAME));
        params.setHeight(request.getParameter(HEIGHT_PARAM_NAME));
        params.setWidth(request.getParameter(WIDTH_PARAM_NAME));
        params.setXPos(request.getParameter(X_PARAM_NAME));
        params.setYPos(request.getParameter(Y_PARAM_NAME));
        params.setZoom(request.getParameter(ZOOM_PARAM_NAME));
        params.setImageMimeType(request.getParameter(OUTPUT_PARAM_NAME));
        params.setReturnImage(true);
        params.setRendering(Rendering.QUALITY);
        return params;
    }
}
