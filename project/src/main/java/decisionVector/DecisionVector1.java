package decisionVector;

import java.util.ArrayList;

import org.orekit.bodies.GeodeticPoint;
import org.orekit.time.AbsoluteDate;

import constellation.Constellation;
import simulation.Simulation;
import utils.Parameters;
import zone.Zone;

/**
 * 
 *
 * <p>
 * <b>This class extends the decision vector for the first Use Case </b>
 * </p>
 *
 * <p>
 * In this case, we consider that all the satellites are on a same orbital plane
 * (meaning that a,e,i,raan,periapsis argument are the same for every
 * satellite).
 * </p>
 * The decision variables are the following: nb_sat,a,e,i,raan,periapsis
 * argument. The cost function is the following : Max revisit time of a zone.
 *
 * <p>
 * The class is a decision vector for the optimization problem. It contains the
 * variables to optimize and also their variation domain. The class is also able
 * to compute the fitness (or cost) function of the vector. To do so, it creates
 * a constellation and a simulation from the decision vector and it calls the
 * correct cost function in the simulation.
 * </p>
 * 
 * <p>
 * These are the guidelines to follow to use this class :
 * </p>
 * 
 * <p>
 * - Instantiate a new Decision Vector with the given constructor. It will
 * randomly initialize all the variables of the vector
 * </p>
 * <p>
 * - Call the method costFunction to launch a simulation from the current state
 * of the vector
 * </p>
 * 
 * <p>
 * WARNING : The cost function has to be thread Safe to allow multithread
 * computing from the optimization library
 * </p>
 * 
 * TODO Write the tests
 *
 * @author Theo N
 */

@SuppressWarnings("rawtypes")
public class DecisionVector1 extends DecisionVector {

	public DecisionVector1(ArrayList<DecisionVariable> decisionVariableList, ArrayList<GeodeticPoint> inputPolygon) {
		super(decisionVariableList, inputPolygon);
	}

	/**
	 * Implementation of the Abstract method which converts the current state of the
	 * decision vector into a Constellation. In this case, it takes the values of
	 * the the 5 Keplerian parameters of the plane and distributes uniformly the
	 * satellites along the orbit in changing the mean anomaly. Threads safety : the
	 * function is reentrant because the storage is done in local variables. It
	 * calls only reentrant functions, and access the global storage with read-only
	 * operations.
	 * 
	 * @param values:ArrayList(Object) current values of the vector from which we
	 *                                 create the constellation.
	 * @return Constellation - a Constellation corresponding to the decision vector.
	 */
	@Override
	public Constellation createConstellationFromVector(final ArrayList<Object> values) {

		// get the values of each parameter from the index in the DecisionVector.
		// since the vector is immutable during the optimization process, it is thread
		// safe to read the index.
		Double inclination = (Double) values.get(getIndex("inclination"));
		Double a = (Double) values.get(getIndex("a"));
		Double eccentricity = (Double) values.get(getIndex("eccentricity"));
		Double rightAscendingNode = (Double) values.get(getIndex("rightAscendingNode"));
		Double periapsisArgument = (Double) values.get(getIndex("periapsisArgument"));
		Integer nbSat = (Integer) values.get(getIndex("nbSat"));
		AbsoluteDate t0 = Parameters.t0;

		Constellation constellation = new Constellation();

		// the anomaly is the only parameter which can change in the use case 1,
		// so we have one value for every satellite
		// read the anomaly and add each satellite to the constellation
		for (Integer i = 0; i < nbSat; i++) {
			String nameAnomaly = "anomaly" + Integer.toString(i);
			Double anomaly = (Double) values.get(getIndex(nameAnomaly));

			constellation.addSatellite(a, eccentricity, inclination, rightAscendingNode, periapsisArgument, anomaly,
					t0);
		}

		System.out.println("Current individual: " + constellation);

		return constellation;
	}

	/**
	 * The cost (or fitness) function of the problem. The goal is to MINIMIZE this
	 * function. It calls the method createConstellationFromVector to create a new
	 * constellation with the current state of the vector and then creates a
	 * simulation to propagate the orbits of each satellite. Threads safety : the
	 * function is reentrant because the storage is done in local variables. It
	 * calls only reentrant functions
	 * 
	 * @param listValues:ArrayList(Object) current values of the vector from which
	 *                                     we compute the fitness.
	 * @return Double - the fitness value
	 */
	@Override
	public Double costFunction(final ArrayList<Object> listValues) {

		// create the constellation from the current decision Vector
		Constellation constellation = createConstellationFromVector(listValues);

		// Begging of the simulation
		AbsoluteDate t0 = Parameters.t0;
		// End date of the simulation
		AbsoluteDate tf = new AbsoluteDate(t0, Parameters.simulationDuration);
		// create the zone to cover
		Zone zone = new Zone(this.inputPolygon);

		Simulation simulation = new Simulation(constellation, t0, tf, zone);
		simulation.propagateOrbits();
		Double maxRevisit = simulation.getMaxRevisit();
		return maxRevisit;

	}

}
