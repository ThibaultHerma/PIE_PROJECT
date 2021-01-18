package decisionVector;

import java.util.ArrayList;
import org.orekit.bodies.GeodeticPoint;
import constellation.Constellation;

/**
 * <p>
 * The class is a decision vector for the optimization problem. It contains the
 * variables to optimize and also their variation domain. The class is also able
 * to compute the fitness (or cost) function of the vector. To do so, it creates
 * a constellation and a simulation from the decision vector and it calls the
 * correct cost function in the simulation.
 * </p>
 * 
 * <p>
 * Because the cost function and the vector depend of the use case, the method
 * createConstellationFromVector() and costFunction() are implemented in
 * subclasses.
 * </p>
 * 
 * <p>
 * WARNING : The cost function has to be thread Safe to allow multithread
 * computing from the optimization library
 * </p>
 * 
 * <p>
 * These are the guidelines to follow to use this class :
 * </p>
 * 
 * <p>
 * - Instantiate a new Decision Vector with the given constructor. It will
 * randomly initialize all the variables of the vector.
 * </p>
 * <p>
 * - Call the method costFunction to launch a simulation from the current state
 * of the vector
 * </p>
 *
 * @author Theo Nguyen
 */

@SuppressWarnings("rawtypes")
public abstract class DecisionVector {

	/*
	 * The optimization variables on which the optimization program will play to
	 * optimize the cost function
	 */
	protected final ArrayList<DecisionVariable> listDecisionVariables;

	/*
	 * The input polygon of the zone to cover (useful for the simulation in the cost
	 * function
	 */
	protected final ArrayList<GeodeticPoint> inputPolygon;

	/*
	 * Constructor of the class :
	 * 
	 * @Param ArrayList<DecisionVariable> decisionVariableList : The list of
	 * decision variables for the problem.
	 * 
	 * @Param ArrayList<GeodeticPoint> inputPolygon : The list of Geodetic points
	 * which forms the zone to cover
	 */
	public DecisionVector(ArrayList<DecisionVariable> listDecisionVariable, ArrayList<GeodeticPoint> inputPolygon) {

		this.listDecisionVariables = listDecisionVariable;

		// stores the points of the polygon
		this.inputPolygon = inputPolygon;

		// initialize randomly the vector
		randomInit();

		// print the decision vector

		System.out.println(this);
	}

	/**
	 * Get the specified decision variable in the vector with its index.
	 * 
	 * @param variableIndex:int the index of the variable to get
	 * @return the decision variable
	 */
	public DecisionVariable get(int variableIndex) {
		return listDecisionVariables.get(variableIndex);

	}

	/**
	 * Get the specified decision variable in the vector with its name.
	 * 
	 * @param variableName:String the name of the variable to get
	 * @return the decision variable
	 */
	public DecisionVariable get(String variableName) {

		return this.listDecisionVariables.get(getIndex(variableName));
	}

	/**
	 * Get the current decision variable values in a list in the correct order.
	 * 
	 * @return ArrayList(Object) - listValues current decision variable values
	 */
	public ArrayList<Object> getValues() {
		ArrayList<Object> listValues = new ArrayList<Object>();
		for (DecisionVariable var : listDecisionVariables) {
			if (var.isDouble()) {
				listValues.add((Double) var.getValue());
			}
			if (var.isInteger()) {
				listValues.add((Integer) var.getValue());
			}

		}
		return listValues;
	}

	/**
	 * Initialize randomly the vector. For each decision variable, take a value in
	 * its domain.
	 */

	public void randomInit() {
		for (DecisionVariable var : listDecisionVariables) {
			var.randomInit();
		}
		System.out.println("Decision Vector initialized randomly");
	}

	/**
	 * Get the index of a variable in the decision Vector. Threads safety : the
	 * function is reentrant because access to the storage is done by atomic
	 * operations since the functions use read-only operations.
	 * 
	 * @param variableName:String the name of the decision variable
	 * @return int - the index of the variable
	 */
	public int getIndex(String variableName) {

		for (int i = 0; i < listDecisionVariables.size(); i++) {
			if (listDecisionVariables.get(i).getName().equals(variableName)) {
				return (i);
			}
		}
		System.out.println("ERROR  The variable : " + variableName + " doesn't exist in the decision vector");
		return -1;
	}

	/**
	 * Convert the decision vector to String.
	 * 
	 * @return String - the string of the decision vector
	 */
	public String toString() {
		String stringVector = "\n";
		for (DecisionVariable var : listDecisionVariables) {
			stringVector += "\n" + var;
		}
		return (stringVector);
	}

	/**
	 * get the size of the vector
	 * 
	 * @return int - the number of decision variable in the vector
	 */
	public int size() {
		return listDecisionVariables.size();
	}

	/**
	 * Abstract method which converts the current state of the decision vector into
	 * a Constellation. Its implementation depends of the Use Case.
	 * 
	 * @param listValues:ArrayList(Object) current values of the vector from which
	 *                                     we create the constellation.
	 * @return Constellation - A Constellation corresponding to the decision vector.
	 */
	public abstract Constellation createConstellationFromVector(final ArrayList<Object> listValues);

	/**
	 * The cost (or fitness) function of the problem. The goal is to MINIMIZE this
	 * function. It calls the method createConstellationFromVector to create a new
	 * constellation with the current state of the vector and then creates a
	 * simulation to propagate the orbits of each satellite. Its implementation
	 * depends of the Use Case.
	 * 
	 * @param listValues:ArrayList(Object) current values of the vector from which
	 *                                     we compute the fitness.
	 * @return Double - the fitness value
	 */
	public abstract Double costFunction(final ArrayList<Object> listValues);
}
