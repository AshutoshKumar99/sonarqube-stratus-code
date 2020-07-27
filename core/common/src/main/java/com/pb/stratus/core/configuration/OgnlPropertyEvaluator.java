package com.pb.stratus.core.configuration;

import ognl.Ognl;
import ognl.OgnlException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class OgnlPropertyEvaluator implements PropertyEvaluator {
    public String getType() {
        return "ognl";
    }

    public String evaluate(String value) {
        Functions functions = new Functions();
        Object result;
        try {
            result = Ognl.getValue(value, functions);
        }
        catch (OgnlException ognlEx) {
            throw new RuntimeException("please check your properties files for ognl expression settings", ognlEx);
        }
        return result.toString();
    }

    static class Functions {
        private String LOCALE_REQUEST_PARAM = "lang";
        private Map map = new HashMap();

        public Map getMap() {
            return map;
        }

        public String urlEncode(String s) {
            try {
                return URLEncoder.encode(s, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        public Map getRequestAttributes() {
            Map map = new HashMap();
            String[] requestAttributes = RequestContextHolder.currentRequestAttributes().getAttributeNames(RequestAttributes.SCOPE_REQUEST);
            for (int i = 0; i < requestAttributes.length; i++) {
                map.put(requestAttributes[i], RequestContextHolder.currentRequestAttributes().getAttribute(requestAttributes[i], RequestAttributes.SCOPE_REQUEST));
            }
            return map;
        }

        public String insertLocale(String separator) {
            String localeParamStr = "";
            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
            String localeParam = request.getParameter(LOCALE_REQUEST_PARAM);
            if(localeParam != null && !localeParam.equals("") ){
                String prefix = "&";
                if(separator != null && !separator.equals("")){
                    prefix = separator;
                }
                localeParamStr = prefix+ "lang=" + localeParam;
            }
            return localeParamStr;
        }
    }
}
