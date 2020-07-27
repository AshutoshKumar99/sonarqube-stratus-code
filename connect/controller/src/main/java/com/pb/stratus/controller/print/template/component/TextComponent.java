package com.pb.stratus.controller.print.template.component;

import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.core.util.ObjectUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class TextComponent implements Component
{
    
    private String text;
    
    public TextComponent(String text)
    {
        if (text == null)
        {
            text = "";
        }
        this.text = text;
    }

    public void generateSAXEvents(ContentHandler handler) throws SAXException
    {
        handler.characters(text.toCharArray(), 0, text.length());
    }

    @Override
    public boolean equals(Object obj)
    {
        TextComponent that = ObjectUtils.castToOrReturnNull(
                TextComponent.class, obj);
        if (that == null)
        {
            return false;
        }
        return ObjectUtils.equals(this.text, that.text);
    }

    @Override
    public int hashCode()
    {
        return text.hashCode();
    }
    
    

}
