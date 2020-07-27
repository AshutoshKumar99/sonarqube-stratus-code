/**
 * 
 */
package com.pb.stratus.controller.print.render;

import com.pb.stratus.controller.print.LayerRenderParams;
import com.pb.stratus.controller.print.RenderException;
import com.pb.stratus.controller.print.TileCompositor;
import com.pb.stratus.controller.print.TileSet;
import com.pb.stratus.controller.tile.service.TileService;

import javax.servlet.ServletException;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Facilitates rendering of TMS (Tile Map Service) Maps.
 * 
 */
public class TMSRenderer implements LayerRenderer {
	/**
	 * Facilitates tile composition
	 */
	private TileCompositor tileCompositor;

	/**
	 * For the tiling service
	 */
	private TileService tileService;

	public TMSRenderer(TileCompositor tileCompositor,
                       TileService tileService) {
		this.tileCompositor = tileCompositor;
		this.tileService = tileService;
	}

	@Override
	public BufferedImage render(LayerRenderParams layerRenderParams) {
		TileSet tileSet = null;

		try {
			/**
			 * Fetch the tiles to stitch
			 */
			tileSet = tileService.getTileSet(layerRenderParams);
		} catch (IOException iox) {
			throw new RenderException(iox);
		} catch (ServletException ex) {
			throw new RenderException(ex);
		}

		return tileCompositor
				.compose(tileSet, layerRenderParams.getImageSize());
	}

}
