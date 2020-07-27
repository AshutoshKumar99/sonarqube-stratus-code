package com.pb.stratus.core.configuration;


import com.pb.stratus.core.common.Preconditions;
import uk.co.graphdata.utilities.resource.ResourceResolver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ConfigurationFileListModificationObserver implements
        FileModificationObserver{

    private ResourceResolver resourceResolver;
    private Set<String> configurationFiles;
    private ConfigurationFileTimeStamp configurationFileTimeStamp;

    public ConfigurationFileListModificationObserver(ResourceResolver resourceResolver,
             Set<String> configurationFiles) {
        Preconditions.checkNotNull(resourceResolver, "resourceResolver cannot be null");
        Preconditions.checkNotNull(configurationFiles, "configurationFiles cannot be null");
        this.resourceResolver = resourceResolver;
        this.configurationFiles = configurationFiles;
        this.configurationFileTimeStamp =
                new ConfigurationFileTimeStamp(resourceResolver, configurationFiles);
    }

    @Override
    public boolean isModified(String file) throws IllegalArgumentException {
        if(!configurationFiles.contains(file))
            throw  new IllegalArgumentException("File is not under observation");
        return this.configurationFileTimeStamp.isFileModified(file);
    }

    private final class ConfigurationFileTimeStamp {
        private Map<String, Long> bookKeeping;

        public ConfigurationFileTimeStamp(ResourceResolver resourceResolver,
                Set<String> configurationFiles) {
            this.bookKeeping = new HashMap<String, Long>();
            init(resourceResolver, configurationFiles);
        }
        
        private void init(ResourceResolver resourceResolver, 
                Set<String> configurationFiles) {
            for(Iterator<String> it = configurationFiles.iterator(); it.hasNext();)
            {
                String fileName = it.next();
                resourceResolver.getLastModified(fileName);
                bookKeeping.put(fileName,
                        resourceResolver.getLastModified(fileName));
            }
        }
        
        private long getLastModifiedTime(String fileName) {
            return this.bookKeeping.get(fileName);
        }
        
        public boolean isFileModified(String fileName) {
            long newTimeStamp = resourceResolver.getLastModified(fileName);
            long oldTimeStamp = getLastModifiedTime(fileName);
            if((newTimeStamp - oldTimeStamp) > 0) {
                bookKeeping.put(fileName, newTimeStamp);
				return true;
            }
            return false;
        }
    }
}
