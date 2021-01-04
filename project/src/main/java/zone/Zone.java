package zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hipparchus.ode.events.Action;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.EventsLogger;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.IERSConventions;

import utils.Parameters;

import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;

// TODO : write unit tests

/**
 * The next class enables the user to mesh a polygon with a set of points,
 * and then computes the time at which the points are seen by a satellite.
 * These are the guidelines to follow to use this class :
 * - instantiate a new zone class by using the default constructor. The parameters of this
 *   constructor are a set of points representing a polygon and a meshing type. The points have
 *   to be given using the orekit class GeodeticPoint. Then, a type of meshing has to be 
 *   given as a string parameter.
 * - call the method createEventsDetector on the zone object and give as parameter
 *   the orekit propagator that will be used to propagate the orbit of the satellite.
 *   The elevation of the points, which correspond to the field of view of the satellite, has
 *   to be given too.
 * - propagate the orbit, using the orekit tools.
 * - call the methods which gives back the list of revisit times on the mesh.
 * 
 * A zone object can be instantiated for a given polygon, and then used for several satellites :
 * the only necessary step is to call the method createEventsDetector before every propagation
 * and read the list of revisit times before propagating an other satellite.
 * 
 * WARNING Thread safety : The class is conditionally thread safe if and only if Parameters are immutable 
 * and each thread has its own instance of Propagator and Logger.
 *  
 * @author Louis Rivoire
 * 
 * 
 */
public class Zone {
	
	
	/**
	 * List of the geodetic points which form the polygon we want to monitor.
	 * For example, list of the points which form the border of France.
	 */
	private ArrayList<GeodeticPoint> inputPolygon;// = new ArrayList<GeodeticPoint>();

	/**
	 * List of the geodetic points which mesh the polygon we want to monitor.
	 * For example, list of the points which map France.
	 */
	private ArrayList<GeodeticPoint> listMeshingPoints;// = new ArrayList<GeodeticPoint>();
	
	
	/**
	 * HashMap which contains all the dates at which a geodetic point is beginning to be seen
	 * by the satellite. It corresponds to the moment at which the geodetic point enters into
	 * the field of view of the satellite.
	 * This can be used to computes data such as the revisit time, etc.
	 */
	private HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> listBegVisibilitiesMesh;// = new HashMap<GeodeticPoint, ArrayList<AbsoluteDate>>();
	
	/**
	 * HashMap which contains all the dates at which a geodetic point is not seen anymore
	 * by the satellite. It corresponds to the moment at which the geodetic point goes out of
	 * the field of view of the satellite.
	 * This will be used to computes data such as the revisit time, etc.
	 */
	private HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> listEndVisibilitiesMesh;// = new HashMap<GeodeticPoint, ArrayList<AbsoluteDate>>();


	/**
	 * We need an event logger in order to retrieve all the events which will occur
	 */
	private EventsLogger logger;// = new EventsLogger();
	
	/**
	 * The resolution of the standard mesh.
	 * The unit is the radian. 
	 * The standard resolution is set to 20km (at the Equator).
	 */
	private double standardMeshResolution = 20000 / org.orekit.utils.Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
 
	/** 
	 * The style of meshing which has to be used to convert a polygon into a list of meshing points
	 */
	private String meshingStyle; 
	 

	/**
	 * Default constructor
	 * Computes the meshing of the input polygon.
	 * 
	 * @param inputPolygon List of the geodetic points which form the polygon we want to monitor.
	 * 
	 */
	public Zone(ArrayList<GeodeticPoint> inputPolygon) {
		super();
		this.inputPolygon = inputPolygon;
		this.meshingStyle = Parameters.meshingStyle;
		if (meshingStyle.equals("lat_lon_standard_meshing")) {
			computeLatLonStandardMeshing(); 
		}
		else {
			System.out.println("The meshing style " + meshingStyle +
					           " which has been given as input does not exist.");
		}
		this.listBegVisibilitiesMesh = new HashMap<GeodeticPoint, ArrayList<AbsoluteDate>>();
		this.listEndVisibilitiesMesh = new HashMap<GeodeticPoint, ArrayList<AbsoluteDate>>();
		this.logger = new EventsLogger();
	}
 
	
	
	/**
	 * This method computes the standard meshing for an input polygon.
	 * The meshing is a linear meshing along the latitude and longitude,
	 * a square between the minimum and maximum latitude and longitude.
	 * We use as altitude the mean altitude of all the points of the input polygon.
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
				GeodeticPoint geodeticPoint = new GeodeticPoint((latMin + row * standardMeshResolution), (lonMin + col * standardMeshResolution), meanAltitude);
				listMeshingPoints.add(geodeticPoint);
				col += 1;
				nb_of_points+=1;
			}
			row += 1;
			col = 0;
		} 
		System.out.println("Nb of points to map the input polygon is " + nb_of_points);
	}
	
	
	/**
	 * This method computes the minimum and maximum latitude and longitude of the
	 * geodetic points of the input polygon. It also computes the mean altitude
	 * of all the geodetic points.
	 * 
	 * @return latLonMinMaxAltitude a list of doubles containing the minimum latitude,
	 * 								the maximum latitude, the minimum longitude,
	 * 								the maximum longitude and the mean altitude.
	 */
	public ArrayList<Double> computeLatLonMinMaxAltitude(){
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
	
	
	/**
	 * This method creates all the events for eachpoint of the meshing.
	 * The events are "ElevationDetector" events.
	 * This method has to be called before propagating the orbit.
	 * 
	 * @param propagator the propagator used
	 * 
	 * @param elevation in radian
	 * 					it is the elevation at which the point begins to be visible
	 * 					(90° - elevation) corresponds to the half extent of the FOV of the satellite
	 */
	public void createEventsDetector(Propagator propagator, EventsLogger logger, double elevation) {
		
		org.orekit.frames.Frame earthFrame = FramesFactory.getITRF(Parameters.projectIERSConventions, true);
		BodyShape earth = new OneAxisEllipsoid(Parameters.projectEarthEquatorialRadius,
											   Parameters.projectEarthFlattening,
		                                       earthFrame);
		
		// TODO : find the meaning of these parameters
		double maxcheck  = 60.0;
		double threshold =  0.001;
		
		// we go through all the points of the meshing and add them as events to be detected
		for (int point_index = 0; point_index < listMeshingPoints.size(); point_index++) {
			
			GeodeticPoint mesh_point = listMeshingPoints.get(point_index);

			TopocentricFrame staFrame = new TopocentricFrame(earth, mesh_point, "mesh_point_" + point_index);
			
			EventDetector staVisi =
			  new ElevationDetector(maxcheck, threshold, staFrame).
			  withConstantElevation(elevation).
			  withHandler(new VisibilityHandler());
			// TODO : the visibility handler is only there to display informations but has no utility
			
			
			// when we add an event detector, we monitor it to be able to retrieve it
			propagator.addEventDetector(logger.monitorDetector(staVisi));
		}
		
	}
	
	
	/**
	 * This method reads what has been stored during the propagation in the list of
	 * logged events. It fills the hashmaps listBegVisibilitiesMesh and
	 * listEndVisibilitiesMesh with the time of revisit of each point of the
	 * meshing.
	 */
	public void fillListRevisitTimeMeshFromLog() {

		List<EventsLogger.LoggedEvent> listEvents = logger.getLoggedEvents();
		
		for (EventsLogger.LoggedEvent event : listEvents) {

			ElevationDetector elevationDetector = (ElevationDetector) event.getEventDetector();
			SpacecraftState spacecraftState = event.getState();
			
			GeodeticPoint mesh_point = elevationDetector.getTopocentricFrame().getPoint();
			AbsoluteDate date = spacecraftState.getDate();
			
			// the method isIncreasing returns a boolean which states
			// whether the satellite is entering or exiting the elevation zone
			if (event.isIncreasing()) {
				addPointAndDateListBegVisibilitiesMesh(mesh_point, date);
			}
			else {
				addPointAndDateListEndVisibilitiesMesh(mesh_point, date);
			}
		}
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
	

	public HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> getListBegVisibilitiesMesh() {
		return listBegVisibilitiesMesh;
	}

	public void setListBegVisibilitiesMesh(HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> listBegVisibilitiesMesh) {
		this.listBegVisibilitiesMesh = listBegVisibilitiesMesh;
	}

	public void addPointAndDateListBegVisibilitiesMesh(GeodeticPoint pointToAdd, AbsoluteDate dateToAdd) {
		this.listBegVisibilitiesMesh.putIfAbsent(pointToAdd, new ArrayList<AbsoluteDate>());
		this.listBegVisibilitiesMesh.get(pointToAdd).add(dateToAdd);
	}
	
	public HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> getListEndVisibilitiesMesh() {
		return listEndVisibilitiesMesh;
	}

	public void setListEndVisibilitiesMesh(HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> listEndVisibilitiesMesh) {
		this.listEndVisibilitiesMesh = listEndVisibilitiesMesh;
	}

	public void addPointAndDateListEndVisibilitiesMesh(GeodeticPoint pointToAdd, AbsoluteDate dateToAdd) {
		this.listEndVisibilitiesMesh.putIfAbsent(pointToAdd, new ArrayList<AbsoluteDate>());
		this.listEndVisibilitiesMesh.get(pointToAdd).add(dateToAdd);
	}
	

	public EventsLogger getLogger() {
		return logger;
	}

	public void setLogger(EventsLogger logger) {
		this.logger = logger;
	}



	public double getStandardMeshResolution() {
		return standardMeshResolution;
	}

	public void setStandardMeshResolution(double standardMeshResolution) {
		this.standardMeshResolution = standardMeshResolution;
	}
	


	
	
}

/**
 * That class is here to display when an event is detected.
 *
 */
class VisibilityHandler implements EventHandler<ElevationDetector> {

    public Action eventOccurred(final SpacecraftState s, final ElevationDetector detector,
                                final boolean increasing) {
        if (increasing) {
            System.out.println(" Visibility on " + detector.getTopocentricFrame().getName()
                                                 + " begins at " + s.getDate());
            return Action.CONTINUE;
        } else {
            System.out.println(" Visibility on " + detector.getTopocentricFrame().getName()
                                                 + " ends at " + s.getDate());
            // we need to continue the simulation once a point is seen because we may have other points
            // or may want to calculate the revisit time
            return Action.CONTINUE;
            }
         
    }

    public SpacecraftState resetState(final ElevationDetector detector, final SpacecraftState oldState) {
        return oldState;
    }

}
