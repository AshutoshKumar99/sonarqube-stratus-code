package com.pb.stratus.controller.service;

// import com.g1.dcg.managers.access.Ace;
import com.g1.dcg.managers.access.AclManagerService;
import com.pb.stratus.security.core.jaxb.NamedResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Created by ra007gi on 12/31/2015.
 */
public class AclManagerServiceImpl implements AclManagerServiceInterface {

    private static final Logger log = LogManager.getLogger(FeatureServiceImpl.class);

    private AclManagerService aclManagerService;

    public static String ACCESS_CONTROL_NAMED_RESOURCE_TYPE = "Location Intelligence.Named Resources";

    public static String ACCESS_CONTROL_DATASET_DML_TYPE = "Location Intelligence.Dataset.DML";

    private static final Logger logger = LogManager.getLogger(AclManagerServiceImpl.class);


    public AclManagerServiceImpl(AclManagerService aclManagerService) {

        this.aclManagerService = aclManagerService;
    }
/*
    public Map<NamedResource, Collection<Ace>> getNamedResourcesHavingPermissionsForRoles(
            List<String> roles, List<NamedResource> resources) {

        Map<NamedResource, Collection<Ace>> namedResourcePermissionMap = new HashMap<>();

        for (NamedResource namedResource : resources) {
            Set<Ace> permissions = new HashSet();
            namedResourcePermissionMap.put(namedResource, permissions);
        }

        for (String role : roles) {
            try {
                List<Ace> aces = aclManagerService.getEntityOverrides(role, false);
                *//*for (Ace ace : aces) {
                    for (NamedResource namedResource : resources) {
                        if (ace.getId().equals(namedResource.getResourcePath()) && ace.getTypeName().equals(ACCESS_CONTROL_NAMED_RESOURCE_TYPE)) {
                            Collection<Ace> permissions = namedResourcePermissionMap.get(namedResource);
                            permissions.add(ace);
                            break;
                        }
                    }
                }*//*
                for (NamedResource namedResource : resources) {
                    //this is to get viewPermission on namedTable first, before we ask for edit permission
                    Ace viewDetails = getAccessControlBasedOnAceType(aces, namedResource, ACCESS_CONTROL_NAMED_RESOURCE_TYPE);
                    if(null != viewDetails) {
                        int grantMask = viewDetails.getGrantMask();
                        String binary = Integer.toBinaryString(grantMask);
                        binary = new StringBuilder(binary).reverse().toString();
                        for (int i = 0; i < binary.length(); i++) {
                            int value = Integer.valueOf(Character.toString(binary.charAt(i)));
                            if (value == 1 && i == 0) { // if namedResource is viewable, then getPermissions
                                //this is to get edit permission now based on DML type
                                Ace permissionDetails = getAccessControlBasedOnAceType(aces, namedResource, ACCESS_CONTROL_DATASET_DML_TYPE);
                                Collection<Ace> permissions = namedResourcePermissionMap.get(namedResource);
                                permissions.add(permissionDetails);
                            }
                        }
                    }

                }
                //Ace permissionDetails = getAccessControlBasedOnAceType(aces, resources, ACCESS_CONTROL_DATASET_DML_TYPE);
                //Collection<Ace> permissions = namedResourcePermissionMap.get(namedResource);
                //permissions.add(ace);
            } catch (javax.xml.ws.soap.SOAPFaultException exception) {
                // if the role does not have permissions on roles and secured entities overrides, soap fault is thrown with message "access is denied"
                String msg = exception.getFault().getFaultString();
                if (msg.equals("Access is denied")) {
                    logger.debug("Role " + role + " does not have permissions for getEntitiesOverrides request resulting in SoapFault");
                }
            }
        }
        return namedResourcePermissionMap;
    }

    private Ace getAccessControlBasedOnAceType(List<Ace> aces, NamedResource namedResource, String aceType) {
        for (Ace ace : aces) {
            if (ace.getId().equals(namedResource.getResourcePath()) && ace.getTypeName().equals(aceType))
                return ace;
        }
        //return null;
        return null;
    }*/
}
