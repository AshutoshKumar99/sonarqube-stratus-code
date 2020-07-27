package com.pb.stratus.controller.geocoder;

import com.pb.stratus.controller.geocoder.GeoSearchParams;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * EGM and GCM parameter constructor .
 * Created by ar009sh on 08-09-2015.
 */
public abstract class SearchParamConstructor {

    public abstract GeoSearchParams constructParams(String gazzeteerPath) throws Exception;

    String getParamValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            if (el.getFirstChild() != null)
                textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }
}
