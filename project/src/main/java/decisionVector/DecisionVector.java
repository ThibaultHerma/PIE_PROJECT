package decisionVector;
import java.util.ArrayList;
import java.util.HashMap;
import org.orekit.bodies.GeodeticPoint;
import constellation.Constellation;

/**
 * The class is a decision vector for the optimization problem. It contains the variables 
 * to optimize and also their variation domain. The class is also able to compute the fitness (or objective) 
 * function of the vector. To do so, it creates a constellation and a simulation from the decision vector and 
 * it calls the correct objective function in the simulation.  
 * 
 * Because the cost function and the vector depends of the use case, the method createConstellationFromVector() and 
 * costFunction() are implemented in children classes
 * 
 * WARNING : The cost function has to be thread Safe to allow multithread computing from the optimization library
 * TODO Ensure that the method objectiveFunction is thread safe.
 * These are the guidelines to follow to use this class :
 *  
 * - Instantiate a new Decision Vector with the given constructor. It will randomly initialize
 * all the variables of the vector
 * - Call the method objectiveFunction to launch a simulation from the current state of the vector
 *
 * @author Theo Nguyen
 */
@SuppressWarnings("rawtypes")
public abstract  class DecisionVector {

	/* The optimization variables on which the optimization program will play to optimize the cost function*/
	protected HashMap<String,DecisionVariable> variablesMap;

	/*The input polygon of the zone to cover (useful for the simulation in the objective function*/
	protected ArrayList<GeodeticPoint>inputPolygon;

	/*
	 * Constructor  of the class : 
	 * @Param ArrayList<DecisionVariable> decisionVariableList : The list of decision variables for the problem.
	 * @Param ArrayList<GeodeticPoint> inputPolygon : The list of Geodetic points  which forms the  zone to cover
	 */
	public DecisionVector(ArrayList<DecisionVariable> decisionVariableList,ArrayList<GeodeticPoint>inputPolygon) {


		this.variablesMap=new HashMap<String,DecisionVariable>();

		//store the decision variables in a dictionary
		for (DecisionVariable var: decisionVariableList) {
			this.variablesMap.put(var.getName(),var);
		}

		//stores the points of the polygon
		this.inputPolygon=inputPolygon;

		//initialize randomly the vector
		randomInit();
	}

	/**
	 * Get the specified decision variable in the vector. 
	 * @param String variableName : the name of the variable to get
	 * @return the decision variable 
	 */
	public DecisionVariable get(String variableName) {

		//Warning if the variable is not stored in the vector.
		if (!variablesMap.containsKey(variableName)) {
			System.out.println("ERROR  The variable : "+variableName+" doesn't exist in the decision vector");
		}
		return(variablesMap.get(variableName));

	}

	/**
	 * Initialize randomly the vector. For each decision variable, take a value in its domain.
	 */

	public void  randomInit() {
		for (String var: variablesMap.keySet()) {
			variablesMap.get(var).randomInit();
		}
	}

	/**
	 * Abstract method which converts the current state of the  decision vector into a Constellation. 
	 * Its implementation depends of the Use Case. 
	 * @return a Constellation corresponding to the decision vector.
	 */
	protected abstract Constellation createConstellationFromVector();

	/**
	 * The objective (or fitness) function of the problem. The goal is to MINIMIZE this function.
	 * It calls the method createConstellationFromVector to create a new constellation with the current
	 * state of the vector and then creates a simulation to propagate the orbits of each satellite.
	 * Its implementation depends of the Use Case. 
	 * @return Double the fitness value
	 */
	public abstract Double objectiveFunction();
}


