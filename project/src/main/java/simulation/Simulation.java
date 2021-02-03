package simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.events.Action;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.EventsLogger;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.PVCoordinates;
import org.orekit.utils.PVCoordinatesProvider;

import utils.Parameters;

import constellation.Constellation;
import constellation.Satellite;
import zone.Zone;

/**
 * The next class enables the user to simulate a constellation of satellites by
 * propagating orbits, and then computes the maximum time of revisit of each
 * point of a mesh
 * 
 * These are the guidelines to follow to use this class :
 * 
 * - Instantiate a new simulation class by using the default constructor. The
 * parameters of this constructor are a constellation, an initial and final
 * dates of simulation and a zone to explore. Dates are AbsoluteDate from Orekit
 * library while constellation and zone are classes implemented in this Java
 * project.
 * 
 * - Call the method propagateOrbits to propagate the orbit of the satellites,
 * using events detectors and orekit tools.
 * 
 * - Call the method getMaxRevisit to have the maximum time of revisit.
 * 
 * WARNING Thread safety: the class is conditionally thread safe if and only if
 * Parameters are immutable and each thread has its own instance of Zone AND
 * Constellation AND Simulation
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
	 * Zone of the Earth to explore. It corresponds to a mesh of the zone on which
	 * the maximum time of revisit is computed.
	 */
	private Zone zone;

	/** Beginning time of the simulation */
	private AbsoluteDate t0;

	/** Ending time of the simulation */
	private AbsoluteDate tf;

	/** Verbose option : if true display results */
	private Boolean verbose = false;

	/**
	 * HashMap which contains all the dates at which a geodetic point is beginning
	 * to be seen by a satellite of the constellation. It corresponds to the moment
	 * at which the geodetic point enters into the field of view of the satellite.
	 * This can be used to computes data such as the revisit time, etc.
	 */
	private HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> listBegVisibilitiesMesh;// = new HashMap<GeodeticPoint,
																					// ArrayList<AbsoluteDate>>();

	/**
	 * HashMap which contains all the dates at which a geodetic point is not seen
	 * anymore by a satellite of the constellation. It corresponds to the moment at
	 * which the geodetic point goes out of the field of view of the satellite. This
	 * will be used to computes data such as the revisit time, etc.
	 */
	private HashMap<GeodeticPoint, ArrayList<AbsoluteDate>> listEndVisibilitiesMesh;// = new HashMap<GeodeticPoint,
																					// ArrayList<AbsoluteDate>>();

	/**
	 * HashMap which contains all the event detectors (LoggedEvents and
	 * EventDetector) and their corresponding geodetic point This will be used to
	 * computes data such as the revisit time, etc.
	 */
	private HashMap<EventDetector, GeodeticPoint> eventDetPoint = new HashMap<EventDetector, GeodeticPoint>();


	
	/**
	 * Default constructor Instantiates the simulation of a constellation.
	 * 
	 * @param constellation : the constellation to simulate
	 * @param t0            : the date corresponding to the beginning of the
	 *                      simulation
	 * @param tf            : the date which corresponds to the end of the
	 *                      simulation
	 * @param zone          : the zone to explore on the Earth
	 */
	public Simulation(Constellation constellation, AbsoluteDate t0, AbsoluteDate tf, Zone zone, Boolean verbose) {
		this.constellation = constellation;
		this.t0 = t0;
		this.tf = tf;
		this.zone = zone;
		this.verbose = verbose;

		this.listBegVisibilitiesMesh = new HashMap<GeodeticPoint, ArrayList<AbsoluteDate>>();
		this.listEndVisibilitiesMesh = new HashMap<GeodeticPoint, ArrayList<AbsoluteDate>>();

		for (GeodeticPoint meshPoint : zone.getListMeshingPoints()) {
			listBegVisibilitiesMesh.put(meshPoint, new ArrayList<AbsoluteDate>());
			listEndVisibilitiesMesh.put(meshPoint, new ArrayList<AbsoluteDate>());
		}
	}

	/**
	 * Default constructor Instantiates the simulation of a constellation.
	 * 
	 * @param constellation : the constellation to simulate
	 * @param t0            : the date corresponding to the beginning of the
	 *                      simulation
	 * @param tf            : the date which corresponds to the end of the
	 *                      simulation
	 * @param zone          : the zone to explore on the Earth
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
	 * This method creates all the events for eachpoint of the meshing. The events
	 * are "ElevationDetector" events. This method has to be called before
	 * propagating the orbit.
	 * 
	 * @param propagator the propagator used
	 * 
	 * @param elevation  in radian it is the elevation at which the point begins to
	 *                   be visible (90° - elevation) corresponds approximately to
	 *                   the half extent of the FOV of the satellite if it is not
	 *                   agile
	 */
	public void createEventsDetector(Propagator propagator, EventsLogger logger, double elevation) {

		double maxcheck = 60.0;
		double threshold = 0.001;

		// we go through all the points of the meshing and add them as events to be
		// detected
		for (int pointIndex = 0; pointIndex < this.zone.getListMeshingPoints().size(); pointIndex++) {

			GeodeticPoint meshPoint = this.zone.getListMeshingPoints().get(pointIndex);

			TopocentricFrame staFrame = new TopocentricFrame(Parameters.earth, meshPoint, "mesh_point_" + pointIndex);

			EventDetector staVisi = new ElevationDetector(maxcheck, threshold, staFrame)
					.withConstantElevation(elevation).withHandler(new VisibilityHandlerSilent());
			if (this.verbose)
				staVisi = new ElevationDetector(maxcheck, threshold, staFrame).withConstantElevation(elevation)
						.withHandler(new VisibilityHandlerVerbose());

			// when we add an event detector, we monitor it to be able to retrieve it
			EventDetector detector = logger.monitorDetector(staVisi);
			// when we add an event detector, we monitor it to be able to retrieve it
			propagator.addEventDetector(detector);
			eventDetPoint.put(detector, meshPoint);
			eventDetPoint.put(staVisi, meshPoint);

		}

	}

	// adaptative maxcheck
	/**
	 * WARNING for now, the orbit is assumed circular, thus the altitude of the
	 * satellite is assumed constant
	 * 
	 * This method creates all the events for eachpoint of the meshing. The events
	 * are "ElevationDetector" events. This method has to be called before
	 * propagating the orbit.
	 * 
	 * @param propagator the propagator used
	 * 
	 * @param halfFOV    in radian half FOV of the satellite
	 * 
	 * @param a          : satellite to center of the Earth distance
	 */
	public void createEventsDetectorSatellite(Propagator propagator, EventsLogger logger, double halfFOV, double a) {

		// angle between the direction from the center of the Earth to the mesh point
		// and the direction from the center of the Earth to the satellite
		double alpha = -halfFOV + Math.asin(a / Parameters.projectEarthEquatorialRadius * Math.sin(halfFOV));

		// elevation at which the point begins to be visible
		double elevation = Math.PI / 2 - (halfFOV + alpha);

		if (verbose) {
			System.out.println("halfFOV is : " + halfFOV * 180 / Math.PI + " degree");
			System.out.println("180 - halfFOV is : " + (90 - halfFOV * 180 / Math.PI) + " degree");
			System.out.println("computed elevation is : " + elevation * 180 / Math.PI + " degree");
		}

		// time window when the satellite is seen by the station (assuming the orbit is
		// passing over the station)
		double visibilityWindow = (alpha / Math.PI) * 2 * Math.PI
				* Math.sqrt(Math.pow(a, 3) / Parameters.projectEarthMu);

		// adaptative maxcheck : increases with altitude >> the higher the maxcheck, the
		// faster the algorithm (the higher the probability of missing a satellite pass)
		double maxcheck = visibilityWindow / 3; // the division factor to tune the frequency at which each detector
												// checks if a satellite is passing over the mesh point
		// double maxcheck = 60;
		if (verbose) {
			System.out.println("Maxcheck is : " + maxcheck);
		}

		double threshold = 0.01;// accuracy of 0.01 s

		// we go through all the points of the meshing and add them as events to be
		// detected
		for (int pointIndex = 0; pointIndex < this.zone.getListMeshingPoints().size(); pointIndex++) {

			GeodeticPoint meshPoint = this.zone.getListMeshingPoints().get(pointIndex);

			TopocentricFrame staFrame = new TopocentricFrame(Parameters.earth, meshPoint, "mesh_point_" + pointIndex);

			EventDetector staVisi = new ElevationDetector(maxcheck, threshold, staFrame)
					.withConstantElevation(elevation).withHandler(new VisibilityHandlerSilent());
			if (this.verbose)
				staVisi = new ElevationDetector(maxcheck, threshold, staFrame).withConstantElevation(elevation)
						.withHandler(new VisibilityHandlerVerbose());

			// when we add an event detector, we monitor it to be able to retrieve it
			EventDetector detector = logger.monitorDetector(staVisi);
			// when we add an event detector, we monitor it to be able to retrieve it
			propagator.addEventDetector(detector);
			eventDetPoint.put(detector, meshPoint);
			eventDetPoint.put(staVisi, meshPoint);
		}

	}

	/**

	 * This method computes the propagation of all satellites of the constellation and 
	 * detect when each point of the mesh is seen by a satellite.
	 * 
	 * The dates at which each point of the mesh enters and exits the satellite view
	 * are stored in the corresponding HashMaps.
	 */
	public void propagateOrbits() {

		for (Satellite sat : constellation.getSatellitesList()) {

			// The logger is different for each satellite
			EventsLogger logger = new EventsLogger();

			// Definition of the propagator of the satellite trajectory
			KeplerianPropagator propagator = sat.getPropagator();

			// Addition of all event detectors : adaptative maxcheck version
			createEventsDetectorSatellite(propagator, logger, Parameters.halfFOV, sat.getA());
			// createEventsDetector(propagator, logger, Parameters.elevation);

			// Propagation of the orbit of the satellite
			propagator.propagate(this.t0, this.tf);

			// For each detector (associated to a point of the mesh) if the point is viewed
			// by the satellite at the beginning of the simulation, it must be stored.
			for (EventDetector detector : propagator.getEventsDetectors()) {
				if (detector.g(propagator.getInitialState()) > 0) {
					GeodeticPoint meshPoint = eventDetPoint.get(detector);
					addPointAndDateListBegVisibilitiesMesh(meshPoint, this.t0);
					if (this.verbose)
						System.out.println(
								"g>0 en debut de simulation pour " + sat.toString() + "   " + meshPoint.toString());
				}
			}

			// Clear of the events detectors on the propagator (ESSENTIAL TO HAVE RELEVANT
			// RESULTS !)
			propagator.clearEventsDetectors();

			for (final EventsLogger.LoggedEvent event : logger.getLoggedEvents()) {

				EventDetector detector = event.getEventDetector();
				GeodeticPoint meshPoint = eventDetPoint.get(detector);
				AbsoluteDate date = event.getState().getDate();

				// The method isIncreasing returns a boolean which states
				// whether the satellite is entering or exiting the elevation zone
				if (event.isIncreasing())
					addPointAndDateListBegVisibilitiesMesh(meshPoint, date);
				else
					addPointAndDateListEndVisibilitiesMesh(meshPoint, date);
			}
		}
	}

	public double getMaxRevisitPoint(GeodeticPoint meshPoint) {

		double maxRevisit = -1;

		// a beginning of visibilty is seen as "(" and an end is seen as ")"
		// nbParenthis is the current number of "(" minus the number of ")".
		// It should always be positive.
		int nbParenthesis = 0;

		AbsoluteDate endDate = t0;

		ArrayList<AbsoluteDate> begDates = listBegVisibilitiesMesh.get(meshPoint);

		// Dans le cas ou le point n'est jamais vu : Double.MAX_VALUE
		if (begDates.isEmpty())
			return Double.MAX_VALUE;

		ArrayList<AbsoluteDate> endDates = listEndVisibilitiesMesh.get(meshPoint);

		Collections.sort(endDates);
		Collections.sort(begDates);

		while (!begDates.isEmpty() && !endDates.isEmpty()) {

			// If the next event is a beginning of view
			if (begDates.get(0).compareTo(endDates.get(0)) < 0) {
				AbsoluteDate date = begDates.remove(0);
				if (nbParenthesis == 0) {
					double revisit = date.durationFrom(endDate);
					if (revisit > maxRevisit)
						maxRevisit = revisit;
				}
				nbParenthesis += 1;
			}
			// If the next event is an end of view
			else {
				AbsoluteDate date = endDates.remove(0);
				nbParenthesis -= 1;
				if (nbParenthesis < 0)

					if (verbose) {
						System.out.println("ERROR:Negative number of parenthesis during");
					}

				if (nbParenthesis == 0)
					endDate = date;
			}
		}

		if (begDates.isEmpty() && !endDates.isEmpty()) {
			while (!endDates.isEmpty()) {
				AbsoluteDate date = endDates.remove(0);
				nbParenthesis -= 1;
				if (nbParenthesis < 0)

					if (verbose) {
						System.out.println("ERROR:Negative number of parenthesis end");
					}

				if (nbParenthesis == 0)
					endDate = date;
			}
			if (nbParenthesis == 0) {
				double revisit = this.tf.durationFrom(endDate);
				if (revisit > maxRevisit)
					maxRevisit = revisit;
			}
		}

		else if (nbParenthesis == 0 && !begDates.isEmpty() && endDates.isEmpty()) {
			AbsoluteDate date = begDates.remove(0);
			double revisit = date.durationFrom(endDate);
			if (revisit > maxRevisit)
				maxRevisit = revisit;
		}

		if (maxRevisit == -1) {
			System.out.println(
					"ERROR maxRevisite = -1 s for constellation : \n" + constellation + "\nand point  " + meshPoint);
			maxRevisit = Double.MAX_VALUE;
		}
		return maxRevisit;
	}

	/**
	 * This method computes the maximum time of revisit on the specified zone of the
	 * problem.
	 * 
	 * It uses the hashmaps of beginning and end of events previously computed
	 * during the propagation.
	 */
	public double getMaxRevisit() {

		double maxRevisit = -1;

		for (GeodeticPoint meshPoint : zone.getListMeshingPoints()) {

			double pointRevisit = getMaxRevisitPoint(meshPoint);
			if (pointRevisit > maxRevisit)
				maxRevisit = pointRevisit;

		}
		if (maxRevisit == -1) {
			System.out.println("ERROR maxRevisite = -1 s for constellation : \n" + constellation);
			maxRevisit = Double.MAX_VALUE;
		}

		return maxRevisit;
	}	
}

/**
 * That class is here to display when an event is detected.
 *
 */
class VisibilityHandlerVerbose implements EventHandler<ElevationDetector> {

	public Action eventOccurred(final SpacecraftState s, final ElevationDetector detector, final boolean increasing) {
		if (increasing) {
			System.out.println(
					" Visibility on " + detector.getTopocentricFrame().getName() + " begins at " + s.getDate());
			return Action.CONTINUE;
		} else {
			System.out
					.println(" Visibility on " + detector.getTopocentricFrame().getName() + " ends at " + s.getDate());
			// we need to continue the simulation once a point is seen because we may have
			// other points
			// or may want to calculate the revisit time
			return Action.CONTINUE;
		}

	}

	public SpacecraftState resetState(final ElevationDetector detector, final SpacecraftState oldState) {
		return oldState;
	}

}

/**
 * Visibility handler class.
 *
 */
class VisibilityHandlerSilent implements EventHandler<ElevationDetector> {

	public Action eventOccurred(final SpacecraftState s, final ElevationDetector detector, final boolean increasing) {
		return Action.CONTINUE;
	}

	public SpacecraftState resetState(final ElevationDetector detector, final SpacecraftState oldState) {
		return oldState;
	}

}
