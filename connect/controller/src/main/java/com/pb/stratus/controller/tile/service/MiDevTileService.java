package com.pb.stratus.controller.tile.service;

import com.pb.stratus.controller.print.*;

import javax.servlet.ServletException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MiDevTileService extends TileService {

	private TileRepository tileRepository;

	public MiDevTileService(TileRepository tileRepository, int maxTiles) {
		super(maxTiles);
		this.tileRepository = tileRepository;
	}

	public TileSet getTileSet(String layerName, BoundingBox bounds, int level,
			String imageMimeType) throws IOException, ServletException {
		TileLayerDescription desc = tileRepository.describe(layerName);
        if(desc == null){
            return null;
        }
		BoundingBox maxBounds = desc.getMaxBounds();
		assertMaxBoundAndBoundsHaveEqualSrs(bounds, maxBounds, layerName);
		double resolution = maxBounds.getWidth() / Math.pow(2, level);
		int startRow = getStartRow(maxBounds, bounds, resolution);
		int endRow = getEndRow(maxBounds, bounds, resolution);
		int startCol = getStartCol(maxBounds, bounds, resolution);
		int endCol = getEndCol(maxBounds, bounds, resolution);
		assertNotTooManyTiles(startRow, endRow, startCol, endCol);
		List<BufferedImage> tiles = getTiles(layerName, startRow, startCol,
				endRow, endCol, level, desc.getTileSize(), imageMimeType);
		int rows = endRow - startRow + 1;
		int cols = endCol - startCol + 1;
		Rectangle pixelBounds = getPixelBounds(maxBounds, bounds, resolution,
				desc.getTileSize(), rows, cols);
		return new TileSet(tiles, rows, cols, pixelBounds, desc.getTileSize());
	}

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
					tiles.add(tileRepository.getTile(layerName, row, col,
							level, imageMimeType));
				}
			}
		}
		return tiles;
	}

	@Override
	public TileSet getTileSet(LayerRenderParams renderParam)
			throws IOException, ServletException {

		return null;
	}

}
