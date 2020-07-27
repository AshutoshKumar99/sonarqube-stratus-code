package com.pb.stratus.controller.print.template.component;


import com.pb.stratus.controller.print.template.Component;
import com.pb.stratus.core.util.ObjectUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import static com.pb.stratus.controller.print.template.XslFoUtils.*;


/*
 * SVG scale component which displays scale value for the map.
 */
public class ScaleValueComponent implements Component
{
    private String text;

    //public static final String INLINE_CONTAINER_ELEMENT = "inline-container";

    public ScaleValueComponent(String text)
    {
        if (text == null || text.equals("null"))
        {
            /**
             * No scale text no scale value.
             */
            text = "";
        }
        this.text = text;
    }

    public void generateSAXEvents(ContentHandler handler) throws SAXException
    {
        //CONN-17200: Remove the explicit styling - relying on the styles the user will be providing.
        //startElement(handler, INLINE_ELEMENT, createAttribute("baseline-shift","super","padding-left", "0.5cm","font-size","9pt","font-family","sans-serif"));
        handler.characters(text.toCharArray(), 0, text.length());
        //endElement(handler, INLINE_ELEMENT);
    }

    @Override
    public boolean equals(Object obj)
    {
        ScaleValueComponent that = ObjectUtils.castToOrReturnNull(
                ScaleValueComponent.class, obj);
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

