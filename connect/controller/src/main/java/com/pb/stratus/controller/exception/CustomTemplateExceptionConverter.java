package com.pb.stratus.controller.exception;

import com.pb.stratus.controller.Constants;
import com.pb.stratus.core.exception.DefaultExceptionConverter;
import org.apache.commons.lang.StringUtils;
import uk.co.graphdata.utilities.contract.Contract;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by GU003DU on 11-Feb-18.
 */
public class CustomTemplateExceptionConverter extends DefaultExceptionConverter {
    public CustomTemplateExceptionConverter(int errorCode) {
        super(errorCode);
    }

    @Override
    public void sendError(Exception x, HttpServletResponse response)
            throws IOException {
        Contract.pre(x instanceof CustomTemplateException,
                "Exception must be an instance of "
                        + CustomTemplateException.class.getName());
        PrintWriter writer = null;
        try {
            CustomTemplateException ex = (CustomTemplateException) x;
            response.setStatus(Constants.ERROR_UNPROCESSABLE_ENTITY);
            writer = response.getWriter();
            writer.write(ex.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
