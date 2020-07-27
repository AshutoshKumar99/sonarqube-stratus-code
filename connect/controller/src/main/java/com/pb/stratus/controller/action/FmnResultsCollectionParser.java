package com.pb.stratus.controller.action;

import com.pb.stratus.controller.marker.MarkerFactory;
import com.pb.stratus.controller.marker.MarkerRepository;
import com.pb.stratus.controller.marker.MarkerType;
import com.pb.stratus.controller.print.Marker;
import com.pb.stratus.controller.print.RenderException;
import com.pb.stratus.controller.print.content.FmnResult;
import com.pb.stratus.controller.print.content.FmnResultsCollection;
import com.pb.stratus.controller.print.image.ImageReader;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Parses a JSON string of a defined structure (XXX document structure) into
 * an {@link FmnResultsCollection}.
 */
public class FmnResultsCollectionParser
{
    
    private static final Logger logger = LogManager.getLogger(
            FmnResultsCollectionParser.class);
    
    private MarkerRepository markerRepository;
    
    private ImageReader imageReader;

    private MarkerFactory markerFactory;
    
    
    public FmnResultsCollectionParser(
            MarkerFactory markerFactory,
            MarkerRepository markerRepository, 
            ImageReader imageReader)
    {
        this.markerFactory = markerFactory;
        this.markerRepository = markerRepository;
        this.imageReader = imageReader;
    }
    
    public FmnResultsCollection parse(String json)
    {
        JSONObject parsedResults = parseFmnResults(json);
        
        List<FmnResult> resultList = getResultsListFromParsedResults(
                parsedResults);
        String title = getTitleFromParsedResults(parsedResults);
        return new FmnResultsCollection(title, resultList);
    }
    
    private JSONObject parseFmnResults(String json)
    {
        return JSONObject.fromObject(json);
    }

    private List<FmnResult> getResultsListFromParsedResults(
            JSONObject parsedResults)
    {
        List<FmnResult> resultsList = new LinkedList<FmnResult>();
        List<JSONObject> jsonObjects = getResultsListAsJSONArray(
                parsedResults);
        for (int i = 0; i < jsonObjects.size(); i++)
        {
            resultsList.add(convertJsonObjectToFmnResult(
                    jsonObjects.get(i), i));
        }
        return resultsList;
    }
    
    @SuppressWarnings("unchecked")
    private List<JSONObject> getResultsListAsJSONArray(JSONObject parsedResults)
    {
        return parsedResults.getJSONArray("results");
    }

    private String getTitleFromParsedResults(JSONObject parsedResults)
    {
        return parsedResults.getString("fmnresultstitle");
    }

    private FmnResult convertJsonObjectToFmnResult(JSONObject obj, int index)
    {
        //FmnResult result = new FmnResult();
        String title = getSummaryFieldValue(obj, "title");
        String description = getSummaryFieldValue(obj, "description");
        String keyValue = getSummaryFieldValue(obj, "keyValue");
        String link = getSummaryFieldValue(obj, "link");
        String imageUrl = getSummaryFieldValue(obj, "image");
        BufferedImage image = null;
        if (imageUrl != null)
        {
            image = getImage(imageUrl);
        }
        Marker marker = createMarker(obj, index);
        FmnResult result = new FmnResult(title, description, keyValue, link, 
                image, marker);
        return result;
    }

    private String getSummaryFieldValue(JSONObject obj, String field)
    {
        try
        {
            String value  = obj.getString(field);
            return value;
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    private Marker createMarker(JSONObject obj, int index)
    {
        BufferedImage icon = readMarkerIcon(obj.getString("icon"));
        //FIXME read shadow
        Point anchorPoint = new Point((int) (icon.getWidth() / 2d), 
                icon.getHeight() - 1);
        Point2D.Double location = new Point2D.Double();
        location.setLocation(Double.parseDouble(obj.getString("x")), 
                Double.parseDouble(obj.getString("y")));
        Marker marker = markerFactory.createMarker(icon, anchorPoint, location);
        return augmentMarker(marker, index);
    }
    
    private Marker augmentMarker(Marker marker, int index)
    {
        BufferedImage numberedIcon = getNumberedIcon(index);
        return marker.augmentWithIcon(new Point(-5, -5), numberedIcon);
    }
    
    private BufferedImage getNumberedIcon(int index)
    {
        String name = String.format("fmn%d.png", index + 1);
        InputStream is = FmnResultsCollectionParser.class
                .getResourceAsStream(name);
        BufferedImage img;
        try
        {
            img = ImageIO.read(is);
            is.close();
        }
        catch (IOException iox)
        {
            throw new RenderException(iox);
        }
        return img;
    }

    private BufferedImage readMarkerIcon(String icon)
    {
        try
        {
            InputStream is = markerRepository.getMarkerImage(icon, 
                    MarkerType.MARKER);
            BufferedImage image = ImageIO.read(is);
            BufferedImage newImage = new BufferedImage(image.getWidth(), 
                    image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            Graphics2D g = newImage.createGraphics();
            g.drawImage(image, null, 0, 0);
            g.dispose();
            is.close();
            return newImage;
        }
        catch (IOException iox)
        {
            throw new RenderException(iox);
        }
    }

    private BufferedImage getImage(String url)
    {
        try
        {
            return imageReader.readFromUrl(new URL(url));
        }
        catch (IOException iox)
        {
            logger.warn("The resource at " + url + "couldn't be retrieved (" 
                    + iox.toString() + "). Continuing without it");
        }
        return null;
    }

}
