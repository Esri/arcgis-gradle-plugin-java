/*
 * Copyright 2024 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
