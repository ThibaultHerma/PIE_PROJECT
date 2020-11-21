package zone;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.orekit.bodies.GeodeticPoint;

class ZoneTest {

	@Test
	void testComputeLatLonStandardMeshing() {
		
		double earthRadius = org.orekit.utils.Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
		double standardMeshResolution = 1000 / earthRadius;
		double latMin = 0;
		double lonMin = 0;
		
		GeodeticPoint geodeticPoint1 = new GeodeticPoint(latMin, lonMin, earthRadius);
		GeodeticPoint geodeticPoint2 = new GeodeticPoint(latMin, lonMin + standardMeshResolution, earthRadius);
		GeodeticPoint geodeticPoint3 = new GeodeticPoint(latMin + standardMeshResolution, lonMin, earthRadius);
		GeodeticPoint geodeticPoint4 = new GeodeticPoint(latMin + standardMeshResolution, lonMin + standardMeshResolution, earthRadius);
		
		ArrayList<GeodeticPoint> inputPolygon = new ArrayList<GeodeticPoint>();
		inputPolygon.add(geodeticPoint1);
		inputPolygon.add(geodeticPoint2);
		inputPolygon.add(geodeticPoint3);
		inputPolygon.add(geodeticPoint4);
		
		
		Zone zone = new Zone(inputPolygon, "lat_lon_standard_meshing");
		
		ArrayList<GeodeticPoint> listMeshingPoints = zone.getListMeshingPoints();
		
		assert(geodeticPoint1.equals(listMeshingPoints.get(0)));
		assert(geodeticPoint2.equals(listMeshingPoints.get(1)));
		assert(geodeticPoint3.equals(listMeshingPoints.get(2)));
		assert(geodeticPoint4.equals(listMeshingPoints.get(3)));
		
		
		
	}

}
