package com.pb.stratus.controller.print.render;

import com.mapinfo.midev.service.mapping.v1.MapImage;
import com.mapinfo.midev.service.mapping.v1.RenderMapResponse;
import com.mapinfo.midev.service.mapping.v1.Rendering;
import com.mapinfo.midev.service.mapping.ws.v1.ServiceException;
import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.LayerRenderParams;
import com.pb.stratus.controller.print.content.ThematicMap;
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
 * Created with IntelliJ IDEA.
 * User: ra007gi
 * Date: 10/28/14
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThematicRenderer implements LayerRenderer{

    private ThematicMap thematicMap;
    private MappingService mappingService;
    private static final Logger logger = LogManager.getLogger(ThematicRenderer.class);

    public ThematicRenderer(MappingService mappingService) {
        this.mappingService = mappingService;
    }

    public void setThematicMap(ThematicMap thematicMap) {
        this.thematicMap = thematicMap;
    }

    /**
     * Calls an MiDeveloper mapping service to request a map image for end user thematic maps.
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

        RenderMapParams renderMapParams = createRenderMapParams(layerRenderParams.getBoundingBox(), layerRenderParams.getImageSize());

        try {
            RenderMapResponse renderMapResponse = mappingService.renderMap(renderMapParams, thematicMap.getMapObject());
            MapImage mapImage = renderMapResponse.getMapImage();
            renderedBufferedImage = this.getBufferedImageFromMapImage(mapImage);
        } catch (ServiceException e) {
            logger.error("Encountered ServiceException while getting map image for Print of end-user thematic map: " + thematicMap.getName(), e);
        }catch (IOException e) {
            logger.error("Encountered IOException while generating buffered map image for Print of end-user thematic map: " + thematicMap.getName(), e);
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
