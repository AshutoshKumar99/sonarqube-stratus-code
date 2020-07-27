package com.pb.stratus.controller.print;

import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometries.v1.Pos;
import com.pb.stratus.controller.geometry.GeometryService;
import com.pb.stratus.controller.print.config.LayerServiceType;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.content.LayerBean;
import com.pb.stratus.controller.print.image.ImageReader;
import com.pb.stratus.controller.tile.service.BingTileService;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BingAttributionRenderer {
	
	 private static final Logger logger = LogManager
     .getLogger(BingAttributionRenderer.class);
	private GeometryService geometryService;
	private ImageReader imageReader;
	private String COVERAGE_BBOX_SRS = "EPSG:4326";
	private int bingLogoWidth = 93;
	private int bingLogoHeight = 29;
	private int LOGO_PADDING_RIGHT = 10;
	private int COPYRIGHT_PADDING_RIGHT = 35;
	private int LOGO_PADDING_BOTTOM = 35;
	private int COPYRIGHT_PADDING_BOTTOM = 10;
	private Font font = new Font("MS Gothic", Font.PLAIN, 9);
	private String BING_ROAD= "Bing Roads";

	public BingAttributionRenderer(GeometryService geometryService){
		
		this.geometryService = geometryService;
		this.imageReader = new ImageReader();
	}
	
	public BufferedImage renderAttribution(Dimension imageSize,
			BoundingBox boundingBox, List<LayerBean> layers, MapConfig mapConfig, int zoomLevel){
		BufferedImage canvas = new BufferedImage(imageSize.width,
                imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = canvas.createGraphics();
        g.setFont(font);
        String bingLayerName = getBingLayerName(layers, mapConfig);
        BingImageryMetaData metadata = BingImageryMetaDataService.getImageryMetadataForAttribution(bingLayerName);
        try{
	        BufferedImage bingLogo = getLogo(metadata);
	        String copyrightString = getBingCopyrightString(metadata, boundingBox, zoomLevel);
	        drawLogo(g, bingLogo, imageSize);
	        drawCopyright(g,copyrightString, imageSize, bingLayerName);
        }catch (Exception e) {
			logger.info(e.getMessage());
		}
        finally{
        	g.dispose();
        }
		return canvas;
	}

	private void drawCopyright(Graphics2D g, String copyrightString, Dimension imageSize, String bingLayername) {
		FontMetrics fontMetrics = g.getFontMetrics();
		fontMetrics.stringWidth(copyrightString);
		fontMetrics.getHeight();
		int x = imageSize.width - COPYRIGHT_PADDING_RIGHT - fontMetrics.stringWidth(copyrightString); 
		int y = imageSize.height - fontMetrics.getHeight() - COPYRIGHT_PADDING_BOTTOM;
		if(BING_ROAD.equals(bingLayername)){
			g.setColor(Color.BLACK);
		}else{
			g.setColor(Color.WHITE);
		}
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.6f));
		g.drawString(copyrightString, x, y);
		
	}

	private void drawLogo(Graphics2D g, BufferedImage bingLogo, Dimension imageSize) {
		int x = imageSize.width - LOGO_PADDING_RIGHT - bingLogoWidth;
		int y = imageSize.height - LOGO_PADDING_BOTTOM - bingLogoHeight;
		
		g.drawImage(bingLogo, null, x, y);
		
	}

	private BufferedImage getLogo(BingImageryMetaData metadata) throws MalformedURLException, IOException {
		String logoUrlString = metadata.getLogoUrl();
		
		return imageReader.readFromUrl(new URL(logoUrlString));
	}

	private String getBingLayerName(List<LayerBean> layers, MapConfig mapConfig) {
		 
		for (LayerBean layer : layers){
			MapDefinition def = mapConfig.getMapDefinitionByMapName(layer.getName());
			if (def.getService().equals(LayerServiceType.BING)){
				return layer.getName();
			}
		}
		return "";
	}

	private String getBingCopyrightString(BingImageryMetaData metadata, BoundingBox boundingBox, int zoomLevel){
		
		String copyright = "";
		boundingBox = transformBoundingBox(boundingBox);
		for(ImageryProvider provider : metadata.getProviders()){
			for(ProviderCoverageArea coverage : provider.getCoverageAreas()){
				double[] bbox = coverage.getBoundingBox();
				BoundingBox coverageBox = new BoundingBox(bbox[3], bbox[1], bbox[0], bbox[2], COVERAGE_BBOX_SRS);
				if(boundingBox.intersects(coverageBox, true) &&
                        zoomLevel <= coverage.getZoomMax() && zoomLevel >= coverage.getZoomMin()){
					copyright += provider.getAttribution() + " ";
				}
			}
		}
		return copyright;
	}
	
	private BoundingBox transformBoundingBox(BoundingBox boundingBox) {
		List<com.mapinfo.midev.service.geometries.v1.Point> points = new ArrayList<com.mapinfo.midev.service.geometries.v1.Point>();
		Pos pos1 = new Pos();
		com.mapinfo.midev.service.geometries.v1.Point point1 = new com.mapinfo.midev.service.geometries.v1.Point();
		Pos pos2 = new Pos();
		com.mapinfo.midev.service.geometries.v1.Point point2 = new com.mapinfo.midev.service.geometries.v1.Point();
		pos1.setX(boundingBox.getWest());
		pos1.setY(boundingBox.getNorth());
		point1.setPos(pos1);
		point1.setSrsName(BingTileService.BING_SRS);
		points.add(point1);
		pos2.setX(boundingBox.getEast());
		pos2.setY(boundingBox.getSouth());
		point2.setPos(pos2);
		point2.setSrsName(BingTileService.BING_SRS);
		points.add(point2);
		
		List<Geometry> transformedPoints = geometryService.transformPoints(points, COVERAGE_BBOX_SRS);
		point1 = (com.mapinfo.midev.service.geometries.v1.Point)transformedPoints.get(0);
		point2 = (com.mapinfo.midev.service.geometries.v1.Point)transformedPoints.get(1);
		BoundingBox transformedBoundingBox = new BoundingBox(point1.getPos().getY(), point2.getPos().getY(), point1.getPos().getX(), point2.getPos().getX(), COVERAGE_BBOX_SRS); 
		return transformedBoundingBox;
	}

	public boolean isBingBasemap(MapConfig mapConfig)
    {
        List<MapDefinition> mapDefinitions = mapConfig.getMapDefinitions();
        for (MapDefinition mapDef : mapDefinitions)
        {
            if (mapDef.getService() == LayerServiceType.BING)
            {
                return true;
            }
        }
        return false;
    }

}
