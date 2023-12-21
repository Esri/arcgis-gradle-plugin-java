package demo;

import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.PolylineBuilder;
import com.esri.arcgisruntime.geometry.SpatialReferences;

public class Library {
  public static Polyline createAPolyline() {
    PolylineBuilder builder = new PolylineBuilder(SpatialReferences.getWgs84());
    builder.addPoint(-10, -10);
    builder.addPoint(-10, 10);
    return builder.toGeometry();
  }

  public static double calculateLength(Polyline polyline) {
    return GeometryEngine.length(polyline);
  }
}
