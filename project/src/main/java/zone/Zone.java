package zone;

import java.util.ArrayList;

import org.orekit.bodies.GeodeticPoint;
import utils.Parameters;

// TODO : write unit tests

/**
 * The next class enables the user to mesh a polygon with a set of points, and
 * then computes the time at which the points are seen by a satellite. These are
 * the guidelines to follow to use this class : - instantiate a new zone class
 * by using the default constructor. The parameters of this constructor are a
 * set of points representing a polygon and a meshing type. The points have to
 * be given using the orekit class GeodeticPoint. Then, a type of meshing has to
 * be given as a string parameter. - call the method createEventsDetector on the
 * zone object and give as parameter the orekit propagator that will be used to
 * propagate the orbit of the satellite. The elevation of the points, which
 * correspond to the field of view of the satellite, has to be given too. -
 * propagate the orbit, using the orekit tools. - call the methods which gives
 * back the list of revisit times on the mesh.
 * 
 * A zone object can be instantiated for a given polygon, and then used for
 * several satellites : the only necessary step is to call the method
 * createEventsDetector before every propagation and read the list of revisit
 * times before propagating an other satellite.
 * 
 * WARNING Thread safety : The class is conditionally thread safe if and only if
 * Parameters are immutable and each thread has its own instance of Propagator
 * and Logger.
 * 
 * @author Louis Rivoire
 * 
 * 
 */
public class Zone {

	/**
	 * List of the geodetic points which form the polygon we want to monitor. For
	 * example, list of the points which form the border of France.
	 */
	private ArrayList<GeodeticPoint> inputPolygon;// = new ArrayList<GeodeticPoint>();

	/**
	 * List of the geodetic points which mesh the polygon we want to monitor. For
	 * example, list of the points which map France.
	 */
	private ArrayList<GeodeticPoint> listMeshingPoints;// = new ArrayList<GeodeticPoint>();

	/**
	 * The resolution of the standard mesh. The unit is the radian. The standard
	 * resolution is set to 20km (at the Equator).
	 */
	private double standardMeshResolution = Parameters.standardMeshResolution;

	/**
	 * The style of meshing which has to be used to convert a polygon into a list of
	 * meshing points
	 */
	private String meshingStyle;

	/**
	 * Default constructor Computes the meshing of the input polygon.
	 * 
	 * @param inputPolygon List of the geodetic points which form the polygon we
	 *                     want to monitor.
	 * 
	 */
	public Zone(ArrayList<GeodeticPoint> inputPolygon) {
		super();
		this.inputPolygon = inputPolygon;
		this.meshingStyle = Parameters.meshingStyle;
		if (meshingStyle.equals("lat_lon_standard_meshing")) {
			computeLatLonStandardMeshing();
		} else {
			System.out.println("The meshing style " + meshingStyle + " which has been given as input does not exist.");
		}
	}

	/**
	 * This method computes the standard meshing for an input polygon. The meshing
	 * is a linear meshing along the latitude and longitude, a square between the
	 * minimum and maximum latitude and longitude. We use as altitude the mean
	 * altitude of all the points of the input polygon.
	 */
	public void computeLatLonStandardMeshing() {
		double latMin, latMax, lonMin, lonMax, meanAltitude;

		ArrayList<Double> latLonMinMaxAltitude = computeLatLonMinMaxAltitude();

		latMin = latLonMinMaxAltitude.get(0);
		latMax = latLonMinMaxAltitude.get(1);
		lonMin = latLonMinMaxAltitude.get(2);
		lonMax = latLonMinMaxAltitude.get(3);
		meanAltitude = latLonMinMaxAltitude.get(4);

		listMeshingPoints = new ArrayList<GeodeticPoint>();

		int row = 0;
		int col = 0;

		int nb_of_points = 0;

		while ((latMin + row * standardMeshResolution) <= latMax) {
			while ((lonMin + col * standardMeshResolution) <= lonMax) {
				GeodeticPoint geodeticPoint = new GeodeticPoint((latMin + row * standardMeshResolution),
						(lonMin + col * standardMeshResolution), meanAltitude);
				listMeshingPoints.add(geodeticPoint);
				col += 1;
				nb_of_points += 1;
			}
			row += 1;
			col = 0;
		}
		System.out.println("Nb of points to map the input polygon is " + nb_of_points);
	}

	/**
	 * This method computes the minimum and maximum latitude and longitude of the
	 * geodetic points of the input polygon. It also computes the mean altitude of
	 * all the geodetic points.
	 * 
	 * @return latLonMinMaxAltitude a list of doubles containing the minimum
	 *         latitude, the maximum latitude, the minimum longitude, the maximum
	 *         longitude and the mean altitude.
	 */
	public ArrayList<Double> computeLatLonMinMaxAltitude() {
		ArrayList<Double> latLonMinMaxAltitude = new ArrayList<Double>();

		double latMin, latMax, lonMin, lonMax, meanAltitude;

		latMin = inputPolygon.get(0).getLatitude();
		latMax = inputPolygon.get(0).getLatitude();
		lonMin = inputPolygon.get(0).getLongitude();
		lonMax = inputPolygon.get(0).getLongitude();
		meanAltitude = 0;

		for (GeodeticPoint geodeticPoint : inputPolygon) {
			if (geodeticPoint.getLatitude() < latMin) {
				latMin = geodeticPoint.getLatitude();
			}
			if (geodeticPoint.getLatitude() > latMax) {
				latMax = geodeticPoint.getLatitude();
			}
			if (geodeticPoint.getLongitude() < lonMin) {
				lonMin = geodeticPoint.getLongitude();
			}
			if (geodeticPoint.getLongitude() > lonMax) {
				lonMax = geodeticPoint.getLongitude();
			}
			meanAltitude += geodeticPoint.getAltitude();
		}

		meanAltitude /= inputPolygon.size();

		latLonMinMaxAltitude.add(latMin);
		latLonMinMaxAltitude.add(latMax);
		latLonMinMaxAltitude.add(lonMin);
		latLonMinMaxAltitude.add(lonMax);
		latLonMinMaxAltitude.add(meanAltitude);

		return latLonMinMaxAltitude;
	}

	public ArrayList<GeodeticPoint> getInputPolygon() {
		return inputPolygon;
	}

	public void setInputPolygon(ArrayList<GeodeticPoint> inputPolygon) {
		this.inputPolygon = inputPolygon;
	}

	public void addPointInputPolygon(GeodeticPoint pointToAdd) {
		this.inputPolygon.add(pointToAdd);
	}

	public ArrayList<GeodeticPoint> getListMeshingPoints() {
		return listMeshingPoints;
	}

	public void setListMeshingPoints(ArrayList<GeodeticPoint> listMeshingPoints) {
		this.listMeshingPoints = listMeshingPoints;
	}

	public void addPointListMeshingPoints(GeodeticPoint pointToAdd) {
		this.listMeshingPoints.add(pointToAdd);
	}

	public double getStandardMeshResolution() {
		return standardMeshResolution;
	}

	public void setStandardMeshResolution(double standardMeshResolution) {
		this.standardMeshResolution = standardMeshResolution;
	}

}
