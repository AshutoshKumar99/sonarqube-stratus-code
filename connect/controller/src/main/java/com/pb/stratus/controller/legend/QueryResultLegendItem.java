package com.pb.stratus.controller.legend;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by vi012gu on 2/23/2015.
 */
public class QueryResultLegendItem extends LegendItem {

    protected QueryResultLegendItem(String friendlyName, String legendType) {
        super(friendlyName, legendType);
    }

    @Override
    protected Collection<? extends BufferedImage> getIcons() {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        // Types of query result layers -
        // 1. 'Query Results' displayed in Red
        // 2. and 'Application Linking Results' displayed in magenta
        // Currently, as there are only 2 such layers, we can assume one as default
        Color colour = Color.red;

        if ("Application Linking Results".equalsIgnoreCase(this.getTitle())) {
            colour = Color.magenta;
        }

        for (int i=0; i < 64; i++) {
            for (int j=0; j < 64; j++) {
                image.setRGB(i, j, colour.getRGB());
            }
        }
        return Arrays.asList(image);
    }
}
