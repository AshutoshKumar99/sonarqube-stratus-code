package com.pb.stratus.controller.filter;

import com.pb.stratus.controller.ErrorHandler;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Converts controller exceptions into HTTP errors.
 */
public class ErrorHandlingFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(ErrorHandlingFilter.class);

    private ErrorHandler handler = new ErrorHandler();

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        try {
            chain.doFilter(request, response);
        }
        catch (Exception e) {
            logger.error("Error during request handling", e);
            if (response instanceof HttpServletResponse) {
                handler.convert(e, (HttpServletResponse) response);
            }
        }
    }

    public void destroy() {
    }
}
