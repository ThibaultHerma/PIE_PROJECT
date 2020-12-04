package simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.EventsLogger;
import org.orekit.time.AbsoluteDate;

import constellation.Constellation;
import constellation.Satellite;
import zone.Zone;


/**
 * The next class enables the user to simulate a constellation of satellites by propagating orbits,
 * and then computes the maximum time of revisit of each point of a mesh
 * 
 * These are the guidelines to follow to use this class :
 * 
 * - Instantiate a new simulation class by using the default constructor. The parameters of this
 *   constructor are a constellation, an initial and final dates of simulation and a zone to explore. 
 *   Dates are AbsoluteDate from Orekit library while constellation and zone are classes implemented 
 *   in this Java project.
 *   
 * - Call the method propagateOrbits to propagate the orbit of the satellites, using events detectors and orekit tools.
 * 
 * - Call the method getMaxRevisit to have the maximum time of revisit.
 * 
 *  
 * @author Julie Bayard
 * 
 * 
 */
public class Simulation {
	
	/**
	 * Constellation which contains all the satellites of the studied constellation
	 */
	private Constellation constellation;
	
	/**
	 * Zone of the Earth to explore. It corresponds to a mesh of the zone on which the maximum time
	 * of revisit is computed.
	 */
	private Zone zone;
	
	/** Beginning time of the simulation */
	private AbsoluteDate t0;
	
	/** Ending time of the simulation */
	private AbsoluteDate tf;

	/** 
	 * Elevation in radian, the elevation at which the point begins to be visible
	 * 	(90Â° - elevation) corresponds to the half extent of the FOV of the satellite
	 * */
	private double elevation = 1;
	
	/**
	 * HashMap which contains all the dates at which a geodetic point is beginning to be seen
	 * by a satellite of the constellation. It corresponds to the moment at which the geodetic 
	 * point enters into the field of view of the satellite.
	 * This can be used to computes data such as the revisit time, etc.
	 */
	private HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> listBegVisibilitiesMesh;// = new HashMap<GeodeticPoint, ArrayList<AbsoluteDate>>();
	
	/**
	 * HashMap which contains all the dates at which a geodetic point is not seen anymore
	 * by a satellite of the constellation. It corresponds to the moment at which the geodetic 
	 * point goes out of the field of view of the satellite.
	 * This will be used to computes data such as the revisit time, etc.
	 */
	private HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> listEndVisibilitiesMesh;// = new HashMap<GeodeticPoint, ArrayList<AbsoluteDate>>();

	
	/**
	 * Default constructor
	 * Instantiates the simulation of a constellation.
	 * 
	 * @param constellation : the constellation to simulate
	 * @param t0 : the date corresponding to the beginning of the simulation
	 * @param tf : the date which corresponds to the end of the simulation
	 * @param zone : the zone to explore on the Earth
	 */
	public Simulation(Constellation constellation, AbsoluteDate t0, AbsoluteDate tf, Zone zone) {
		this.constellation = constellation;
		this.t0 = t0;
		this.tf = tf;
		this.zone = zone;
		
		this.listBegVisibilitiesMesh = new HashMap<GeodeticPoint, ArrayList<AbsoluteDate>>();
		this.listEndVisibilitiesMesh = new HashMap<GeodeticPoint, ArrayList<AbsoluteDate>>();
		
		for (GeodeticPoint meshPoint : zone.getListMeshingPoints()) {
			listBegVisibilitiesMesh.put(meshPoint, new ArrayList<AbsoluteDate>());
			listEndVisibilitiesMesh.put(meshPoint, new ArrayList<AbsoluteDate>());
		}
	}
	
	
	
	public void addPointAndDateListEndVisibilitiesMesh(GeodeticPoint pointToAdd, AbsoluteDate dateToAdd) {
		this.listEndVisibilitiesMesh.putIfAbsent(pointToAdd, new ArrayList<AbsoluteDate>());
		this.listEndVisibilitiesMesh.get(pointToAdd).add(dateToAdd);
	}
	
	public void addPointAndDateListBegVisibilitiesMesh(GeodeticPoint pointToAdd, AbsoluteDate dateToAdd) {
		this.listBegVisibilitiesMesh.putIfAbsent(pointToAdd, new ArrayList<AbsoluteDate>());
		this.listBegVisibilitiesMesh.get(pointToAdd).add(dateToAdd);
	}
	
	
	
	/**
	 * This method computes the propagation of all satellites of the coonstellation and 
	 * detect when each point of the mesh is seen by a satellite.
	 * 
	 * The dates at which each point of the mesh enters and exits the satellite view are
	 * stored in the corresponding HashMaps. 
	 */
	public void propagateOrbits() {
		
		for (Satellite sat : constellation.getSatellitesList()) {
			
			// The logger is different for each satellite
			EventsLogger logger = new EventsLogger();
			
			// Definition of the propagator of the satellite trajectory
			KeplerianPropagator propagator = new KeplerianPropagator(sat.getInitialOrbit());
			
			// Addition of all event detectors
			zone.createEventsDetector(propagator, logger, elevation);
			
			// Propagation of the orbit of the satellite
			propagator.propagate(t0, tf);
			
			// Clear of the events detectors on the propagator (ESSENTIAL TO HAVE RELEVANT RESULTS !!)
 			propagator.clearEventsDetectors();
 			
 			// For each detector (associated to a point of the mesh) if the point is viewed
 			// by the satellite at the beginning of the simulation, it must be stored.
 			for (EventDetector detector : propagator.getEventsDetectors()) {
 				if (detector.g(propagator.getInitialState())>0)  {
 					ElevationDetector eDetector = (ElevationDetector) detector;
 					addPointAndDateListBegVisibilitiesMesh(eDetector.getTopocentricFrame().getPoint(), t0)  ;
 				}
 			}

 			
			for (final EventsLogger.LoggedEvent event : logger.getLoggedEvents()) {
				System.out.println(event.toString());
				ElevationDetector elevationDetector = (ElevationDetector) event.getEventDetector();
				
				GeodeticPoint mesh_point = elevationDetector.getTopocentricFrame().getPoint();				
				AbsoluteDate date = event.getState().getDate();
				
				// The method isIncreasing returns a boolean which states
				// whether the satellite is entering or exiting the elevation zone
				if (event.isIncreasing()) addPointAndDateListBegVisibilitiesMesh(mesh_point, date);
				else addPointAndDateListEndVisibilitiesMesh(mesh_point, date);
			}	
		}
	}
	
	
	
	/**
	 * This method computes the maximum time of revisit on the specified zone of the problem.
	 * 
	 * It uses the hashmaps of beginning and end of events previously computed during the propagation.
	 */
	private double getMaxRevisit(Vector3D point) {
		
		double maxRevisite = 0;
		
		for (GeodeticPoint meshPoint : zone.getListMeshingPoints()) {
			
			// a beginning of visibilty is seen as "(" and an end is seen as ")" 
			// nbParenthis is the current number of "(" minus the number of ")".
			// It should always be positive.
			int nbParenthesis = 0;
			
			AbsoluteDate endDate = t0;
			
			
			ArrayList<AbsoluteDate> begDates = listBegVisibilitiesMesh.get(meshPoint);
			ArrayList<AbsoluteDate> endDates = listEndVisibilitiesMesh.get(meshPoint);
			
			Collections.sort(endDates);
			Collections.sort(begDates);
			
			
			while (!begDates.isEmpty() && !endDates.isEmpty()) {
				
				// If the next event is a beginning of view
				if (begDates.get(0).compareTo(endDates.get(0))<0) {
					AbsoluteDate date = begDates.remove(0);
					if (nbParenthesis == 0) {
						double revisite = date.durationFrom(endDate);
						if (revisite>maxRevisite) maxRevisite=revisite;
					}
					nbParenthesis += 1;
				}
				// If the next event is an end of view
				else {
					AbsoluteDate date = endDates.remove(0);
					nbParenthesis += -1;
					if (nbParenthesis <0) System.out.println("Negative number of parenthesis...");
					if (nbParenthesis==0) {
						endDate = date;
					}
				}
			}
			
			if (begDates.isEmpty()) while (!endDates.isEmpty()){
				AbsoluteDate date = endDates.remove(0);
				nbParenthesis += -1;
				if (nbParenthesis <0) System.out.println("Negative number of parenthesis...");
				if (nbParenthesis==0) {
					endDate = date;
				}
			}		
		}
		return maxRevisite;
	}
}
