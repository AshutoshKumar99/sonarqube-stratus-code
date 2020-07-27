package com.pb.stratus.controller.geometry;


import com.mapinfo.midev.service.geometries.v1.Geometry;
import com.pb.stratus.core.common.Preconditions;

import java.util.Collections;
import java.util.List;

public class GeometryCollection
{
    private List<Geometry> geometries;

    public GeometryCollection(List<Geometry> geometries) {
        Preconditions.checkNotNull(geometries, "geometries cannot be null");
        this.geometries = geometries;
    }

    public List<Geometry> getGeometries() {
        return Collections.unmodifiableList(geometries);
    }
}
