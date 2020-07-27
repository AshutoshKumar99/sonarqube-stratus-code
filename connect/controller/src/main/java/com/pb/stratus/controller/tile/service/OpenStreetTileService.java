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
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * For Open Street Map Tiles
 * 
 * @author sa021sh
 * 
 */
public class OpenStreetTileService extends TileService {
	private final static String TILE_URL = "http://tile.openstreetmap.org/";
	private final static int TILE_SIZE = 256;
	private ControllerConfiguration config;
	
	public static final double MAX_NORTH_BOUND = 20037508;
	public static final double MAX_SOUTH_BOUND = -20037508;
	public static final double MAX_EAST_BOUND = 20037508;
	public static final double MAX_WEST_BOUND = -20037508;
	public static final String OSM_SRS = "epsg:3857";

	private ImageReader imageReader;

	public OpenStreetTileService(ControllerConfiguration config) {
        super(config.getMaximumNumberOfPrintableTiles());
		imageReader = new ImageReader();
	}

	@Override
	public TileSet getTileSet(LayerRenderParams renderParam)
			throws IOException, ServletException {
		MapConfig mapCfg = renderParam.getMapConfig();
		MapConfigDefinition cfgDefinition = mapCfg.getMapConfigDefinition();
		// openstreetmap is zero based zoom index. 
		int level = renderParam.getZoomLevel();
		BoundingBox bounds = renderParam.getBoundingBox();
		String layerName = renderParam.getLayerName();

		String imageMimeType = mapCfg.getMapDefinitionByMapName(layerName)
				.getImageMimeType();

		if (StringUtils.isBlank(imageMimeType)) {
			imageMimeType = "image/gif";
		}

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
				endRow, endCol, level, TILE_SIZE, imageMimeType);

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
	 * @param imageMimeType
	 *            - Image type
	 * @return
	 * @throws IOException
	 */
	private List<BufferedImage> getTiles(String layerName, int startRow,
			int startCol, int endRow, int endCol, int level, int tileSize,
			String imageMimeType) throws IOException {
		List<BufferedImage> tiles = new LinkedList<BufferedImage>();
		BufferedImage blankTile = createBlankTile(tileSize);

		int maxTiles = (int) Math.pow(2, level);

		for (int row = startRow; row <= endRow; row++) {
			for (int col = startCol; col <= endCol; col++) {
				if (row < 0 || col < 0 || row >= maxTiles || col >= maxTiles) {
					tiles.add(blankTile);
				} else {
					tiles.add(fetchOpenStreetTile(row, col, level));
				}
			}
		}
		return tiles;

	}

	private URL getTileURL(int row, int col, int level)
			throws MalformedURLException {
		String url = String.format("%s%d/%d/%d.png", TILE_URL, level, col, row);
		return new URL(url);
	}

	private BufferedImage fetchOpenStreetTile(int row, int col, int level) {

		try {
			return imageReader.readFromUrl(getTileURL(row, col, level));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
