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
 * Render an Bing bufferedImage with the given bounds and image size. The bing
 * renderer will covert bounding box to the Bing coordinate system and set the
 * necessary scale factor.
 */
public class BingRenderer implements LayerRenderer {

	private TileCompositor tileCompositor;

	private TileService tileService;

	public BingRenderer(TileCompositor tileCompositor, TileService tileService) {
		this.tileCompositor = tileCompositor;
		this.tileService = tileService;
	}

	/**
	 * Render a bing map image using the bounds and imagesize as its dimensions
	 * 
	 * @param layerName
	 * @param mapBoundingBox
	 * @param imageSize
	 * @return
	 */
	public BufferedImage render(LayerRenderParams layerRenderParams) {
		TileSet tileSet;
		MapDefinition def = layerRenderParams.getMapConfig()
				.getMapDefinitionByMapName(layerRenderParams.getLayerName());
		String imageMimeType = def.getImageMimeType();
		if (StringUtils.isBlank(imageMimeType)) {
			imageMimeType = "image/gif";
		}
		try {
			tileSet = tileService.getTileSet(layerRenderParams.getLayerName(),
					layerRenderParams.getBoundingBox(),
					layerRenderParams.getZoomLevel(), imageMimeType);
		} catch (IOException iox) {
			throw new RenderException(iox);
		} catch (ServletException ex) {
			throw new RenderException(ex);
		}
		return tileCompositor
				.compose(tileSet, layerRenderParams.getImageSize());
	}

}
