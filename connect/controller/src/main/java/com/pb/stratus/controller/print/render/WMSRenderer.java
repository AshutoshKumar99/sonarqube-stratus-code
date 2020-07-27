package com.pb.stratus.controller.print.render;


import com.pb.stratus.controller.print.LayerRenderParams;
import com.pb.stratus.controller.print.content.WMSMap;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import uk.co.graphdata.utilities.contract.Contract;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;

/**
 * Renders the specified map as a BufferedImage, by calling HTTPProxyAction (which is assigned in the constructor).
 */
public class WMSRenderer implements LayerRenderer
{
    private static final Logger logger = LogManager.getLogger(WMSRenderer.class);
    private static final String DEFAULT_ENCODING = "UTF-8";
    private WMSMap wmsMap = null;

    /**
     * Constructor
     * @param wmsMap
     */
    public WMSRenderer(WMSMap wmsMap) {
        this.wmsMap = wmsMap;
    }

    /**
     * Calls to request a map image.
     * @param layerRenderParams An object containing the parameters needed to render this layer
     * @return BufferedImage The requested layer, rendered to a BufferedImage.
     */
    public BufferedImage render(LayerRenderParams layerRenderParams)
    {
        Contract.pre((layerRenderParams != null), "layerRenderParams cannot be null");
        String layerName = layerRenderParams.getLayerName();
        try
        {
                String url = wmsMap.getUrl();
                if(wmsMap.isSecure()){
                   url= URLDecoder.decode(wmsMap.getUrl(), DEFAULT_ENCODING) ;
                }
                WMSRendererHelper wmsRendererHelper = new WMSRendererHelper();
                StringBuilder completeURL = wmsRendererHelper.buildURL(layerRenderParams.getBoundingBox(),
                        new Double(layerRenderParams.getImageSize().getHeight()),
                        new Double(layerRenderParams.getImageSize().getWidth()), url, wmsMap, false);
                CloseableHttpClient httpClient = wmsMap.getHttpClientFactory().getHttpClient(url, wmsMap.isSecure(), completeURL);
                HttpGet serviceRequest = new HttpGet(completeURL.toString());
                HttpResponse serviceResponse = httpClient.execute(serviceRequest);
                if (serviceResponse.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                   return ImageIO.read(serviceResponse.getEntity().getContent());
                } else {
                    logger.warn("Exception while requesting the map image for layer "
                            + layerName + "and status code is " + serviceResponse.getStatusLine().getStatusCode() +
                            "Error is - " + IOUtils.toString(serviceResponse.getEntity().getContent()));
                }
        }
        catch (MalformedURLException ex) {
            logger.error("Exception while requesting the map image for layer "
                    + layerName + ". Continuing with empty image", ex);
        }
        catch (IOException ex) {
            logger.error("Exception while requesting the map image for layer "
                    + layerName + ". Continuing with empty image", ex);
        }
        catch (Exception ex) {
            logger.error("Exception while requesting the map image for layer "
                    + layerName + ". Continuing with empty image", ex);
        }
        return null;
    }

}
