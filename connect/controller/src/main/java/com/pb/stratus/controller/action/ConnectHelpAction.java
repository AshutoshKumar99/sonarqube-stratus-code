package com.pb.stratus.controller.action;

import com.pb.stratus.controller.i18n.LocaleResolver;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to retrieve the Connect Help link when the Help button is clicked
 */
public class ConnectHelpAction extends DataInterchangeFormatControllerAction {

    private static final String HELP_LINK_DEFAULT = "help/en/index.html";

    private static final String CURRENT_URL = "currentUrl";

    private static final String QUERY_STRING_CHAR = "?";

    private List<String> localeList = Arrays.asList("cy", "da", "de","fi", "fr", "nl" , "pt" , "sv", "en-gb", "en-au", "es", "ja");

    private String locale;

    public ConnectHelpAction() { }

    /**
     * Constructs connect help link string in form of JSON and sends back to the caller
     *
     * @param request the request of the caller that the JSON response will be
     *                sent to.
     * @return JSON containing connect help link
     */
    @Override
    protected Object createObject(HttpServletRequest request) throws IOException {

        return getConnectHelpLink(request);
    }

    /**
     * Returns connect help link defined in tenant-settings.xml, if not found, it returns default connect help link
     *
     * @param request HTTP request object
     * @return URL of connect help link
     */
    private String getConnectHelpLink(HttpServletRequest request) {

        String link = null;
        locale = LocaleResolver.getLocale().toString();
        if(StringUtils.isBlank(locale) || !validLocale()) {
            link = HELP_LINK_DEFAULT;
        } else {
            link="help/"+ locale +"/index.html";
        }
        String currentUrl = request.getParameter(CURRENT_URL);
        // ignore mapconfig query string, if exist in current url.
        int index = currentUrl.indexOf(QUERY_STRING_CHAR);
        if (index > 0)  {
            currentUrl = currentUrl.substring(0, index) ;
        }
        link = currentUrl + link;
        return link;
    }

    private boolean validLocale(){
        if(localeList.contains(locale))
        {
            return true;
        }
        setCorrectLocale();
        return localeList.contains(locale);
    }

    private void setCorrectLocale() {
        if(locale.equalsIgnoreCase("en_GB")) {
            locale = "en-gb";
        } else if(locale.equalsIgnoreCase("en_AU")) {
            locale = "en-au";
        }

        if(locale.indexOf("_") != -1) {
            locale = locale.substring(0, locale.indexOf("_"));
        }
    }
}