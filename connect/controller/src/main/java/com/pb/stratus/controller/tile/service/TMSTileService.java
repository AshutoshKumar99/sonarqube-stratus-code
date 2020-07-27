/**
 * 
 */
package com.pb.stratus.controller.tile.service;

import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.LayerRenderParams;
import com.pb.stratus.controller.print.TileSet;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapConfigDefinition;
import com.pb.stratus.controller.print.image.ImageReader;
import com.pb.stratus.core.configuration.ControllerConfiguration;

import javax.servlet.ServletException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class TMSTileService extends TileService {
	private final static int TILE_SIZE = 256;
	private ControllerConfiguration config;

	private ImageReader imageReader;

	public TMSTileService(ControllerConfiguration config) {
        super(config.getMaximumNumberOfPrintableTiles());
		imageReader = new ImageReader();
	}

	@Override
	public TileSet getTileSet(LayerRenderParams renderParam)
			throws IOException, ServletException {
		MapConfig mapCfg = renderParam.getMapConfig();
		MapConfigDefinition cfgDefinition = mapCfg.getMapConfigDefinition();
		int level = renderParam.getZoomLevel();
		BoundingBox bounds = renderParam.getBoundingBox();
		String layerName = renderParam.getLayerName();
		String imageFormat = renderParam.getLayer().getFormat();
        List<String> urlArray = renderParam.getLayer().getUrlArray();

		BoundingBox maxBounds = new BoundingBox(
				cfgDefinition.getMaxBoundsTop(),
				cfgDefinition.getMaxBoundsBottom(),
				cfgDefinition.getMaxBoundsLeft(),
				cfgDefinition.getMaxBoundsRight(),
				cfgDefinition.getProjection());

		double resolution = maxBounds.getWidth() / Math.pow(2, level);

		int startRow = getStartRow(maxBounds, bounds, resolution);
		int endRow = getEndRow(maxBounds, bounds, resolution);
		int startCol = getStartCol(maxBounds, bounds, resolution);
		int endCol = getEndCol(maxBounds, bounds, resolution);
		assertMaxBoundAndBoundsHaveEqualSrs(bounds, maxBounds, layerName);
		assertNotTooManyTiles(startRow, endRow, startCol, endCol);

		List<BufferedImage> tiles = getTiles(layerName, startRow, startCol,
				endRow, endCol, level, TILE_SIZE,urlArray,imageFormat);

		int rows = endRow - startRow + 1;
		int cols = endCol - startCol + 1;
		Rectangle pixelBounds = getPixelBounds(maxBounds, bounds, resolution,
				TILE_SIZE, rows, cols);

		return new TileSet(tiles, rows, cols, pixelBounds, TILE_SIZE);

	}

	@Override
	public TileSet getTileSet(String layerName, BoundingBox bounds, int level,
			String imageMimeType) throws IOException, ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Fetch the tiles and return the information
	 *
	 * @param layerName
	 *            - The name of the layer
	 * @param startRow
	 *            - The start row
	 * @param startCol
	 *            - Column
	 * @param endRow
	 *            - Last row
	 * @param endCol
	 *            - last col
	 * @param level
	 *            - Zoom level
	 * @param tileSize
	 *            - Size of the tile
     * @param urlArray
     *            - List of TMS urls
	 * @return
	 * @throws java.io.IOException
	 */
	private List<BufferedImage> getTiles(String layerName, int startRow,
			int startCol, int endRow, int endCol, int level, int tileSize,List<String> urlArray,String format) throws IOException {
		List<BufferedImage> tiles = new LinkedList<BufferedImage>();
		BufferedImage blankTile = createBlankTile(tileSize);

		int maxTiles = (int) Math.pow(2, level);
        for (int row = startRow; row <= endRow; row++) {
			for (int col = startCol; col <= endCol; col++) {

				if (row < 0 || col < 0 || row >= maxTiles || col >= maxTiles) {
					tiles.add(blankTile);
				} else {
					tiles.add(fetchTile(row, col, level,urlArray,format));
				}
			}
		}
		return tiles;

	}

	private BufferedImage fetchTile(int row, int col, int level,List<String> urlArray,String format) {
        BufferedImage image = null;
		try {
            //TMS has a flipped y coordinate when compared to xyz maps
            row = ((Double)(Math.pow(2, level))).intValue() - row - 1;
            String url = String.format("%s/%d/%d/%d.%s", urlArray.get(0), level, col, row,format);
            URL finalUrl =  new URL(url);
            image = imageReader.readFromUrl(finalUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        if(image == null && urlArray.size()>1){
            //try to retrieve image from second sub-domain if present
            try {
                String url = String.format("%s/%d/%d/%d.%s", urlArray.get(1), level, col, row,format);
                URL finalUrl =  new URL(url);
                image = imageReader.readFromUrl(finalUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return image;
	}
}
