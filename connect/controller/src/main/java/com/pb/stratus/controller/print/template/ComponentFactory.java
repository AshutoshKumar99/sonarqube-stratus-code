/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pb.stratus.controller.print.template;

import com.pb.stratus.controller.print.DocumentParameters;
import org.xml.sax.Attributes;

import javax.xml.namespace.QName;

/**
 * Creates components for XML elements in the template 
 */
public interface ComponentFactory
{

    /**
     * Creates a component that corresponds to the XML element represented by
     * the given QName. Implementations can use the attributes and parameters
     * to further parameterise the returned component instance.  
     */
   public Component createComponent(QName qname, Attributes attrs, 
           DocumentParameters params);

}
