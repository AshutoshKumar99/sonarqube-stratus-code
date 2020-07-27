package com.pb.stratus.security.core.resourceauthorization;

import com.pb.stratus.core.common.Preconditions;
import com.pb.stratus.core.util.ObjectUtils;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 11:49 AM
 * This class encapsulates authorization properties of a resource.
 */
public class ResourceAuthorizationConfig {

    // name of the resource
    private String name;

    private List<? extends  GrantedAuthority> grantedAuthorities;


    public ResourceAuthorizationConfig(String name,
                                       List<? extends  GrantedAuthority> grantedAuthorities) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        Preconditions.checkNotNull(grantedAuthorities, "grantedAuthorities cannot be null");
        this.name = name;
        this.grantedAuthorities = grantedAuthorities;
    }

    public String getName() {
        return name;
    }

    public List<? extends  GrantedAuthority> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (!(o instanceof ResourceAuthorizationConfig))
            return false;
        ResourceAuthorizationConfig that = (ResourceAuthorizationConfig) o;
        return this.name.equals(that.name) &&
                this.grantedAuthorities.equals(that.grantedAuthorities);
    }

    public int hashCode() {
        int hc = ObjectUtils.SEED;
        hc = ObjectUtils.hash(hc, name);
        hc = ObjectUtils.hash(hc, grantedAuthorities);
        return hc;
    }
}
