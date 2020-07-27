package com.pb.stratus.controller.info;

import java.util.ArrayList;
import java.util.List;


/**
 * A very simple class to model an Info Row so that it can be serialised into
 * JSON.
 * 
 * @author vilam
 */
public class InfoRow
{

    private String title;
    private String key;
    private String description;
    private String link;
    private String image;
    List<InfoField> additionalFields = new ArrayList<InfoField>();

    /**
     * Constructor for an Location object.
     */
    public InfoRow(String title, String key, String descripton, String link,
            String image)
    {
        this.title = title;
        this.key = key;
        this.description = descripton;
        this.link = link;
        this.image = image;
    }

    public InfoRow(String title, String key, String descripton, String link,
            String image,   List<InfoField> additionalFields)
    {
        this.title = title;
        this.key = key;
        this.description = descripton;
        this.link = link;
        this.image = image;
        this.additionalFields = additionalFields;
    }

    /**
     * Accessor method for this instance's title attribute.
     * 
     * @return the value of the title attribute.
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Mutator method for this instance's title attribute.
     * 
     * @param title
     *            the title for this instance.
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Accessor method for this instance's key attribute.
     * 
     * @return the value of the key attribute.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Mutator method for this instance's key attribute.
     * 
     * @param key
     *            the key for this instance.
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Accessor method for this instance's description attribute.
     * 
     * @return the value of the description attribute.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Mutator method for this instance's description attribute.
     * 
     * @param description
     *            the description for this instance.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Accessor method for this instance's link attribute.
     * 
     * @return the value of the link attribute.
     */
    public String getLink()
    {
        return link;
    }

    /**
     * Mutator method for this instance's link attribute.
     * 
     * @param link
     *            the link for this instance.
     */
    public void setLink(String link)
    {
        this.link = link;
    }

    /**
     * Accessor method for this instance's image attribute.
     * 
     * @return the value of the image attribute.
     */
    public String getImage()
    {
        return image;
    }

    /**
     * Mutator method for this instance's image attribute.
     * 
     * @param image
     *            the image for this instance.
     */
    public void setImage(String image)
    {
        this.image = image;
    }

    /**
     * Accessor method for this instance's additional fields attribute.
     * 
     * @return the value of the image attribute.
     */
    public   List<InfoField> getAdditionalFields()
    {
        return this.additionalFields ;
    }    

    /**
     * Mutator method for this instance's additional fields attribute.
     * 
     * @param image
     *            the image for this instance.
     */
    public void setAdditionalFields(  List<InfoField>  fields)
    {
        this.additionalFields = fields;
    }
}
