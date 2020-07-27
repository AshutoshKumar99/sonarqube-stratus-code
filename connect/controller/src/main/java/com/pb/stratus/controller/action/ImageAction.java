package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ConfigReader;

import javax.servlet.http.HttpServletRequest;

/**
 * Action class to get image from the configuration directory
 * If type is populated and is of template the image is fetched
 * from print templates directory otherwise from images directory
 * 
 * @author 
 */
public class ImageAction extends BaseConfigProviderAction
{
    
    public ImageAction(ConfigReader configReader)
    {
        super(configReader);
    }
    
    @Override
    protected String getFileName(HttpServletRequest request)
    {
        return request.getParameter("name");
    }

    @Override
    protected String getMimeType()
    {
        //FIXME what if the image is not a gif?
        return "image/gif";
    }

}
