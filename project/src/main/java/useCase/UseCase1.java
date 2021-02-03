package useCase;

import java.util.ArrayList;

import constellation.Constellation;
import decisionVector.DecisionVector;
import decisionVector.DecisionVector1;
import optimisation.Optimisation;
import decisionVector.DecisionVariable;

/**
 * 
 * <p>
 * <b>This class extends the UseCase for the first Use Case </b>
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
 * The class is the main class of the program. It loads the input, instantiates
 * a decision vector, optimizes the type of constellation given, and export the
 * best constellation found.
 * </p>
 * 
 * <p>
 * These are the guidelines to follow to use this class :<br>
 * - Instantiate a UseCase corresponding to the use case number that you want to
 * solve.<br>
 * - Call the method loadParams. <br>
 * - Call the method optimizeConstellation<br>
 * - Eventually export the constellation with the method exportConstellation()
 * </p>
 * 
 * @author Theo Nguyen
 */
//TODO create the test

public class UseCase1 extends UseCase {

	/**
	 * Optimize a constellation with several satellites on the same orbital plane.
	 * 
	 * @return Constellation - The constellation optimized.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Constellation optimizeConstellation() {

		/** initialize randomly the demo decision vector */
		System.out.println("\n---- INITIALIZE DECISION VECTOR -----");
		// we need to modify the list of variables, so as to add a variable for the
		// anomaly of every potential satellite
		modifyVariablesList();
		System.out.print(this.variablesList + "\n");

		DecisionVector decisionVector1 = new DecisionVector1(this.variablesList, this.inputPolygon);
		// decisionVectorDemo.get("nbSat").setValue(2);
		// decisionVectorDemo.randomInit();

		Optimisation optimisationProblem = new Optimisation(decisionVector1);

		int populationSize = 25;
		int generationNb = 10;

		ArrayList<Object> optimisedValues = optimisationProblem.optimize(decisionVector1, populationSize, generationNb);

		// compute objective function (the objective function is thread safe, therefore
		// we have to pass the values as an argument of the function
		System.out.println("Max revisit time: " + decisionVector1.costFunction(optimisedValues));

		// get and return the constellation from the decision vector
		return (decisionVector1.createConstellationFromVector(optimisedValues));
	}

	/**
	 * This method modifies the list of variables (variablesList) so as to add a
	 * variable of anomaly for every potential satellite. The potential number of
	 * satellites corresponds to the maximum number of satellites, given as a
	 * parameter.
	 * 
	 */
	public void modifyVariablesList() {
		// the first step is to find the decision variables anomaly and nbSat
		DecisionVariable decVarAnomaly = null;
		DecisionVariable decVarNbSat = null;

		// we go through the list of variables to find the two decision variables
		for (int i = 0; i < this.variablesList.size(); i++) {

			if (this.variablesList.get(i).getName().equals("anomaly")) {
				// we need to remove that element as it will be replaced by a lot
				// of different anomalies, one for every potential satellite
				decVarAnomaly = this.variablesList.remove(i);
			}

			else if (this.variablesList.get(i).getName().equals("nbSat")) {
				decVarNbSat = this.variablesList.get(i);
			}
		}

		// it is possible that these variables were not found : we need to display it
		if (decVarAnomaly.equals("")) {
			System.out.println("THE VARIABLE anomaly HAS NOT BEEN FOUND");
		}
		if (decVarNbSat.equals("")) {
			System.out.println("THE VARIABLE nbSat HAS NOT BEEN FOUND");
		}

		// then we have the second step :
		// we need a new decision variable for every potential satellite,
		// corresponding to its anomaly

		// first of all we find the maximum number of satellites
		Integer nbSatMax = (Integer) decVarNbSat.getMax();

		// then we add the new decision variables
		for (int sat = 0; sat < (nbSatMax - 1); sat++) {
			String newName = decVarAnomaly.getName() + Integer.toString(sat);
			DecisionVariable newDecVarAnomaly = new DecisionVariable(decVarAnomaly.getType(), newName,
					decVarAnomaly.getMin(), decVarAnomaly.getMax());
			this.variablesList.add(newDecVarAnomaly);
		}
	}
}
