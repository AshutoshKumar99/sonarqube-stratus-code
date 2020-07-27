package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.print.content.FmnResult;
import com.pb.stratus.controller.print.content.FmnResultsCollection;
import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.controller.print.template.XslFoUtils;
import com.pb.stratus.core.util.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import static com.pb.stratus.controller.print.template.XslFoUtils.*;

//FIXME rename to FmnResultsCollectionComponent
public class FmnResultsComponent implements Component
{
    
    private FmnResultsCollection fmnResults;
    
    private ComponentConverter converter;
    
    private String masterReference;

    public FmnResultsComponent(FmnResultsCollection fmnResults,
            String masterReference)
    {
        this.fmnResults = fmnResults;
        converter = new ComponentConverter();
        this.masterReference = masterReference;
    }
    
    
    protected void setComponentConverter(ComponentConverter converter)
    {
        this.converter = converter;
    }
    
    public void generateSAXEvents(ContentHandler handler) throws SAXException
    {
        if (fmnResults == null)
        {
            return;
        }

        if (StringUtils.isBlank(masterReference))
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
        generateHeader(handler);
        generateBody(handler);
        XslFoUtils.endElement(handler, TABLE_ELEMENT);

        XslFoUtils.endElement(handler, FLOW_ELEMENT);
        XslFoUtils.endElement(handler, PAGE_SEQUENCE_ELEMENT);
    }
    
    private void generateHeader(ContentHandler handler) throws SAXException
    {
        startElement(handler, TABLE_COLUMN_ELEMENT, 
                createAttribute("column-width", "5%"));
        endElement(handler, TABLE_COLUMN_ELEMENT);
        startElement(handler, TABLE_COLUMN_ELEMENT, 
                createAttribute("column-width", "65%"));
        endElement(handler, TABLE_COLUMN_ELEMENT);
        startElement(handler, TABLE_COLUMN_ELEMENT, 
                createAttribute("column-width", "30%"));
        endElement(handler, TABLE_COLUMN_ELEMENT);
    }

    private void generateBody(ContentHandler handler) throws SAXException
    {
        startElement(handler, TABLE_BODY_ELEMENT);
        generateTitleRow(handler);
        generateResultRows(handler);
        endElement(handler, TABLE_BODY_ELEMENT);
    }
    
    private void generateTitleRow(ContentHandler handler) throws SAXException
    {
        startElement(handler, TABLE_ROW_ELEMENT, 
                createAttribute("background-color", "#EEEEEE", 
                        "font-weight", "bold"));
        startElement(handler, TABLE_CELL_ELEMENT, createAttribute(
                "number-columns-spanned", "3"));
        String title = fmnResults.getTitle();
        startElement(handler, BLOCK_ELEMENT, createAttribute(
                "padding", "1mm"));
        handler.characters(title.toCharArray(), 0, title.length());
        endElement(handler, BLOCK_ELEMENT);
        endElement(handler, TABLE_CELL_ELEMENT);
        endElement(handler, TABLE_ROW_ELEMENT);
    }

    private void generateResultRows(ContentHandler handler) throws SAXException
    {
        for (FmnResult result : fmnResults.getFmnResults())
        {
            generateResultRow(handler, result);
        }
    }
    
    private void generateResultRow(ContentHandler handler, FmnResult result) 
            throws SAXException
    {
        Component comp = converter.convertToComponent(result);
        comp.generateSAXEvents(handler);
    }

    @Override
    public boolean equals(Object obj)
    {
        FmnResultsComponent that = ObjectUtils.castToOrReturnNull(
                FmnResultsComponent.class, obj);
        if (that == null)
        {
            return false;
        }
        return ObjectUtils.equals(this.fmnResults, that.fmnResults);
    }

    @Override
    public int hashCode()
    {
        int hashCode = ObjectUtils.SEED;
        hashCode = ObjectUtils.hash(hashCode, fmnResults);
        return hashCode;
    }
    
}
