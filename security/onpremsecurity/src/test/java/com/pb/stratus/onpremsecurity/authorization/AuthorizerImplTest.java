package com.pb.stratus.onpremsecurity.authorization;

import com.g1.dcg.managers.access.AclManagerService;
// import com.g1.dcg.managers.access.RoleDescriptor;
import com.pb.stratus.security.core.authorization.AuthorizationException;
import com.pb.stratus.security.core.authorization.AuthorizerVO;
import com.pb.stratus.security.core.jaxb.NamedResource;
import com.pb.stratus.security.core.jaxb.NamedResourceType;
import com.pb.stratus.security.core.jaxb.Role;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.xml.ws.WebServiceException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: HA008SA
 * Date: 3/10/14
 * Time: 6:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizerImplTest {

    private AclManagerService mockAclManagerService;
    private AuthorizerImpl authorizer;
    private AnalystAuthorizationHelper mockHelper;

    @Before
    public void setUp() throws Exception {
        mockAclManagerService = mock(AclManagerService.class);
        mockHelper = mock(AnalystAuthorizationHelper.class);
        authorizer = new AuthorizerImpl(mockAclManagerService);
        authorizer.setAuthorizationHelper(mockHelper);
    }

/*    public void GetAllRolesForSystemRole() throws Exception {
        List<RoleDescriptor> mockRoleDescriptors = createMockRolesResponse(true);
        when(mockAclManagerService.listRoles()).thenReturn(mockRoleDescriptors);
        AuthorizerVO vo = new AuthorizerVO();
//        Set<Role> actualRolesList = authorizer.getAllRoles(vo);
//
//        assertTrue(actualRolesList.size() == 1);
//
//        for (Role role : actualRolesList) {
//            assertEquals(role.getName(), mockRoleDescriptors.get(0).getName());
//        }
    }*/

/*    public void GetAllRolesForCustomRole() throws Exception {
        List<RoleDescriptor> mockRoleDescriptors = createMockRolesResponse(false);
        when(mockAclManagerService.listRoles()).thenReturn(mockRoleDescriptors);
        AuthorizerVO vo = new AuthorizerVO();
//        Set<Role> actualRolesList = authorizer.getAllRoles(vo);
//
//        assertTrue(actualRolesList.size() == 1);
//        for (Role role : actualRolesList) {
//            assertEquals(role.getName(), mockRoleDescriptors.get(0).getName());
//        }

    }*/


    public void testSetAllRolesForNamedResourcesException() throws AuthorizationException {
//        doThrow(new WebServiceException()).when(mockAclManagerService).getEntityOverrides(anyString(), anyBoolean());

        List<NamedResource> resources = new ArrayList<>();
        resources.add(createNamedResource("M1", new String[]{"R1", "R3"}));
        resources.add(createNamedResource("T1", new String[]{"R1", "R3"}));
        resources.add(createNamedResource("T2", new String[]{"R1", "R2", "R3"}));
        Collection<Role> deletedRole = new HashSet<>();
//        deletedRole.add(AnalystAuthorizationHelperTest.createRole("R3"));
//        authorizer.setAllRolesForNamedResources(resources, deletedRole);
    }

    /*
    *  M(NamedMap), T(NamedTable), MC(MapConfig), R(Role)
    *  Given :
    *  M1->T1,T2     M2->T2     M3->T3
    *  MC1 (M1) R1,R2
    *  MC2 (M2,M3) R2
    *
    *  R1->M1,T1,T2
    *  R2->M1,M2,M3,T1,T2,T3
    *
    *  When MC1 role list(R1,R2) changed to R1,R3
    *  Then
    *  R1->M1,T1,T2
    *  R2->M2,M3,T2,T3
    *  R3->M1,T1,T2
    *
    * */


/*    public void testSetAllRolesForNamedResources() throws AuthorizationException {
        ArgumentCaptor<String> roles = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<List> aceLists = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<Boolean> bools = ArgumentCaptor.forClass(Boolean.class);

*//*        when(mockAclManagerService.getEntityOverrides("R1", false))
                .thenReturn(AnalystAuthorizationHelperTest.createAceList(new String[]{"M1", "T1", "T2"}));
        when(mockAclManagerService.getEntityOverrides("R2", false))
                .thenReturn(AnalystAuthorizationHelperTest.createAceList(new String[]{"M2", "M3", "T2", "T3"}));
        when(mockAclManagerService.getEntityOverrides("R3", false))
                .thenReturn(AnalystAuthorizationHelperTest.createAceList(new String[]{"M1", "T1", "T2"}));*//*

        //when(mockHelper.updateEntityOverrides(anyMap(),
        //        anyCollectionOf(NamedResource.class))).thenReturn(null);
        //when(mockHelper.updateEntityOverridesForNamedStyle(
        //        anyMap())).thenReturn(null);

        List<NamedResource> resources = new ArrayList<>();
        resources.add(createNamedResource("M1", new String[]{"R1", "R3"}));
        resources.add(createNamedResource("T1", new String[]{"R1", "R3"}));
        resources.add(createNamedResource("T2", new String[]{"R1", "R2", "R3"}));
        Collection<Role> deletedRole = new HashSet<>();
        deletedRole.add(AnalystAuthorizationHelperTest.createRole("R3"));

//        authorizer.setAllRolesForNamedResources(resources, deletedRole);
//        verify(mockHelper).updateEntityOverrides(anyMap(),anyList());
//        verify(mockHelper).updateEntityOverridesForNamedStyle(anyMap());
//        verify(mockAclManagerService, times(3)).getEntityOverrides(anyString(), anyBoolean());
//        verify(mockAclManagerService, times(3)).setEntityOverrides(roles.capture(), bools.capture(), aceLists.capture());
//
//        assertTrue(roles.getAllValues().contains("R1"));
//        assertTrue(roles.getAllValues().contains("R2"));
//        assertTrue(roles.getAllValues().contains("R3"));
//
//        assertEquals(4, aceLists.getAllValues().get(0).size());
//        assertEquals(3, aceLists.getAllValues().get(1).size());

    }*/

/*    private List<RoleDescriptor> createMockRolesResponse(boolean checkSystemRole) {
        List<RoleDescriptor> roleDescriptors = new ArrayList<RoleDescriptor>();

        RoleDescriptor roleDescriptor = new RoleDescriptor();

        if (checkSystemRole) {
            roleDescriptor.setName("AnalystAdministrators");
            roleDescriptor.setReadOnly(true);

        } else {
            roleDescriptor.setName("AnalystStratus Admin");
            roleDescriptor.setReadOnly(false);
        }

        roleDescriptors.add(roleDescriptor);
        return roleDescriptors;
    }*/

    public static NamedResource createNamedResource(String path, String[] roleList){
        NamedResource resource = new NamedResource();
        resource.setResourcePath(path);
        if(path.lastIndexOf("/") != -1){
            resource.setResourceName(path.substring(path.lastIndexOf("/"), path.length()));
        }else{
            resource.setResourceName(path);
        }
        resource.setResourceType(NamedResourceType.NAMED_MAP);
        for(String roleName :roleList){
            Role role = new Role();
            role.setName(roleName);
            role.setProduct("analyst");
            role.setTenant("tenant");
            resource.getRoles().add(role);
        }
        return resource;
    }
}
