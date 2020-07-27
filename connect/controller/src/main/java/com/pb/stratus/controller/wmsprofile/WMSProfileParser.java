package com.pb.stratus.controller.wmsprofile;

import com.pb.stratus.controller.httpclient.Authentication;
import com.pb.stratus.controller.httpclient.UrlUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pb.stratus.controller.settings.Constants.*;

/**
 * Created by vi012gu on 24-09-2018.
 */
public class WMSProfileParser {
    private static final Logger LOGGER = LogManager.getLogger(WMSProfileParser.class);

    public WMSProfile getWMSProfile(InputStream is) throws XMLStreamException {

        WMSProfile wmsProfile = new WMSProfile();
        XMLEventReader eventReader = null;

        try {
            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            eventReader = inputFactory.createXMLEventReader(is);
            // Read the XML document
            Map<String, String> credentials = null;
            List<String> legendURLs = new ArrayList<>();

            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    // If we have a item element we create a new item
                    if (event.isStartElement()) {
                        if (event.asStartElement().getName().getLocalPart().equals(URL)) {
                            event = eventReader.nextEvent();
                            wmsProfile.setUrl(retrieveCharacterData(event));
                            continue;
                        }
                    }

                    if (event.asStartElement().getName().getLocalPart().equals(AUTH)) {
                        credentials = new HashMap<String, String>();
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart().equals(TYPE)) {
                        event = eventReader.nextEvent();
                        wmsProfile.setAuthn(Authentication.valueOf(retrieveCharacterData(event)));
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart().equals(USER_NAME)) {
                        event = eventReader.nextEvent();
                        credentials.put(USER_NAME, event.asCharacters().getData());
                        continue;
                    }
                    if (event.asStartElement().getName().getLocalPart().equals(PASSWORD)) {
                        event = eventReader.nextEvent();
                        credentials.put(PASSWORD, retrieveCharacterData(event));
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart().equals(USER_QUERY_PARAM)) {
                        event = eventReader.nextEvent();
                        credentials.put(USER_QUERY_PARAM, retrieveCharacterData(event));
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart().equals(PASSWORD_QUERY_PARAM)) {
                        event = eventReader.nextEvent();
                        credentials.put(PASSWORD_QUERY_PARAM, retrieveCharacterData(event));
                        continue;
                    }

                    if (event.asStartElement().getName().getLocalPart().equals(LEGEND_URL)) {
                        event = eventReader.nextEvent();
                        String legendURL = retrieveCharacterData(event);
                        legendURLs.add(UrlUtil.truncateQueryParams(legendURL));
                        continue;
                    }
                }
                // If we reach the end of an item element we add it to the list
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (endElement.getName().getLocalPart().equals(WMS_SERVICE)) {
                        wmsProfile.setCredentials(credentials);
                        wmsProfile.setLegendURLs(legendURLs);
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw e;
        } finally {
            if (eventReader != null) {
                try {
                    eventReader.close();
                    IOUtils.closeQuietly(is);
                } catch (XMLStreamException e) {
                    LOGGER.warn("Failed to close the xml reader", e);
                }
            }
        }

        return wmsProfile;
    }

    private String retrieveCharacterData(XMLEvent event) {
        if (event.isCharacters()) {
            return event.asCharacters().getData();
        }
        return "";
    }
}
