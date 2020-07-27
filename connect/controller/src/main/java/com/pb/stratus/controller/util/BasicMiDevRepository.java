package com.pb.stratus.controller.util;

/**
 * An abstract representation of the partition of the MIDev repository that
 * is specific to a single tenant.
 */
public abstract class BasicMiDevRepository implements MiDevRepository {

    protected String tenantName;
    protected String namedMaps = "NamedMaps";
    protected String namedTables = "NamedTables";
    protected String namedLayers = "NamedLayers";

    public BasicMiDevRepository(String tenantName) {
        this.tenantName = tenantName;
    }

    public abstract String getInternalResourceName(String externalResourceName);

    protected String removeSlashes(String value) {
        return value.replaceAll("/", "");
    }
}
