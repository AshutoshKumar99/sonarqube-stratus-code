package com.pb.stratus.security.core.authorization;

import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ra007gi
 * Date: 3/19/14
 * Time: 7:01 PM
 * To change this template use File | Settings | File Templates.
 */
enum StratusRoleType {

    ADMIN ("ADMIN"),
    OTHERS ("USERS","CUSTOM","PUBLIC","SPATIAL-ADMIN");

    private final List<String> values;

    StratusRoleType(String ...values) {
        this.values = Arrays.asList(values);
    }

    public List<String> getValues() {
        return values;
    }

    public static StratusRoleType find(String role) {
        for (StratusRoleType stratusRoleType : StratusRoleType.values()) {
            if (stratusRoleType.getValues().contains(role.toUpperCase())) {
                return stratusRoleType;
            }
        }
        return StratusRoleType.OTHERS;
    }

}
