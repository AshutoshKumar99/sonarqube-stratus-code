package com.pb.stratus.core.configuration;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileListModificationObserverFactory {

    public static enum ObserverType {
        TENANT_LOCAL
    }
    
    private Map<ObserverType, FileModificationObserver> observerMap;

    public FileListModificationObserverFactory() {
        observerMap = new ConcurrentHashMap<ObserverType, FileModificationObserver>();
    }

    public void addObserver(ObserverType observerType,
            FileModificationObserver fileModificationObserver) {
        observerMap.put(observerType, fileModificationObserver);
    }

    public FileModificationObserver getFileModificationObserver(
            ObserverType observerType) {
        return observerMap.get(observerType);
    }

}
