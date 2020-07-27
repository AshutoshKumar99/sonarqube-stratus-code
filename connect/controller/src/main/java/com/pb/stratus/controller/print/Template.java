package com.pb.stratus.controller.print;

import com.pb.stratus.core.util.ObjectUtils;

/**
 * A template for a document. A template describes the layout of a document
 * and the components that should go into it.
 */
public class Template
{
    
    private String templateContent;
    
    public Template(String templateContent)
    {
        this.templateContent = templateContent;
    }
    
    public String getTemplateContent()
    {
        return templateContent;
    }
    
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        }
        if (o == null)
        {
            return false;
        }
        if (o.getClass() != this.getClass())
        {
            return false;
        }
        Template that = (Template) o;
        return (ObjectUtils.equals(this.templateContent, 
                that.templateContent));
    }
    
    public int hashCode()
    {
        return ObjectUtils.hash(ObjectUtils.SEED, templateContent);
    }
    

}
