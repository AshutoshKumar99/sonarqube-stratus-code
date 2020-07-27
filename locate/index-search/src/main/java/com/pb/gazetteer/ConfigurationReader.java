package com.pb.gazetteer;

import com.pb.gazetteer.lucene.LuceneInstance;
import com.pb.gazetteer.search.SearchLogic;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.rmi.ServerException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.pb.gazetteer.ConfigurationFileConstants.*;

/**
 * ConfigurationReader class is used for reading configuration.xml.
 */
public class ConfigurationReader {
    private final static Logger logger = LogManager.getLogger(ConfigurationReader.class);
    private static final SearchLogic DEFAULT_SEARCH_LOGIC = SearchLogic.DEFAULT_LOGIC;
    private static final int DEFAULT_IDLE_DURATION = 30;

    private Document document;

    private Map<String, IndexSearchFactory> m_factories;

    public ConfigurationReader(File file, Map<String, IndexSearchFactory> factories) throws IOException,
            SAXException, ParserConfigurationException {
        m_factories = factories;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();
        document = parser.parse(file);
    }

    public LocateConfig getLocateConfig() throws ConfigurationException, ServerException {
        LocateConfig locateConfig = new LocateConfig();

        List<LocateEngine> engines = populateEngines();
        locateConfig.setLocateEngines(engines);

        List<LocateInstance> instances = populateInstances(locateConfig);
        locateConfig.setLocateInstances(instances);

        NodeList defaultInstance = document
                .getElementsByTagName(_DEFAULT_INSTANCE_);
        String defaultIns = defaultInstance.item(0).getTextContent();

        LocateInstance locateDefaultInstance = locateConfig
                .getLocateInstance(defaultIns);

        if (locateDefaultInstance == null) {
            locateDefaultInstance = instances.get(0);
        }
        locateConfig.setDefaultInstance(locateDefaultInstance);

        return locateConfig;
    }

    private List<LocateEngine> populateEngines() {
        NodeList enginesList = document.getElementsByTagName(_ENGINE_);
        List<LocateEngine> locateEngines = new ArrayList<LocateEngine>();
        LocateEngine locateEngine;

        for (int i = 0; i < enginesList.getLength(); i++) {
            Element engine = (Element) enginesList.item(i);
            String name = getElementText(engine, _NAME_);
            String className = getElementText(engine, _CLASS_);

            locateEngine = new LocateEngine();
            locateEngine.setName(name);
            locateEngine.setClassName(className);

            if (engine.getElementsByTagName(_IDLE_PERIOD_).getLength() > 0) {
                String idle = getElementText(engine, _IDLE_PERIOD_);
                int idlePeriod = DEFAULT_IDLE_DURATION;
                try {
                    idlePeriod = Integer.parseInt(idle);
                } catch (NumberFormatException e) {
                    throw new ConfigurationException("Idle duration '"
                            + idlePeriod + "' is not an integer", e);
                }
                locateEngine.setIdlePeriod(idlePeriod);
            }

            locateEngines.add(locateEngine);
        }
        return locateEngines;
    }

    private List<LocateInstance> populateInstances(LocateConfig locateConfig) throws ServerException{
        NodeList instancesList = document.getElementsByTagName(_INSTANCE_);
        List<LocateInstance> locateInstances = new ArrayList<LocateInstance>();

        for (int i = 0; i < instancesList.getLength(); i++) {
            Element instance = (Element) instancesList.item(i);

            populateLuceneInstance(instance, locateConfig, locateInstances);
        }
        return locateInstances;
    }

    /**
     * Populates the lucene instance.
     * @param instance
     * @param locateConfig
     * @param locateInstances
     */
    private void populateLuceneInstance(Element instance,
                                        LocateConfig locateConfig,
                                        List<LocateInstance> locateInstances) throws ServerException{
        Element configElement = ((Element) instance.getElementsByTagName(
                _CONFIG_).item(0));
        //Who uses it?
        Element remoteIndexDir = (Element) configElement.getElementsByTagName(
                _LOCAL_PENDING_DIR_ELEMENT_).item(0);
        if (remoteIndexDir == null) {
            return;
        }

        LuceneInstance luceneInstance = new LuceneInstance();
        try {
            Element localPathEl = (Element) configElement.getElementsByTagName(
                    _LOCAL_INDEX_DIR_ELEMENT_).item(0);
            luceneInstance.setLocalIndexDir(new File(localPathEl
                    .getTextContent().trim()));
        } catch (NullPointerException npe) {
            logger.error("Caught :"+npe);
            throw new ConfigurationException("Throw :Lucene Configuration "
                    + _LOCAL_INDEX_DIR_ELEMENT_ + " element not found", npe);
        }

        try {
            Element proj = (Element) configElement.getElementsByTagName(
                    _PROJECTION_ELEMENT_).item(0);
            String srs = proj.getTextContent();
            luceneInstance.setSrs(srs);
        } catch (NullPointerException npe) {
            logger.error("Caught :"+npe);
            throw new ConfigurationException("Throw :Lucene Configuration "
                    + _PROJECTION_ELEMENT_ + " element not found", npe);
        }
        try {
            Element searchLogicElement = (Element) configElement.getElementsByTagName(
                    _SEARCH_LOGIC_ELEMENT_).item(0);
            SearchLogic searchLogic;

            //CONN-14598 In case of missing tag searchLogic,application will throw error.So,default value is set.
            if (searchLogicElement == null)
                searchLogic = SearchLogic.DEFAULT_LOGIC;
            else {
                String constantValue = searchLogicElement.getTextContent();
                if (constantValue != null && (!constantValue.isEmpty())) {
                    try {
                        searchLogic = SearchLogic.valueOf(constantValue);
                    } catch (IllegalArgumentException ex) {
                        logger.error(ex+" ,resetting search Logic to DEFAULT_LOGIC");
                        searchLogic = SearchLogic.DEFAULT_LOGIC;
                    }
                } else {
                    searchLogic = SearchLogic.DEFAULT_LOGIC;
                }

            }

            luceneInstance.setSearchLogic(searchLogic);
        } catch (NullPointerException npe) {
            throw new ConfigurationException("Lucene Configuration "
                    + _SEARCH_LOGIC_ELEMENT_ + " element not found", npe);
        }
        populateLocateEngine(instance, locateConfig, luceneInstance);
        locateInstances.add(luceneInstance);
    }

    /**
     * Populates the locate engine.
     * @param instance
     * @param locateConfig
     * @param locateInstance
     * @return
     */
    private LocateEngine populateLocateEngine(Element instance,
                                              LocateConfig locateConfig, LocateInstance locateInstance) throws ServerException{
        String name = getElementText(instance, _NAME_);
        locateInstance.setName(name);
        String engineName = getElementText(instance, _ENGINE_NAME_);

        LocateEngine locateEngine = locateConfig.getLocateEngine(engineName);
        locateInstance.setEngine(locateEngine);

        IndexSearchFactory factory = m_factories.get(locateEngine.getClassName());
        if (factory == null) {
            throw new ConfigurationException("Locate Configuration - invalid engine class: "
                    + locateEngine.getClassName());
        }
        locateInstance.setIndexFactory(factory);
        return locateEngine;
    }

}
