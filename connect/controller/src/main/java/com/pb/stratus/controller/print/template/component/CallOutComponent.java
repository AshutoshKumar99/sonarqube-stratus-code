package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.controller.info.Feature;
import com.pb.stratus.controller.print.Marker;
import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.controller.print.template.XslFoUtils;
import com.pb.stratus.core.util.ObjectUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.util.*;
import java.util.Map.Entry;

import static com.pb.stratus.controller.print.template.XslFoUtils.*;

/**
 * Created with IntelliJ IDEA.
 * User: HA008SA
 * Date: 14/7/12
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class CallOutComponent implements Component {

    private List<Feature> features;

    private Marker callOutMarker;

    private Boolean isShowAllInfoFields;

    private String masterReference;

    private ComponentConverter converter;

    private Map<String, List<String>> wmsCalloutData;

    public CallOutComponent(List<Feature> features, Marker callOutMarker,
           Boolean isShowAllInfoFields, String masterReference, Map<String,List<String>> wmsCalloutData)
    {
        this.features = features;
        this.callOutMarker = callOutMarker;
        this.isShowAllInfoFields = isShowAllInfoFields;
        this.masterReference = masterReference;
        converter = new ComponentConverter();
        this.wmsCalloutData = wmsCalloutData;
    }

    public void generateSAXEvents(ContentHandler handler) throws SAXException
    {
        //handler.characters(text.toCharArray(), 0, text.length());
        if ((features == null || features.size() == 0 || (!isShowAllInfoFields && !checkToInsertTable(features)))
                && (wmsCalloutData == null || wmsCalloutData.size() == 0))
        {
            return;
        }
        XslFoUtils.startElement(handler, PAGE_SEQUENCE_ELEMENT,
                createAttribute(MASTER_REFERENCE_ATTR, masterReference));

        XslFoUtils.startElement(handler, FLOW_ELEMENT,
                createAttribute(FLOW_NAME_ATTR, "xsl-region-body"));

        Attributes attrs = createAttribute(
                "width", "100%",
                "font-size", "10pt",
                "table-layout", "fixed");
        XslFoUtils.startElement(handler, TABLE_ELEMENT, attrs);
        startElement(handler, TABLE_COLUMN_ELEMENT,
                createAttribute("column-width", "3%"));
        endElement(handler, TABLE_COLUMN_ELEMENT);
        startElement(handler, TABLE_COLUMN_ELEMENT,
                createAttribute("column-width", "97%"));
        endElement(handler, TABLE_COLUMN_ELEMENT);
        startElement(handler, TABLE_BODY_ELEMENT);
        startElement(handler, TABLE_ROW_ELEMENT, createAttribute("font-weight", "bold", "font-size", "15pt"));

        startElement(handler, TABLE_CELL_ELEMENT, createAttribute("padding-right", "2mm"));
        startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm"));
        Component comp = converter.convertToComponent(callOutMarker.getIcon());
        comp.generateSAXEvents(handler);
        endElement(handler, BLOCK_ELEMENT);
        endElement(handler, TABLE_CELL_ELEMENT);

        startElement(handler, TABLE_CELL_ELEMENT);
        startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm", "font-weight", "bold"));
        String calloutText = lookupLocalisation("print.map.callout.text");
        handler.characters(calloutText.toCharArray(), 0, calloutText.length());
        endElement(handler, BLOCK_ELEMENT);
        endElement(handler, TABLE_CELL_ELEMENT);
        endElement(handler, TABLE_ROW_ELEMENT);
        endElement(handler, TABLE_BODY_ELEMENT);
        XslFoUtils.endElement(handler, TABLE_ELEMENT);

        if (isShowAllInfoFields)
        {
            XslFoUtils.startElement(handler, TABLE_ELEMENT, attrs);
            startElement(handler, TABLE_COLUMN_ELEMENT,
                    createAttribute("column-width", "50%"));
            endElement(handler, TABLE_COLUMN_ELEMENT);
            startElement(handler, TABLE_COLUMN_ELEMENT,
                    createAttribute("column-width", "50%"));
            endElement(handler, TABLE_COLUMN_ELEMENT);
            startElement(handler, TABLE_BODY_ELEMENT);
            for (Feature feature : features)
            {
                startElement(handler, TABLE_ROW_ELEMENT, createAttribute("background-color", "#EEEEEE", "font-weight", "bold"));
                startElement(handler, TABLE_CELL_ELEMENT, createAttribute("number-columns-spanned", "2"));
                startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm", "font-weight", "bold"));
                String stratusId = feature.getProperties().get("stratusid").toString();
                if (stratusId.indexOf('-') != -1)
                {
                    stratusId = stratusId.substring(0, stratusId.indexOf('-'));
                }
                stratusId = getFeatureNameFromRepositoryPath(stratusId);
                handler.characters(stratusId.toCharArray(), 0, stratusId.length());
                endElement(handler, BLOCK_ELEMENT);
                endElement(handler, TABLE_CELL_ELEMENT);
                endElement(handler, TABLE_ROW_ELEMENT);
                Object titleObj = feature.getProperties().get("TITLE");
                Object uniqueNoObj = feature.getProperties().get("UNIQUE_NO");
                if (titleObj != null)
                {
                    startElement(handler, TABLE_ROW_ELEMENT);
                    startElement(handler, TABLE_CELL_ELEMENT, createAttribute("number-columns-spanned", "2"));
                    startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm", "font-weight", "bold"));
                    String title = titleObj.toString();
                    if (uniqueNoObj != null)
                    {
                        String uniqueNo = uniqueNoObj.toString();
                        handler.characters((title + " " + uniqueNo).toCharArray(), 0, (title + " " + uniqueNo).length());
                    } else
                    {
                        handler.characters(title.toCharArray(), 0, title.length());
                    }
                    endElement(handler, BLOCK_ELEMENT);
                    endElement(handler, TABLE_CELL_ELEMENT);
                    endElement(handler, TABLE_ROW_ELEMENT);
                }
                Object descObj = feature.getProperties().get("DESCRIPTION");
                if (descObj != null)
                {
                    startElement(handler, TABLE_ROW_ELEMENT);
                    startElement(handler, TABLE_CELL_ELEMENT, createAttribute("number-columns-spanned", "2"));
                    startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm"));
                    String description = descObj.toString();
                    handler.characters(description.toCharArray(), 0, description.length());
                    endElement(handler, BLOCK_ELEMENT);
                    endElement(handler, TABLE_CELL_ELEMENT);
                    endElement(handler, TABLE_ROW_ELEMENT);
                }

                Object keyValue = feature.getProperties().get("KEYVALUE");
                if (keyValue != null)
                {
                    getSummaryField(handler, keyValue, "KEYVALUE");
                }

                for (Entry<String, Object> fEntry : feature.getProperties().entrySet())
                {
                    if (!fEntry.getKey().equals("stratusid")
                            && !fEntry.getKey().equals("TITLE")
                            && !fEntry.getKey().equals("DESCRIPTION")
                            && !fEntry.getKey().equals("KEYVALUE")
                            && !fEntry.getKey().equals("HYPERLINK"))
                    {
                        startElement(handler, TABLE_ROW_ELEMENT);
                        startElement(handler, TABLE_CELL_ELEMENT);
                        startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm", "font-weight", "bold"));
                        handler.characters(fEntry.getKey().toString().toCharArray(), 0, fEntry.getKey().toString().length());
                        endElement(handler, BLOCK_ELEMENT);
                        endElement(handler, TABLE_CELL_ELEMENT);
                        startElement(handler, TABLE_CELL_ELEMENT);
                        startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm"));
                        handler.characters(fEntry.getValue().toString().toCharArray(), 0, fEntry.getValue().toString().length());
                        endElement(handler, BLOCK_ELEMENT);
                        endElement(handler, TABLE_CELL_ELEMENT);
                        endElement(handler, TABLE_ROW_ELEMENT);
                    }
                }
                Object linkObj = feature.getProperties().get("HYPERLINK");
                if (linkObj != null)
                {
                    getHyperLink(handler, linkObj);
                }


            }
            endElement(handler, TABLE_BODY_ELEMENT);
            XslFoUtils.endElement(handler, TABLE_ELEMENT);
        } else
        { //show only summary field is clicked
            if(checkToInsertTable(features)){

                XslFoUtils.startElement(handler, TABLE_ELEMENT, attrs);
                startElement(handler, TABLE_BODY_ELEMENT);
                for (Feature feature : features)
                {
                    if (!validateSummaryFields(feature))
                    {
                        continue;
                    }
                    startElement(handler, TABLE_ROW_ELEMENT, createAttribute("background-color", "#EEEEEE", "font-weight", "bold"));
                    startElement(handler, TABLE_CELL_ELEMENT);
                    startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm", "font-weight", "bold"));
                    String stratusId = feature.getProperties().get("stratusid").toString();
                    if (stratusId.indexOf('-') != -1)
                    {
                        stratusId = stratusId.substring(0, stratusId.indexOf('-'));
                    }
                    stratusId = getFeatureNameFromRepositoryPath(stratusId);
                    handler.characters(stratusId.toCharArray(), 0, stratusId.length());
                    endElement(handler, BLOCK_ELEMENT);
                    endElement(handler, TABLE_CELL_ELEMENT);
                    endElement(handler, TABLE_ROW_ELEMENT);
                    Object titleObj = feature.getProperties().get("TITLE");
                    Object uniqueNoObj = feature.getProperties().get("UNIQUE_NO");
                    if (titleObj != null)
                    {
                        startElement(handler, TABLE_ROW_ELEMENT);
                        startElement(handler, TABLE_CELL_ELEMENT);
                        startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm", "font-weight", "bold"));
                        String title = titleObj.toString();
                        if (uniqueNoObj != null)
                        {
                            String uniqueNo = uniqueNoObj.toString();
                            handler.characters((title + " " + uniqueNo).toCharArray(), 0, (title + " " + uniqueNo).length());
                        } else
                        {
                            handler.characters(title.toCharArray(), 0, title.length());
                        }
                        endElement(handler, BLOCK_ELEMENT);
                        endElement(handler, TABLE_CELL_ELEMENT);
                        endElement(handler, TABLE_ROW_ELEMENT);
                    }
                    Object descObj = feature.getProperties().get("DESCRIPTION");
                    if (descObj != null)
                    {
                        getValue(handler, descObj);
                    }

                    Object keyValue = feature.getProperties().get("KEYVALUE");
                    if (keyValue != null)
                    {
                        getSummaryField(handler, keyValue, "KEYVALUE");
                    }

                    Object image = feature.getProperties().get("IMAGE");
                    if (image != null)
                    {

                        getSummaryField(handler, image, "IMAGE");

                    }

                    Object linkObj = feature.getProperties().get("HYPERLINK");
                    if (linkObj != null)
                    {
                        getHyperLink(handler, linkObj);
                    }

                }
                endElement(handler, TABLE_BODY_ELEMENT);
                XslFoUtils.endElement(handler, TABLE_ELEMENT);
            }

        }
        // for WMS callout information

        if(wmsCalloutData != null && wmsCalloutData.size() > 0)
        {

            XslFoUtils.startElement(handler, TABLE_ELEMENT, createAttribute("width", "60%",
                    "font-size", "10pt", "table-layout", "fixed"));
            startElement(handler, TABLE_BODY_ELEMENT);
            startElement(handler, TABLE_ROW_ELEMENT, createAttribute("background-color", "#EEEEEE", "font-weight", "bold"));
            startElement(handler, TABLE_CELL_ELEMENT);
            startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm"));
            String header = lookupLocalisation("wms.feature.callout.information");
            handler.characters(header.toCharArray(), 0, header.length());
            endElement(handler, BLOCK_ELEMENT);
            endElement(handler, TABLE_CELL_ELEMENT);
            endElement(handler, TABLE_ROW_ELEMENT);

            Iterator<String> wmsMapNameIterator = wmsCalloutData.keySet().iterator();
            while(wmsMapNameIterator.hasNext())
            {
                String mapName = wmsMapNameIterator.next();
                List<String> wmsUrls = wmsCalloutData.get(mapName);
                for(String url : wmsUrls)
                {
                    startElement(handler, TABLE_ROW_ELEMENT);
                    startElement(handler, TABLE_CELL_ELEMENT);
                    startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm"));
                    handler.characters(mapName.toCharArray(), 0, mapName.length());
                    endElement(handler, BLOCK_ELEMENT);
                    endElement(handler, TABLE_CELL_ELEMENT);

                    startElement(handler, TABLE_CELL_ELEMENT);
                    startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm"));
                    startElement(handler, BASIC_LINK_ELEMENT,
                            createAttribute("external-destination", url, "text-decoration", "underline", "color", "#3366CC"));
                    String clickOpen = lookupLocalisation("click.to.open");
                    handler.characters(clickOpen.toCharArray(), 0, clickOpen.length());
                    endElement(handler, BASIC_LINK_ELEMENT);
                    endElement(handler, BLOCK_ELEMENT);
                    endElement(handler, TABLE_CELL_ELEMENT);
                    endElement(handler, TABLE_ROW_ELEMENT);
                }
            }
            endElement(handler, TABLE_BODY_ELEMENT);
            XslFoUtils.endElement(handler, TABLE_ELEMENT);
        }

        XslFoUtils.endElement(handler, FLOW_ELEMENT);
        XslFoUtils.endElement(handler, PAGE_SEQUENCE_ELEMENT);
    }

    private boolean checkToInsertTable(List<Feature> features) {

        if(features != null && features.size() > 0)
        {
            for(Feature feature: features){
                if (validateSummaryFields(feature))
                {
                    return true;
                }
            }
        }
        return false;
    }


    private void getValue(ContentHandler handler, Object object) throws SAXException
    {
        startElement(handler, TABLE_ROW_ELEMENT);
        startElement(handler, TABLE_CELL_ELEMENT);
        startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm"));
        String value = object.toString();
        handler.characters(value.toCharArray(), 0, value.length());
        endElement(handler, BLOCK_ELEMENT);
        endElement(handler, TABLE_CELL_ELEMENT);
        endElement(handler, TABLE_ROW_ELEMENT);
    }

    private void getHyperLink(ContentHandler handler, Object linkObj) throws SAXException
    {
        startElement(handler, TABLE_ROW_ELEMENT);
        startElement(handler, TABLE_CELL_ELEMENT);
        startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm", "font-weight", "bold"));
        handler.characters("HYPERLINK".toCharArray(), 0, "HYPERLINK".length());
        endElement(handler, BLOCK_ELEMENT);
        endElement(handler, TABLE_CELL_ELEMENT);
        StringBuilder externalLink = new StringBuilder("url('");
        externalLink.append(linkObj.toString());
        externalLink.append("')");
        startElement(handler, TABLE_CELL_ELEMENT);
        startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm"));
        startElement(handler, BASIC_LINK_ELEMENT, createAttribute("external-destination"
                , externalLink.toString(), "text-decoration", "underline", "color", "#3366CC"));
        String clickOpen = lookupLocalisation("click.to.open");
        handler.characters(clickOpen.toCharArray(), 0, clickOpen.length());
        endElement(handler, BASIC_LINK_ELEMENT);
        endElement(handler, BLOCK_ELEMENT);
        endElement(handler, TABLE_CELL_ELEMENT);
        endElement(handler, TABLE_ROW_ELEMENT);
    }

    private void getSummaryField(ContentHandler handler, Object obj, String name) throws SAXException
    {
        startElement(handler, TABLE_ROW_ELEMENT);
        startElement(handler, TABLE_CELL_ELEMENT);
        startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm", "font-weight", "bold"));
        handler.characters(name.toCharArray(), 0, name.length());
        endElement(handler, BLOCK_ELEMENT);
        endElement(handler, TABLE_CELL_ELEMENT);
        startElement(handler, TABLE_CELL_ELEMENT);
        startElement(handler, BLOCK_ELEMENT, createAttribute("padding", "1mm"));
        String value = obj.toString();
        handler.characters(value.toCharArray(), 0, value.length());
        endElement(handler, BLOCK_ELEMENT);
        endElement(handler, TABLE_CELL_ELEMENT);
        endElement(handler, TABLE_ROW_ELEMENT);

    }

    @Override
    public boolean equals(Object obj)
    {
        CallOutComponent that = ObjectUtils.castToOrReturnNull(
                CallOutComponent.class, obj);
        if (that == null)
        {
            return false;
        }
        return ObjectUtils.equals(this.features, that.features);
    }

    @Override
    public int hashCode()
    {
        return features.hashCode();
    }

    /**
     * This method checks if values are present for any of the summary fields.
     * @param feature
     * @return true if feature has a valid value for at least one summary field.
     */
    private boolean validateSummaryFields(Feature feature)
    {
        if (feature.getProperties().get("TITLE") != null || feature.getProperties().get("DESCRIPTION") != null
                || feature.getProperties().get("DESCRIPTION") != null || feature.getProperties().get("KEYVALUE") != null
                || feature.getProperties().get("IMAGE") != null || feature.getProperties().get("HYPERLINK") != null)
        {
            return true;
        }
        return false;
    }
    /**
     * Lookup the localised text using the given key
     * @param key
     * @return
     */
    private String lookupLocalisation(String key)
    {
        ResourceBundle messages = PropertyResourceBundle.getBundle("legendmessages", LocaleResolver.getLocale());
        return messages.getString(key);
    }

    public String getFeatureNameFromRepositoryPath(String path)
    {
        if(path.indexOf('/')!=-1) {
            return path.substring(path.lastIndexOf('/')+1);
        }
        else return path;
    }

}