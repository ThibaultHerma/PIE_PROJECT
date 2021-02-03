package zone;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.orekit.bodies.GeodeticPoint;

import utils.Parameters;

class ZoneTest {

	@Test
	/**
	 * The following test computes the meshing in a very simple case.
	 */
	void testComputeLatLonStandardMeshing() {

		double standardMeshResolution = Parameters.standardMeshResolution;
		double latMin = 0;
		double lonMin = 0;

		// we create 4 simple geodetic points which correspond to the input polygon
		GeodeticPoint geodeticPoint1 = new GeodeticPoint(latMin, lonMin, 0);
		GeodeticPoint geodeticPoint2 = new GeodeticPoint(latMin, lonMin + standardMeshResolution, 0);
		GeodeticPoint geodeticPoint3 = new GeodeticPoint(latMin + standardMeshResolution, lonMin, 0);
		GeodeticPoint geodeticPoint4 = new GeodeticPoint(latMin + standardMeshResolution,
				lonMin + standardMeshResolution, 0);

		ArrayList<GeodeticPoint> inputPolygon = new ArrayList<GeodeticPoint>();
		inputPolygon.add(geodeticPoint1);
		inputPolygon.add(geodeticPoint2);
		inputPolygon.add(geodeticPoint3);
		System.out.println(inputPolygon.size());
		// we instanciate a zone object, which will create a mesh for the input polygon
		Zone zone = new Zone(inputPolygon);

		// as the input polygon is very simple (a square) and as it has dimensions
		// fitting
		// the standard mesh resolution, the points of the mesh have to match the
		// four points of the input polygon
		ArrayList<GeodeticPoint> listMeshingPoints = zone.getListMeshingPoints();
		System.out.println("size:" + listMeshingPoints.size());
		assert (geodeticPoint1.equals(listMeshingPoints.get(0)));
		assert (geodeticPoint2.equals(listMeshingPoints.get(1)));
		assert (geodeticPoint3.equals(listMeshingPoints.get(2)));
		assert (geodeticPoint4.equals(listMeshingPoints.get(3)));

	}
	


	@Test
	/**
	 * The following test verifies the function computeLatLonMinMaxAltitude.
	 */
	void testComputeLatLonMinMaxAltitude() {

		double standardMeshResolution = Parameters.standardMeshResolution;
		double latMin = 0;
		double lonMin = 2;
		
		double alt1 = 0;
		double alt2 = 100;
		double altMean = (3*alt1 + alt2)/4;

		// we create 4 simple geodetic points which correspond to the input polygon
		GeodeticPoint geodeticPoint1 = new GeodeticPoint(latMin, lonMin, alt1);
		GeodeticPoint geodeticPoint2 = new GeodeticPoint(latMin, lonMin + standardMeshResolution, alt1);
		GeodeticPoint geodeticPoint3 = new GeodeticPoint(latMin + standardMeshResolution, lonMin, alt1);
		GeodeticPoint geodeticPoint4 = new GeodeticPoint(latMin - standardMeshResolution,
				lonMin + standardMeshResolution, alt2);

		ArrayList<GeodeticPoint> inputPolygon = new ArrayList<GeodeticPoint>();
		inputPolygon.add(geodeticPoint1);
		inputPolygon.add(geodeticPoint2);
		inputPolygon.add(geodeticPoint3);
		inputPolygon.add(geodeticPoint4);
		System.out.println(inputPolygon.size());
		// we instanciate a zone object, which will create a mesh for the input polygon
		Zone zone = new Zone(inputPolygon);
		
		ArrayList<Double> latLonMinMaxAltitude = zone.computeLatLonMinMaxAltitude();
		System.out.println(latLonMinMaxAltitude);

		assert (latLonMinMaxAltitude.get(0).equals(latMin - standardMeshResolution));
		assert (latLonMinMaxAltitude.get(1).equals(latMin + standardMeshResolution));
		assert (latLonMinMaxAltitude.get(2).equals(lonMin));
		assert (latLonMinMaxAltitude.get(3).equals(lonMin + standardMeshResolution));
		assert (latLonMinMaxAltitude.get(4).equals(altMean));


	}

}
