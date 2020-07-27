package com.pb.stratus.controller.print.render;

import com.pb.stratus.controller.print.LayerRenderParams;
import com.pb.stratus.controller.print.RenderException;
import com.pb.stratus.controller.print.TileCompositor;
import com.pb.stratus.controller.print.TileSet;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.tile.service.TileService;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 
 * @author vi001ty
 *
 */
public class MiDevTileLayerRenderer implements LayerRenderer
{
    
    private TileCompositor tileCompositor;
    private TileService tileService;
    
    /**
     * 
     * @param tileCompositor
     * @param tileService
     */
    public MiDevTileLayerRenderer(TileCompositor tileCompositor, 
            TileService tileService)
    {
        this.tileCompositor = tileCompositor;
        this.tileService = tileService;
    }

    /**
     * 
     */
    public BufferedImage render(LayerRenderParams layerRenderParams)
    {
        TileSet tileSet;
        MapDefinition def = layerRenderParams.getMapConfig()
                .getMapDefinitionByMapName(layerRenderParams.getLayerName());
        String imageMimeType = def.getImageMimeType();
        if (StringUtils.isBlank(imageMimeType))
        {
            imageMimeType = "image/gif";
        }
        try 
        {
            //ss tileservice is 1 zoomindex based,so print system requires 1 less zoom level.  
        	tileSet = tileService.getTileSet(
                    layerRenderParams.getLayerName(), 
                    layerRenderParams.getBoundingBox(), 
                    layerRenderParams.getZoomLevel()- 1 , imageMimeType);
        }
        catch (IOException iox)
        {
            throw new RenderException(iox);
        }catch(ServletException ex){
            
            throw new RenderException(ex);
        }
        if(tileSet == null){
            return null;
        }
        return tileCompositor.compose(tileSet, 
                layerRenderParams.getImageSize());
    }
    
}
