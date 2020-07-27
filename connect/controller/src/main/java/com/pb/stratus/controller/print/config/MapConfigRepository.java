package com.pb.stratus.controller.print.config;

/**
 * Manages access to map configurations
 */
public interface MapConfigRepository {
    MapConfig getMapConfig(String mapConfigName);
}
