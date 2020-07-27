package com.pb.stratus.controller.print.render;

import com.pb.stratus.controller.print.Resolution;
import com.pb.stratus.controller.print.config.LayerServiceType;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.content.LayerBean;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renders an image with a copyright statement in the bottom-right corner. 
 * The rendered text contains the copyright statements of all layers passed
 * into {@link #renderCopyright(java.awt.Dimension, com.pb.stratus.controller.print.Resolution, java.util.List, com.pb.stratus.controller.print.config.MapConfig)}. The
 * background behind the copyright text is filled with a white translucent 
 * colour. The white background is offset from the bottom-right corner by 
 * 10x10 pixels. A padding of half a line height is added between the text and 
 * the edge of the white box.
 */
public class CopyrightRenderer
{
    private int copyrightTextLimitPerLine;
    private final int BOX_PADDING = 10;
    Font font = Font.getFont("MS Gothic");

    public BufferedImage renderCopyright(Dimension imageSize, Resolution res,
            List<LayerBean> layers, MapConfig mapConfig)
    {
        BufferedImage canvas = new BufferedImage(imageSize.width,
                imageSize.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = canvas.createGraphics();
        g.setFont(font);
        try
        {
            List<String> copyrightText = getCopyrightText(layers, mapConfig);

            int i = 0;
            for (String line : copyrightText)
            {
                line = extractLinksAndRemoveHtmlTags(line);
                copyrightText.set(i, line);
                i++;
            }
            if(copyrightText != null && copyrightText.size() > 0)
            {
                copyrightTextLimitPerLine = calculateTextLimitPerLine(g, copyrightText.get(0), imageSize);
            }

            Dimension boxSize = getCopyrightBoxSize(copyrightText, g, imageSize);
            boolean isBing = isBingBasemap(mapConfig);
            Point topLeft = calculateTopLeft(imageSize, boxSize, isBing);
            drawBox(g, imageSize, boxSize, topLeft);
            drawText(g, imageSize, boxSize, copyrightText, topLeft);
        }
        finally
        {
            g.dispose();
        }
        return canvas;
    }

    private int calculateTextLimitPerLine(Graphics2D g, String copyrightText, Dimension imageSize) {
        double boxWidth = imageSize.getWidth()/2;
        FontMetrics fm = g.getFontMetrics();
        double avgCharWidth = ((double) copyrightText.length()) / ( (double) fm.stringWidth(copyrightText));
        double textLimitPerLine = boxWidth*avgCharWidth;
        return Math.max(0, (int) textLimitPerLine-2);
    }

    private void drawBox(Graphics2D g, Dimension imageSize, Dimension boxSize,
            Point topLeft)
    {
        Composite origComposite = g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OVER, 0.6f));
        Rectangle box = new Rectangle(topLeft.x, topLeft.y,
                boxSize.width, boxSize.height);
        g.setColor(Color.WHITE);
        g.fill(box);
        g.setComposite(origComposite);
    }

    private void drawText(Graphics2D g, Dimension imageSize, Dimension boxSize,
            List<String> copyrightText, Point topLeft)
    {
        FontMetrics fm = g.getFontMetrics();
        int lineHeight = fm.getHeight();
        int left = topLeft.x + lineHeight / 2;
        int top = topLeft.y + fm.getAscent() + lineHeight / 2;
        g.setColor(Color.BLACK);
        for (String line : copyrightText)
        {
            if (line.length() > copyrightTextLimitPerLine) {
                String wrapedStr = WordUtils.wrap(line, copyrightTextLimitPerLine);
                String[] textStrArr = wrapedStr.split("\r\n");
                for (String subStrs : textStrArr) {
                    g.drawString(subStrs, left, top);
                    top +=lineHeight;
                }
            } else {
                g.drawString(line, left, top);
                top +=lineHeight;
            }
        }
    }

    /**
     * Removes html tags and extracts link url and puts in parenthesis to display the copyright text
     * As we display the link as simple text only
     * @param htmlString
     * @return
     */
    private String extractLinksAndRemoveHtmlTags(String htmlString) {

        Pattern htmlLinkPattern = Pattern.compile("<a\\b[^>]*href=\"[^>]*>(.*?)</a>");
        Pattern hrefPattern = Pattern.compile("href=\"[^>]*\">");
        Pattern linkLabelPattern = Pattern.compile(">(.*?)</a>");

        htmlString = StringEscapeUtils.unescapeHtml(htmlString);

        Matcher htmlLinkMatcher = htmlLinkPattern.matcher(htmlString);
        while (htmlLinkMatcher.find()) {
            Matcher linkLabelMatcher = linkLabelPattern.matcher(htmlLinkMatcher.group());
            String linkLabel = "";
            if (linkLabelMatcher.find()) {
                linkLabel = linkLabelMatcher.group().replaceFirst("^>", "").replaceFirst("</a>", "");
            }

            Matcher hrefMatcher = hrefPattern.matcher(htmlLinkMatcher.group());
            String hrefLink = "";
            if (hrefMatcher.find()) {
                hrefLink = hrefMatcher.group().replaceFirst("href=\"", "")
                            .replaceFirst("\">", "")
                            .replaceFirst("\"[\\s]?target=\"[a-zA-Z_0-9]*", "");
            }

            if (linkLabel.equalsIgnoreCase(hrefLink.replaceFirst("mailto:|http://", ""))) {
                htmlString = htmlString.replaceAll(htmlLinkMatcher.group(), linkLabel);
            } else {
                String replacementStr = linkLabel + " (" + hrefLink + ")";
                htmlString = htmlString.replaceAll(htmlLinkMatcher.group(), replacementStr);
            }
        }

        return htmlString;
    }

    /**
     * As the name implies, this method determines where the top-left corner
     * of the copyright box should be. It takes into account the small padding
     * distance between the box and the edge of the image, plus the need to move
     * the box up above the Bing copyright, if we're using a Bing base map.
     *
     * @param imageSize The size of the rendered map image. Most of the image
     * will be transparent, apart from the copyright box itself.
     * @param boxSize The size of the copyright box. This is calculated
     * elsewhere, using the font size and length of the longest copyright text.
     * @param isBing If we're using a Bing base map, we need to move the box up
     * slightly to avoid obscuring the Bing copyright.
     * @return A java.awt.Point representing the top-left corner of the box.
     */
    private Point calculateTopLeft(Dimension imageSize, Dimension boxSize,
            boolean isBing)
    {
        Point topLeft = new Point(
                BOX_PADDING,
                imageSize.height - BOX_PADDING - boxSize.height);
        return topLeft;
    }

    private Dimension getCopyrightBoxSize(List<String> copyrightText, Graphics2D g, Dimension imageSize)
    {
        FontMetrics fm = g.getFontMetrics();
        int boxWidth = (int) imageSize.getWidth()/2;
        int lineHeight = fm.getHeight();
        int boxHeight = lineHeight;
        int maxWidth = 0;
        for (String s : copyrightText)
        {
            int width = fm.stringWidth(s);
            if (width > boxWidth){
                s = WordUtils.wrap(s, copyrightTextLimitPerLine);
                String[] strArr = s.split("\r\n");
                boxHeight = boxHeight*strArr.length;
            } else {
                boxHeight += lineHeight;
            }
            if (width > maxWidth) {
                maxWidth = width;
            }
        }
        boxWidth = Math.min(boxWidth, maxWidth) + lineHeight;
        boxHeight += lineHeight;
        return new Dimension(boxWidth, boxHeight);
    }

    private List<String> getCopyrightText(List<LayerBean> layers,
            MapConfig mapConfig)
    {
        List<String> copyrightText = new LinkedList<String>();

        String defaultCopyright = mapConfig.getDefaultCopyright();
        if (StringUtils.isNotBlank(defaultCopyright))
        {
            copyrightText.add(defaultCopyright);
        }

        for (int i = layers.size() - 1; i >= 0; i--)
        {   
            MapDefinition mapDef = mapConfig.
                    getMapDefinitionByMapName(layers.get(i).getName());
            String line = mapDef.getCopyright();
            if (StringUtils.isNotBlank(line))
            {
                copyrightText.add(line);
            }
        }
        return copyrightText;
    }

    private boolean isBingBasemap(MapConfig mapConfig)
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
