package optimisation;

import java.util.List;

import java.util.ArrayList;
import decisionVector.DecisionVariable;
import decisionVector.DecisionVector;
import io.jenetics.Chromosome;
import io.jenetics.DoubleChromosome;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;

/**
 * Optimisation class. At the moment, it is only suitable for the demo case.
 * 
 * @author Loic Mace
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Optimisation {

	private final Genotype CODE;
	private static DecisionVector decisionVector;

	// Additional parameters can be added on how to perform the simulation

	/**
	 * Constructor of the Optimisation class.
	 * 
	 * @param decisionVector: DecisionVector The decision vector of the usecase
	 * @return the optimisation instance
	 */
	public Optimisation(DecisionVector decisionVector) {

		Optimisation.decisionVector = decisionVector;

		/*
		 * listDecisionVariables was changed from protected to public for the next line.
		 * Way to kep it protected ?
		 */
		ArrayList<DecisionVariable> listDecisionVariables = decisionVector.listDecisionVariables;
		/*
		 * A list of chromosomes is created depending on their type, in the order given
		 * by the input file. They are all cast as Chromosome to allow the creation of a
		 * multi-type genotype. All chromosome sizes are set to 1.
		 */
		List<Chromosome> listChromosomes0 = new ArrayList<Chromosome>();
		for (DecisionVariable currentVariable : listDecisionVariables) {
			if (currentVariable.isDouble()) {
				listChromosomes0.add((Chromosome) DoubleChromosome.of((Double) currentVariable.getMin(),
						(Double) currentVariable.getMax(), 1));
			} else if (currentVariable.isInteger()) {
				listChromosomes0.add((Chromosome) IntegerChromosome.of((Integer) currentVariable.getMin(),
						(Integer) currentVariable.getMax(), 1));
			} else {
				// TO DO: return an error if no other type allowed
			}
		}
		/*
		 * Genotype.of cannot take a list as argument. TO DO: find a way to write the
		 * following line without adding each chromosome individually
		 */
		// this.CODE = Genotype.of(listChromosomes0);
		this.CODE = Genotype.of(listChromosomes0.remove(0), listChromosomes0.remove(0), listChromosomes0.remove(0),
				listChromosomes0.remove(0), listChromosomes0.remove(0), listChromosomes0.remove(0));
	}

	/**
	 * Optimisation method
	 * 
	 * @param decisionVector: DecisionVector Decision vector of the use case
	 * @return optimisedValues: ArrayList<Object> Values for the optimal
	 *         constellation
	 */
	public ArrayList<Object> optimise(DecisionVector decisionVector) {

		final Engine engine = Engine.builder(Optimisation::fitness, this.CODE).optimize(Optimize.MINIMUM)
				.populationSize(10) // Small value for tests
				.build();

		final Phenotype bestConstellation = (Phenotype) engine.stream().limit(5) // Small value for tests
				.collect(EvolutionResult.toBestPhenotype());

		// Best constellation found
		System.out.println(bestConstellation);

		// get the values of the decision vector
		ArrayList<Object> optimisedValues = new ArrayList<Object>();
		for (int i = 0; i < bestConstellation.genotype().length(); i++) {
			if (decisionVector.get(i).isDouble()) {
				DoubleChromosome dc = (DoubleChromosome) bestConstellation.genotype().get(i);
				optimisedValues.add((Double) dc.doubleValue());
			}
			if (decisionVector.get(i).isInteger()) {
				IntegerChromosome ic = (IntegerChromosome) bestConstellation.genotype().get(i);
				optimisedValues.add((Integer) ic.intValue());
			}
		}

		return optimisedValues;
	}

	/**
	 * Fitness function using the cost function defined in the use case
	 * 
	 * @param currentGenotype: Genotype Set of values to be evaluated
	 * @return cost: double Cost of the set of values
	 */
	private static double fitness(final Genotype currentGenotype) { // Use decisionVector1

		// compute the Objective Function from a sentinel constellation
		System.out.println("\n---- COMPUTE OBJECTIVE FUNCTION -----");

		DecisionVector decisionVector = Optimisation.decisionVector;

		/*
		 * Values from the genotype are converted into an ArrayList<Object>, so we can
		 * use the cost function defined in the decision vector.
		 */
		ArrayList<Object> listValues = new ArrayList<Object>();
		for (int i = 0; i < currentGenotype.geneCount(); i++) {
			if (decisionVector.get(i).isDouble()) {
				DoubleChromosome doubleChr = (DoubleChromosome) currentGenotype.get(i);
				listValues.add((Double) doubleChr.doubleValue());
			}
			if (decisionVector.get(i).isInteger()) {
				IntegerChromosome intChr = (IntegerChromosome) currentGenotype.get(i);
				listValues.add((Integer) intChr.intValue());
			}
		}
		System.out.print("EVALUATION OF THE GENOTYPE :" + listValues + "\n");

		double cost = decisionVector.costFunction(listValues);
		System.out.print(cost + "\n");
		return cost;
	}

	/**
	 * Get the encoding of the genotype.
	 * 
	 * @return CODE: Genotype Genotype used in the optimisation process
	 */
	public Genotype getCode() {
		return CODE;
	}

	/**
	 * Get the decision vector encoding the genotype.
	 * 
	 * @return decisionVector: DecisionVector Decision vector used to create the
	 *         genotype
	 */
	public DecisionVector getDecisionVector() {
		return decisionVector;
	}

}