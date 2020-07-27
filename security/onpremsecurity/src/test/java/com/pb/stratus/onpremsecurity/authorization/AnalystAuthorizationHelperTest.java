package com.pb.stratus.onpremsecurity.authorization;

// import com.g1.dcg.managers.access.Ace;
import com.pb.stratus.security.core.jaxb.NamedResource;
import com.pb.stratus.security.core.jaxb.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: vi001ty
 * Date: 3/31/14
 * Time: 12:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnalystAuthorizationHelperTest {

    private AnalystAuthorizationHelper target;

    @Before
    public void setup(){
        target = new AnalystAuthorizationHelper();
    }

    @After
    public void teardown()
    {
        RequestContextHolder.resetRequestAttributes();
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
/*    @Test
    public void testUpdateEntityOverrides(){
        Map<Role,List<Ace>> overrides = new HashMap<>();
        overrides.put(createRole("R1"), createAceList(new String[]{"M1","T1","T2"}));
        overrides.put(createRole("R2"), createAceList(new String[]{"M1","M2","M3","T1","T2","T3"}));
        overrides.put(createRole("R3"), createAceList(new String[]{}));

        Set<NamedResource> resources = new HashSet<>();
        resources.add(AuthorizerImplTest.createNamedResource("M1", new String[]{"R1", "R3"}));
        resources.add(AuthorizerImplTest.createNamedResource("T1", new String[]{"R1", "R3"}));
        resources.add(AuthorizerImplTest.createNamedResource("T2", new String[]{"R1", "R2", "R3"}));

        overrides = target.updateEntityOverrides(overrides, resources);
        assertEquals(3, overrides.get(createRole("R1")).size());
        assertEquals(4, overrides.get(createRole("R2")).size());
        assertEquals(3, overrides.get(createRole("R3")).size());

        assertTrue(containsAll(overrides.get(createRole("R1")), new String[]{"M1", "T1", "T2"}));
        assertTrue(containsAll(overrides.get(createRole("R2")), new String[]{"M2", "M3", "T2", "T3"}));
        assertTrue(containsAll(overrides.get(createRole("R3")), new String[]{"M1","T1", "T2"}));


    }*/

/*
    @Test
    public void testUpdateEntityOverridesForNamedStyle(){
        Map<Role,List<Ace>> overrides = new HashMap<>();
        overrides.put(createRole("R1"), createAceList(new String[]{"M1","T1","T2", "/tenantName/NamedStyles/PrintStyle"}));
        overrides.put(createRole("R2"), createAceList(new String[]{"M2","M3","T2","T3"}));
        overrides.put(createRole("R3"), createAceList(new String[]{}));
        setTenantInRequest("tenantName");
        target.updateEntityOverridesForNamedStyle(overrides);

        assertTrue(containsAll(overrides.get(createRole("R1")), new String[]{"M1", "T1", "T2","/tenantName/NamedStyles/PrintStyle"}));
        assertTrue(containsAll(overrides.get(createRole("R2")), new String[]{"M2", "M3", "T2", "T3","/tenantName/NamedStyles/PrintStyle"}));
        assertTrue(containsAll(overrides.get(createRole("R3")), new String[]{"/tenantName/NamedStyles/PrintStyle"}));

    }
*/

    private void setTenantInRequest(String tenantName) {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setAttribute("tenant", tenantName);
        ServletRequestAttributes mockAttrib = new ServletRequestAttributes(mockRequest);
        RequestContextHolder.setRequestAttributes(mockAttrib);
    }

/*
    private boolean containsAll(Collection<Ace> r2, String[] resources) {
        boolean found = false;
        for(String str: resources){
            found = false;
            for(Ace ace: r2){
                if(str.equals(ace.getId())){
                    found = true;
                    break;
                }

            }
            if(!found) return false;
        }
        if(r2.size() == resources.length)
            return true;
        else
            return false;
    }
*/

    public static Role createRole(String roleName){
        Role role = new Role();
        role.setName(roleName);
        role.setProduct("analyst");
        role.setTenant("tenant");
        return role;
    }

/*    public static List<Ace> createAceList(String[] resources){
        List<Ace> aces = new ArrayList<>();
        for(String resource: resources){
            Ace ace = new Ace();
            ace.setId(resource);
            ace.setTypeName("LIType");
            ace.setDenyMask(1);
            ace.setGrantMask(10);
            aces.add(ace);
        }
        return aces;
    }*/
}
