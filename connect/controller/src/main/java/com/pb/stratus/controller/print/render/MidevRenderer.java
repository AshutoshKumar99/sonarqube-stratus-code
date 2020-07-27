package com.pb.stratus.controller.print.render;

import com.mapinfo.midev.service.mapping.v1.MapImage;
import com.mapinfo.midev.service.mapping.v1.RenderMapResponse;
import com.mapinfo.midev.service.mapping.v1.Rendering;
import com.pb.stratus.controller.info.LayerInfoBean;
import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.LayerRenderParams;
import com.pb.stratus.controller.service.MappingService;
import com.pb.stratus.controller.service.RenderMapParams;
import com.pb.stratus.controller.service.RenderNamedMapParams;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import uk.co.graphdata.utilities.contract.Contract;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders the specified map as a BufferedImage, by calling an MiDeveloper
 * MappingService (which is assigned in the constructor).
 */
public class MidevRenderer implements LayerRenderer
{
    private static final Logger logger = LogManager.getLogger(MidevRenderer.class);
    private MappingService mappingService;

    public MidevRenderer(MappingService mappingService)
    {
        this.mappingService = mappingService;
    }

    /**
     * Calls an MiDeveloper mapping service to request a map image.
     * @param layerRenderParams An object containing the parameters needed to render this layer
     * @return BufferedImage The requested layer, rendered to a BufferedImage.
     */
    public BufferedImage render(LayerRenderParams layerRenderParams)
    {
        Contract.pre((layerRenderParams != null), "layerRenderParams cannot be null");
        List<LayerInfoBean> namedLayersList = layerRenderParams.getNamedLayersList();
        BoundingBox boundingBox = layerRenderParams.getBoundingBox();
        Dimension imageSize = layerRenderParams.getImageSize();

        Contract.pre(!namedLayersList.isEmpty(), "layers list cannot be empty");
        Contract.pre((boundingBox != null), "boundingBox cannot be null");
        Contract.pre((imageSize != null), "imageSize cannot be null");

        RenderMapParams params = createRenderMapParams(layerRenderParams.getNamedLayersList(),
                layerRenderParams.getBoundingBox(), layerRenderParams.getImageSize(),layerRenderParams.isRenderLabelLayer());
        BufferedImage renderedBufferedImage = new BufferedImage(imageSize.width,
                imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
        try
        {
            //RenderNamedMapResponse namedMapResponse = mappingService.getNamedMap(params);
            RenderMapResponse namedLayerResponse = mappingService.getLayerMap(params);
            MapImage mapImage = namedLayerResponse.getMapImage();
            renderedBufferedImage = this.getBufferedImageFromMapImage(mapImage);
        }
        catch (Exception e)
        {
            logger.warn("Exception while requesting the map image for layer list of "
                    + namedLayersList.get(0).getLayerMapPath() + ". Continuing with empty image", e);
        }
        return renderedBufferedImage;
    }

    /**
     * This requires us to pass in the requested image size, as we don't get
     * this info back as part of the RenderNamedMapResponse as far as I can see.
     */
    private BufferedImage getBufferedImageFromMapImage(MapImage mapImage)
            throws IOException
    {
        BufferedImage buffImage =
                ImageIO.read(new ByteArrayInputStream(mapImage.getImage()));
        return buffImage;
    }

    private RenderNamedMapParams createRenderNamedMapParams(String layerName,
                                                            BoundingBox boundingBox, Dimension imageSize)
    {
        RenderNamedMapParams params = new RenderNamedMapParams();
        //XXX Find out which of the following parameters are absolutely
        //required and which can be omitted, if any.
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
        params.setMapName(layerName);
        params.setBoundsLeft(boundingBox.getWest());
        params.setBoundsRight(boundingBox.getEast());
        params.setBoundsTop(boundingBox.getNorth());
        params.setBoundsBottom(boundingBox.getSouth());
        return params;
    }

    private RenderMapParams createRenderMapParams(List<LayerInfoBean> namedLayers,
                                                  BoundingBox boundingBox, Dimension imageSize,boolean renderLabelLayer)
    {
        RenderMapParams params = new RenderMapParams();
        //XXX Find out which of the following parameters are absolutely
        //required and which can be omitted, if any.
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
        List<String> layersList = getLayersList(namedLayers,renderLabelLayer);
        params.setMapLayers(layersList);
        params.setBoundsLeft(boundingBox.getWest());
        params.setBoundsRight(boundingBox.getEast());
        params.setBoundsTop(boundingBox.getNorth());
        params.setBoundsBottom(boundingBox.getSouth());
        return params;
    }

    /**
     * CONN-186001
     * Since we now print label layers separately from the named Layers hence we push only Named Feature Layers here.
     * @param namedLayers
     * @return
     */
    private List<String> getLayersList(List<LayerInfoBean> namedLayers , boolean renderLabelLayer) {
        List<String> layers = new ArrayList<String>();
        for (LayerInfoBean layerInfo : namedLayers) {

            if(layerInfo.getLayerPath() != null && !renderLabelLayer)
            {
                layers.add(layerInfo.getLayerPath());
            }

            if(layerInfo.getLayerLabelPath() != null && renderLabelLayer)
            {
                layers.add(layerInfo.getLayerLabelPath());
            }

        }
        return layers;
    }

    public boolean supportsSpecificResolutions()
    {
        return false;
    }

    public double[] getResolutions()
    {
        return null;
    }
}
