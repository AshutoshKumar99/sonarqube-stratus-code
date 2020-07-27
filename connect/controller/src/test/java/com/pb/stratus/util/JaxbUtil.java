package com.pb.stratus.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 * This is a class that provides utility methods to unmarshal JAXB objects.
 * 
 * @author Volker Leidl
 */
public class JaxbUtil {

    /**
     * Unmarshals the given xml string into an object of the given type.
     * 
     * @param <T>
     *            The generic type used in this method
     * @param xml
     *            a non-empty string containing xml data that can be
     *            unmarshalled into an instance of type <code>T</code>
     * @param type
     *            the type of the result object
     * @return a instance of type <code>T</code> corresponding to the given
     *         XML data
     * @throws JAXBException
     *             if the XML data could not be unmarshalled into an instance of
     *             the given type.
     */
    public static <T> T fromXml(String xml, Class<T> type) throws JAXBException {
        ByteArrayInputStream is;
        try {
            is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException uex) {
            // UTF-8 must be supported according to Java spec
            throw new Error(uex);
        }
        return fromXml(is, type);
    }

    /**
     * Unmarshals the given xml input stream into an object of the given type.
     * 
     * @param <T>
     *            The generic type used in this method
     * @param xml
     *            an input stream providing the xml data to be unmarshalled into
     *            an instance of type <code>T</code>
     * @param type
     *            the type of the result object
     * @return a instance of type <code>T</code> corresponding to the given
     *         XML data
     * @throws JAXBException
     *             if the XML data could not be unmarshalled into an instance of
     *             the given type.
     */
    public static <T> T fromXml(InputStream xml, Class<T> type)
            throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(type);
        Unmarshaller u = ctx.createUnmarshaller();
        JAXBElement<T> element = u.unmarshal(new StreamSource(xml), type);
        T result = element.getValue();
        return result;
    }

    /**
     * Creates an object of type <code>type</code> from the given XML file.
     * The specified type must be equipped with appropriate JAXB annotations so
     * that it can be unmarshalled from the given XML file.
     * 
     * @param fileName
     *            the name of a class path resource relative to the base
     *            parameter that contains the XML to be unmarshalled.
     * @param type
     *            the actual type of the generated object
     * @param base
     *            the class that is used as a base for looking up the class path
     *            resource.
     * @return an instance of <code>T</code>
     * @throws JAXBException
     *             if the unmarshalling process fails
     */
    public static <T, B> T createObject(String fileName, Class<T> type,
            Class<B> base) throws JAXBException {

        InputStream is = base.getResourceAsStream(fileName);
        if (is == null) {
            throw new IllegalArgumentException("Resource '" + fileName
                    + "' doesn't exist");
        }
        return fromXml(is, type);
    }

}
