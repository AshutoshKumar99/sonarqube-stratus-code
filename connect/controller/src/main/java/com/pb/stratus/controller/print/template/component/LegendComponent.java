package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.controller.legend.*;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.MapDefinition;
import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.core.util.ObjectUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import uk.co.graphdata.utilities.contract.Contract;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import static com.pb.stratus.controller.print.template.XslFoUtils.*;

/**
 * Transforms a legend into XSL-FO SAX events
 */
public class LegendComponent implements Component
{

    private final static String BUSINESS_MAPS_UNAVAILABLE =
        "business.maps.unavailable";

    private final static String TABLE_TITLE = "table";
    private final static String ALL_OTHERS = "all others";

    private String legendTitle;
    
    private LegendData legendData;
    
    private MapConfig mapConfig;

    private boolean isUserThematic = false;
    
    public LegendComponent(String legendTitle, MapConfig config, 
            LegendData legendData)
    {
        Contract.pre(legendTitle != null, "Legend title required");
        // commenting out below condition because in case of config having wms base map and
        // wms overlays , mapping overlay legend data would not be present
//        Contract.pre(legendData != null, "Legend data required");
        Contract.pre(config != null, "Map Configuration is required");
        this.legendTitle = legendTitle;
        this.legendData = legendData;
        this.mapConfig = config;
    }

    /**
     * Lookup the localised text using the given key
     * @param key
     * @return
     */
    private String lookupLocalisation(String key)
    {
        // XXX: RENAME LEGENDMESSAGES TO MESSAGES
        ResourceBundle messages = PropertyResourceBundle.
            getBundle("legendmessages", LocaleResolver.getLocale());

        return messages.getString(key);
    }

    /**
     * Constructs an XSL-FO legend with each of the map names and there
     * layers.  Each layer will contain an icon representing that layer
     * @param handler
     * @throws SAXException
     */
    public void generateSAXEvents(ContentHandler handler) throws SAXException
    {
        startElement(handler, BLOCK_CONTAINER_ELEMENT,
                createAttribute("border-style", "solid", "overflow", "hidden"));
        addLegendTitle(handler);
        addLegendContent(handler);
        endElement(handler, BLOCK_CONTAINER_ELEMENT);
    }

    /**
     * Add a Legend Title to the contentHandler
     * @param handler
     * @throws SAXException
     */
    private void addLegendTitle(ContentHandler handler) throws SAXException
    {
        startElement(handler, BLOCK_ELEMENT, createAttribute(
                "background-color", "#CCC", "font-weight", "bold"));
        handler.characters(legendTitle.toCharArray(), 0, legendTitle.length());
        endElement(handler, BLOCK_ELEMENT);
    }

    /**
     * Add the legendContent to the contentHandler
     * @param handler
     * @throws SAXException
     */
    private void addLegendContent(ContentHandler handler) throws SAXException
    {
        if(legendData != null)
        {
            List<OverlayLegend> overlayLegends = legendData.getOverlayLegends();
            if (overlayLegends.isEmpty())
            {
                addNoOverlaysMessage(handler,
                        lookupLocalisation(BUSINESS_MAPS_UNAVAILABLE));
            }
            else
            {
                for (OverlayLegend overlayLegend : overlayLegends)
                {
                    startElement(handler, BLOCK_ELEMENT);
                    addOverlayLegend(handler, overlayLegend);
                    endElement(handler, BLOCK_ELEMENT);
                }
            }
        }
    }



    private void addNoOverlaysMessage(ContentHandler handler,
            String selectedText)throws SAXException
    {
        startElement(handler, BLOCK_ELEMENT);
        startElement(handler, INLINE_ELEMENT,
            createAttribute("padding-right", "0.5cm"));
        handler.characters(selectedText.toCharArray(), 0, selectedText.length());
        endElement(handler, INLINE_ELEMENT);
        endElement(handler, BLOCK_ELEMENT);
    }

    /**
     * Adds an OverlayLegend to the overall legend
     * 
     * @param handler
     * @param overlayLegend
     * @throws SAXException
     */
    private void addOverlayLegend(ContentHandler handler,
            OverlayLegend overlayLegend) throws SAXException
    {

        addOverlayTitle(handler, overlayLegend);
        if(overlayLegend.isEndUserThematic()){
            isUserThematic = true;
            addTableName(handler, overlayLegend);
        }else{
            isUserThematic = false;
        }

        for (LegendItem legendItem : overlayLegend.getLegendItems())
        {
            startElement(handler, BLOCK_ELEMENT);
            if (legendItem instanceof SingleLegendItem)
            {
                addSingleLegendItem(handler, (SingleLegendItem) legendItem, 
                        false);
            }
            else
            {
                addThematicLegendItem(handler, 
                        (ThematicLegendItem) legendItem);
                
            }
            endElement(handler, BLOCK_ELEMENT);
        }
    }
    
    /**
     * Add the mapname to the legend body
     * @param handler
     * @param overlayLegend
     * @throws SAXException
     */
    private void addOverlayTitle(ContentHandler handler, 
            OverlayLegend overlayLegend) throws SAXException
    {
        startElement(handler, BLOCK_ELEMENT, createAttribute(
                "background-color", "#DDD"));
        String title = overlayLegend.getTitle();
        String friendlyName = getFriendlyNameForMap(title);
        handler.characters(friendlyName.toCharArray(), 0, friendlyName.length());
        endElement(handler, BLOCK_ELEMENT);
    }

    private String getFriendlyNameForMap(String mapName)
    {

        MapDefinition def = mapConfig.getMapDefinitionByMapNameOrNull(mapName);
        if(def != null){
            return def.getFriendlyName();
        }else {
            return mapName;
        }

    }

    private void addTableName(ContentHandler handler,
                                 OverlayLegend overlayLegend) throws SAXException
    {
        startElement(handler, BLOCK_ELEMENT, createAttribute(
                "background-color", "#DDD"));
        startElement(handler, INLINE_ELEMENT,
                createAttribute("padding-left", "1mm", "overflow", "auto"));
        String tableTitle = lookupLocalisation(TABLE_TITLE)+" : " + overlayLegend.getTableName();
        handler.characters(tableTitle.toCharArray(), 0, tableTitle.length());
        endElement(handler, INLINE_ELEMENT);
        endElement(handler, BLOCK_ELEMENT);
    }
    
    private void addSingleLegendItem(ContentHandler handler,
            SingleLegendItem legendItem, boolean indent) throws SAXException
    {
        if(isUserThematic && legendItem.getTitle().equalsIgnoreCase(ALL_OTHERS)){
            // For explicitly removing legend item corresponding to "All Others" option returned for Individual Symbol thematics
            return;
        }
        if(legendItem.isWMSLegendItem()){
            startElement(handler, BLOCK_ELEMENT);
            addTitle(handler, legendItem, false);
            endElement(handler, BLOCK_ELEMENT);
        }
        startElement(handler, BLOCK_ELEMENT);
        if (indent)
        {
            startElement(handler, INLINE_ELEMENT, createAttribute(
                    "padding-left", "2em"));
        }
        else
        {
            startElement(handler, INLINE_ELEMENT, createAttribute(
                    "padding-left", "1mm"));
        }
        addIcon(handler, legendItem);
        endElement(handler, INLINE_ELEMENT);
        if(!legendItem.isWMSLegendItem()){
            addTitle(handler, legendItem, false);
        }
        endElement(handler, BLOCK_ELEMENT);
    }

    private void addThematicLegendItem(ContentHandler handler,
            ThematicLegendItem legendItem) throws SAXException
    {
        startElement(handler, BLOCK_ELEMENT);
        addTitle(handler, legendItem, true);
        endElement(handler, BLOCK_ELEMENT);
        for (SingleLegendItem childItem : legendItem.getLegendItems())
        {
            addSingleLegendItem(handler, childItem, true);
        }
    }

    private void addIcon(ContentHandler handler, SingleLegendItem legendItem) 
            throws SAXException
    {
        try
        {
            BufferedImage image = legendItem.getIcon();
            if(legendItem.isWMSLegendItem())
            {
                if(image != null){
                    Double ratio = new Double(image.getHeight())/new Double(image.getWidth());
                    if(ratio > 2)
                    {
                        startElement(handler, EXTERNAL_GRAPHIC, createAttribute(
                                "src", encodeImageInBase64(image), "height","10cm",
                                "content-width","scale-down-to-fit"));
                    }
                    else
                    {
                        startElement(handler, EXTERNAL_GRAPHIC, createAttribute(
                                "src", encodeImageInBase64(image), "width","5cm",
                                "content-height","scale-down-to-fit"));
                    }
                }
            }else{
                startElement(handler, EXTERNAL_GRAPHIC, createAttribute(
                        "src", encodeImageInBase64(image),
                        "content-width", "1em", "content-height", "1em"));
            }

        }
        catch (IOException iox)
        {
            throw new SAXException(iox);
        }
        endElement(handler, EXTERNAL_GRAPHIC);
    }

    private void addTitle(ContentHandler handler, LegendItem legendItem, 
            boolean indent) throws SAXException
    {
        if(legendItem instanceof SingleLegendItem && ((SingleLegendItem)legendItem).isWMSLegendItem())
        {

            startElement(handler, INLINE_ELEMENT,
                    createAttribute("padding-left", "1mm", "overflow", "auto"));
            String title = legendItem.getTitle();
            handler.characters(title.toCharArray(), 0, title.length());
            endElement(handler, INLINE_ELEMENT);
        }
        else
        {
            String title = legendItem.getTitle();
            if(title != null){ // title can be null in case of thematic legends
                if (indent)
                {
                    startElement(handler, INLINE_ELEMENT,
                            createAttribute("padding-left", "2em"));
                }
                else
                {
                    startElement(handler, INLINE_ELEMENT,
                            createAttribute("padding-left", "1mm"));
                }
                handler.characters(title.toCharArray(), 0, title.length());
                endElement(handler, INLINE_ELEMENT);
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
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
        LegendComponent that = (LegendComponent) obj;
        if (!ObjectUtils.equals(this.legendTitle, that.legendTitle))
        {
            return false;
        }
        if (!ObjectUtils.equals(this.legendData, that.legendData))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hc = ObjectUtils.SEED;
        hc = ObjectUtils.hash(hc, legendTitle);
        hc = ObjectUtils.hash(hc, legendData);
        return hc;
    }

}
