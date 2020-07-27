package com.pb.stratus.controller.geometry;

import com.mapinfo.midev.service.geometry.v1.*;
import com.mapinfo.midev.service.geometry.ws.v1.GeometryServiceInterface;
import com.pb.stratus.controller.ExceptionConverter;
import com.pb.stratus.controller.FeatureServiceExceptionConverter;
import com.pb.stratus.controller.UnknownSrsException;
import uk.co.graphdata.utilities.contract.Contract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class deals with lookups between spatial reference systems (SRS) from 
 * different code spaces. A code space is an abstract term for an organisation 
 * or standard that attempts to bring different SRSs into a canonical form.
 * Examples for such organisations are EPSG and Oracle.
 */
public class SrsLookupImpl implements SrsLookup
{
    
    private GeometryServiceInterface geometryService;

    private boolean populated = false;

    // mapping contains SRS code and spatial reference system.
    private Map<String, String> srsMappings = new HashMap<String, String>();
    
    //FIXME don't use feature service exc conv for geometry service
    private ExceptionConverter conv 
            = new FeatureServiceExceptionConverter();
    
    public void setGeometryService(GeometryServiceInterface geometryService) 
    {
        this.geometryService = geometryService;
    }
    
    /**
     * Verifies if two SRS codes refer to the same SRS. The two strings can
     * potentially be from two different code spaces (e.g. EPSG and Oracle 
     * SRID) and therefore be different (with regard to string comparison).
     * However, both might represent the same spatial reference system.
     * Let's use the World Geodetic System from 1984 (WGS84) as an example. 
     * The EPSG (European Petroleum Survey Group) refers to this system with
     * the code <code>EPSG:4326</code> whereas Oracle Spatial uses the 
     * identifier <code>8307</code>. To allow this class to tell which code
     * space (e.g. Oracle, EPSG, ...) a code belongs to, the parameters must
     * be prefixed with a code space identifier followed by a colon (e.g. 
     * srid:8307 for the Oracle example above). The supported code spaces can
     * be retrieved by calling {@link SrsLookup#getCodeSpaces()}.
     * 
     * @param srs1 the first SRS (prefixed with the code space)
     * @param srs2 the second SRS (prefixed with the code space)
     * @return <code>true</code> if the two (potentially different) codes
     *         refer to the same SRS.
     */
    public boolean areSrsCodesEquivalent(String srs1, String srs2) {
        Contract.pre(srs1 != null, "SRS 1 required");
        Contract.pre(srs2 != null, "SRS 2 required");
        populateIfRequired();
        String globalId1 = getGlobalId(srs1);
        String globalId2 = getGlobalId(srs2);
        return globalId1.equals(globalId2);
    }
    
    private String getGlobalId(String srs)
    {
        String globalId = srsMappings.get(srs.toLowerCase());
        if (globalId == null)
        {
            throw new UnknownSrsException(srs);
        }
        return globalId;
    }
    
    private void populateIfRequired()
    {
        if (!populated)
        {
            populate();
        }
    }
    
    private void populate()
    {
        //single thread should populate mappings if not pre-populated
        synchronized (this.srsMappings){
            if(!populated){
                List<String> codeSpaces = getCodeSpaces();
                for (String codeSpace : codeSpaces)
                {
                    createMappings(codeSpace);
                }
                populated = true;
            }
        }
    }
    
    /**
     * Returns a list of supported code spaces.
     *  
     * @return a non-empty list of strings.
     */
    public List<String> getCodeSpaces()
    {
        ListCodeSpacesRequest request = new ListCodeSpacesRequest();
        ListCodeSpacesResponse response;
        try
        {
            response = geometryService.listCodeSpaces(request);
        }
        catch (Exception x)
        {
            throw conv.convert(x);
        }
        return response.getCodeSpace();
    }
    
    private void createMappings(String codeSpace)
    {
        ListCoordSysByCodeSpaceRequest request = new ListCoordSysByCodeSpaceRequest();
        request.setCodeSpace(codeSpace);
        ListCoordSysByCodeSpaceResponse response;
        try
        {
            response = geometryService.listCoordSysByCodeSpace(request);
        }
        catch (Exception x)
        {
            throw conv.convert(x);
        }
        for (CoordSys coordSys : response.getCoordSys())
        {
            srsMappings.put(coordSys.getSrsName().toLowerCase(), 
                    coordSys.getDisplayName());
        }
    }

}
