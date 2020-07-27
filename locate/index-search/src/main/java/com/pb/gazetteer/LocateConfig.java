package com.pb.gazetteer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.rmi.ServerException;
import java.util.List;

/**
 * A structured representation of the main application configuration file.
 */
public class LocateConfig
{
    private static final Logger log = LogManager.getLogger(LocateConfig.class);
    private static final String SERVER_MESSAGE = "Locate engine 'Lucene' not found in configuration";
    private LocateInstance defaultInstance;

    private List<LocateInstance> locateInstances;

    private List<LocateEngine> locateEngines;

    public void setLocateInstances(List<LocateInstance> locateInstances)
    {
        this.locateInstances = locateInstances;
    }

    public List<LocateInstance> getLocateInstances()
    {
        return locateInstances;
    }

    public void setLocateEngines(List<LocateEngine> locateEngines)
    {
        this.locateEngines = locateEngines;
    }

    public void setDefaultInstance(LocateInstance defaultInstance)
    {
        this.defaultInstance = defaultInstance;
    }

    public LocateInstance getDefaultInstance()
    {
        return defaultInstance;
    }

    public LocateInstance getLocateInstance(String gazetteerName)
    {
        LocateInstance instance = new LocateInstance();
        instance.setName(gazetteerName);
        if (locateInstances.contains(instance))
        {
            return locateInstances.get(locateInstances.indexOf(instance));
        }
        return null;
    }

    public LocateEngine getLocateEngine(String engineName) throws ServerException {
        LocateEngine engine = new LocateEngine();
        engine.setName(engineName);
         try {

             return locateEngines.get(locateEngines.indexOf(engine));
         }
         catch (Exception exception) {
             log.error(SERVER_MESSAGE, exception);
             throw new ServerException(SERVER_MESSAGE);
         }

    }

}
