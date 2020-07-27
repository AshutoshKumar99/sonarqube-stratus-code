package com.pb.stratus.controller.datainterchangeformat;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class to extract format from the request object.
 * currently only multipart request and normal get/post request
 * are supported.
 */
public class ExtractFormatFromRequest {

    private ServletFileUpload servletFileUpload;

    private static interface ExtractFormat {
        boolean canHandle(HttpServletRequest request);

        String handle(HttpServletRequest request) throws FileUploadException;
    }


    private class MultiPartHandler implements ExtractFormat {

        @Override
        public boolean canHandle(HttpServletRequest request) {
            return request.getContentType() != null &&
                    request.getContentType().toLowerCase().indexOf(
                            "multipart/form-data") > -1;
        }

        @Override
        public String handle(HttpServletRequest request)
                throws FileUploadException {
            List uploadItems =
                    servletFileUpload.parseRequest(request);
            request.setAttribute("Multipart-Items", uploadItems);
            for (Iterator i = uploadItems.iterator(); i.hasNext(); ) {
                FileItem fileItem = (FileItem) i.next();
                if (fileItem.getFieldName().equals("format")) {
                    return fileItem.getString();
                }
            }
            return null;
        }
    }

    private class DefaultHandler implements ExtractFormat {
        public static final String DATA_INTERCHANGE_FORMAT_PARAM = "format";

        @Override
        public boolean canHandle(HttpServletRequest request) {
            return true;
        }

        @Override
        public String handle(HttpServletRequest request)
                throws FileUploadException {
            return request.getParameter(DATA_INTERCHANGE_FORMAT_PARAM);
        }
    }

    private final List<ExtractFormat> extractFormats =
            new ArrayList<ExtractFormat>();


    public ExtractFormatFromRequest() {
        FileItemFactory factory = new DiskFileItemFactory();
        this.servletFileUpload = new ServletFileUpload(factory);
        extractFormats.add(new MultiPartHandler());
        extractFormats.add(new DefaultHandler());
    }

    public String extract(HttpServletRequest request) {
        for (ExtractFormat extractFormat : extractFormats) {
            if (extractFormat.canHandle(request)) {
                try {
                    return extractFormat.handle(request);
                } catch (FileUploadException e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * This method is for testing purpose only.
     * @param servletFileUpload
     */
    protected void setServletFileUpload(ServletFileUpload servletFileUpload)
    {
        this.servletFileUpload = servletFileUpload;
    }
}
