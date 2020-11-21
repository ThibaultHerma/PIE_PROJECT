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
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;

// TODO : write unit tests

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
	 * HashMap which contains all the dates at which a geodetic point is seen by the satellite
	 * This will be used to computes data such as the revisit time, etc.
	 */
	private HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> listRevisitTimeMesh;// = new HashMap<GeodeticPoint, ArrayList<AbsoluteDate>>();


	/**
	 * We need an event logger in order to retrieve all the events which will occur
	 */
	private EventsLogger logger;// = new EventsLogger();
	
	/**
	 * The resolution of the standard mesh.
	 * The unit is the radian.
	 * The standard resolution is set to 1km (at the Equator).
	 */
	private double standardMeshResolution = 1000 / org.orekit.utils.Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
	

	/**
	 * Default constructor
	 * Computes the meshing of the input polygon.
	 * 
	 * @param inputPolygon
	 * @param meshingStyle The style of meshing which has to be used to convert a polygon into a list of meshing points
	 */
	public Zone(ArrayList<GeodeticPoint> inputPolygon, String meshingStyle) {
		super();
		this.inputPolygon = inputPolygon;
		if (meshingStyle == "lat_lon_standard_meshing") {
			computeLatLonStandardMeshing();
		}
		else {
			System.out.println("The meshing style " + meshingStyle +
					           " which has been given as input does not exist.");
		}
		this.listRevisitTimeMesh = new HashMap<GeodeticPoint, ArrayList<AbsoluteDate>>();;
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

		while ((latMin + row * standardMeshResolution) <= latMax) {
			while ((lonMin + col * standardMeshResolution) <= lonMax) {
				GeodeticPoint geodeticPoint = new GeodeticPoint((latMin + row * standardMeshResolution), (lonMin + col * standardMeshResolution), meanAltitude);
				listMeshingPoints.add(geodeticPoint);
				col += 1;
			}
			row += 1;
			col = 0;
		}		
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
	public void createEventsDetector(Propagator propagator, double elevation) {
		
		org.orekit.frames.Frame earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
		BodyShape earth = new OneAxisEllipsoid(org.orekit.utils.Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
											   org.orekit.utils.Constants.WGS84_EARTH_FLATTENING,
		                                       earthFrame);
		// TODO : see if the parameters before can be set globally (the orekit constants)
		
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
	 * logged events. It fills the hashmap listRevisitTimeMesh with the time of 
	 * revisit of each point of the meshing.
	 * 
	 */
	public void fillListRevisitTimeMeshFromLog() {

		List<EventsLogger.LoggedEvent> listEvents = logger.getLoggedEvents();
		
		for (EventsLogger.LoggedEvent event : listEvents) {
			
			// the method isIncreasing returns a boolean which states
			// whether the satellite is entering or exiting the elevation zone
			// we decide to add the event only if that boolean is true
			// because we should not add an event for the enter AND the exit of a zone
			if (event.isIncreasing()) {

				ElevationDetector elevationDetector = (ElevationDetector) event.getEventDetector();
				SpacecraftState spacecraftState = event.getState();
				
				GeodeticPoint mesh_point = elevationDetector.getTopocentricFrame().getPoint();
				AbsoluteDate date = spacecraftState.getDate();
				
				addPointAndDateListRevisitTimeMesh(mesh_point, date);
				
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
	

	public HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> getListRevisitTimeMesh() {
		return listRevisitTimeMesh;
	}

	public void setListRevisitTimeMesh(HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> listRevisitTimeMesh) {
		this.listRevisitTimeMesh = listRevisitTimeMesh;
	}

	public void addPointAndDateListRevisitTimeMesh(GeodeticPoint pointToAdd, AbsoluteDate dateToAdd) {
		this.listRevisitTimeMesh.putIfAbsent(pointToAdd, new ArrayList<AbsoluteDate>());
		this.listRevisitTimeMesh.get(pointToAdd).add(dateToAdd);
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
 * That class is here to define what has to be done once a mesh point
 * is detected by a satellite
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
