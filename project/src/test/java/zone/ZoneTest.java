package zone;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.orekit.bodies.GeodeticPoint;

class ZoneTest {
	
	@Test
	/**
	 * The following test computes the meshing in a very simple case.
	 */
	void testComputeLatLonStandardMeshing() {
		
		double earthRadius = org.orekit.utils.Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
		double standardMeshResolution = 1000 / earthRadius;
		double latMin = 0;
		double lonMin = 0;
		
		// we create 4 simple geodetic points which correspond to the input polygon
		GeodeticPoint geodeticPoint1 = new GeodeticPoint(latMin, lonMin, earthRadius);
		GeodeticPoint geodeticPoint2 = new GeodeticPoint(latMin, lonMin + standardMeshResolution, earthRadius);
		GeodeticPoint geodeticPoint3 = new GeodeticPoint(latMin + standardMeshResolution, lonMin, earthRadius);
		GeodeticPoint geodeticPoint4 = new GeodeticPoint(latMin + standardMeshResolution, lonMin + standardMeshResolution, earthRadius);
		
		ArrayList<GeodeticPoint> inputPolygon = new ArrayList<GeodeticPoint>();
		inputPolygon.add(geodeticPoint1);
		inputPolygon.add(geodeticPoint2);
		inputPolygon.add(geodeticPoint3);
		inputPolygon.add(geodeticPoint4);
		 
		// we instanciate a zone object, which will create a mesh for the input polygon
		Zone zone = new Zone(inputPolygon); 
		
		// as the input polygon is very simple (a square) and as it has dimensions fitting
		// the standard mesh resolution, the points of the mesh have to match the
		// four points of the input polygon
		ArrayList<GeodeticPoint> listMeshingPoints = zone.getListMeshingPoints();
		
		assert(geodeticPoint1.equals(listMeshingPoints.get(0)));
		assert(geodeticPoint2.equals(listMeshingPoints.get(1)));
		assert(geodeticPoint3.equals(listMeshingPoints.get(2)));
		assert(geodeticPoint4.equals(listMeshingPoints.get(3)));
		
		
		
	}

}
