package com.pb.stratus.controller.util;

/**
 * User: CH010TH
 * Date: Dec 22, 2011
 * Time: 4:52:00 PM
 */
public class TileMappingMiDevRepository implements MiDevRepository {
    private String namedTiles = "NamedTiles";
    private String namedMaps = "NamedMaps";

    private String tenantName;

    public TileMappingMiDevRepository(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getInternalResourceName(String externalResourceName) {
        StringBuilder b = new StringBuilder("/");
        if (tenantName != null) {
            b.append(tenantName);
            b.append("/");
        }
        if (namedTiles != null) {
            b.append(namedTiles);
            b.append("/");
        }
        if (namedMaps != null) {
            b.append(namedMaps);
            b.append("/");
        }

        b.append(removeSlashes(externalResourceName));
        return b.toString();
    }

    private String removeSlashes(String value) {
        return value.replaceAll("/", "");
    }
}
