/**
 * 
 */
package com.pb.stratus.controller.action;

import com.mapinfo.midev.service.geometries.v1.Point;
import com.pb.stratus.controller.annotation.Annotation;
import com.pb.stratus.controller.annotation.AnnotationParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author SI001JY
 * 
 */
public class AnnotationParserTest {
	private AnnotationParser annotationParser;
	private String geoJsonString;
	private final String SRS = "epsg:27700";

	@Before
	public void setUp() {
		annotationParser = new AnnotationParser();
		geoJsonString = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"properties\":" +
                "{\"annotationType\":\"Point\",\"annotationName\":\"Point 1\",\"visible\":true,\"styleObj\":" +
                "{\"fillColor\":\"#808080\",\"fillOpacity\":0.5,\"strokeColor\":\"rgb(85, 170, 255)\",\"strokeOpacity\":" +
                "1,\"strokeWidth\":3,\"strokeDashstyle\":\"solid\",\"pointRadius\":3,\"graphicName\":\"square\"}},\"geometry\":" +
                "{\"type\":\"Point\",\"coordinates\":[-16351.005871702,6717770.869885]}},{\"type\":\"Feature\",\"properties\":" +
                "{\"annotationType\":\"Line\",\"annotationName\":\"Line 1\",\"visible\":true,\"styleObj\":{\"fillColor\":" +
                "\"#808080\",\"fillOpacity\":0.5,\"strokeColor\":\"rgb(255, 123, 83)\",\"strokeOpacity\":1,\"strokeWidth\":" +
                "5,\"strokeDashstyle\":\"1 4\",\"pointRadius\":\"3\",\"fontSize\":12,\"fontFamily\":\"Ms Gothic\",\"fontColor\":\"rgb(255, 123, 83)\",\"graphicName\":\"square\"}},\"geometry\":{\"type\":" +
                "\"LineString\",\"coordinates\":[[-17153.594668586,6718215.1601118],[-18682.335234076,6717207.1468014],[-17072.380326044,6716710.3061176]]}}]}";
	}

	@After
	public void tearDown() {
		geoJsonString = null;
		annotationParser = null;
	}

	/**
	 * There is no point creating expected Annotation object as neither
	 * Annotation nor the underlying Geometry implents equals method and we
	 * won't be able to compare expected and actual
	 */
	/*
	 * private String createMockAnnotationListJSON() { List<Annotation>
	 * mockedAnnotation = new LinkedList<Annotation>(); Geometry polygon = new
	 * Polygon(); //AnnotationStyle style = new AnnotationStyle("00EEEE", 0.5,
	 * "0000EE"); Annotation annotation = new Annotation(polygon, null);
	 * mockedAnnotation.add(annotation); Ring ring = new Ring(); LineString
	 * lineString = new LineString(); Pos pos1 = new Pos(), pos2 = new Pos(),
	 * pos3 = new Pos(), pos4 = new Pos(), pos5 = new Pos(); pos1.setX(100.0);
	 * pos1.setY(0.0); pos2.setX(101.0); pos2.setY(0.0); pos3.setX(101.0);
	 * pos3.setY(1.0); pos4.setX(100.0); pos4.setY(1.0); pos5.setX(100.0);
	 * pos5.setY(0.0); lineString.getPos().add(pos1);
	 * lineString.getPos().add(pos2); lineString.getPos().add(pos3);
	 * lineString.getPos().add(pos4); lineString.getPos().add(pos5);
	 * ring.setSrsName(SRS); ring.getLineString().add(lineString);
	 * polygon.setSrsName(SRS); ((Polygon)polygon).setExterior(ring); JSONArray
	 * jsonArray=new JSONArray(); jsonArray.addAll(mockedAnnotation); return
	 * jsonArray.toString(); }
	 */

	/**
	 * Test method for
	 * {@link com.pb.stratus.controller.annotation.AnnotationParser#parseAnnotations(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testParseAnnotations() {
		assertTrue("Should have returned Empty list if the input JSon String is null",
                annotationParser.parseAnnotations(null, null).size() == 0);
		List<Annotation> annotaionList = annotationParser.parseAnnotations(
				geoJsonString, SRS);
		assertFalse("Annotation List should not be empty but returning null", annotaionList.size() == 0);
		Annotation annotation = annotaionList.get(0);

		assertTrue(annotation.getGeometry() instanceof Point);
	}

}
