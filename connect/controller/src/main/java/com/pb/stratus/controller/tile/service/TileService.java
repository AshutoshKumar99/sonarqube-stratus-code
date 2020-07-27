package com.pb.stratus.controller.tile.service;

import com.pb.stratus.controller.print.BoundingBox;
import com.pb.stratus.controller.print.LayerRenderParams;
import com.pb.stratus.controller.print.MaxNumberOfTilesExceededException;
import com.pb.stratus.controller.print.TileSet;

import javax.servlet.ServletException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A class to retrieve sets of contiguous tiles from a tile repository
 */
public abstract class TileService {

	private static final int DEFAULT_MAX_TILES = 300; //see CONN-17129 for setting tile limit to this value

	private int maxTiles;

	public TileService() {
		this.maxTiles = DEFAULT_MAX_TILES;
	}

	public TileService(int maxTiles) {
		this.maxTiles = maxTiles;
	}

	/**
	 * This is an overridden abstraction of fetching the tile set information
	 * 
	 * @param renderParam
	 *            - All the information bits to create the tile set
	 * @return {@link TileSet}
	 * @throws IOException
	 * @throws ServletException
	 */
	public abstract TileSet getTileSet(LayerRenderParams renderParam)
			throws IOException, ServletException;

	/**
	 * Retrieves a TileSet that satisfies the given parameters.
	 * 
	 * @param layerName
	 *            the name of the tile layer
	 * @param bounds
	 *            the bounds the returned TileSet is supposed to cover
	 * @param level
	 *            the zoom level the tiles are requested for
	 * @return a set of tiles from the given layer at the given zoom level
	 *         covering the given bounds
	 * @throws IOException
	 *             if one or more tiles couldn't be retrieved from the tile
	 *             repository
	 */
	public abstract TileSet getTileSet(String layerName, BoundingBox bounds,
			int level, String imageMimeType) throws IOException,
			ServletException;

	protected void assertMaxBoundAndBoundsHaveEqualSrs(BoundingBox bounds,
			BoundingBox maxBounds, String layerName) {
		if (!bounds.getSrs().equalsIgnoreCase(maxBounds.getSrs())) {
			throw new IllegalArgumentException("Requested SRS different "
					+ "from SRS of tile layer '" + layerName + "'");
		}
	}

	protected void assertNotTooManyTiles(int startRow, int endRow,
			int startCol, int endCol) {
		int numTiles = (endRow - startRow + 1) * (endCol - startCol + 1);
		if (numTiles > maxTiles) {
			throw new MaxNumberOfTilesExceededException(maxTiles, numTiles);
		}
	}

	protected int getStartRow(BoundingBox maxBounds, BoundingBox bounds,
			double resolution) {
		return (int) Math.floor((maxBounds.getNorth() - bounds.getNorth())
				/ resolution);
	}

	protected int getEndRow(BoundingBox maxBounds, BoundingBox bounds,
			double resolution) {
		double y = maxBounds.getNorth() - bounds.getSouth();
		int endRow = (int) Math.floor(y / resolution);
		if (y % resolution == 0) {
			endRow -= 1;
		}
		return endRow;
	}

	protected int getStartCol(BoundingBox maxBounds, BoundingBox bounds,
			double resolution) {
		return (int) Math.floor((bounds.getWest() - maxBounds.getWest())
				/ resolution);
	}

	protected int getEndCol(BoundingBox maxBounds, BoundingBox bounds,
			double resolution) {
		double x = bounds.getWest() + bounds.getWidth() - maxBounds.getWest();
		int endCol = (int) Math.floor(x / resolution);
		if (x % resolution == 0) {
			endCol -= 1;
		}
		return endCol;
	}

	protected BufferedImage createBlankTile(int tileSize) {
		BufferedImage expectedImage = new BufferedImage(tileSize, tileSize,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = expectedImage.createGraphics();
		try {
			g.setBackground(Color.WHITE);
			g.clearRect(0, 0, tileSize, tileSize);
		} finally {
			g.dispose();
		}
		return expectedImage;
	}

	protected Rectangle getPixelBounds(BoundingBox maxBounds,
			BoundingBox bounds, double resolution, int tileSize, int rows,
			int cols) {
		maxBounds = expandMaxBoundsIfTooSmall(maxBounds, bounds, resolution);
		double x1 = bounds.getWest() - maxBounds.getWest();
		double pixelX1 = (x1 % resolution) / resolution * tileSize;
		double y1 = maxBounds.getNorth() - bounds.getNorth();
		double pixelY1 = (y1 % resolution) / resolution * tileSize;

		double x2 = bounds.getEast() - maxBounds.getWest();
		double pixelX2 = (x2 % resolution) / resolution * tileSize;
		double y2 = maxBounds.getNorth() - bounds.getSouth();
		double pixelY2 = (y2 % resolution) / resolution * tileSize;
		double width = cols * tileSize - pixelX1;
		if (pixelX2 > 0) {
			width -= (tileSize - pixelX2);
		}
		double height = rows * tileSize - pixelY1;
		if (pixelY2 > 0) {
			height -= (tileSize - pixelY2);
		}
		return new Rectangle((int) Math.round(pixelX1),
				(int) Math.round(pixelY1), (int) Math.round(width),
				(int) Math.round(height));
	}

	private BoundingBox expandMaxBoundsIfTooSmall(BoundingBox maxBounds,
			BoundingBox bounds, double resolution) {
		double north = maxBounds.getNorth();
		if (north < bounds.getNorth()) {
			north += Math.ceil((bounds.getNorth() - north) / resolution)
					* resolution;
		}
		double south = maxBounds.getSouth();
		if (south > bounds.getSouth()) {
			south -= Math.ceil((south - bounds.getSouth()) / resolution)
					* resolution;
		}
		double west = maxBounds.getWest();
		if (west > bounds.getWest()) {
			west -= Math.ceil((west - bounds.getWest()) / resolution)
					* resolution;
		}
		double east = maxBounds.getEast();
		if (east < bounds.getEast()) {
			east += Math.ceil((bounds.getEast() - east) / resolution)
					* resolution;
		}
		return new BoundingBox(north, south, west, east, maxBounds.getSrs());
	}

}
