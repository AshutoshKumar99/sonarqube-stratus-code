package com.pb.stratus.controller.geometry;

import com.mapinfo.midev.service.geometries.v1.Geometry;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class GeometryCollectionTest
{
    private GeometryCollection geometryCollection;

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPE()
    {
        geometryCollection = new GeometryCollection(null);
    }

    @Test (expected = UnsupportedOperationException.class)
    public void shouldReturnUnmodifiableList()
    {
        List<Geometry> mockGeometries = mock(List.class);
        geometryCollection = new GeometryCollection(mockGeometries);
        List<Geometry> geometries = geometryCollection.getGeometries();
        Geometry geometry = mock(Geometry.class);
        // should throw exception if try to add a new geometry
        geometries.add(geometry);
    }

    @Test
    public void shouldReturnTheSameGeometries()
    {
        List<Geometry> mockGeometries = mock(List.class);
        Geometry mockGeometry = mock(Geometry.class);
        when(mockGeometries.get(0)).thenReturn(mockGeometry);
        geometryCollection = new GeometryCollection(mockGeometries);
        List<Geometry> result = geometryCollection.getGeometries();
        List<Geometry> expected = Collections.unmodifiableList(mockGeometries);
        // both list should return the same mock geometry.
        assertEquals(expected.get(0), result.get(0));
    }

}
