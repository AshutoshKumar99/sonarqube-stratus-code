package com.pb.stratus.controller.action;

import com.pb.stratus.controller.featuresearch.FeatureService;
import com.pb.stratus.controller.service.SearchAtPointParams;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class FeatureSearchAtPointAction extends FeatureSearchAction
{
    public static final String WIDTH_PARAM = "width";
    
    public static final String TABLE_NAMES_PARAM = "tableNames";
    
    
    public FeatureSearchAtPointAction(FeatureService featureService)
    {
        super(featureService);
    }

    protected Object createObject(HttpServletRequest request) 
        throws ServletException, IOException
    {
        return getFeatureService().searchAtPoint(createParams(request));

       // return getFeatureService().searchByGeometry(createParams(request));
    }
    
    private SearchAtPointParams createParams(HttpServletRequest request)
    {
        SearchAtPointParams params = new SearchAtPointParams();
        populateParams(params, request);

        String[] tableNames = request.getParameterValues(TABLE_NAMES_PARAM);
        String[] widthArr = request.getParameterValues("width");
        String[] sqlArr =   request.getParameterValues("viewTableSQL");

        for (int i = 0; i < widthArr.length; i++)
        {
            params.addTable(tableNames[i], Double.parseDouble(widthArr[i]));

        }

        if (sqlArr != null){   // used for callout
            for (int i = 0; i < sqlArr.length; i++)
            {
                params.addQuerySQL(tableNames[i], sqlArr[i]);

            }
        }
        else  // Used when pick from map control is active
        { for (int i = 0; i < tableNames.length; i++)
          {
            params.addQuerySQL(tableNames[i], "NA");

          }

        }

        params.setPoint(createPoint(request));
		// enabling totalCount attributes per table
        params.setReturnTotalCount(Boolean.parseBoolean(request.getParameter("returnTotalCount")));
        return params;
    }


    
}
