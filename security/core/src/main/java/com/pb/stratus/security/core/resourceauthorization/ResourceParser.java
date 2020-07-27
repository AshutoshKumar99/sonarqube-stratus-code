package com.pb.stratus.security.core.resourceauthorization;

import org.springframework.security.core.GrantedAuthority;

import java.io.InputStream;
import java.util.List;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 12:42 PM
 * Interface to parse the resource files.
 */
public interface ResourceParser {

    /**
     * Parse the given resource authorization file and return a list of GrantedAuthority
     * for the given resource.
     * @param is
     * @return
     * @throws ResourceException
     */
    public List<GrantedAuthority> parse(InputStream is) throws ResourceException;

    /**
     * This is a convenient method keeping the Spring implementation in mind
     * where the prefix will be appended to the role in each authorization.
     * @return
     */
    public String getPrefix();
}
