package com.pb.stratus.controller.geocoder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Encapsulates xml -> java objects for EGM config
 * Created by ar009sh on 07-09-2015.
 */
public class EGMSearchParamConstructor extends SearchParamConstructor {

    private static EGMSearchParamConstructor instance;

    private EGMSearchParamConstructor() {
    }

    @Override
    public GeoSearchParams constructParams(String gazetteerPath) throws Exception {

        GeoSearchParams params = null;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            InputStream stream = new FileInputStream(gazetteerPath);
            InputSource input = new InputSource(stream);
            input.setEncoding("UTF-8");
            Document document = parser.parse(input);
            Element docEle = document.getDocumentElement();
            params = new GeoSearchParams();

            params.setEndPoint(getParamValue(docEle,
                    EGMConstants.URL));
            params.setCountry(getParamValue(docEle,
                    EGMConstants.DATA_COUNTRY));
            params.setUsername(getParamValue(docEle,
                    EGMConstants.USERNAME));
            params.setPassword(getParamValue(docEle,
                    EGMConstants.PASSWORD));


        } catch (SAXException | IOException | ParserConfigurationException ex) {
            throw new Exception("Could Not read geoConfig");
        }

        return params;

    }

    public static EGMSearchParamConstructor getInstance() {
        if (null == instance) {
            instance = new EGMSearchParamConstructor();
        }
        return instance;
    }

}
