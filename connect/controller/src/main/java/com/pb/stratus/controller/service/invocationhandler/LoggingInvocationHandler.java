/**
 * A dynamic proxy implementation for intercepting the method calls of a class.This proxy can be used to log the
 * time taken to execute a particular method call. Currently it is used for capturing time for service level calls.
 * The implementation as an InvocationHandler allows this class to be used like an AOP aspect in the following form:
 * <p/>
 * <pre>
 * ...
 *  WebServiceInterface ws = (WebServiceInterface)LoggingInvocationHandler.newInstance(webServiceStub)
 * ...
 * ws.someWebServiceMethod();
 * </pre>
 * <p/>
 * User: GU003DU
 * Date: 10/1/13
 * Time: 4:12 PM
 *
 */

package com.pb.stratus.controller.service.invocationhandler;
import com.pb.stratus.controller.util.TransactionMonitor;
import com.pb.stratus.core.util.ServiceLoggingUtil;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class LoggingInvocationHandler implements InvocationHandler {

    private static final String UNEXPECTED_ERROR = "Method threw unexpected invocation exception";

    private Logger logger = LogManager.getLogger(LoggingInvocationHandler.class);
    private Object target;
    private  TransactionMonitor monitor;

    private LoggingInvocationHandler(Object target, TransactionMonitor monitor) {
        this.target = target;
        this.monitor = monitor;
    }

    public static Object newInstance(Object target, TransactionMonitor monitor) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(), target
                .getClass().getInterfaces(), new LoggingInvocationHandler(target, monitor));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Object result;
        try {
            ServiceLoggingUtil.captureClientDetailsAtStart(target, method.getName(), args[0]);
            result = method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException(UNEXPECTED_ERROR, e);
        } finally {
            ServiceLoggingUtil.captureClientDetailsAtEnd();
            monitor.logTransaction();
            ServiceLoggingUtil.clearContext();
        }
        return result;
    }
}
