package com.pb.stratus.security.core.authority.mapping;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * TenantGrantedAuthoritiesMapper
 * User: GU003DU
 * Date: 12/26/13
 * Time: 10:42 PM
 */
public interface TenantGrantedAuthoritiesMapper {

    Collection<GrantedAuthority> mapAuthorities(Authentication authentication, String tenant);
}
