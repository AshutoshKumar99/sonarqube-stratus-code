package com.pb.stratus.controller.json.geojson;

import com.mapinfo.midev.service.geometries.v1.*;
import com.pb.stratus.controller.UnsupportedGeometryException;
import com.pb.stratus.controller.featuresearch.FeatureGeometryConverter;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.pb.stratus.controller.GeometryType.*;
import static com.pb.stratus.controller.Constants.*;

/**
 * This class is responsible for parsing the GeoJSON string into Geometry
 * object.
 */
public class GeoJsonParser {
    private String srsName;

    public GeoJsonParser(String srsName) {
        this.srsName = srsName;
    }

    /**
     * Parses as geometry.
     *
     * @param json
     * @return
     * @throws JSONException
     */
    public Geometry parseGeometry(JSONObject json) throws JSONException {
        Geometry result = null;
        String type = json.getString(GEOMETRY_TYPE);

        JSONArray coordinates = json.getJSONArray(COORDINATES);
        if (Point.name().equals(type)) {
            result = parsePoint(coordinates);
        } else if (MultiPoint.name().equals(type)) {
            result = parseMultiPoint(coordinates);
        } else if (MultiLineString.name().equals(type)) {
            result = parseMultiLineString(coordinates);
        } else if (LineString.name().equals(type)) {
            result = parseLineString(coordinates);
        } else if (Polygon.name().equals(type)) {
            result = parsePolygon(coordinates);
        } else if (MultiPolygon.name().equals(type)) {
            result = parseMultiPolygon(coordinates);
        } else {
            throw new UnsupportedGeometryException("Type '" + type
                    + "' is not supported");
        }

        return result;
    }

    /**
     * Parse a given Geometry instance as a FeatureGeometry
     *
     * @param json A single geometry  instance
     * @return
     * @throws JSONException
     */
    public FeatureGeometry parseAsFeatureGeometry(JSONObject json) throws JSONException {
        FeatureGeometry result = null;
        String type = json.getString(GEOMETRY_TYPE);

        JSONArray coordinates = json.getJSONArray(COORDINATES);
        if (Point.name().equals(type)) {
            result = parsePoint(coordinates);
        } else if (MultiPoint.name().equals(type)) {
            result = parseMultiPoint(coordinates);
        } else if (MultiLineString.name().equals(type)) {
            FeatureGeometryConverter converter =
                    new FeatureGeometryConverter(parseMultiLineString(coordinates),
                            srsName);
            result = converter.convert();
        } else if (LineString.name().equals(type)) {
            FeatureGeometryConverter converter =
                    new FeatureGeometryConverter(parseLineString(coordinates),
                            srsName);
            result = converter.convert();
        } else if (Polygon.name().equals(type)) {
            FeatureGeometryConverter converter =
                    new FeatureGeometryConverter(parsePolygon(coordinates));
            result = converter.convert();
        } else if (MultiPolygon.name().equals(type)) {
            result = parseMultiPolygon(coordinates);
        } else {
            throw new UnsupportedGeometryException("Type '" + type
                    + "' is not supported");
        }

        return result;
    }

    /**
     * Check if all the elements are Polygons.
     *
     * @param geometries
     * @return
     */
    private boolean checkIfAllArePolygons(JSONArray geometries) {
        String type = null;
        for (int i = 0; i < geometries.size(); i++) {
            type = geometries.getJSONObject(i).getString(GEOMETRY_TYPE);
            if (Point.name().equals(type) || MultiPolygon.name().equals(type) ||
                    MultiLineString.name().equals(type) || LineString.name().equals(type)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Parse a GeoJason String into a Geometry.
     * CONN#19688: fixing the issue with MultiFeatureGeometry
     *
     * @param geoJsonString
     * @return
     * @throws JSONException
     */
    public Geometry parseGeometry(String geoJsonString) throws JSONException {
        JSONObject json = JSONObject.fromObject(geoJsonString);
        String type = json.getString(GEOMETRY_TYPE);
        /**
         * For cases where the user has chosen mixed geometries, like a polygon and a point or a line,
         * we have no choice but to pass them as individual FeatureGeometries
         * (of type MultiPoint, MultiCurve or MultiPolygon).
         */
        MultiFeatureGeometry multiFeatureGeometry = null;

        // CONN-18813: Querying in more than one annotation will result in a GeometryCollection
        // CONN-19688: Using MultiPolygon for querying.
        if (type.equals(GEOMETRY_COLLECTION)) {

            JSONArray geometries = json.getJSONArray(GEOMETRIES);

            if (checkIfAllArePolygons(geometries)) {
                MultiPolygon mp = new MultiPolygon();
                for (int i = 0; i < geometries.size(); i++) {
                    mp.getPolygon().add(parsePolygon(geometries.getJSONObject(i).getJSONArray(COORDINATES)));
                }
                mp.setSrsName(srsName);
                return mp;
            } else {
                List<FeatureGeometry> geometryList = new ArrayList<>();
                for (int i = 0; i < geometries.size(); i++) {
                    geometryList.add(parseAsFeatureGeometry(geometries.getJSONObject(i)));
                }
                multiFeatureGeometry = new MultiFeatureGeometry();
                multiFeatureGeometry.setSrsName(srsName);
                multiFeatureGeometry.getFeatureGeometry().addAll(geometryList);
                return multiFeatureGeometry;
            }
        } else {
            return parseGeometry(json);
        }
    }

    private MultiPolygon parseMultiPolygon(JSONArray coordinates) {
        MultiPolygon mPolygon = new MultiPolygon();
        for (int index = 0; index < coordinates.size(); ++index) {
            mPolygon.getPolygon().add(
                    parsePolygon(coordinates.getJSONArray(index)));
        }
        mPolygon.setSrsName(srsName);
        return mPolygon;
    }

    private LineString parseMultiLineString(JSONArray linesArray) {
        LineString mLineString = new LineString();
        for (int i = 0; i < linesArray.size(); i++) {
            LineString ls = parseLineString(linesArray.getJSONArray(i));
            mLineString.getPos().addAll(ls.getPos());
        }
        mLineString.setSrsName(srsName);
        return mLineString;
    }

    private LineString parseLineString(JSONArray coordinates) {
        LineString lineString = new LineString();
        lineString.setSrsName(srsName);
        Point points[] = parsePoints(coordinates);
        for (Point point : points) {
            lineString.getPos().add(point.getPos());
        }

        return lineString;
    }

    private MultiPoint parseMultiPoint(JSONArray coordinates) {
        MultiPoint mPoint = new MultiPoint();
        mPoint.setSrsName(srsName);
        for (int index = 0; index < coordinates.size(); index++) {
            mPoint.getPoint().add(parsePoint(coordinates.getJSONArray(index)));
        }
        return mPoint;
    }

    private Polygon parsePolygon(JSONArray coordinates) {
        // first index is for outer ring
        Ring outer = createRing(parsePoints(coordinates.getJSONArray(0)));
        outer.setSrsName(srsName);

        // Further items in the jsonarray are for interior list
        InteriorList iList = new InteriorList();
        for (int index = 1; index < coordinates.size(); index++) {
            iList.getRing().add(
                    createRing(parsePoints(coordinates.getJSONArray(index))));
        }

        Polygon polygon = new Polygon();
        polygon.setExterior(outer);
        // CONN-13488 Don't set empty element.
        if (iList.getRing() != null && !iList.getRing().isEmpty()) {
            polygon.setInteriorList(iList);
        }
        polygon.setSrsName(srsName);
        return polygon;
    }

    private Ring createRing(Point[] points) {
        LineString lineString = new LineString();
        lineString.setSrsName(srsName);

        for (Point p : points) {
            lineString.getPos().add(p.getPos());
        }

        Ring ring = new Ring();
        ring.getLineString().add(lineString);
        ring.setSrsName(srsName);
        return ring;
    }

    private Point[] parsePoints(JSONArray coordinates) throws JSONException {
        Point[] result = new Point[coordinates.size()];

        for (int index = 0; index < result.length; index++) {
            result[index] = parsePoint(coordinates.getJSONArray(index));
        }

        return result;
    }

    private Point parsePoint(JSONArray coord) throws JSONException {
        Point point = new Point();
        if (coord == null || coord.size() <= 0) {
            return point;
        }

        Pos pos = new Pos();
        double x = 0, y = 0;
        x = coord.getDouble(0);
        if (coord.size() > 1) {
            y = coord.getDouble(1);
        }

        pos.setX(x);
        pos.setY(y);

        point.setPos(pos);
        point.setSrsName(srsName);
        return point;
    }
}