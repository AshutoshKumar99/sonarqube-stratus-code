package com.pb.stratus.controller.annotation;

import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.mapinfo.midev.service.geometries.v1.Point;
import com.pb.stratus.controller.action.FmnResultsCollectionParser;
import com.pb.stratus.controller.json.geojson.GeoJsonParser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

import static com.pb.stratus.controller.annotation.AnnotationStylingConstants.*;

/**
 * Parse the annotation information.
 * To change this template use File | Settings | File Templates.
 */
public class AnnotationParser {
    private static final Logger logger = LogManager.getLogger(
            FmnResultsCollectionParser.class);

    /**
     * Parses annotations from the JSON string.
     *
     * @param jsonRep
     * @param srs
     * @return
     */
    public List<Annotation> parseAnnotations(String jsonRep, String srs) {
        List<Annotation> annotations = new LinkedList<Annotation>();

        if (jsonRep == null) {
            logger.debug(" Empty json");
            return annotations;
        }
        /**
         * Fetch the geometries array from the JSON input string.
         */
        JSONArray features = getAnnotations(jsonRep);

        if (features != null) {
            Annotation annotation = null;
            for (int i = 0; i < features.size(); i++) {
                /**
                 * Get the feature JSON object
                 */
                annotation = new Annotation();
                JSONObject jsonAnnotation = JSONObject.fromObject(features.get(i));
                Geometry geometry = getGeometry(jsonAnnotation.getJSONObject("geometry"), srs);
                annotation.setGeometry(geometry);
                JSONObject properties = jsonAnnotation.getJSONObject("properties");
                annotation.setName((String) properties.get("annotationName"));

                JSONObject styleObj = properties.getJSONObject("styleObj");
                AnnotationStyle annotationStyle = null;

                if (!(properties.get("annotationType") == null ||
                        "null".equals(properties.get("annotationType").toString()))) {
                    annotation.setType(AnnotationType.valueOf(((String)
                            properties.get("annotationType")).toUpperCase()));

                } else {// In case of printing imported annotations, the type is coming as null.
                    // This code basically handles in above scenario.
                    if (geometry instanceof Point) {
                        if (properties.get("annotationType") == "text")
                            annotation.setType(AnnotationType.TEXT);
                        else
                            annotation.setType(AnnotationType.POINT);
                    } else if ((properties.get("annotationType") == "MultiLineString")) {
                        annotation.setType(AnnotationType.MULTILINESTRING);
                    } else {
                        annotation.setType(AnnotationType.FEATURE);
                    }
                }
                annotationStyle = createAnnotationStyle(annotation.getType(), styleObj);
                annotation.setStyle(annotationStyle);
                annotations.add(annotation);
            }

            return annotations;
        }
        return annotations;
    }

    private Geometry getGeometry(JSONObject json, String srs) {
        GeoJsonParser gJson = new GeoJsonParser(srs);
        return gJson.parseGeometry(json);
    }

    private JSONArray getAnnotations(String json) {
        JSONObject featureCollection = JSONObject.fromObject(json);
        return featureCollection.getJSONArray("features");
    }

    /**
     * fills annotation type with SS based annotations or text annotation based on annotation type
     *
     * @param type
     * @param style
     * @return
     */
    private AnnotationStyle createAnnotationStyle(Enum type, JSONObject style) {

        if (type == AnnotationType.TEXT) {
            return new TextAnnotationStyle(
                    (String) style.get(FONT_COLOR),
                    (String) style.get(FONT_FAMILY),
                    Integer.valueOf(style.get(FONT_SIZE).toString()).toString());
        } else if (type == AnnotationType.POINT) {
            return new PointAnnotationStyle(
                    (String) style.get(GRAPHIC_NAME),
                    (String) style.get(FILL_COLOR),
                    Integer.valueOf(style.get(POINT_RADIUS).toString()).toString());
        } else {  //line,polygon,rectangle,square,ellipse,circle
            Boolean fill = (Boolean) style.get(FILL);
            Integer strokeWidth = Integer.valueOf(style.get(STROKE_WIDTH).toString());

            AnnotationStyle object
                    = new AnnotationStyle(
                    (String) style.get(FILL_COLOR), 0.5d,
                    (String) style.get(STROKE_COLOR),
                    strokeWidth != null ? strokeWidth.toString() : null,
                    (String) style.get(STROKE_DASH_STYLE));


            if (type != AnnotationType.LINE) {

                // additional params like external graphic ,fill for polygon
                String graphicFillPattern = (String) style.get(EXTERNAL_GRAPHIC);
                if (fill != null && fill == false) {
                    object.setSpatialGraphicCode("1");
                }
                if (!StringUtils.isEmpty(graphicFillPattern)) {// hash, diagonal fill
                    if (graphicFillPattern.contains("fillPattern-2.png")) {
                        object.setSpatialGraphicCode("5");
                    } else if (graphicFillPattern.contains("fillPattern-3.png")) {
                        object.setSpatialGraphicCode("6");
                    } else if (graphicFillPattern.contains("fillPattern-4.png")) {
                        object.setSpatialGraphicCode("8");
                    }
                }
            }

            return object;
        }

    }

}
