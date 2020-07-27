package com.pb.stratus.controller.action;

import com.pb.stratus.controller.IllegalRequestException;
import com.pb.stratus.controller.i18n.LocaleResolver;
import com.pb.stratus.controller.json.LegendDataHolder;
import com.pb.stratus.controller.legend.LegendData;
import com.pb.stratus.controller.legend.LegendService;
import com.pb.stratus.controller.legend.OverlayLegend;
import com.pb.stratus.core.common.Preconditions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class LegendAction extends DataInterchangeFormatControllerAction
{

    private LegendService legendService;
    
    private String legendImageActionBasePath;
    
    public LegendAction(LegendService legendService, 
            String legendImageActionBasePath)
    {
        this.legendService = legendService;
        this.legendImageActionBasePath = legendImageActionBasePath;
    }
    
    protected Object createObject(HttpServletRequest request) 
            throws ServletException, IOException
    {
        String[] overlayNames = getOverlayNames(request); 
        LegendData legendData = legendService.getLegendData(
                LocaleResolver.getLocale(), null,null, overlayNames);
        String iconUrl = createIconUrl(overlayNames, request);
        // sort the legends in the order of the request.
        legendData.sort(new LegendDataComparator(overlayNames));
        return new LegendDataHolder(legendData, iconUrl);
    }

    private String createIconUrl(String[] overlayNames, 
            HttpServletRequest request)
    {
        StringBuilder b = new StringBuilder("/controller");
        b.append(legendImageActionBasePath);
        b.append("?overlays=");
        int i = 0;
        for (String overlayName : overlayNames)
        {
            if (i++ > 0)
            {
                b.append(",");
            }
            try
            {
                b.append(URLEncoder.encode(overlayName, "UTF-8"));
            }
            catch (UnsupportedEncodingException ux)
            {
                // UTF-8 must be supported
                throw new Error(ux);
            }
        }
        return b.toString();
    }

    private String[] getOverlayNames(HttpServletRequest request)
    {
        String overlayNames = request.getParameter("overlays");
        if (overlayNames == null)
        {
            throw new IllegalRequestException();
        }
        return overlayNames.split("\\s*,\\s*");
    }

    private final class LegendDataComparator implements Comparator<OverlayLegend>
    {

        List<String> requestOverlays;

        public LegendDataComparator(String[] requestOverlays)
        {
            Preconditions.checkNotNull(requestOverlays, "requestOverlays cannot be null");
            this.requestOverlays = Arrays.asList(requestOverlays);
        }

        @Override
        public int compare(OverlayLegend o1, OverlayLegend o2) {
            // ideally the indexes computed below should never be -1. if it is
            // there there is some bug prior to this code, which needs to be fixed.
            // this logic is fine.
            int index1 = this.requestOverlays.indexOf(o1.getTitle());
            int index2 = this.requestOverlays.indexOf(o2.getTitle());
            return index1-index2;
        }
    }

}
