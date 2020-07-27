package com.pb.stratus.controller.print.render;

import com.mapinfo.midev.service.mapping.v1.MapImage;
import com.mapinfo.midev.service.mapping.v1.RenderMapResponse;
import com.mapinfo.midev.service.mapping.v1.Rendering;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.LayerRenderParams;
import com.pb.stratus.controller.print.content.QueryResultOverlayMap;
import com.pb.stratus.controller.service.MappingService;
import com.pb.stratus.controller.service.RenderMapParams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.graphdata.utilities.contract.Contract;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by vi012gu on 2/20/2015.
 */
public class QueryResultRenderer implements LayerRenderer {

    private QueryResultOverlayMap queryResultOverlayMap;
    private MappingService mappingService;
    private static final Logger logger = LogManager.getLogger(QueryResultRenderer.class);

    public QueryResultRenderer(MappingService mappingService) {
        this.mappingService = mappingService;
    }

    public void setQueryResultOverlayMap(QueryResultOverlayMap queryResultOverlayMap) {
        this.queryResultOverlayMap = queryResultOverlayMap;
    }

    /**
     * Calls an MiDeveloper mapping service to request a map image for query results.
     * @param layerRenderParams An object containing the parameters needed to render this layer
     * @return BufferedImage The requested layer, rendered to a BufferedImage.
     */
    public BufferedImage render(LayerRenderParams layerRenderParams) {

        BoundingBox boundingBox = layerRenderParams.getBoundingBox();
        Dimension imageSize = layerRenderParams.getImageSize();
        Contract.pre((boundingBox != null), "boundingBox cannot be null");
        Contract.pre((imageSize != null), "imageSize cannot be null");

        BufferedImage renderedBufferedImage = new BufferedImage(imageSize.width,
                imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);

        RenderMapParams renderMapParams = createRenderMapParams(
                layerRenderParams.getBoundingBox(), layerRenderParams.getImageSize());

        try {
            RenderMapResponse renderMapResponse = mappingService.renderMap(renderMapParams,
                    queryResultOverlayMap.getMapObject());
            MapImage mapImage = renderMapResponse.getMapImage();
            renderedBufferedImage = this.getBufferedImageFromMapImage(mapImage);
        } catch (ServiceException e) {
            logger.error("Encountered ServiceException while getting map image for " +
                    "print of end-user thematic map: " + queryResultOverlayMap.getName(), e);
        }catch (IOException e) {
            logger.error("Encountered IOException while generating buffered map image for " +
                    "print of end-user thematic map: " + queryResultOverlayMap.getName(), e);
        }

        return renderedBufferedImage;
    }


    private RenderMapParams createRenderMapParams(BoundingBox boundingBox, Dimension imageSize)
    {
        RenderMapParams params = new RenderMapParams();
        params.setSrs(boundingBox.getSrs());
        Point2D center = boundingBox.getCenter();
        params.setXPos(center.getX());
        params.setYPos(center.getY());
        params.setHeight(imageSize.getHeight());
        params.setWidth(imageSize.getWidth());
        params.setZoom(boundingBox.getWidth());
        params.setImageMimeType("image/png");
        params.setReturnImage(true);
        params.setRendering(Rendering.QUALITY);
        return params;
    }


    private BufferedImage getBufferedImageFromMapImage(MapImage mapImage)
            throws IOException
    {
        BufferedImage buffImage =
                ImageIO.read(new ByteArrayInputStream(mapImage.getImage()));
        return buffImage;
    }
}
