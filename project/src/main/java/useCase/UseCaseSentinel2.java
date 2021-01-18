package useCase;

import java.util.ArrayList;

import constellation.Constellation;
import decisionVector.DecisionVector;
import decisionVector.DecisionVector1;
import decisionVector.DecisionVectorDemo;

/**
 * 
 * <p>
 * <b>This class extends the UseCase for a demonstration of the simulation </b>
 * </p>
 *
 * <p>
 * In this case, we load a constellation with the characteristics of the
 * sentinel2 mission. Optimize constellation computes only the costFunction of
 * this constellation.
 * </p>
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

public class UseCaseSentinel2 extends UseCase {
	/**
	 * Instantiate randomly a decision vector and compute the cost function for a
	 * demonstration of the simulation
	 * 
	 * @return Constellation - The constellation randomly defined.
	 */
	@Override
	public Constellation optimizeConstellation() {

		/** initialize randomly the demo decision vector */
		System.out.println("\n---- INITIALIZE DECISION VECTOR -----");
		DecisionVector decisionVectorS2 = new DecisionVectorDemo(this.variablesList, this.inputPolygon);
		// decisionVectorDemo.get("nbSat").setValue(2);
		// decisionVectorDemo.randomInit();

		// compute the Objective Function from a sentinel constellation
		System.out.println("\n---- COMPUTE OBJECTIVE FUNCTION -----");
		// get the values of the decision vector

		ArrayList<Object> values = decisionVectorS2.getValues();
		// compute objective function (the objective function is thread safe, therefore
		// we have
		// to pass the values as an argument of the function
		System.out.println("Max revisit time: " + decisionVectorS2.costFunction(values));

		// get and return the constellation from the decision vector
		return (decisionVectorS2.createConstellationFromVector(values));

	}
}