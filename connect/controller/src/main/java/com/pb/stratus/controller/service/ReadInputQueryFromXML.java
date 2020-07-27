package com.pb.stratus.controller.service;

import com.pb.stratus.controller.exception.QueryConfigException;
import com.pb.stratus.controller.queryutils.*;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class takes care of maintaining a map for DOM Objects as well as reading
 * the queryCOnfig XML
 *
 * @author ar009sh
 */
public class ReadInputQueryFromXML implements ReadInputQuery {

    private static Map<String, QueryMetadata> xmlMap = Collections
            .synchronizedMap(new LRUMap());

    public static Map<String, QueryMetadata> getXmlMap() {
        return xmlMap;
    }

    private static final Logger log = LogManager
            .getLogger(ReadInputQueryFromXML.class);

    private QueryMetadata xmlDOMObject;

    /**
     * Creates the attributeQuery data from the searchByQueryParams
     *
     * @param params SearchByQueryParams the original request.
     * @return AttributeQueryXmlData
     */
    public QueryMetadata createQueryMetadata(SearchByQueryParams params) {

        String queryPath = params.getPath();

        String key = generateKey(queryPath);

        xmlDOMObject = managekey(key, queryPath);
        Map<String, List<String>> map = params.getIdValueMap();

        populateParamsInXML(map, xmlDOMObject);
        return xmlDOMObject;
    }

    /**
     * This method populates the value variable of DOM object with map returned
     * from the params object
     *
     * @param map
     * @param data
     */
    private void populateParamsInXML(Map<String, List<String>> map,
                                     QueryMetadata data) {
        List<CriteriaParams> list = data.getCriteriaParams();

        for (int i = 0; i < list.size(); i++) {
            CriteriaParams b = list.get(i);

            if (b.getDisplayType().equalsIgnoreCase(
                    XMlConfigConstants.USER_INPUT)
                    || b.getDisplayType().equalsIgnoreCase(
                    XMlConfigConstants.USE_PICKLIST)) {
                List<String> values = map.get(b.getId());
                if (ValidateDynamicQueryParams.validate(values, b.getColumnType())) {
                    b.setValues(values);
                } else {
                    throw new QueryConfigException(
                            "Invalid value entered for label",
                            XMlConfigConstants.DYNAMICVALIDATION_FAILURE, b
                            .getLabel());
                }
            } else if (b.getDisplayType().equalsIgnoreCase(
                    XMlConfigConstants.USE_THIS_VALUE)) {
                log.debug("setting label as the value");
                List<String> values = new ArrayList<String>();
                values.add(b.getLabel());
                b.setValues(values);
            }
        }

        log.debug("xmldata stage two:" + data.print());
    }

    /**
     * Key used to store a DOM object is based on the timeStamp as well as the
     * query Name .This method returns the DOM object from the map
     *
     * @param key
     * @param queryPath
     * @return AttributeQueryXmlData
     */

    private QueryMetadata managekey(String key, String queryPath) {
        QueryMetadata xmlDOMObject = null;
        synchronized (ReadInputQueryFromXML.class) {
            if (xmlMap.containsKey(key)) {

                xmlDOMObject = xmlMap.get(key);

                log.debug("XMLDOMObject Found in cache with HashCode["
                        + xmlDOMObject.hashCode() + "]");
                return xmlDOMObject;

            } else {

                xmlDOMObject = QueryXMLDOMParser.parseDocument(queryPath);
                log
                        .debug("XMLDOMObject Not in cache, Creating new XMLDOMObject with hashcode["
                                + xmlDOMObject.hashCode() + "]");

                xmlMap.put(key, xmlDOMObject);

            }
        }
        return xmlDOMObject;
    }

    /**
     * This method generates the unique key for xml file
     *
     * @param queryPath
     * @return key
     */
    private String generateKey(String queryPath) {
        // unique key for any query xml
        String key = "";
        if (queryPath != null) {
            String fileName = FilenameUtils.getBaseName(queryPath);
            Long var = new File(queryPath).lastModified();
            key = fileName + var.toString();
        }
        return key;
    }

}