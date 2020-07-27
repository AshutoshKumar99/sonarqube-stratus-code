package com.pb.gazetteer.webservice;

import com.pb.gazetteer.ConfigurationException;
import com.pb.gazetteer.IndexSearchFactory;
import com.pb.gazetteer.lucene.LuceneIndexSearchFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Listens to the application context + Startup
 */
public class ApplicationContextListener implements ServletContextListener
{
    public static final String PROVIDER_KEY = "com.pb.gazetteer.webservice.LocateConfigProvider";
    private static final String BASE_LOCATE_CONFIGURATION_DIR = "stratus.customer.config.dir";
    private static final String LUCENE_CLEANER_DELAY_KEY = "com.pb.gazetteer.lucene.LuceneIndexCleaner.delay";
    private static final String LUCENE_CLEANER_THREAD_CNT_KEY = "com.pb.gazetteer.lucene.LuceneIndexCleaner.threadPoolSize";

    private static final Logger logger = LogManager.getLogger(ApplicationContextListener.class);

    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        ServletContext context = servletContextEvent.getServletContext();

        //system properties override init params
        String baseConfigDir = System.getProperty(BASE_LOCATE_CONFIGURATION_DIR);
        if (StringUtils.isBlank(baseConfigDir))
        {
            //no override, so check init params
            baseConfigDir = context.getInitParameter(BASE_LOCATE_CONFIGURATION_DIR);
            if (StringUtils.isBlank(baseConfigDir))
            {
                //no init param or override, must fail
                logger.error("Could not load Locate properly...." +
                        BASE_LOCATE_CONFIGURATION_DIR + " system property not found");
                throw new ConfigurationException("System property '"
                        + BASE_LOCATE_CONFIGURATION_DIR + "' has not been set");
            }
        }

        File configDir = new File(baseConfigDir);
        logger.info(" Configuration: " + baseConfigDir);

        if (!configDir.isDirectory() || !configDir.exists())
        {
            logger.error("Base configuration directory does not exist: " + baseConfigDir);
            throw new ConfigurationException("Base configuration directory does not exist: " + baseConfigDir);
        }

        //create search factories
        Map<String, IndexSearchFactory> factories = new HashMap<String, IndexSearchFactory>();

        addFactory(factories, buildLuceneFactory(context));

        LocateConfigProvider configProvider = new LocateConfigProvider(baseConfigDir, factories);
        context.setAttribute(PROVIDER_KEY, configProvider);
    }

    private void addFactory(Map<String, IndexSearchFactory> factories, IndexSearchFactory factory)
    {
        factories.put(factory.getClass().getName(), factory);
    }

    /**
     * Builds information at startup.
     * @param context
     * @return
     */
    private IndexSearchFactory buildLuceneFactory(ServletContext context)
    {
        int delay = getInitParamAsInt(context, LUCENE_CLEANER_DELAY_KEY, -1);
        int threadCnt = getInitParamAsInt(context, LUCENE_CLEANER_THREAD_CNT_KEY, -1);

        return new LuceneIndexSearchFactory(delay, threadCnt);
    }


    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        ServletContext context = servletContextEvent.getServletContext();
        LocateConfigProvider provider = (LocateConfigProvider) context.getAttribute(PROVIDER_KEY);
        if (provider != null) {
            provider.release();
            context.removeAttribute(PROVIDER_KEY);
        }
    }

    private int getInitParamAsInt(ServletContext servletContext, String key, int defaultValue)
    {
        String valueStr = servletContext.getInitParameter(key);
        if (StringUtils.isNotBlank(valueStr))
        {
            try
            {
                return Integer.parseInt(valueStr);
            } catch (NumberFormatException e)
            {
                logger.error("Invalid value for " + key + ": " + e.getMessage());
                throw new ConfigurationException("Invalid value for " + key, e);
            }
        }

        return defaultValue;
    }
}
