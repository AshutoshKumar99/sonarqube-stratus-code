package com.pb.stratus.controller.action;


import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.kml.ExportAnnotationsAsKMLResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class ExportKMLAction extends DataInterchangeFormatControllerAction{

    public final static String KML_STRING = "kmlString";

    @Override
    protected Object createObject(HttpServletRequest request) throws
            ServletException, IOException, InvalidGazetteerException {
        String kmlString = request.getParameter(KML_STRING);
        return new ExportAnnotationsAsKMLResponse(kmlString);
    }
}
