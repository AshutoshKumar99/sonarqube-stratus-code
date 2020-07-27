package com.pb.stratus.controller.util;

/**
 * An abstract representation of the partition of the MIDev repository that
 * is specific to a single tenant.
 */
public class MiDevNamedTableRepository extends BasicMiDevRepository {

    public MiDevNamedTableRepository(String tenantName) {
        super(tenantName);
    }

    public String getInternalResourceName(String externalResourceName) {
        StringBuilder b = new StringBuilder("/");
        if (tenantName != null) {
            b.append(tenantName);
            b.append("/");
        }
        if(namedTables != null)
        {
            b.append(namedTables);
            b.append("/");
        }
        b.append(removeSlashes(externalResourceName));
        return b.toString();
    }


}
