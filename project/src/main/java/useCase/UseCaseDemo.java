package useCase;

import java.util.ArrayList;

import constellation.Constellation;
import decisionVector.DecisionVectorDemo;
import io.jenetics.DoubleChromosome;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import optimisation.Optimisation;
import utils.Parameters;

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

@SuppressWarnings({ "rawtypes", "unchecked" })
public class UseCaseDemo extends UseCase {
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
		DecisionVectorDemo decisionVectorDemo = new DecisionVectorDemo(this.variablesList, this.inputPolygon);
		// decisionVectorDemo.get("nbSat").setValue(2);
		// decisionVectorDemo.randomInit();

		Optimisation optimisationProblem = new Optimisation(decisionVectorDemo);
		final Engine engine = Engine.builder(UseCaseDemo::fitnessDemo, optimisationProblem.getCode())
				.optimize(Optimize.MINIMUM).populationSize(2) // Small value for tests
				.build();

		final Phenotype bestConstellation = (Phenotype) engine.stream().limit(2) // Small value for tests
				.collect(EvolutionResult.toBestPhenotype());

		// Best constellation found
		System.out.println(bestConstellation);

		// get the values of the decision vector
		ArrayList<Object> optimisedValues = new ArrayList<Object>();
		for (int i = 0; i < bestConstellation.genotype().length(); i++) {
			if (decisionVectorDemo.get(i).isDouble()) {
				DoubleChromosome dc = (DoubleChromosome) bestConstellation.genotype().get(i);
				optimisedValues.add((Double) dc.doubleValue());
			}
			if (decisionVectorDemo.get(i).isInteger()) {
				IntegerChromosome ic = (IntegerChromosome) bestConstellation.genotype().get(i);
				optimisedValues.add((Integer) ic.intValue());
			}
		}

		// compute objective function (the objective function is thread safe, therefore
		// we have to pass the values as an argument of the function
		System.out.println("Max revisit time: " + decisionVectorDemo.costFunction(optimisedValues));

		// get and return the constellation from the decision vector
		return (decisionVectorDemo.createConstellationFromVector(optimisedValues));

	}

	/**
	 * Fitness function for the Demo case
	 * 
	 * @param currentGenotype: Genotype Set of values to be evaluated
	 * @return cost: double Cost of the set of values
	 */
	private static double fitnessDemo(final Genotype currentGenotype) { // Use decisionVector1

		// compute the Objective Function from a sentinel constellation
		System.out.println("\n---- COMPUTE OBJECTIVE FUNCTION -----");

		/*
		 * TO DO: Find a way to get the initial decision vector without creating it
		 * again. Problem is that the keyword "this" cannot be used in static method
		 */
		UseCaseDemo useCaseDemo = new UseCaseDemo();
		useCaseDemo.loadParams(Parameters.inputPath + "DemoSentinel2.json");
		DecisionVectorDemo decisionVectorDemo = new DecisionVectorDemo(useCaseDemo.variablesList,
				useCaseDemo.inputPolygon);

		/*
		 * Values from the genotype are converted into an ArrayList<Object>, so we can
		 * use the cost function defined in the decision vector.
		 */
		ArrayList<Object> listValues = new ArrayList<Object>();
		for (int i = 0; i < currentGenotype.geneCount(); i++) {
			if (decisionVectorDemo.get(i).isDouble()) {
				DoubleChromosome doubleChr = (DoubleChromosome) currentGenotype.get(i);
				listValues.add((Double) doubleChr.doubleValue());
			}
			if (decisionVectorDemo.get(i).isInteger()) {
				IntegerChromosome intChr = (IntegerChromosome) currentGenotype.get(i);
				listValues.add((Integer) intChr.intValue());
			}
		}
		System.out.print("EVALUATION OF THE GENOTYPE :" + listValues + "\n");

		double cost = decisionVectorDemo.costFunction(listValues);
		return cost;
	}

}
