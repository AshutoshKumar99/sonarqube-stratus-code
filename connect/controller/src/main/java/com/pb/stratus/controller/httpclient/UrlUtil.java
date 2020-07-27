package com.pb.stratus.controller.httpclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * This class provides some utility methods for creating http request.
 * User: GU003DU
 * Date: 4/18/13
 * Time: 4:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class UrlUtil {

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String PARAM_SEPARATOR = "&";
    private static final String QUERY_SEPARATOR = "?";

    /**
     * Encode query parameters.
     *
     * @param map
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encode(Map<?, ?> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append(PARAM_SEPARATOR);
            }
            sb.append(String.format("%s=%s",
                    URLEncoder.encode(entry.getKey().toString(), DEFAULT_ENCODING),
                    URLEncoder.encode(entry.getValue().toString(), DEFAULT_ENCODING)
            ));
        }
        return sb.toString();
    }

    public static String appendQueryParameters(String url, Map<String, String> params)
            throws UnsupportedEncodingException {

        int index = url.indexOf(QUERY_SEPARATOR);
        if (index < 0) {
            return url += QUERY_SEPARATOR + UrlUtil.encode(params);
        } else if (index == url.length() - 1) {
            return url + UrlUtil.encode(params);
        }
        return url + PARAM_SEPARATOR + UrlUtil.encode(params);
    }

    public static String truncateQueryParams(String url) {

        if (url == null){
            return null;
        }

        int index = url.indexOf(QUERY_SEPARATOR);
        if (index < 0) {
            return url;
        }
        return url.substring(0, index);
    }
}
