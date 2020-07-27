package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.*;
import com.pb.stratus.controller.json.OwnedConverterStrategy;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Base class for converting Midev geometries into GeoJSON Strings. All subclasses need to do is provide the GeoJSON
 * geometry type and the logic to render the coordinates into a string.
 *
 * @author Volker Leidl
 */
public abstract class GeoJsonStrategy extends OwnedConverterStrategy
{

    public void processValue(Object value, StringBuilder b)
    {
        b.append("{");
        b.append("\"type\": \"" + StringEscapeUtils.escapeJavaScript(getType(value)) + "\", ");
        processSrs(value, b);
        b.append("\"coordinates\": ");
        processCoordinates(value, b);
        b.append("}");
    }

    /**
     * Returns the GeoJSON type of the converted geometry
     *
     * @return one of <code>Point</code>, <code>MultiPoint</code>, <code>LineString</code>, <code>MultiLineString</code>
     *         , <code>Polygon</code> or <code>MultiPolygon</code>.
     */
    protected abstract String getType(Object value);

    /**
     * Processes the spatial reference system of the given object into a GeoJSON string. If the given geometry object
     * does not provide an SRS this method doesn't write anything at all to the given string builder.
     *
     * @param value the geometry that holds an SRS identifier
     * @param b the StringBuilder to write the GeoJSON string to
     */
    protected void processSrs(Object value, StringBuilder b)
    {
        // N.B. Because of an issue with SRS codes in Midev we leave
        // this out entirely for now. There will be a PBI to resolve
        // the issue in the near future.

         Geometry g = (Geometry) value;
         String srs = g.getSrsName();
         if (StringUtils.isBlank(srs))
         {
             return;
         }

         // Midev returns the SRS in mapinfo codespace, whereas we want EPSG codespace.
         String newSrs = CodespaceConverter.getInstance().getEpsgForMapinfoSrs(srs);
         //String newSrs = swapMapinfoSrsForEpsg(srs);

         //If our SRS is still in the mapinfo codespace, we'll omit the srs altogether for now.
         if (StringUtils.contains(newSrs, "mapinfo:coordsys"))
         {
             return;
         }

         b.append("\"crs\": {");
         b.append("\"type\": \"name\", ");
         b.append("\"properties\": {\"name\": ");
         b.append("\"");
         b.append(StringEscapeUtils.escapeJavaScript(newSrs));
         b.append("\"");
         b.append("}");
         b.append("}");
         b.append(", ");
    }


    //
    // Take the srs string, which will be inthe mapinfo codespace (e.g. "mapinfo:coordsys 1, 104") and, if it's one of a few common srs values
    // used by our UK customers, convert it to epsg code.
    //
    // For the purpose of this PBI CONN-951, we only want to get the most common different coordsys working for a demo.
    // There will hopefully be another PBI to implement this properly.
    //
    protected String swapMapinfoSrsForEpsg(String oldSrs)
    {
        if (StringUtils.isBlank(oldSrs))
        {
            return oldSrs;
        }

        String newSrs = oldSrs;

        if (oldSrs.equals("mapinfo:coordsys 8, 79, 7, -2.0, 49.0, 0.9996012717, 400000.0, -100000.0"))
        {
            newSrs = "epsg:27700";
        }
        else if (oldSrs.equals("mapinfo:coordsys 1, 104"))
        {
            newSrs = "epsg:4326";
        }

        return newSrs;
    }

    /**
     * Processes a given geometry object into a string representation. GeoJSON expects a multi-dimensional array of
     * coordinates for all its geometry types, however it's still up to the actual subclass implementation to create the
     * correct output.
     *
     * @param value a geometry that needs to be converted into a JSON string
     * @param b a StringBuilder to write the string representation to
     */
    protected abstract void processCoordinates(Object value, StringBuilder b);

    /**
     * Serialised the given polygon into a two dimensional JSON array of double values.
     *
     * @param polygon the polygon to be converted
     */
    protected void processPolygonCoordinates(Polygon polygon, StringBuilder b)
    {
        b.append("[");
        processRingCoordinates(polygon.getExterior(), b);
        InteriorList inner = polygon.getInteriorList();
        if (inner != null && inner.getRing().size() > 0)
        {
            b.append(", ");
            int i = 0;
            for (Ring r : inner.getRing())
            {
                if (i++ > 0)
                {
                    b.append(", ");
                }
                processRingCoordinates(r, b);
            }
        }
        b.append("]");
    }

    /**
     * Serialised the given envelope into a two dimensional JSON array of double values.
     *
     * @param envelope the envelope to be converted
     */
    protected void processEnvelopeCoordinates(Envelope envelope, StringBuilder b)
    {        
        if(envelope != null)
        {
           processLineStringEnvelopeCoordinates(envelope.getPos(), b);
        }
    }

    /**
     * Processes the given ring into a two-dimensional JSON array of double values.
     *
     * @param ring the ring to be converted
     */
    private void processRingCoordinates(Ring ring, StringBuilder b)
    {
        // FIXME support multiple line strings
        if (ring.getLineString().size() > 1)
        {
            throw new UnsupportedOperationException("Multiple line strings not supported");
        }
        LineString ls = ring.getLineString().get(0);
        processLineStringCoordinates(ls, b);
    }

    protected void processCurveCoordinates(Curve curve, StringBuilder b)
    {
        if (curve.getLineString().size() > 1)
        {
            throw new UnsupportedOperationException("Multiple line strings not supported");
        }
        LineString ls = curve.getLineString().get(0);
        processLineStringCoordinates(ls, b);
    }

    protected void processLineStringCoordinates(LineString ls, StringBuilder b)
    {
        if(ls != null)
        {
           processLineStringEnvelopeCoordinates(ls.getPos(), b);
        }
    }
    
    /**
     * This is a common piece of code to process Line String and Envelope geometries
     * @param posList
     * @param sb
     */
    private void processLineStringEnvelopeCoordinates (List<Pos> posList, StringBuilder sb)
    {
        int i = 0;
        sb.append("[");
        for (Pos pos : posList)
        {
            if (i++ > 0)
            {
                sb.append(", ");
            }
            processPositionCoordinates(pos, sb);
        }
        sb.append("]");
    }

    protected void processPointCoordinates(Point p, StringBuilder b)
    {
        processPositionCoordinates(p.getPos(), b);
    }

    protected void processLegacyTextCoordinates(LegacyText lt, StringBuilder b)
    {
        Pos position = lt.getCalloutTarget();
        processPositionCoordinates(position, b);
    }

    private void processPositionCoordinates(Pos pos, StringBuilder b)
    {
        b.append("[");
        b.append(pos.getX());
        b.append(", ");
        b.append(pos.getY());
        b.append("]");
    }
}
