package com.pb.stratus.controller.legend;

import com.mapinfo.midev.service.mapping.v1.Legend;
import com.mapinfo.midev.service.mapping.v1.LegendRow;
import com.mapinfo.midev.service.mapping.v1.LegendType;
import com.pb.stratus.controller.i18n.LocaleResolver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A very simple class to model a layer so that it can be serialized into JSON.
 * 
 * @author Colin Kirkham
 */
public class LayerDescription
{

    private String name;

    private List<String> iconDescriptions = new ArrayList<String>();
    private List<byte[]> icons = new ArrayList<byte[]>();
    private List<Integer> iconID = new ArrayList<Integer>();

    private String legendTitle;

    /**
     * Constructor for a LayerDescription object.
     * 
     * @param name
     *            a string representing the name defined for a layer.
     */
    public LayerDescription(String name)
    {
        this.name = name;
    }

    /**
     * Constructs a LayerDescription object for the given Midev FeatureLayer.
     * 
     * @param layer an Midev FeatureLayer object that encapsulates the layer
     *        name 
     * @param legend the legend object that describes the visual properties of 
     *        the given feature layer
     */
    public LayerDescription(String name, Legend legend, IndexCounter counter) throws IOException
    {
        this.name = name;
        LegendLocalizer localiser =
                                new LegendLocalizer(LocaleResolver.getLocale());

        if (legend.getLegendType().equals(LegendType.RANGED_THEME)
                || legend.getLegendType().equals(
                        LegendType.INDIVIDUAL_VALUE_THEME))
        {
            
            legendTitle = localiser.localiseLabelRangesBy(legend.getTitle());
        }

        if (legend.getLegendRow().size() > 0)
        {
            for (LegendRow row : legend.getLegendRow())
            {
                iconDescriptions.add(localiser.localiseRange(row.getDescription()));
                iconID.add(counter.nextIndex());
                byte[] image = row.getImage();
                if (image == null) // No legend icon
                {
                    image = getMissingLegendIcon();
                }
                this.icons.add(image);
            }
        }
        else // tables with empty rows
        {
             iconID.add(counter.nextIndex());
             byte[] image = getMissingLegendIcon();
             this.icons.add(image);
        }
    }

    protected byte[] getMissingLegendIcon() throws IOException
    {
        InputStream is = LayerDescription.class.
                getResourceAsStream("missingLegendIcon.png");
        BufferedImage image=  ImageIO.read(is);
        is.close();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", bos);
        bos.close();
        return bos.toByteArray();
    }
    
    public LayerDescription(String name, List<String> iconDescription)
    {
        this.name = name;
        this.iconDescriptions = iconDescription;

    }

    /**
     * Accessor method for this instance's name attribute.
     * 
     * @return the value of the name attribute.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Mutator method for this instance's name attribute.
     * 
     * @param name
     *            the name for this instance.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Accessor method for this instance's legend icon description attribute.
     * 
     * @return the value of the name attribute.
     */
    public void setIconDescriptions(List<String> iconsDescriptions)
    {
        this.iconDescriptions = iconsDescriptions;
    }

    /**
     * Mutator method for this instance's legend icon description attribute.
     * 
     * @param name
     *            the name for this instance.
     */
    public List<String> getIconDescriptions()
    {
        return this.iconDescriptions;
    }

    /**
     * Accessor method for this instance's legend title.
     * 
     * @return the value of the name attribute.
     */
    public void setLegendTitle(String legendTitle)
    {
        this.legendTitle = legendTitle;
    }

    /**
     * Mutator method for this instance's legend title.
     * 
     * @param name
     *            the name for this instance.
     */
    public String getLegendTitle()
    {
        return this.legendTitle;
    }

    public List<Integer> getIconID()
    {
        return iconID;
    }

    public void setIconID(List<Integer> iconID)
    {
        this.iconID = iconID;
    }

    public List<byte[]> iconBytes()
    {
        return icons;
    }

}
