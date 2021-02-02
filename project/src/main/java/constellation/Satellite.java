package constellation;

import java.util.ArrayList;
import java.lang.Object;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.utils.PVCoordinates;

import utils.Parameters;
import zone.Zone;

import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.utils.Constants;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;

/**
 * Class Satellite A satellite is mainly composed of the login of its plan, its
 * six keplerian parameters, the time t0 at which the data are given. It also
 * contains two ArrayList of Vectors3D
 * 
 * WARNINGS : - The time is an AbsoluteDate parameter 
 * 			  - The frame chosen is GCRF
 * 			  - Earth gravitational constant is taken from EGM96 model: 3.986004415e14
 * m�/s�. 
 * 
 * Thread Safety : the class is conditionally thread safe if and only if
 * Parameters are immutable. (see later with the method isCoveredBySat) TODO put
 * constants in Parameters class
 * 
 * @autor Amelie Falcou
 */

public class Satellite {

	private String idPlan; // always written "inclinaison_Omega"
	private double a; // semi-major axis
	private double e; // eccentricity
	private double i; // inclination
	private double raan; // rigthAscNode;
	private double w; // Periapsis Argument
	private double M; // True anomaly

	private double T; // Orbital Period
	AbsoluteDate t0; // Time

	// The first term is at t0 and the time step dt is defined and the same for all
	// the satellites
	private ArrayList<Vector3D> positions;
	private ArrayList<Vector3D> velocities;

	private KeplerianOrbit keplerian; //Orbit associated with the satellite
	private KeplerianPropagator propagator; // Propagator associated with the orbit

	/**
	 * First constructor 
	 * @param keplerian parameters a, e, i, raan, w and M
	 * @param Absolute time t0 at which the true anomaly is given 
	 * The orbital period is calculated thanks to the inputs. The Lists positions and velocities 
	 * are filled only for t = t0 and calculated with the Orekit classes "KeplerianOrbit" and "PVCoordinates".
	 */
	public Satellite(double a, double e, double i, double raan, double w, double M, AbsoluteDate t0) {
		// Keplerian parameters and time
		this.a = a;
		this.e = e;
		this.i = i;
		this.raan = raan;
		this.w = w;
		this.M = M;
		this.t0 = t0;

		// Create the plan id
		this.idPlan = String.valueOf(i) + '_' + String.valueOf(raan);
		
		// Create the keplerian orbit
		this.keplerian = new KeplerianOrbit(a, e, i, w, raan, M, PositionAngle.MEAN, FramesFactory.getGCRF(), t0,
				Constants.EGM96_EARTH_MU);
		
		// Create the ArrayLists of positions and velocities
		this.positions = new ArrayList<Vector3D>();
		this.velocities = new ArrayList<Vector3D>();
		// Compute the position and velocity for time = t0
		PVCoordinates pvCoordinates = keplerian.getPVCoordinates();
		positions.add(pvCoordinates.getPosition());
		velocities.add(pvCoordinates.getVelocity());

		// Orbital period
		this.T = keplerian.getKeplerianPeriod();
		
		this.propagator = new KeplerianPropagator(this.keplerian);

	}
	
	/**
	 * Second constructor 
	 * @param ArrayList<Vector3D> position and velocity 
	 * @param Absolute time t0 at which position and velocities are given
	 * The orbital period is calculated thanks to the inputs. The keplerian parameters are calculated 
	 * with the Orekit classes "KeplerianOrbit" and "PVCoordinates".
	 */

	public Satellite(ArrayList<Vector3D> positions, ArrayList<Vector3D> velocities, AbsoluteDate t0) {
		// Calculation of the keplerian parameters
		PVCoordinates pvCoordinates = new PVCoordinates(positions.get(0), velocities.get(0));
		this.keplerian = new KeplerianOrbit(pvCoordinates, FramesFactory.getGCRF(), t0, Constants.EGM96_EARTH_MU);
		this.a = keplerian.getA();
		this.e = keplerian.getE();
		this.i = keplerian.getI();
		this.raan = keplerian.getRightAscensionOfAscendingNode();
		this.w = keplerian.getPerigeeArgument();
		this.M = keplerian.getMeanAnomaly();

		// absolute time
		this.t0 = t0;

		// Orbital period
		this.T = keplerian.getKeplerianPeriod();

		// Create the plan id
		this.idPlan = String.valueOf(i) + '_' + String.valueOf(raan);

		// Cartesian coordinates position and velocity
		this.positions = positions;
		this.velocities = velocities;
		
		this.propagator = new KeplerianPropagator(this.keplerian);
	}

	/**
	 * @return the a-parameter - semi-major axis -  of the satellites
	 */
	public double getA() {
		return (a);
	}

	/**
	 * @return the e-parameter - eccentricity - of the satellites
	 */
	public double getE() {
		return (e);
	}

	/**
	 * @return the i-parameter - inclination - of the satellites
	 */
	public double getI() {
		return (i);
	}

	/**
	 * @return the raan-parameter - right ascending node - of the satellites
	 */
	public double getRaan() {
		return (raan);
	}

	/**
	 * @return the w-parameter - periapsis argument - of the satellites
	 */
	public double getW() {
		return (w);
	}

	/**
	 * @return the M-parameter - true anomaly - of the satellites
	 */
	public double getM() {
		return (M);
	}

	/**
	 * @return the t0-parameter of the satellites
	 */
	public AbsoluteDate getT0() {
		return (t0);
	}
	
	/**
	 * @return the propagator of the satellite
	 */
	public KeplerianPropagator getPropagator() {
		return this.propagator;
	}

	/**
	 * @return the keplerian orbit
	 */
	public Orbit getInitialOrbit() {
		return keplerian;
	}

	public boolean isCoveredBySat(Zone zone, AbsoluteDate t) {
		return true;
	}
	
	
	/**
	 * @param date the date at which the position is computed
	 * @param frame the frame in which the position is computed
	 * @return the position of the satellite in frame and date given
	 */
	public Vector3D getPosition(AbsoluteDate date, Frame frame) {
		return this.propagator.getPVCoordinates(date, frame).getPosition();
	}
	
	/**
	 * @param date the date at which the velocity is computed
	 * @param frame the frame in which the velocity is computed
	 * @return the velocity of the satellite in frame and date given
	 */
	public Vector3D getVelocity(AbsoluteDate date, Frame frame) {
		return this.propagator.getPVCoordinates(date, frame).getVelocity();
	}
	
	/**
	 * @param date the date at which the position is computed
	 * @param frame the frame in which the position is given
	 * @param earth the body shape considered
	 * @return the position (geodetic point - latitude, longitude, altitude) of the satellite at date given
	 */
	public GeodeticPoint getGeodeticPoint(AbsoluteDate date, Frame frame, BodyShape earth) {
		return earth.transform(this.getPosition(date, frame), frame, date);
	}

	/**
	 * print the satellite parameters
	 */
	public String toString() {
		return "satellite - idPlane : " + idPlan + " a : " + a + " e : " + e + " i : " + i + " raan : " + raan + " w : "
				+ w + " M : " + M;
	}

}
