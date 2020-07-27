package com.pb.stratus.controller.util;

import com.pb.stratus.core.configuration.TenantSpecificFileSystemResourceResolver;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Utility class for custom brand.
*/ 

public class BrandUtil
{

    public static String BRAND_NAME_TAG = "brand-name";
    public static String TENANT_DEFAULT_BRAND_TAG = "tenantDefaultBrand";
    public static String TENANT_DEFAULT_BRAND_TAG_ATTRIBUTE = "value";
    public static String BRAND_PARAM = "brand";
    public static String MAPCFG_PARAM = "mapcfg";
    public static String TYPE_PARAM = "type";
    public static String DEFAULT_BRAND = "default";

    private static final Logger log = LogManager.getLogger(BrandUtil.class);

    /**
     * Returns the brand name from mapconfig
     * Returns tenant default brand if mapconfig doesn't have any.
     * Returns default brand in case of any parsing exception.
     * @param resourceResolver Filesystem resourceResolver
     * @param mapConfig mapconfig name.
     * @return brand
     */

    public static String getBrand(TenantSpecificFileSystemResourceResolver resourceResolver ,String mapConfig)
    {
        String brand = DEFAULT_BRAND;
        try{
            String path = "/config/" + mapConfig + ".xml";
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource input = new InputSource(resourceResolver.getResourceAsStream(path));
            input.setEncoding("UTF-8");
            Document doc = builder.parse(input);

            Element docEle = doc.getDocumentElement();

            NodeList nl = docEle
                    .getElementsByTagName(BRAND_NAME_TAG);
            if (nl != null && nl.getLength() > 0)
            {
                Element el = (Element) nl.item(0);
                brand = el.getFirstChild().getNodeValue();
            }else
            {
                // returns tenantdefault brand from tenant-settings.xml
                path = "/tenant-settings.xml";
                try{
                    input = new InputSource(resourceResolver.getResourceAsStream(path));
                    input.setEncoding("UTF-8");
                    doc = builder.parse(input);
                    docEle = doc.getDocumentElement();
                    nl = docEle
                            .getElementsByTagName(TENANT_DEFAULT_BRAND_TAG);
                    if (nl != null && nl.getLength() > 0)
                    {
                        Element el = (Element) nl.item(0);
                        String value = el.getAttribute(TENANT_DEFAULT_BRAND_TAG_ATTRIBUTE);
                        if(value!= null && value != "")
                            brand = value;
                    }
                }catch(Exception e)
                {
                    log.info("Unable to parse tenant-settings.xml ,returning default brand.");
                }

            }
        }catch (Exception ex){
            log.info("Unable to parse brand from mapconfig("+ mapConfig +".xml) ,returning default brand.");
        }
        return brand;
    }

    /*
    * Returns brand style path in theme folder.
    * @param brand name
    * */
    public static String getBrandStylePath(String brand){
        return "/theme/branding/" + brand + "/custom.css";
    }
    
}
