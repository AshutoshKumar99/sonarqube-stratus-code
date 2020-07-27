package com.pb.stratus.controller.util;

/**
 * An concrete impl for MIDev Named Map repository that
 * is specific to a single tenant.
 */
public class MiDevNamedMapRepository extends BasicMiDevRepository {

    public MiDevNamedMapRepository(String tenantName) {
        super(tenantName);
    }

    public String getInternalResourceName(String externalResourceName) {
        StringBuilder b = new StringBuilder("/");
        if (tenantName != null) {
            b.append(tenantName);
            b.append("/");
        }
        if(namedMaps != null)
        {
            b.append(namedMaps);
            b.append("/");
        }
        b.append(removeSlashes(externalResourceName));
        return b.toString();
    }

}
