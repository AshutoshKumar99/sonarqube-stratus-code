package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.print.content.FmnResult;
import com.pb.stratus.controller.print.image.ScalableImage;
import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.core.util.ObjectUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import uk.co.graphdata.utilities.contract.Contract;

import java.awt.image.BufferedImage;

import static com.pb.stratus.controller.print.template.XslFoUtils.*;

/**
 * Transforms a single FmnResult into XSL-FO SAX events 
 */
public class FmnResultComponent implements Component
{
    
    private FmnResult fmnResult;
    
    private ComponentConverter converter;

    /**
     * Creates a new FmnResultComponent for the given FmnResult
     */
    public FmnResultComponent(FmnResult fmnResult)
    {
        Contract.pre(fmnResult != null, "FmnResult required");
        this.fmnResult = fmnResult;
        converter = new ComponentConverter();
    }
    
    protected void setComponentConverter(ComponentConverter converter)
    {
        this.converter = converter;
    }

    /**
     * Constructs an XSL-FO table row with three cells. The first cell contains
     * a marker icon that allows a user to locate the corresponding FmnResult
     * on a map. The second cell contains the image of the FmnResult (if 
     * there is any). The third cell contains the title, key value, link, and 
     * description of the FmnResult.
     */
    public void generateSAXEvents(ContentHandler handler) throws SAXException
    {
        startElement(handler, TABLE_ROW_ELEMENT, createAttribute(
                "border-bottom", "solid 1px #D4D4D4"));
        generateMapMarkerImageCell(handler);
        generateTextCell(handler);
        generateFmnImageCell(handler);
        endElement(handler, TABLE_ROW_ELEMENT);
    }

    private void generateMapMarkerImageCell(ContentHandler handler) 
            throws SAXException
    {
        generateImageCell(handler, fmnResult.getMarker().getIcon());
    }

    private void generateFmnImageCell(ContentHandler handler) 
            throws SAXException
    {
        BufferedImage image = fmnResult.getImage();
        if (image == null)
        {
            generateEmptyTableCell(handler);
            return;
        }
        ScalableImage scl = createScaleableImage(image);
        scl.setMaxSideLength(100);
        image = scl.getScaledImage();
        generateImageCell(handler, image);
    }
    
    private void generateEmptyTableCell(ContentHandler handler) 
            throws SAXException
    {
        startElement(handler, TABLE_CELL_ELEMENT);
        startElement(handler, BLOCK_ELEMENT);
        endElement(handler, BLOCK_ELEMENT);
        endElement(handler, TABLE_CELL_ELEMENT);
    }

    private void generateImageCell(ContentHandler handler, BufferedImage image)
            throws SAXException
    {
        /**
         * Padding was added here to ensure that there is a gap between an FMN
         * Marker image, and any FMN Result Image.
         */
        startElement(handler, TABLE_CELL_ELEMENT, createAttribute("padding-right","2mm"));
        startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm"));
        Component comp = converter.convertToComponent(image);
        comp.generateSAXEvents(handler);
        endElement(handler, BLOCK_ELEMENT);
        endElement(handler, TABLE_CELL_ELEMENT);
    }
    
    private void generateTextCell(ContentHandler handler) throws SAXException
    {
        String title = fmnResult.getTitle();
        String keyValue = fmnResult.getKeyValue();
        String link = fmnResult.getLink();
        String desc = fmnResult.getDescription();
        // checking that at least one text field is not null so that empty table cell elements are not formed
        if(title!=null ||keyValue != null || link != null  || desc != null){
            startElement(handler, TABLE_CELL_ELEMENT);
            if (title != null)
            {
                startElement(handler, BLOCK_ELEMENT,
                        createAttribute("padding", "1mm", "font-weight", "bold"));
                handler.characters(title.toCharArray(), 0, title.length());
                endElement(handler, BLOCK_ELEMENT);
            }

            if (keyValue != null)
            {
                startElement(handler, BLOCK_ELEMENT, createAttribute("padding",
                        "1mm", "color", "#666666"));
                handler.characters(keyValue.toCharArray(), 0, keyValue.length());
                endElement(handler, BLOCK_ELEMENT);
            }

            if (link != null)
            {
                createLinkElement(handler, link);
            }

            if (desc != null)
            {
                startElement(handler, BLOCK_ELEMENT, createAttribute("padding",
                        "1mm"));
                handler.characters(desc.toCharArray(), 0, desc.length());
                endElement(handler, BLOCK_ELEMENT);
            }
            endElement(handler, TABLE_CELL_ELEMENT);
        }

    }

    private void createLinkElement(ContentHandler handler, String link) throws SAXException
    {   
        StringBuilder externalLink = new StringBuilder("url('");
        externalLink.append(link);
        externalLink.append("')");
        startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm"));
        startElement(handler, BASIC_LINK_ELEMENT,
            createAttribute("external-destination", externalLink.toString(),
                "text-decoration", "underline", "color", "#3366CC"));
        handler.characters(link.toCharArray(), 0, link.length());
        endElement(handler, BASIC_LINK_ELEMENT);
        endElement(handler, BLOCK_ELEMENT);
    }

    @Override
    public boolean equals(Object obj)
    {
        FmnResultComponent that = ObjectUtils
                .castToOrReturnNull(FmnResultComponent.class, obj);
        if (that == null)
        {
            return false;
        }
        return this.fmnResult.equals(that.fmnResult);
    }

    @Override
    public int hashCode()
    {
        return fmnResult.hashCode();
    }

    protected ScalableImage createScaleableImage(BufferedImage image)
    {
        return new ScalableImage(image);
    }
    
    

}
