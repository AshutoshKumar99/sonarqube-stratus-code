package com.pb.stratus.controller.print.template;

import com.pb.stratus.controller.print.*;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


/**
 *  Generates a PDF document from a given template
 */
public class TemplateRenderer
{
    
    private TemplateContentHandlerFactory factory;
    
    public TemplateRenderer(TemplateContentHandlerFactory factory)
    {
        this.factory = factory;
    }
    
    public InputStream render(Template template, DocumentParameters params)
    {
        try
        {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            Source src = new StreamSource(
                    createInputStreamFromTemplate(template));
            TemplateContentHandler handler = factory
                    .createTemplateContentHandler(params);
            Result res = new SAXResult(handler);
            transformer.transform(src, res);
            return handler.getDocument();
        }
        catch (TransformerException tfx)
        {
            throw new RenderException(tfx);
        }
    }
    
    private InputStream createInputStreamFromTemplate(Template template)
    {
        byte[] content;
        try 
        {
            content = template.getTemplateContent().getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException uex)
        {
            // UTF-8 must be supported by JVM
            throw new Error(uex);
        }
        return new ByteArrayInputStream(content);
    }

}
