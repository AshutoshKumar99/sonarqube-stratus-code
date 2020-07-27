package com.pb.gazetteer;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Created with IntelliJ IDEA.
 * User: SA021SH
 * Date: 12/27/13
 * Time: 12:19 PM
 * To change this template use File | Settings | File Templates.
 */
public final class ConfigurationFileConstants {
    public final static String _INSTANCE_ = "instance";
    public final static String _NAME_ = "name";
    public final static String _CONFIG_ = "config";
    public static final String _PROJECTION_ELEMENT_ = "srs";
    public static final String _SEARCH_LOGIC_ELEMENT_ = "searchlogic";
    public static final String _ENGINE_NAME_ = "engine-name";
    public static final String _CLASS_ = "class";
    public static final String _IDLE_PERIOD_ = "idle-period";
    public static final String _ENGINE_="engine";
    public static final String _DEFAULT_INSTANCE_ = "default-instance";
    public static final String _LOCAL_INDEX_DIR_ELEMENT_ = "active-directory";
    public static final String _LOCAL_PENDING_DIR_ELEMENT_ = "pending-directory";

    /**
     * Returns the TAG text.
     * @param element
     * @param elementName
     * @return
     */
    public static String getElementText(Element element, String elementName) {
        String elementText = "";

        if(element != null)
        {
            NodeList nodeList = element.getElementsByTagName(elementName);

            if (nodeList != null && nodeList.getLength() > 0) {
                Element elem = (Element) nodeList.item(0);
                elementText = elem.getTextContent();
            }
        }
        return elementText;
    }
}
