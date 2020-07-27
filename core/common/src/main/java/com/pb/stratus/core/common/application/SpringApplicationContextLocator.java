package com.pb.stratus.core.common.application;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This is a ServiceLocator adapter for the Spring ApplicationContext.
 * It exists because some elements of the app need to get access to
 * beans managed by the app context, but cannot have dependencies injected
 * because they are not, themselves, in the app context.
 *
 * I believe that the ApplicationStartupFilter could be removed (since
 * the Spring ContextLoaderListener serves the purpose of hooking the app startup),
 * and the ServletApplication (or even the separate things it creates) could be
 * directly created by the app context.  I have not made that change myself because
 * I am afraid of creating subtle problems, and I'm not really fluent with the app.
 */
public class SpringApplicationContextLocator implements ApplicationContextAware {
    private static ApplicationContext context;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }
}
