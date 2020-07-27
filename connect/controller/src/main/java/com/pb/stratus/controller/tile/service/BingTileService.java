package com.pb.stratus.controller.tile.service;

import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.controller.print.*;
import com.pb.stratus.controller.print.image.ImageReader;
import com.pb.stratus.core.configuration.ControllerConfiguration;
import com.pb.stratus.security.core.util.AuthorizationUtils;

import javax.servlet.ServletException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class BingTileService extends TileService {

    private static final int TILE_SIZE = 256;

    private ImageReader imageReader;
    private BingUrlBuilder urlBuilder;
    private ControllerConfiguration config;
    private AuthorizationUtils authorizationUtils;

    public static final double MAX_NORTH_BOUND = 20037508;
    public static final double MAX_SOUTH_BOUND = -20037508;
    public static final double MAX_EAST_BOUND = 20037508;
    public static final double MAX_WEST_BOUND = -20037508;
    public static final String BING_SRS = "epsg:3857";

    /**
     * 
     * @param config
     */
    public BingTileService(ControllerConfiguration config, ImageReader imageReader,
            BingUrlBuilder urlBuilder, AuthorizationUtils authorizationUtils) {
        super(500);
        this.imageReader = imageReader;
        this.urlBuilder = urlBuilder;
        this.config = config;
        this.authorizationUtils = authorizationUtils;
    }

    @Override
    public TileSet getTileSet(String layerName, BoundingBox bounds, int level,
            String imageMimeType) throws IOException, ServletException {

        BoundingBox maxBounds = new BoundingBox(MAX_NORTH_BOUND, MAX_SOUTH_BOUND, MAX_WEST_BOUND,
                MAX_EAST_BOUND, BING_SRS);
        assertMaxBoundAndBoundsHaveEqualSrs(bounds, maxBounds, layerName);
        double resolution = maxBounds.getWidth() / Math.pow(2, level);
        int startRow = getStartRow(maxBounds, bounds, resolution);
        int endRow = getEndRow(maxBounds, bounds, resolution);
        int startCol = getStartCol(maxBounds, bounds, resolution);
        int endCol = getEndCol(maxBounds, bounds, resolution);
        assertNotTooManyTiles(startRow, endRow, startCol, endCol);

        List<BufferedImage> tiles = getTiles(layerName, startRow, startCol,
                endRow, endCol, level, TILE_SIZE, imageMimeType);
        int rows = endRow - startRow + 1;
        int cols = endCol - startCol + 1;
        Rectangle pixelBounds = getPixelBounds(maxBounds, bounds, resolution,
                TILE_SIZE, rows, cols);
        return new TileSet(tiles, rows, cols, pixelBounds, TILE_SIZE);

    }

    /**
     * 
     * @param layerName
     * @param startRow
     * @param startCol
     * @param endRow
     * @param endCol
     * @param level
     * @param tileSize
     * @param imageMimeType
     * @return
     * @throws IOException
     */
    private List<BufferedImage> getTiles(String layerName, int startRow,
            int startCol, int endRow, int endCol, int level, int tileSize,
            String imageMimeType) throws IOException {
        List<BufferedImage> tiles = new LinkedList<BufferedImage>();

        BufferedImage blankTile = createBlankTile(tileSize);

        int maxTiles = (int) Math.pow(2, level);

        BingImageryMetaData metadata = BingImageryMetaDataService.getImageryMetadata(layerName, config, authorizationUtils);
        int subDomainIndex = 0;

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                if (row < 0 || col < 0 || row >= maxTiles || col >= maxTiles) {
                    tiles.add(blankTile);
                } else {
                    tiles.add(retrieveBingTile(metadata.getImageUrl(), row,
                            col, level, metadata.getImageUrlSubdomains()
                                    .get(subDomainIndex)));
                    if (++subDomainIndex >= metadata.getImageUrlSubdomains()
                            .size()) {
                        subDomainIndex = 0;
                    }
                }
            }
        }
        return tiles;
    }

    /**
     * Converts tile XY coordinates into a QuadKey at a specified level of
     * detail.
     * 
     * @param tileX
     *            Tile X coordinate.
     * @param tileY
     *            Tile Y coordinate.
     * @param levelOfDetail
     *            Level of detail, from 1 (lowest detail) to 23 (highest
     *            detail).
     * @return a string containing the QuadKey.
     */
    private String TileXYToQuadKey(int tileX, int tileY, int levelOfDetail) {
        StringBuilder quadKey = new StringBuilder();
        for (int i = levelOfDetail; i > 0; i--) {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((tileX & mask) != 0) {
                digit++;
            }
            if ((tileY & mask) != 0) {
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }

    /**
     * Get a Bing map Tile for given row column and zoom level.
     * 
     * @param row
     *            tile Y coordinate.
     * @param col
     *            tile X coordinate.
     * @param level
     *            zoom level
     * @param subdomain
     *            subdomain to be used to get bing tile
     * @return Tile image.
     */
    private BufferedImage retrieveBingTile(String url, int row, int col,
            int level, String subdomain) throws RenderException {
        String locale = LocaleResolver.getLocale().toString();
        String culture = locale + "-" + locale.toUpperCase();
        try {
            return imageReader.readFromUrl(urlBuilder.constructBingTileURL(url,
                    TileXYToQuadKey(col, row, level), subdomain, culture));
        } catch (IOException ioe) {
            throw new RenderException(ioe);
        }
    }

    @Override
    public TileSet getTileSet(LayerRenderParams renderParam)
            throws IOException, ServletException {
        // TODO Auto-generated method stub
        return null;
    }
}
