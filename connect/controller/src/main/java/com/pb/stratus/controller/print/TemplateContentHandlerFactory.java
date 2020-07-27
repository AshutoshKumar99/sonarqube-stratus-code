package com.pb.stratus.controller.print;

import com.pb.stratus.controller.print.template.ComponentFactory;
import org.apache.fop.apps.FopFactory;

/**
 * Creates new TemplateContentHandler instances
 */
public class TemplateContentHandlerFactory
{
    
    private ComponentFactory componentFactory;
    
    private FopFactory fopFactory;
    

    public TemplateContentHandlerFactory(ComponentFactory componentFactory, 
            FopFactory fopFactory)
    {
        this.componentFactory = componentFactory;
        this.fopFactory = fopFactory;
    }

    public TemplateContentHandler createTemplateContentHandler(
            DocumentParameters params)
    {
        return new TemplateContentHandler(params, componentFactory, 
                fopFactory);
    }

}
