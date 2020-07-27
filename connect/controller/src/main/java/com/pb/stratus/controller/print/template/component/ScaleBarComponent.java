package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.print.ScaleBar;
import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.controller.print.template.XslFoUtils;
import com.pb.stratus.core.util.ObjectUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.awt.*;
import java.util.Map;

/*
 * SVG scale component which creates svg scale bar.
 */
public class ScaleBarComponent  implements Component
{
    private final String SVGNAMESPACE = "http://www.w3.org/2000/svg";

    private ScaleBar scaleBar;

    private final String CM = "cm";

    /**
     * Maximum width of the scale bar.
     */
    private int maxWidth = 0;
    private Map<String, String> scaleBarAttributes;

    private final int DEFAULT_HEIGHT = 6;

    public ScaleBarComponent(ScaleBar scaleBar, Map<String, String> scaleBarAttributes)
    {
        this.scaleBar = scaleBar;
        this.scaleBarAttributes = scaleBarAttributes;
        setMaxWidth(scaleBarAttributes);
    }

    private void setMaxWidth(Map<String, String> scaleBarAttributes)
    {
        if(scaleBarAttributes != null && (scaleBarAttributes.get(XslFoUtils.MAX_BAR_WIDTH)!= null))
        {
            try {
                this.maxWidth = Integer.parseInt(scaleBarAttributes.get(XslFoUtils.MAX_BAR_WIDTH));
            }
            catch(NumberFormatException ex)
            {
                this.maxWidth = 0;
            }
        }
    }
    public void generateSAXEvents(ContentHandler handler) throws SAXException
    {
        Dimension viewBox = calculateViewBox();
        startContainer(handler, viewBox);
        drawScalePolyLine(handler, viewBox);
        drawMiddleVerticalLine(handler, viewBox);
        drawScaleLabels(handler, viewBox);
        endContainer(handler);
    }

    private Dimension calculateViewBox()
    {
        int calculateWidth = (int) (this.maxWidth>0?(this.maxWidth):(scaleBar.getWidthOnMapInCm() * 10));
        return new Dimension((int) calculateWidth, DEFAULT_HEIGHT);
    }

    private void startContainer(ContentHandler handler, Dimension viewBox) 
            throws SAXException
    {
        XslFoUtils.startElement(handler, XslFoUtils.INSTREAM_OBJECT);

        Attributes attrs = XslFoUtils.createAttribute(
                "width",  scaleBar.getWidthOnMapInCm() + CM,
                "height", "1" + CM,
                "viewBox", "0 0 " + viewBox.width + " " + viewBox.height,
                "xmlns:svg", SVGNAMESPACE);

        handler.startElement(SVGNAMESPACE, "svg", "svg:svg", attrs);
        handler.startElement(SVGNAMESPACE,"g","svg:g", 
                XslFoUtils.createAttribute());
        
    }
    
    private void drawScalePolyLine(ContentHandler handler, Dimension viewBox) 
            throws SAXException
    {
        StringBuffer points = new StringBuffer("0,4 0,");
        points.append(viewBox.height);
        points.append(" ");
        points.append(viewBox.width);
        points.append(",");
        points.append(viewBox.height);
        points.append(" ");
        points.append(viewBox.width);
        points.append(",");
        points.append("4");
        
        Attributes attrs = XslFoUtils.createAttribute(
                "points", points.toString(), 
                "fill", "none",
                "style", "stroke-width:0.2;stroke:" + getFontColor() + ";");
        handler.startElement(SVGNAMESPACE,"polyline", "svg:polyline", attrs);
        handler.endElement(SVGNAMESPACE,"polyline", "svg:polyline");
    }

    private void drawMiddleVerticalLine(ContentHandler handler, 
            Dimension viewBox) throws SAXException
    {
        Attributes attrs = XslFoUtils.createAttribute(
                "x1", Double.toString(viewBox.width / 2),
                "y1", "4",
                "x2", Double.toString(viewBox.width / 2),
                "y2", Double.toString(viewBox.height),
                "style", "stroke-width:0.1;stroke:" + getFontColor() + ";");
        handler.startElement(SVGNAMESPACE, "line", "svg:line", attrs);
        handler.endElement(SVGNAMESPACE, "line", "svg:line");
    }

    private void drawScaleLabels(ContentHandler handler, Dimension viewBox) 
            throws SAXException
    {
        drawLabel(handler, scaleBar.getLeftLabel(), 0, 3, "start");
        drawLabel(handler, scaleBar.getMiddleLabel(), viewBox.width / 2, 3, 
                "middle");
        drawLabel(handler, scaleBar.getRightLabel(), viewBox.width, 3, "end");
    }

    private int getFontSize()
    {
        if(scaleBarAttributes != null && (scaleBarAttributes.get("font-size")!= null))
        {
            try {
                return Integer.parseInt(scaleBarAttributes.get("font-size"));
            }
            catch(NumberFormatException ex)
            {

            }
        }
        return 3;
    }


    private String getFontFamily()
    {
        if(scaleBarAttributes != null && (scaleBarAttributes.get("font-family")!= null))
        {
            try {
                return scaleBarAttributes.get("font-family");
            }
            catch(NumberFormatException ex)
            {

            }
        }
        return "sans-serif";
    }

    private String getFontColor()
    {
        if(scaleBarAttributes != null && (scaleBarAttributes.get("color")!= null))
        {
            try {
                return scaleBarAttributes.get("color");
            }
            catch(NumberFormatException ex)
            {

            }
        }
        return "black";
    }

    private void drawLabel(ContentHandler handler, 
            String text, int x, int y, String anchor) throws SAXException
    {

        Attributes attrs = XslFoUtils.createAttribute(
                "x", Integer.toString(x),
                "y", Integer.toString(y),
                "style", "text-anchor:" + anchor + ";" + "stroke:none;fill:" + getFontColor() + ";"
                        + "font-size:" + getFontSize() + ";font-family:" + getFontFamily());
        handler.startElement(SVGNAMESPACE, "text", "svg:text", attrs);
        handler.characters(text.toCharArray(), 0, text.length());
        handler.endElement(SVGNAMESPACE, "text", "svg:text");   
    }

    private void endContainer(ContentHandler handler) throws SAXException
    {
        handler.endElement(SVGNAMESPACE, "g","svg:g");
        handler.endElement(SVGNAMESPACE, "svg","svg:svg");
        XslFoUtils.endElement(handler, "instream-foreign-object");
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (this.getClass() != obj.getClass())
        {
            return false;
        }
        ScaleBarComponent that = (ScaleBarComponent) obj;
        return ObjectUtils.equals(this.scaleBar, that.scaleBar);
    }

    @Override
    public int hashCode()
    {
        int hc = ObjectUtils.SEED;
        return ObjectUtils.hash(hc, scaleBar);
    }

}

