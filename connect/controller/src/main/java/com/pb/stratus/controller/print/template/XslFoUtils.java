package com.pb.stratus.controller.print.template;

import org.apache.commons.codec.binary.Base64;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A class to help produce markup quicker so we don't have to keep
 * retyping namespaces etc. each time we add an fo element or close one.
 */
public class XslFoUtils {
    public static final String PAGE_SEQUENCE_ELEMENT = "page-sequence";

    public static final String MASTER_REFERENCE_ATTR = "master-reference";

    public static final String FLOW_ELEMENT = "flow";

    public static final String FLOW_NAME_ATTR = "flow-name";

    public static final String TABLE_ELEMENT = "table";

    public static final String BLOCK_ELEMENT = "block";

    public static final String BLOCK_CONTAINER_ELEMENT = "block-container";

    public static final String TABLE_ROW_ELEMENT = "table-row";

    public static final String TABLE_CELL_ELEMENT = "table-cell";

    public static final String TABLE_COLUMN_ELEMENT = "table-column";

    public static final String TABLE_BODY_ELEMENT = "table-body";

    public static final String INLINE_ELEMENT = "inline";

    public static final String LEADER_ELEMENT = "leader";

    public static final String BASIC_LINK_ELEMENT = "basic-link";

    public static final String NS_URI = "http://www.w3.org/1999/XSL/Format";

    private static final String NS_PREFIX = "fo";

    public static final String EXTERNAL_GRAPHIC = "external-graphic";

    public static final String MAX_BAR_WIDTH = "max-bar-width";

    /**
     * For SVG graphics.
     */
    public static final String INSTREAM_OBJECT = "instream-foreign-object";

    /**
     * For the static date element.
     */
    public static final String DATE_FORMAT = "date-format";

    /**
     * Starts a basic XSL-FO element
     */
    public static void startElement(ContentHandler handler, String element)
            throws SAXException {
        startElement(handler, element, new AttributesImpl());
    }

    /**
     * Starts an XSL-FO element with attributes
     */
    public static void startElement(ContentHandler handler, String element,
                                    Attributes attr) throws SAXException {
        handler.startElement(NS_URI, element, NS_PREFIX + ":" + element, attr);
    }

    /**
     * Ends an XSL-FO element.
     */
    public static void endElement(ContentHandler handler, String element)
            throws SAXException {
        handler.endElement(NS_URI, element, NS_PREFIX + ":" + element);
    }

    public static Attributes createAttribute(String... attrs) {
        AttributesImpl newAttrs = new AttributesImpl();
        for (int i = 0; i < attrs.length - 1; i = i + 2) {
            newAttrs.addAttribute("", attrs[i], attrs[i], "CDATA",
                    attrs[i + 1]);
        }
        return newAttrs;
    }

    public static String encodeImageInBase64(BufferedImage image)
            throws IOException {
        byte[] out = encodeImageInPngByteArray(image);
        return encodeByteArrayInBase64(out);
    }

    public static String encodeByteArrayInBase64(byte[] bytes) {
        StringBuilder b = new StringBuilder("url('data:image/png;base64,");
        String encoded = Base64.encodeBase64String(bytes)
                .replaceAll("\\s", "");
        b.append(encoded);
        b.append("')");
        return b.toString();
    }

    private static byte[] encodeImageInPngByteArray(BufferedImage image)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        return out.toByteArray();
    }

}
