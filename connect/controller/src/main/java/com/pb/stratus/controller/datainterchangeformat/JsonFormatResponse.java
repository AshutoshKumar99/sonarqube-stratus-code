package com.pb.stratus.controller.datainterchangeformat;


import com.mapinfo.midev.service.featurecollection.v1.AttributeDefinition;
import com.mapinfo.midev.service.featurecollection.v1.KeyDefinition;
import com.mapinfo.midev.service.geometries.v1.*;
import com.mapinfo.midev.service.units.v1.Distance;
import com.pb.stratus.controller.info.DescribeTableResult;
import com.pb.stratus.controller.json.*;
import com.pb.stratus.controller.json.geojson.*;
import net.sf.json.JSONNull;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Class responsible for converting the given Object into Json response.
 */
public class JsonFormatResponse extends BaseDataInterchangeFormatResponse
{
    public void send(HttpServletResponse response, Object results)
            throws IOException
    {
        JsonConverter converter = createJsonConverter();
        String result = converter.toJson(results);
        byte[] bs;
        try
        {
            bs = result.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException x)
        {
            // UTF-8 must be supported at the minimum so we can safely
            // ignore it here
            throw new Error(x);
        }
        response.setContentType("text/json-comment-filtered");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(bs.length);
        OutputStream os = response.getOutputStream();
        os.write(bs);
        os.flush();
    }

    /**
     * Creates a new JSON converter that will be used by
     * to do the JSON
     * conversion. The default converter is an instance of
     * {@link com.pb.stratus.controller.json.TypedConverter} extended by
     * conversion strategies that convert
     * Midev geometries into GeoJSON. Subclasses can override this method
     * to either add their own strategies to the default converter or replace
     * it with an entirely different implementation.
     *
     * @return a JSON converter instance. Sub classes that override this
     *         method must not return <code>null</code>
     */
    protected JsonConverter createJsonConverter()
    {
        TypedConverter converter = new TypedConverter();
        converter.addStrategy(Point.class, new PointStrategy());
        converter.addStrategy(JSONNull.class,new NullStrategy());
        converter.addStrategy(MultiPoint.class, new MultiPointStrategy());
        converter.addStrategy(LineString.class, new LineStringStrategy());
        converter.addStrategy(Polygon.class, new PolygonStrategy());
        converter.addStrategy(Envelope.class, new EnvelopeStrategy());
        converter.addStrategy(LegacyText.class, new LegacyTextStrategy());
        converter.addStrategy(MultiPolygon.class, new MultiPolygonStrategy());
        converter.addStrategy(Curve.class, new CurveStrategy());
        converter.addStrategy(MultiCurve.class, new MultiCurveStrategy());
        converter.addStrategy(LegendDataHolder.class, new LegendDataStrategy());
        converter.addStrategy(AttributeDefinition.class, new AttributeDefinitionStrategy());
        converter.addStrategy(KeyDefinition.class, new KeyDefinitionStrategy());
        converter.addStrategy(Distance.class, new DistanceStrategy());
        converter.addStrategy(DescribeTableResult.class,new DescribeTableResultStrategy());
        return converter;
    }
}
