package com.pb.stratus.controller.action;

import com.pb.stratus.core.configuration.ControllerConfiguration;

import java.util.Properties;


/**
 * This class provides a common default implementation for the ControllerAction
 * interface. No implementation is provided for the 
 * {@link ControllerAction#execute(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, String)}
 * method. Subclasses are forced to implement that on their own.
 */
public abstract class BaseControllerAction implements ControllerAction {

    public static final String TEMPLATE_NAME_PARAM = "template";

    public static final String TITLE_PARAM = "title";
    
    public static final String CALLOUT_PARAM = "calloutinfo";

    public static final String LEGEND_TITLE_PARAM = "legendTitle";

    public static final String DISPLAY_UNIT_PARAM = "displayUnit";

    public static final String FMN_RESULTS_PARAM = "fmnResults";

    public static final String ANNOTATION_RESULTS_PARAM = "annotationResults";

    public static final String WMS_BASE_MAP_PARAM = "wmsBaseMap";

    public static final String WMS_OVERLAYS_MAP_PARAM = "wmsOverlays";

    public static final String MAP_CFG = "mapcfg";

    public static final String LOCATOR_IMAGE_PARAM = "locatorImagePath";

    private ControllerConfiguration config;
    
    private String path;
    
    public void setConfig(ControllerConfiguration config)
    {
        this.config = config;
    }
    
    /**
     * Exposes a previously set configuration to subclasses.
     * 
     * @return a configuration object or <code>null</code> if 
     *         {@link #setConfig(Properties)} has not been called yet. 
     */
    protected ControllerConfiguration getConfig()
    {
        return config;
    }
    
    public void setPath(String path)
    {
        this.path = path;
    }
    
    /**
     * Checks whether the given path matches the path of this controller 
     * action. This default implementation checks if the given path starts
     * with the path that was previously set with {@link #setPath(String)}. 
     * However, special care is taken to avoid partial directory matches. 
     * Those bits in the path that are separated by <code>/</code> characters
     * are interpreted as directories. A partial directory match would be
     * <code>/test/map</code> and <code>/test/mapleTree</code>. Note that
     * the latter path starts with the former, however passing the latter
     * to this method will yield a <code>false</code> result, because only
     * full directory matches are accepted. In the above example, the path
     * <code>/test/map/wms</code> would be seen as a positive match. This 
     * is done to avoid matches for paths that happen to start with the same
     * string, but are otherwise unrelated.
     * 
     * @param pathToMatch
     * @return <code>true</code> if a match according to the above rules can
     *         be found, <code>false</code> otherwise. A 
     *         <code>pathToMatch</code> of <code>null</code> will always yield 
     *         <code>false</code>.
     */
    public boolean matches(String pathToMatch)
    {
        if (pathToMatch == null)
        {
            return false;
        }
        if (pathToMatch.equals(path))
        {
            return true;
        }
        if (pathToMatch.startsWith(path + "/"))
        {
            return true;
        }
        return false;
    }


    /**
     * Does nothing
     */
    public void init()
    {
    }
}
