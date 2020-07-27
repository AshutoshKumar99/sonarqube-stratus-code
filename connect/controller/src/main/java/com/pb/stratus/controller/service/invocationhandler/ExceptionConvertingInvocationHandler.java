package com.pb.stratus.controller.service.invocationhandler;

import com.pb.stratus.controller.ExceptionConverter;
import com.pb.stratus.controller.featuresearch.MismatchingTargetException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This InvocationHandler can be used to convert exceptions thrown by methods
 * of an arbitrary target object. Typically this will be used to convert
 * exceptions from a web service into application exceptions. The implementation
 * as an InvocationHandler allows this class to be used like an AOP aspect in
 * the following form:
 * <p/>
 * <pre>
 * ...
 * InvocationHandler handler = new ExceptionConvertingInvocationHandler(
 *         converter, webServiceStub);
 * WebServiceInterface ws = (WebServiceInterface) java.lang.reflect.Proxy.newProxyInstance(..., handler);
 * ...
 * ws.someWebServiceMethod();
 * </pre>
 * <p/>
 * Every call to ws.*() will be transparently passed through the handler and
 * exceptions will be converted accordingly and the caller is freed of that
 * responsibility.
 */
public class ExceptionConvertingInvocationHandler implements InvocationHandler {

    private Logger logger = LogManager.getLogger(ExceptionConvertingInvocationHandler.class);

    private ExceptionConverter converter;

    private Object target;

    public ExceptionConvertingInvocationHandler(ExceptionConverter converter,
                                                Object target) {
        this.converter = converter;
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        return invokeMethod(method, args);
    }

    private Object invokeMethod(Method method, Object[] args) {

        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException itx) {
            Throwable cause = itx.getCause();
            if (cause instanceof Error) {
                throw (Error) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw converter.convert((Exception) cause);
            }
        } catch (IllegalAccessException iax) {
            throw new MismatchingTargetException(target + " doesn't implement"
                    + method.getDeclaringClass(), iax);
        }
    }
}
