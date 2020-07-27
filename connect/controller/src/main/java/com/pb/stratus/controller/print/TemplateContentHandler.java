package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.controller.print.template.ComponentFactory;
import com.pb.stratus.core.util.ObjectUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * A SAX content handler that handles SAX events from parsing a print template
 * and passes them on to Apache FOP in order to generate a PDF document. 
 * The template is expected to consist of XSL FO markup, potentially mixed with
 * Stratus-specific elements. Stratus-specific elements are expanded into pure 
 * XSL FO by means of a ComponentFactory. The document parameters passed into
 * the constructor of this class are used in that expansion process. 
 */
public class TemplateContentHandler implements ContentHandler
{
    
    public static final String NAMESPACE 
            = "http://stratus.pbinsight.com/print-template";
    
    private DocumentParameters params;
    
    private ComponentFactory componentFactory;
    
    private FopFactory foProcessorFactory;
    
    private ContentHandler foProcessorHandler;
    
    private ByteArrayOutputStream targetOutputStream;
    
    private QName currentElement;
    
    private Attributes currentAttributes;

    private Component currentComponent;


    public TemplateContentHandler(DocumentParameters params, 
            ComponentFactory componentFactory, FopFactory foProcessorFactory) 
            throws RenderException
    {
        this.params = params;
        this.componentFactory = componentFactory;
        this.foProcessorFactory = foProcessorFactory;
        this.targetOutputStream = new ByteArrayOutputStream();
        try
        {
            Fop fop = foProcessorFactory
                    .newFop("application/pdf", targetOutputStream);
            foProcessorHandler = fop.getDefaultHandler();
        }
        catch (FOPException fx)
        {
            throw new RenderException(fx);
        }
    }

    public void characters(char[] arg0, int arg1, int arg2) throws SAXException
    {
        foProcessorHandler.characters(arg0, arg1, arg2);
    }

    public void endDocument() throws SAXException
    {
        foProcessorHandler.endDocument();
    }

    public void startElement(String ns, String localName, String qName,
            Attributes attrs) throws SAXException
    {
        if (NAMESPACE.equals(ns))
        {
            currentElement = new QName(ns, localName);
            currentAttributes = attrs;
            //initialised component in startElement() to be used in endElement().
            // jdk6 xml apis clears attributes after startElement() that causes problem in initialising this in endElement()
            currentComponent = componentFactory.createComponent(currentElement, 
                    currentAttributes, params);
           
        }
        else
        {
            foProcessorHandler.startElement(ns, localName, qName, attrs);
        }
    }

    public void endElement(String arg0, String arg1, String arg2)
            throws SAXException
    {
        if (currentElement != null)
        {
            currentComponent.generateSAXEvents(foProcessorHandler);
            currentElement = null;
            currentAttributes = null;
        }
        else
        {
            foProcessorHandler.endElement(arg0, arg1, arg2);
        }
    }

    public void endPrefixMapping(String arg0) throws SAXException
    {
        foProcessorHandler.endPrefixMapping(arg0);
    }

    public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
            throws SAXException
    {
        foProcessorHandler.ignorableWhitespace(arg0, arg1, arg2);
    }

    public void processingInstruction(String arg0, String arg1)
            throws SAXException
    {
        foProcessorHandler.processingInstruction(arg0, arg1);
    }

    public void setDocumentLocator(Locator arg0)
    {
        foProcessorHandler.setDocumentLocator(arg0);
    }

    public void skippedEntity(String arg0) throws SAXException
    {
        foProcessorHandler.skippedEntity(arg0);
    }

    public void startDocument() throws SAXException
    {
        foProcessorHandler.startDocument();
    }

    public void startPrefixMapping(String arg0, String arg1)
            throws SAXException
    {
        foProcessorHandler.startPrefixMapping(arg0, arg1);
    }
    
    public InputStream getDocument()
    {
        return new ByteArrayInputStream(targetOutputStream.toByteArray());
    }

    protected boolean isEquivalent(TemplateContentHandler other)
    {
        if (!ObjectUtils.equals(this.componentFactory, other.componentFactory))
        {
            return false;
        }
        if (!ObjectUtils.equals(this.params, other.params))
        {
            return false;
        }
        if (!ObjectUtils.equals(this.foProcessorFactory, 
                other.foProcessorFactory))
        {
            return false;
        }
        return true;
    }

}
