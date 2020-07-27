package com.pb.stratus.onpremsecurity.authorization;

// import com.g1.dcg.managers.access.Ace;
import com.pb.stratus.security.core.jaxb.NamedResource;
import com.pb.stratus.security.core.jaxb.Role;
import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This class is to encapsulate any businessLogic for Analyst authorizer.
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/31/14
 * Time: 12:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnalystAuthorizationHelper {

    private static final Logger logger = Logger.getLogger(AnalystAuthorizationHelper.class);
    private final String PRINT_STYLE_PATH = "NamedStyles/PrintStyle";

    /*
    * Updates entity overrides for given(affected) roles.
    * Each namedResource will be added or removed from Affected Role's EntityOverrides list.
    * if that roleList in namedResource contains that Role then entity will be added
    * else entity will be deleted from that role.
    *
    * @param entityOverrides  Map of affected roles as keys and their existing entityOverrides list as value.
    * @param resources NamedResources with their RoleList.
    * @return entityOverrides  updated map.
    */
/*    public Map<Role, List<Ace>> updateEntityOverrides(Map<Role, List<Ace>> entityOverrides, Collection<NamedResource> resources){

        for(NamedResource namedResource :resources){
            Ace namedResourceAsAce = new Ace();
            namedResourceAsAce.setDenyMask(AuthorizerImpl.DENY_MASK);
            namedResourceAsAce.setGrantMask(AuthorizerImpl.GRANT_MASK);
            namedResourceAsAce.setId(namedResource.getResourcePath());
            namedResourceAsAce.setTypeName(AuthorizerImpl.ACE_TYPE_NAME);

            for(Map.Entry<Role, List<Ace>> entry : entityOverrides.entrySet()){
                Role role = entry.getKey();
                List<Ace> acesToUpdate = entry.getValue();
                if(namedResource.getRoles().contains(role))
                {
                    if(!containsAce(namedResourceAsAce, acesToUpdate))
                        acesToUpdate.add(namedResourceAsAce);
                }else
                {
                    if(containsAce(namedResourceAsAce, acesToUpdate))
                        deleteAce(namedResourceAsAce, acesToUpdate);
                }
            }
        }
        logger.debug("Updated Entity Overrides for affected roles");
        return entityOverrides;
    }*/

    /*
    * Adds NamedStyle/PrintStyle as entity override for all affected roles.
    *
    * @param entityOverrides  Map of affected roles as keys and their entityOverrides list as value.
    * @return entityOverrides  updated entityOverride map.
    */
/*    public Map<Role, List<Ace>> updateEntityOverridesForNamedStyle(Map<Role, List<Ace>> entityOverrides) {
        Ace printStyleAce = createNamedStyleAccessControlEntity();
        for(Map.Entry<Role, List<Ace>> entry : entityOverrides.entrySet())
        {
            List<Ace> aces = entry.getValue();
            if(!containsAce(printStyleAce, aces)){
                aces.add(printStyleAce);
            }
        }
        logger.debug("Updated entity overrides for NamedStyles/PrintStyle");
        return entityOverrides;
    }*/

/*    private Ace createNamedStyleAccessControlEntity() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String tenantName = (String)request.getAttribute("tenant");
        if(tenantName == null)
        {
            return null;
        }
        Ace printStyleAce = new Ace();
        printStyleAce.setId("/" + tenantName + "/" + PRINT_STYLE_PATH);
        printStyleAce.setTypeName(AuthorizerImpl.ACE_TYPE_NAME);
        printStyleAce.setDenyMask(AuthorizerImpl.DENY_MASK);
        printStyleAce.setGrantMask(AuthorizerImpl.GRANT_MASK);
        return printStyleAce;
    }*/

/*    private boolean containsAce(Ace aceToFind, List<Ace> aces){
        for(Ace ace: aces){
            if(ace.getId().equals(aceToFind.getId()))
            {
                return true;
            }
        }
        return false;
    }*/

/*    private boolean deleteAce(Ace aceToDelete, List<Ace> aces){
        for(Ace ace: aces){
            if(ace.getId().equals(aceToDelete.getId())){
                aces.remove(ace);
                return true;
            }
        }
        return false;
    }*/
}
