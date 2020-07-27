package com.pb.gazetteer.webservice;

import com.pb.gazetteer.ConfigurationException;
import com.pb.gazetteer.ConfigurationReader;
import com.pb.gazetteer.IndexSearchFactory;
import com.pb.gazetteer.LocateConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class LocateConfigProvider
{
    private static final Logger log = LogManager.getLogger(LocateConfigProvider.class);
    private static final String LOCATE_CONFIGURATION_FILE_NAME = "locateconfig" + File.separator + "configuration.xml";
    private static final String LOCATE_CONFIGURATION_CSV_FILE_NAME = "luceneCSVdata";
    private final String m_baseConfigDir;
    private final Map<String, IndexSearchFactory> m_factories;

    public LocateConfigProvider(String baseConfigDir, Map<String, IndexSearchFactory> factories)
    {
        m_baseConfigDir = baseConfigDir;
        m_factories = factories;
    }

    public LocateConfig getConfig(String tenantName)
    {
        if (StringUtils.isBlank(tenantName))
        {
            log.error("Required tenantName is empty/null.");
            throw new IllegalArgumentException("tenantName is required.");
        }

        try
        {
            String configFile = getLocateConfigPath(tenantName);
            ConfigurationReader reader = new ConfigurationReader(new File(configFile), m_factories);
            return reader.getLocateConfig();
        } catch (IOException e)
        {
            log.error("Error processing locate configuration file: " + e.getMessage());
            throw new ConfigurationException("Error processing locate configuration file.", e);
        } catch (SAXException e)
        {
            log.error("Error processing locate configuration file: " + e.getMessage());
            throw new ConfigurationException("Error processing locate configuration file.", e);
        } catch (ParserConfigurationException e)
        {
            log.error("Error processing locate configuration file: " + e.getMessage());
            throw new ConfigurationException("Error processing locate configuration file.", e);
        }
    }

    public String getLocateConfigPath(String tenantName)
    {
         String configFile = m_baseConfigDir + File.separator + tenantName + File.separator
            + LOCATE_CONFIGURATION_FILE_NAME;
         return configFile;
     }

    public String getLocateConfigCSVPath(String tenantName)
    {
        String addressCSVPath = m_baseConfigDir + File.separator + tenantName + File.separator
                + LOCATE_CONFIGURATION_CSV_FILE_NAME;
        return addressCSVPath;
    }

    public void release()
    {
        for (IndexSearchFactory factory : m_factories.values())
        {
            factory.shutdown();
        }
        m_factories.clear();
    }
}
