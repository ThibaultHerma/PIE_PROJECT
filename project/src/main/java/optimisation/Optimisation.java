package optimisation;

import java.util.List;

import java.util.ArrayList;
import decisionVector.DecisionVariable;
import decisionVector.DecisionVector;
import decisionVector.DecisionVector1;
import decisionVector.DecisionVectorDemo;
import io.jenetics.Chromosome;
import io.jenetics.DoubleChromosome;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.Optimize;
import io.jenetics.Phenotype;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import useCase.UseCase;
import useCase.UseCase1;
import useCase.UseCaseDemo;
import utils.Parameters;

/**
 * Optimisation v0, v1 is on the way.
 * 
 * @author Loic Mace
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Optimisation {

	private final Genotype CODE;
	
	// Additional parameters can be added on how to perform the simulation

	
	/**
	 * Constructor of the Optimisation class.
	 * 
	 * @param decisionVector: DecisionVector The decision vector of the usecase
	 * @return the optimisation instance
	 */
	public Optimisation(DecisionVector decisionVector) {

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
		this.CODE = Genotype.of(listChromosomes0.remove(0), listChromosomes0.remove(0), listChromosomes0.remove(0),
				listChromosomes0.remove(0), listChromosomes0.remove(0), listChromosomes0.remove(0));
	}

	/**
	 * Get the encoding of the genotype.
	 * 
	 * @return CODE: Genotype Genotype used in the optimisation process
	 */
	public Genotype getCode() {
		return CODE;
	}

}
