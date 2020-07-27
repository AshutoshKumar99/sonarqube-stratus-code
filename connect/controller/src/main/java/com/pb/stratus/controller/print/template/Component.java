package com.pb.stratus.controller.print.template;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * A component is a reusable, self-contained element on a print document. 
 * Examples for components are map images, legends, scale bars, etc.
 */
public interface Component
{
    
    public void generateSAXEvents(ContentHandler handler) throws SAXException;

}
