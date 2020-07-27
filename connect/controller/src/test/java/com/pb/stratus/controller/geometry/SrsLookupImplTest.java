package com.pb.stratus.controller.geometry;

import com.mapinfo.midev.service.geometry.v1.ListCodeSpacesRequest;
import com.mapinfo.midev.service.geometry.v1.ListCodeSpacesResponse;
import com.mapinfo.midev.service.geometry.v1.ListCoordSysByCodeSpaceRequest;
import com.mapinfo.midev.service.geometry.v1.ListCoordSysByCodeSpaceResponse;
import com.mapinfo.midev.service.geometry.ws.v1.GeometryServiceInterface;
import com.pb.stratus.controller.UnknownSrsException;
import com.pb.stratus.util.JaxbUtil;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class SrsLookupImplTest
{

    private SrsLookupImpl srsLookup;

    @Before
    public void setUp() throws Exception 
    {
        GeometryServiceInterface mockGeomService = createMock(GeometryServiceInterface.class);
        ListCodeSpacesResponse resp = new ListCodeSpacesResponse();
        List<String> codeSpaces = resp.getCodeSpace();
        codeSpaces.add("codeSpace1");
        codeSpaces.add("codeSpace2");

        expect(mockGeomService.listCodeSpaces(
                (ListCodeSpacesRequest) anyObject())).andStubReturn(resp);

        IAnswer<ListCoordSysByCodeSpaceResponse> answer 
                = new IAnswer<ListCoordSysByCodeSpaceResponse>() 
        {
            public ListCoordSysByCodeSpaceResponse answer() throws Throwable 
            {
                Object[] args = EasyMock.getCurrentArguments();
                ListCoordSysByCodeSpaceRequest arg = (ListCoordSysByCodeSpaceRequest) args[0];
                return JaxbUtil.createObject(arg.getCodeSpace() + ".xml",
                        ListCoordSysByCodeSpaceResponse.class,
                        SrsLookupImplTest.class);
            }
        };

        expect(
                mockGeomService
                        .listCoordSysByCodeSpace((ListCoordSysByCodeSpaceRequest) anyObject()))
                .andStubAnswer(answer);
        replay(mockGeomService);
        srsLookup = new SrsLookupImpl();
        srsLookup.setGeometryService(mockGeomService);
    }

    @Test
    public void testAreSrsCodesEquivalentDifferentStringsButEquivalent() 
    {
        String srs1 = "codeSpace1:coordSys1";
        String srs2 = "codeSpace2:coordSys1";
        String msg = String.format("Expected %s and %s to be equivalent, "
                + "but they aren't", srs1, srs2);
        assertTrue(msg, srsLookup.areSrsCodesEquivalent(srs1, srs2));
    }

    @Test
    public void testAreSrsCodesEquivalentNonEquivalentValues() 
    {
        String srs1 = "codeSpace1:coordSys1";
        String srs2 = "codeSpace2:coordSys2";
        String msg = String.format("Expected %s and %s not to be equivalent, "
                + "but they are", srs1, srs2);
        assertFalse(msg, srsLookup.areSrsCodesEquivalent(srs1, srs2));
    }

    @Test
    public void testGetCodeSpaces()
    {
        List<String> codeSpaces = srsLookup.getCodeSpaces();
        assertEquals(codeSpaces.get(0), "codeSpace1");
        assertEquals(codeSpaces.get(1), "codeSpace2");
    }
    
    @Test
    public void testAreSrsCodesEquivalentException()
    {
        String srs1 = "codeSpace1:coordSys5";
        String srs2 = "codeSpace2:coordSys1";
        try
        {
            srsLookup.areSrsCodesEquivalent(srs1, srs2);
            fail("Expected UnknownSrsException exception.");
        }
        catch(UnknownSrsException unknownSrs)
        {
            // expected
        }
    }
    
    @Test 
    public void testAreSrsCodesEquivalentCaseInsensitivity()
    {
        String srs1 = "cODESpace1:COordSYs1";
        String srs2 = "codesPace2:coORdSys1";
        assertTrue(srsLookup.areSrsCodesEquivalent(srs1, srs2));
    }
}
