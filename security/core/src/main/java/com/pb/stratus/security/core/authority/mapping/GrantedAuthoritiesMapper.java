/**
 * GrantedAuthoritiesMapper
 * User: GU003DU
 * Date: 12/26/13
 * Time: 10:20 PM
 */

package com.pb.stratus.security.core.authority.mapping;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface GrantedAuthoritiesMapper {
    Collection<GrantedAuthority> mapAuthorities(Authentication authentication);
}
