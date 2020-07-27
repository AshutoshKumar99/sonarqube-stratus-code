package com.pb.stratus.controller.catalog;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Provides methods to discover the structure of available back end data
 */
public interface CatalogService
{
    
    /**
     * Returns the tables one or more overlays are comprised of. The iteration
     * order of the keys in the returned map corresponds to the order of
     * the items determined by the <code>overlayNames</code> parameter.
     */
    Map<String, List<String>> getTableNames(Collection<String> overlayNames);

}
