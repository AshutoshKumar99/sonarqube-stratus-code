package com.pb.stratus.security.core.common;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/25/12
 * Time: 1:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class MimeMapping {
    private static final HashMap<String, String> MAPPINGS = new HashMap<String, String>();

    //Register supported mime types
    static {
        //From output to mime
        MAPPINGS.put("text", "text/plain");
        MAPPINGS.put("json", "application/json");
        MAPPINGS.put("geojson", "application/geojson");
        MAPPINGS.put("xml", "application/xml");
        MAPPINGS.put("png", "image/png");
        MAPPINGS.put("gif", "image/gif");
        MAPPINGS.put("jpg", "image/jpg");
        MAPPINGS.put("jpeg", "image/jpeg");
        //From mime to output
        MAPPINGS.put("image/png", "image/png");
        MAPPINGS.put("image/gif", "image/gif");
        MAPPINGS.put("image/jpg", "image/jpg");
        MAPPINGS.put("image/jpeg", "image/jpeg");
    }

    /**
     * Return mime type for alias from registered set.
     *
     * @param type - mime type alias (text, png, etc).
     * @return - mime type.
     */
    public static String getMimeForType(String type) {
        return MAPPINGS.get(type);
    }
}
