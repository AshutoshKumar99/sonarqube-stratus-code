/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.print.template.Component;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 *
 * This is a component that should be called if a SAXEvent doesn't correspond
 * with any known components. It does nothing.
 *
 * @author mo002mi
 */
public class NullComponent implements Component
{
    
    public static NullComponent INSTANCE = new NullComponent();
    
    private NullComponent()
    {
    }

    /**
     * Does nothing, as it represents an ignored SAXEvent
     * 
     * @param handler
     * @throws SAXException
     */
    public void generateSAXEvents(ContentHandler handler) throws SAXException
    {
        // do nothing
    }

}
