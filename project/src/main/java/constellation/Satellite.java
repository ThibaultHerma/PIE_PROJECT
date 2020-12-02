package constellation;

import java.util.ArrayList;
import java.lang.Object;
import org.orekit.orbits.KeplerianOrbit; 
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.utils.PVCoordinates;
import org.orekit.orbits.PositionAngle;
import org.orekit.utils.Constants;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;


/**Class Satellite 
 * A satellite is mainly composed of the login of its plan, its six 
 * keplerian parameters, the time t0 at which the data are given. 
 * It also contains two ArrayList of Vectors3D 
 * 
 * WARNINGS : - The time is an AbsoluteDate parameter 
 * 			  - The frame chosen is GCRF
 * 			  - Earth gravitational constant is taken from EGM96 model: 3.986004415e14 m�/s�.
 * 
 * @autor Am�lie Falcou
 */

public class Satellite {
	
	private String idPlan; // always written "inclinaison_Omega"
	private double a; //semi-major axis
	private double e; //eccentricty
	private double i; //inclinaison
	private double raan; //rigthAscNode;
	private double w; //Periapsis Argument 
	private double M; //True anomaly
	
	private double T; //Orbital Period
	AbsoluteDate t0; //Time
	
	// The first term is at t0 and the time step dt is defined and the same for all the satellites
	private ArrayList<Vector3D> positions; 
	private ArrayList<Vector3D> velocities;
	
	/** First contructor 
	 * Input parameters : keplerian parameters and the absolute time t0 
	 * The orbital period is calculated thanks to the inputs 
	 * The Lists positions and velocities are filled only for t = t0 and calculated with 
	 * the Orekit classes "KeplerianOrbit" and "PVCoordinates". 
	 */
	public Satellite(double a, double e, double i, double raan, double w, double M, AbsoluteDate t0) {
		// Keplerian parameters and time 
		this.a = a; 
		this.e = e; 
		this.i = i; 
		this.raan= raan; 
		this.w = w;
		this.M = M; 
		this.t0 = t0;
		
		//Create the plan id 
		this.idPlan = String.valueOf(i)+'_'+String.valueOf(raan);
		
		// Create the ArrayLists of positions and velocities
		// Compute the position and velocity for time = t0
		KeplerianOrbit keplerian = new KeplerianOrbit(a, e, i, w, raan, M, PositionAngle.MEAN, FramesFactory.getGCRF(), t0, Constants.EGM96_EARTH_MU);
		PVCoordinates pvCoordinates = keplerian.getPVCoordinates();
		this.positions = new ArrayList<Vector3D>(); 
		this.velocities = new ArrayList<Vector3D>();
		positions.add(pvCoordinates.getPosition());
		velocities.add(pvCoordinates.getVelocity());
		
		//Orbital period
		this.T = keplerian.getKeplerianPeriod(); 
		
	}
	
	public Satellite(ArrayList<Vector3D> positions, ArrayList<Vector3D> velocities, AbsoluteDate t0) {
		//Calculation of the keplerian parameters 
		PVCoordinates pvCoordinates = new PVCoordinates(positions.get(0), velocities.get(0));
		KeplerianOrbit keplerian = new KeplerianOrbit(pvCoordinates, FramesFactory.getGCRF(), t0, Constants.EGM96_EARTH_MU); 
		this.a = keplerian.getA(); 
		this.e = keplerian.getE(); 
		this.i = keplerian.getI(); 
		this.raan = keplerian.getRightAscensionOfAscendingNode(); 
		this.w = keplerian.getPerigeeArgument();
		this.M = keplerian.getMeanAnomaly(); 
		
		//absolute time
		this.t0 = t0 ; 
		
		//Orbital period
		this.T = keplerian.getKeplerianPeriod(); 
		
		//Create the plan id 
		this.idPlan = String.valueOf(i)+'_'+String.valueOf(raan);
		
		// Cartesian coordinates position and velocity
		this.positions = positions; 
		this.velocities = velocities; 
	}
	
	public boolean isCoveredBySat(zone Zone, AbsoluteDate t) {
		return true;
	}
	

}
