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
 *---------------------------------------------------------------------------------------------------
 *---THIS CLASS EXTENDS THE CLASS DECISION VECTOR FOR A DEMONSTRATION BASED OF  THE USE CASE --------
 *---------------------------------------------------------------------------------------------------
 * In this case, we consider that all the satellites are on a same orbital plane (meaning that 
 * a,e,i,raan,periapsis argument are the same for every satellite).
 * The decision variables are  the following: nb_sat,a,e,i,raan,periapsis argument.
 * The cost  function is the following : Max revisit  time of a zone.
 *---------------------------------------------------------------------------------------------------
 * * The class is a decision vector for the optimization problem. It contains the variables 
 * to optimize and also their variation domain. The class is also able to compute the fitness 
 * (or objective) function of the vector. To do so, it creates a constellation and a simulation from 
 * the decision vector and it calls the correct objective function in the simulation.  
 * 
 * These are the guidelines to follow to use this class :
 *  
 * - Instantiate a new Decision Vector with the given constructor. It will randomly initialize
 * all the variables of the vector
 * - Call the method objectiveFunction to launch a simulation from the current state of the vector
 * 
 * WARNING : The cost function has to be thread Safe to allow multithread computing from the optimization 
 * library
 * TODO Ensure that the method objectiveFunction is thread safe.
 * TODO Write the tests
 *
 * @author Theo N
 */


@SuppressWarnings("rawtypes") 
public  class DecisionVectorDemo extends DecisionVector{

	public DecisionVectorDemo(ArrayList<DecisionVariable> decisionVariableList,ArrayList<GeodeticPoint>inputPolygon) {
		super(decisionVariableList,inputPolygon);
	}

	/**
	 * Implementation of the Abstract method which converts the current state of the  decision vector into
	 * a Constellation. 
	 * In this case, it takes the values of the the 5 Keplerian parameters of the plane and distributes
	 * uniformly the satellites along the orbit in changing the mean anomaly 
	 * @return a Constellation corresponding to the decision vector.
	 */
	@Override
	public Constellation createConstellationFromVector() {

		Double inclination=(Double) get("inclination").getValue();
		Double a=(Double) get("a").getValue();
		Double eccentricity= (Double) get("eccentricity").getValue();
		Double rightAscendingNode=(Double) get("rightAscendingNode").getValue();
		Double periapsisArgument=(Double) get("periapsisArgument").getValue();
		Integer nbSat= (Integer) get("nbSat").getValue();
		AbsoluteDate t0= Parameters.t0;


		//TODO WAITING TO BE ABLE TO INSTANTIATE A CONSTELLATION

		//add the satellite to the constellation
		for(Double i=0.0;i<nbSat;i++) {
			// We consider for the demonstration that the satellites are uniformly distributed along the orbit
			Double anomaly=i/nbSat *2* Math.PI;
			//constellation.addSatellite(a,eccentricity,inclination,rightAscNode,periapsisArgument,anomaly, t0)
		}

		//return constellation
		return null;

	}




	/**
	 * The objective (or fitness) function of the problem. The goal is to MINIMIZE this function.
	 * It calls the method createConstellationFromVector to create a new constellation with the current
	 * state of the vector and then creates a simulation to propagate the orbits of each satellite.
	 * 
	 * @return Double the fitness value
	 */
	@Override
	public  Double objectiveFunction() {

		//create the constellation from the current decision Vector
		Constellation constellation=createConstellationFromVector();

		// Begging of the simulation
		AbsoluteDate t0= Parameters.t0;
		// End date of the simulation
		AbsoluteDate tf= new AbsoluteDate(t0, Parameters.simulationDuration);
		// create the zone to cover
		Zone zone =new Zone(this.inputPolygon);


		//TODO WAITING TO BE ABLE TO INSTANTIATE A CONSTELLATION
		//Simulation simulation=new Simulation(constellation,t0,tf,zone);
		//simulation.propagateOrbits();
		//Double maxRevisit=simulation.getMaxRevisit();
		//return maxRevisit;
		return null;
	}

}
