package com.pb.stratus.controller.action;

import com.pb.stratus.controller.InvalidGazetteerException;
import com.pb.stratus.controller.datainterchangeformat.PlainTextConvertible;
import com.pb.stratus.controller.datainterchangeformat.PlainTextConvertibleImpl;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;


public class ImportKMLAction extends DataInterchangeFormatControllerAction {


    public static final StringBuilder EMPTY_KML = new StringBuilder();

    static {
        EMPTY_KML.append("<kml xmlns=\"http://earth.google.com/kml/2.0\">");
        EMPTY_KML.append("<Folder><name>OpenLayers export</name><description>");
        EMPTY_KML.append("Exported on ");
        EMPTY_KML.append(" </description></Folder></kml>");
    }

    @Override
    protected Object createObject(HttpServletRequest request)
            throws ServletException, IOException,
            InvalidGazetteerException {
        PlainTextConvertible plainTextConvertible = null;
        // the API does not support generics
        List uploadItems = (List) request.getAttribute("Multipart-Items");
        // handle for only one file, that's what we are supporting currently
        if (uploadItems != null && uploadItems.size() > 0) {
            FileItem fileItem = (FileItem) uploadItems.get(0);
            plainTextConvertible = new PlainTextConvertibleImpl(fileItem.getString("UTF-8"));
        }
        if (plainTextConvertible == null) {
            plainTextConvertible = new PlainTextConvertibleImpl(EMPTY_KML.toString());
        }
        return plainTextConvertible;
    }
}