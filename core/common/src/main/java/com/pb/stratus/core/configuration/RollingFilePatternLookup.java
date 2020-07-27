package com.pb.stratus.core.configuration;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 7/25/14
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
@Plugin(name = "filepattern", category = "Lookup")
public class RollingFilePatternLookup implements StrLookup {

    /**
     * Lookup the value for the key.
     * @param key  the key to be looked up, may be null
     * @return The value for the key.
     */
    public String lookup(String key) {
        try {
            return System.getProperty(key).replace("\\", "/");
        } catch (final Exception ex) {
            // Should this be logged?
            return null;
        }
    }

    /**
     * Lookup the value for the key using the data in the LogEvent.
     * @param event The current LogEvent.
     * @param key  the key to be looked up, may be null
     * @return The value associated with the key.
     */
    public String lookup(LogEvent event, String key) {
        try {
            return System.getProperty(key).replace("\\", "/");
        } catch (final Exception ex) {
            // Should this be logged?
            return null;
        }
    }
}
