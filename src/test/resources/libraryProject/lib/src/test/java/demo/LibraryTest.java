package demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {
  @Test
  void testPolylineLength() {
    var polyline = Library.createAPolyline();
    var length = Library.calculateLength(polyline);
    assertEquals(20.0, length, 0.0);
  }
}