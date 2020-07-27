package com.pb.stratus.security.core.resourceauthorization;

import com.pb.stratus.core.common.Preconditions;
import org.apache.commons.io.FilenameUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.*;

/**
 * User: sh003bh
 * Date: 11/11/11
 * Time: 11:58 AM
 */
public abstract class AbstractReadResourceAuthorization implements ResourceAuthorization {

    protected ResourceAuthorizationReader resourceAuthorizationReader;
    protected ResourceParser resourceParser;
    private static final String DEFAULT_EXTENSION = ".auth";
    private static final String ROLE_PREFIX = "ROLE_";

    public void setAdministratorRole(String administratorRole) {
        this.administratorRole = administratorRole;
    }

    private String administratorRole;



    public AbstractReadResourceAuthorization(ResourceAuthorizationReader resourceAuthorizationReader,
                                             ResourceParser resourceParser)
            throws FileNotFoundException, ResourceException {
        Preconditions.checkNotNull(resourceAuthorizationReader, "resourceAuthorizationReader cannot be null");
        Preconditions.checkNotNull(resourceParser, "resourceParser cannot be null");
        this.resourceAuthorizationReader = resourceAuthorizationReader;
        this.resourceParser = resourceParser;
    }

    /**
     * This method should be overridden by the subclasses to suggest whether they
     * support nested dir structure. for example: fmn and map configs have
     * only a top level directory but query configs have nested dir structure
     * where directories corresponding to the table-names are created.
     * This method is to support the "getAuthorizationConfigs" method which takes
     * a sub directory as argument. if this method return true the second parameter
     * in the "getAuthorizationConfigs" will be ignored.
     *
     * @return
     */
    protected abstract boolean isIgnoreResourceDir();

    /**
     * return the path relative to the base path from where the authorization
     * should be read.
     *
     * @return
     */
    protected abstract String getPath();

    /**
     * Read the granted authorities that have been given in the auth file.
     *
     * @param file
     * @return
     * @throws FileNotFoundException
     * @throws ResourceException
     */
    protected List<GrantedAuthority> getGrantedAuthoritiesList(File file)
            throws FileNotFoundException, ResourceException {
        InputStream is = this.resourceAuthorizationReader.getConfigFile(file);
        return this.resourceParser.parse(is);
    }


    /**
     * Return List of GrantedAuthority for the given resource. if the resource
     * is not found then a ResourceException exception is thrown.
     *
     * @param resourceName
     * @return List<GrantedAuthority>
     * @throws ResourceException
     */
    public List<GrantedAuthority> getAuthorities(String resourceName)
            throws ResourceException {

        if (resourceName != null && !resourceName.startsWith("/")) {
            resourceName = "/" + resourceName;
        }
        File resourceFile =
                this.resourceAuthorizationReader.getFile(getPath() +
                        resourceName + getResourceExtension());
        try {
            return getGrantedAuthoritiesList(resourceFile);
        } catch (FileNotFoundException e) {
            throw new ResourceException(e);
        }
    }

    /**
     * /**
     * The file name extension of the file being used to maintain the authorization
     * related for the given resource like ".auth". This is the default implementation
     *
     * @return
     */
    protected String getResourceExtension() {
        return DEFAULT_EXTENSION;
    }


    public Set<ResourceAuthorizationConfig> getAuthorizationConfigs(
            Collection<GrantedAuthority> grantedAuthorities, String resourceDirPath)
            throws ResourceException {
        if (!isIgnoreResourceDir() && resourceDirPath == null) {
            throw new IllegalArgumentException("resourceDir cannot be null");
        }
        File resourceDir = getResourceDir(resourceDirPath);
        if (!resourceDir.exists() || !resourceDir.isDirectory()) {
            throw new ResourceException(resourceDirPath + "is not a directory");
        }
        List<String> configNames = getListOfAllConfigNames(resourceDir);

        //CONN-16910
        Set<ResourceAuthorizationConfig> set = new HashSet<ResourceAuthorizationConfig>(); //new ConcurrentSkipListSet <ResourceAuthorizationConfig>();

        for (String configName : configNames) {
            File authFile =
                    new File(resourceDir, configName + getResourceExtension());
            try {
                addResourceAuthorizationConfig(set, authFile, configName, grantedAuthorities);
            } catch (FileNotFoundException e) {
                throw new ResourceException(e);
            }
        }
        return set;
    }

    /**
     * Will add ResourceAuthorizationConfig in the given set. if the auth file for
     * the configname does not exist then it will be given admin role by default.
     * ResourceAuthorizationConfig will be added to the set only if
     * "grantedAuthorities" has at least one role mentioned in the auth file.
     *
     * @param set
     * @param authFile
     * @param configName
     * @param grantedAuthorities
     * @throws FileNotFoundException
     * @throws ResourceException
     */
    protected void addResourceAuthorizationConfig(
            Set<ResourceAuthorizationConfig> set, File authFile,
            String configName, Collection<GrantedAuthority> grantedAuthorities)
            throws FileNotFoundException, ResourceException {
        Preconditions.checkNotNull(set, "set cannot be null");
        Preconditions.checkNotNull(authFile, "authFile cannot be null");
        Preconditions.checkNotNull(configName, "configName cannot be null");
        Preconditions.checkNotNull(grantedAuthorities, "grantedAuthorities cannot be null");
        if (!authFile.exists() && isRoleAuthorized(grantedAuthorities, adminRoleOnly())) {
            set.add(getAuthorizationConfigWithAdminRole(configName));
        } else if (authFile.exists() && isRoleAuthorized(grantedAuthorities, getGrantedAuthoritiesList(authFile))) {
            set.add(new ResourceAuthorizationConfig(configName,
                    getGrantedAuthoritiesList(authFile)));
        }
    }

    /**
     * This method will return true if current user is member of at  least one  of the roles  read from the auth file.
     * An auth file can contain one of the roles Administrators, Users, Public and  one or more of the custom roles.
     *
     * @param grantedRoles  roles current  user is member of.
     * @param requiredRoles roles  assigned to a config, read from the auth file.
     * @return
     */
    protected boolean isRoleAuthorized(Collection<? extends GrantedAuthority> grantedRoles,
                                       Collection<? extends GrantedAuthority> requiredRoles) {
        Preconditions.checkNotNull(grantedRoles, "requestedRoles cannot be null");
        Preconditions.checkNotNull(requiredRoles, "actualRoles cannot be null");
        for (GrantedAuthority grantedRole : grantedRoles) {
            if (requiredRoles.contains(grantedRole)) {
                return true;
            }
        }
        return false;
    }

    protected ResourceAuthorizationConfig getAuthorizationConfigWithAdminRole(
            String configName) {
        Preconditions.checkNotNull(configName, "configName cannot be null");
        return new ResourceAuthorizationConfig(configName, adminRoleOnly());
    }

    private List<? extends GrantedAuthority> adminRoleOnly() {

        List<? extends  GrantedAuthority> adminAuthority = administratorRole != null?
                Collections.singletonList(new GrantedAuthorityImpl(administratorRole)):
                Collections.singletonList(new GrantedAuthorityImpl(ROLE_PREFIX + AuthorizationRoles.ADMINISTRATORS.getRoleName()));
        return adminAuthority;
    }


    protected File getResourceDir(String resourceDir) {
        String path = isIgnoreResourceDir() || resourceDir == null ?
                getPath() : getPath() + "/" + resourceDir;
        return this.resourceAuthorizationReader.getFile(path);
    }

    /**
     * Will return the list of all config names stripping the file extension.
     *
     * @param dir
     * @return
     */
    protected List<String> getListOfAllConfigNames(File dir) {
        Preconditions.checkNotNull(dir, "dir cannot be null");
        List<String> configNames = Arrays.asList(dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        }));
        for (int i = 0; i < configNames.size(); i++) {
            configNames.set(i, FilenameUtils.removeExtension(configNames.get(i)));
        }
        return configNames;
    }
}