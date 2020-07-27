package com.pb.stratus.controller.customtags;

import com.pb.stratus.controller.configuration.TenantProfile;
import com.pb.stratus.controller.configuration.TenantProfileManagerImpl;
import com.pb.stratus.controller.print.config.LayerServiceType;
import com.pb.stratus.controller.print.config.MapConfig;
import com.pb.stratus.controller.print.config.MapConfig.ThirdPartyAPIKey;
import com.pb.stratus.controller.print.config.MapConfigRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.util.List;

/**
 * Custom JSP Script tag to add script elements for Bing and Google
 * This class scans through the map definitions and if it finds Google or
 * Bing maps, a script tag is created with the users api key
 */
public class ScriptInserter extends TagSupport
{
    public final static String MAP_CONFIG_REPOSITORY_ATTR
            = "com.pb.stratus.map.config.repository";

    private String config;
    
    public void setConfig(String config)
    {
        this.config = config;
    }

    @Override
    public int doEndTag() throws JspException
    {
        try
        {
            MapConfig mapConfig = getMapConfig(pageContext);
            List<MapConfig.MapDefinition> mapDefs = mapConfig
                    .getMapDefinitions();
            boolean googleRequired = false;

            for (MapConfig.MapDefinition mapDef : mapDefs)
            {
                if (mapDef.getService() == LayerServiceType.GOOGLE)
                {
                    googleRequired = true;
                    break;
                }
            }

            if (googleRequired)
            {
                for (ThirdPartyAPIKey key : mapConfig.getThirdPartyAPIKeys())
                {
                    if (key.getService() == LayerServiceType.GOOGLE)
                    {
                        writeGoogleScriptTag(key.getKey());
                        break;
                    }
                }
            }
        }
        catch (IOException iox)
        {
            throw new JspException(iox);
        }
        catch (RuntimeException rx)
        {
            throw rx;
        }
        catch (Exception x)
        {
            throw new JspException(x);
        }

        return super.doEndTag();
    }

    private MapConfig getMapConfig(PageContext pageContext) throws Exception
    {
        TenantProfile tenantProfile = TenantProfileManagerImpl.getRequestProfile((HttpServletRequest)pageContext.getRequest());
        MapConfigRepository repo = tenantProfile.getMapConfigRepository();
        
        return repo.getMapConfig(config);
    }

    private void writeGoogleScriptTag(String key) throws IOException
    {
        JspWriter writer = pageContext.getOut();
        writer.println("<script type=\"text/javascript\" "
            + "src=\"http://maps.google.com/maps?file=api&amp;v=2&amp;key="
            + key + "\"></script>");
    }
}
