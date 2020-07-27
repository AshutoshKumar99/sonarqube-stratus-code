package com.pb.stratus.controller;

import com.pb.stratus.controller.exception.*;
import com.pb.stratus.controller.print.MaxNumberOfTilesExceededException;
import com.pb.stratus.controller.print.RenderException;

import com.pb.stratus.core.exception.ConfigurationException;
import com.pb.stratus.core.exception.ConfigurationExceptionConverter;
import com.pb.stratus.core.exception.DefaultExceptionConverter;
import com.pb.stratus.core.exception.HttpExceptionConverter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts an exception to an HTTP error
 *
 * @author Volker Leidl
 */
public class ErrorHandler {

    private static final Logger logger = LogManager.getLogger(ErrorHandler.class);

    private Map<Class<?>, HttpExceptionConverter> exceptions = new HashMap<Class<?>, HttpExceptionConverter>();

    public ErrorHandler() {
        exceptions.put(Exception.class, new DefaultExceptionConverter());
        exceptions.put(IllegalRequestException.class, 
                new DefaultExceptionConverter(400));
        exceptions.put(MapNotFoundException.class, 
                new DefaultExceptionConverter(451));
        exceptions.put(MaxNumberOfTilesExceededException.class, 
                new DefaultExceptionConverter(400));
        exceptions.put(UnknownTableException.class, new UnknownTableExceptionConverter(452));
        exceptions.put(UnknownPropertyException.class, new UnknownPropertyExceptionConverter(453));
        exceptions.put(UnknownSrsException.class, new DefaultExceptionConverter(454));
        exceptions.put(InvalidParameterException.class, new DefaultExceptionConverter(455));
        exceptions.put(UnknownTenantException.class, new DefaultExceptionConverter(456));

        exceptions.put(UnsupportedGeometryException.class, new DefaultExceptionConverter(551));
        exceptions.put(MissingGeometryException.class, new DefaultExceptionConverter(553));
        exceptions.put(IdentifierResolveFailureException.class, new IdentifierResolveFailuerExceptionConverter(552));
        exceptions.put(AdminConsoleNotFoundException.class, new DefaultExceptionConverter(554));
        exceptions.put(LocateServiceNotFoundException.class, new DefaultExceptionConverter(555));
        exceptions.put(TenantThemeNotFoundException.class, new DefaultExceptionConverter(556));
        exceptions.put(MapResolveFailureException.class, new MapResolveFailureExceptionConverter(557));
        exceptions.put(QueryConfigException.class, new QueryConfigExceptionConverter(559));
        exceptions.put(ConfigurationException.class, new ConfigurationExceptionConverter(404));
        exceptions.put(CatalogConfigException.class, new CatalogConfigExceptionConverter(457));
		exceptions.put(RenderException.class, new DefaultExceptionConverter(500));
        exceptions.put(CustomTemplateException.class, new CustomTemplateExceptionConverter(422));

    }

    /**
     * Writes an error to <code>response</code> based on the given exception. If the exception cannot be mapped to an
     * error code, the generic failure code 500 will be used.
     *
     * @param x
     * @param response
     */
    public void convert(Exception x, HttpServletResponse response) {
        if (response.isCommitted()) {
            //attempting to send an error response after we've already committed
            //will cause another exception.  Since we can't send anything to the
            //client now, we'll just stop.
            logger.debug("Response already committed, cannot send error", x);
            return;
        }

        Class<?> c = x.getClass();
        HttpExceptionConverter conv = null;
        while (c.getSuperclass() != null) {
            conv = exceptions.get(c);
            if (conv != null) {
                break;
            }
            c = c.getSuperclass();
        }
        if (conv == null) {
            conv = new DefaultExceptionConverter();
        }
        try {
            conv.sendError(x, response);
        }
        catch (IOException iox) {
            logger.error("An error occurred but couldn't be sent to the client", x);
        }
    }

}
