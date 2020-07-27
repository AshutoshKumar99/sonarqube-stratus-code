package com.pb.stratus.controller.print;

import com.pb.stratus.controller.geometry.GeometryService;
import com.pb.stratus.controller.print.config.LayerServiceType;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.content.LayerBean;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class OpenStreetMapAttributionRenderer {
	
	private static final Logger logger = LogManager.getLogger(OpenStreetMapAttributionRenderer.class);
	private GeometryService geometryService;
	private int COPYRIGHT_PADDING_RIGHT = 35;
	private int COPYRIGHT_PADDING_BOTTOM = 10;
	private Font font = new Font("MS Gothic", Font.PLAIN, 9);
	private String copyrightString = "\u00A9 www.openstreetmap.org contributors, www.creativecommons.org";

	public OpenStreetMapAttributionRenderer(GeometryService geometryService)
	{
		this.geometryService = geometryService;
	}
	
	public BufferedImage renderAttribution(Dimension imageSize,
			BoundingBox boundingBox, List<LayerBean> layers, MapConfig mapConfig, int zoomLevel)
	{
		BufferedImage canvas = new BufferedImage(imageSize.width,
                imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = canvas.createGraphics();
        g.setFont(font);
        try
        {
	        drawCopyright(g,copyrightString, imageSize);
        }
        catch (Exception e)
        {
			logger.info(e.getMessage());
		}
        finally{
        	g.dispose();
        }
		return canvas;
	}

	private void drawCopyright(Graphics2D g, String copyrightString, Dimension imageSize)
	{
		FontMetrics fontMetrics = g.getFontMetrics();
		fontMetrics.stringWidth(copyrightString);
		fontMetrics.getHeight();
		int x = imageSize.width - COPYRIGHT_PADDING_RIGHT - fontMetrics.stringWidth(copyrightString); 
		int y = imageSize.height - fontMetrics.getHeight() - COPYRIGHT_PADDING_BOTTOM;
		g.setColor(Color.BLACK);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.8f));
		g.drawString(copyrightString, x, y);
		
	}

	public boolean isOpenStreetBasemap(MapConfig mapConfig)
    {
        List<MapDefinition> mapDefinitions = mapConfig.getMapDefinitions();
        for (MapDefinition mapDef : mapDefinitions)
        {
            if (mapDef.getService() == LayerServiceType.OSM)
            {
                return true;
            }
        }
        return false;
    }

}
