package com.pb.stratus.controller.legend;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class SingleLegendItem extends LegendItem implements Serializable
{
    private static final long serialVersionUID = -8274316118956162101L;
    
    // keeping BufferedImage as transient as it is not serializable.
    private transient BufferedImage icon;

    // introducing this field so that image data can be serialized and image
    // can be recreated after de-serialization.
    private byte[] iconData;

    private String base64image;

    private boolean isWMSLegendItem = false;

    public SingleLegendItem(String displayName, byte[] iconData)
    {
        super(displayName, "single");
        this.iconData = iconData;
        initIcon();
    }
    
    /**
     * create icon from byte array.
     */
    private void initIcon()
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(iconData);
        try
        {
            icon = ImageIO.read(bis);
        }
        catch (IOException iox)
        {
            throw new FailedToReadLegendIconException(iox);
        }
    }

    public BufferedImage getIcon()
    {
        if(icon == null)
        {
            initIcon();
        }
        return icon;
    }

    @Override
    protected Collection<? extends BufferedImage> getIcons()
    {
        if(icon == null)
        {
            initIcon();
        }
        return Collections.singletonList(icon);
    }

    public boolean isWMSLegendItem() {
        return isWMSLegendItem;
    }

    public void setWMSLegendItem(boolean WMSLegendItem) {
        isWMSLegendItem = WMSLegendItem;
    }

    public String getBase64image() {
        return base64image;
    }

    public void setBase64image(String base64image) {
        this.base64image = base64image.replaceAll("\n", "");
    }
}
