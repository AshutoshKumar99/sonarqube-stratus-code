package com.pb.stratus.controller.queryutils;

import com.pb.stratus.controller.exception.QueryConfigException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * This class returns the XMLData object for input path for XML
 *
 * @author ar009sh
 */
public final class QueryXMLDOMParser {

    private static final Logger log = LogManager.getLogger(QueryXMLDOMParser.class);
    private static QueryMetadata xmlDOMobject;

    /**
     * Parses the input queryConfig XML .
     *
     * @param path
     * @return AttributeQueryXmlData
     */
    public static QueryMetadata parseDocument(String path) {

        try {

            xmlDOMobject = new QueryMetadata();

            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setIgnoringComments(true);

            factory.setIgnoringElementContentWhitespace(true);
            // factory.setValidating(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File(path);
            InputStream stream = new FileInputStream(file);
            InputSource input = new InputSource(stream);
            input.setEncoding("UTF-8");
            Document doc = builder.parse(input);
            Element docEle = doc.getDocumentElement();
            // this piece of code will populate CriteriaParams
            NodeList nl = docEle
                    .getElementsByTagName(XMlConfigConstants.COLUMN);
            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {

                    Element el = (Element) nl.item(i);
                    CriteriaParams e = getCriteriaParams(el);
                    xmlDOMobject.getCriteriaParams().add(e);
                }
            }

            // TODO This set of code is not required .Keeping it for some future
            // purpose
            xmlDOMobject.setTableName(getParamValue(docEle,
                    XMlConfigConstants.TABLE_NAME));
            xmlDOMobject.setQueryName(getParamValue(docEle,
                    XMlConfigConstants.QUERY_DESC));

            log.debug("xmldata stage one:" + xmlDOMobject.print());

        } catch (Exception ex) {
            throw new QueryConfigException(XMlConfigConstants.ERROR_READING_XML,
                    "Error reading query :", xmlDOMobject.getQueryName());
        }

        return xmlDOMobject;
    }

    private static CriteriaParams getCriteriaParams(Element c1) {

        // Create a new CriteriaParam with the value read from the xml nodes
        String id = c1.getAttribute(XMlConfigConstants.COLUMN_ID);
        String operator = getParamValue(c1, XMlConfigConstants.OPERATOR);
        String columnType = getParamValue(c1, XMlConfigConstants.COLUMN_TYPE);
        String columnName = getParamValue(c1, XMlConfigConstants.COLUMN_NAME);
        String displayType = getParamValue(c1, XMlConfigConstants.DISPLAY_TYPE);
        String label = getParamValue(c1, XMlConfigConstants.LABEL);
        CriteriaParams e = new CriteriaParams(operator, displayType, label, id,
                columnName, columnType);

        return e;
    }

    private static String getParamValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if (nl != null && nl.getLength() > 0) {
            Element el = (Element) nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }

        return textVal;
    }
}
