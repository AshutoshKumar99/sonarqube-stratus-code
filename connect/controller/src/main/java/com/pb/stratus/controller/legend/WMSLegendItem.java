package com.pb.stratus.controller.legend;

import com.pb.stratus.controller.httpclient.HttpClientFactory;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A WMSLegendItem describes a sub-layer of an wms overlay. This can be either a
 * simple icon and description, or a range of icons with corresponding descriptions in a single image.
 */
public class WMSLegendItem extends LegendItem
{
    private String displayName;
    private String legendUrl;
    private String mapUrl;
    private boolean secure;
    private static final String DEFAULT_ENCODING = "UTF-8";

    private HttpClientFactory httpClientFactory;

    private static final Logger logger = LogManager.getLogger(WMSLegendItem.class);

    public WMSLegendItem(String friendlyName, String displayName,
                         String mapUrl, boolean isSecure, String legendUrl, HttpClientFactory httpClientFactory) {
        super(friendlyName, "single");
        this.displayName = displayName;
        this.secure = isSecure;
        this.legendUrl = legendUrl;
        this.mapUrl= mapUrl ;
        this.httpClientFactory = httpClientFactory;
    }

    public String getTitle()
    {
        return displayName;
    }

    /**  This method retrieves the image for the WMS Legend
     *
     * */
    protected Collection<? extends BufferedImage> getIcons()
    {
        List<BufferedImage> imageList = new ArrayList<BufferedImage>();
        try
        {
            if(secure){
                mapUrl =  URLDecoder.decode(mapUrl, DEFAULT_ENCODING) ;
            }
            CloseableHttpClient httpClient = httpClientFactory.getHttpClient(mapUrl, secure, new StringBuilder(legendUrl));
            HttpGet serviceRequest = new HttpGet(legendUrl.toString());
            HttpResponse serviceResponse = httpClient.execute(serviceRequest);
            if (serviceResponse.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                BufferedImage image = ImageIO.read(serviceResponse.getEntity().getContent());
                if(image != null){
                    imageList.add(image);
                }
            } else {
                logger.error("Exception while requesting the legend image from url "
                        + legendUrl + "and status code is " + serviceResponse.getStatusLine().getStatusCode() +
                        "Error is - " + IOUtils.toString(serviceResponse.getEntity().getContent()));
            }
        }
        catch (MalformedURLException ex) {
            logger.error("Exception while requesting the legend image from url "
                    + legendUrl + ". Continuing with empty image", ex);
        }
        catch (IOException ex) {
            logger.error("Exception while requesting the legend image from url "
                    + legendUrl + ". Continuing with empty image", ex);
        }
        catch (Exception ex) {
            logger.error("Exception while requesting the legend image from url"
                    + legendUrl + ". Continuing with empty image", ex);
        }
        return imageList;
    }

}
