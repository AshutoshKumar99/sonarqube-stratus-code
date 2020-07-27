package com.pb.stratus.core.configuration;


public interface FileModificationObserver {
    public boolean isModified(String file) throws IllegalArgumentException;
}
