package com.pb.stratus.controller.action;

import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.catalog.CatalogService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Fetches the midev tables for overlays and returns the result as a json
 * object which contains an overlay and its tables
 */
public class OverlaysNamedTablesAction extends DataInterchangeFormatControllerAction
{
    private CatalogService catalogService;

    private static final String OVERLAY_PARAM_NAME = "overlays";

    public OverlaysNamedTablesAction(CatalogService catalogService)
    {
        this.catalogService = catalogService;
    }

    @Override
    protected Object createObject(HttpServletRequest request)
    {
        String[] overlayNames = request.getParameterValues(OVERLAY_PARAM_NAME);
        if (overlayNames == null || overlayNames.length == 0)
        {
            throw new IllegalRequestException();
        }
        return catalogService.getTableNames(Arrays.asList(overlayNames));
    }

}
