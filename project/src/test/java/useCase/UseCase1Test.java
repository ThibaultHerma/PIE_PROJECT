package useCase;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import decisionVector.DecisionVariable;
import utils.Parameters;

import useCase.UseCase1;

class UseCase1Test {

	@Test
	/**
	 * The following test is about the function modifyVariablesList.
	 */
	void testModifyVariablesList() {
		
		UseCase1 useCase1 = new UseCase1();
		useCase1.loadParams(Parameters.inputPath + "1.json");
		
		// as in the function to test, we need to find the value of some parameters :
		// the decision variables anomaly and nbSat
		DecisionVariable decVarAnomaly = null;
		DecisionVariable decVarNbSat = null;

		// we go through the list of variables to find the two decision variables
		for (int i = 0; i < useCase1.variablesList.size(); i++) {

			if (useCase1.variablesList.get(i).getName().equals("anomaly")) {
				// we need to remove that element as it will be replaced by a lot
				// of different anomalies, one for every potential satellite
				decVarAnomaly = useCase1.variablesList.get(i);
			}

			else if (useCase1.variablesList.get(i).getName().equals("nbSat")) {
				decVarNbSat = useCase1.variablesList.get(i);
			}
		}
		
		useCase1.modifyVariablesList();

		// first of all we find the maximum number of satellites
		Integer nbSatMax = (Integer) decVarNbSat.getMax();
		// then the range of the values of the anomaly
		Double minAnomaly = (Double) decVarAnomaly.getMin();
		Double maxAnomaly = (Double) decVarAnomaly.getMax();
		
		// that counter will help us verify that there is the correct
		// number of anomaly variables added
		Integer counterAnomaly = 0;

		// we will check that every value of anomaly added has the correct
		// values for the min and the max
		for (Integer i = 0; i < useCase1.variablesList.size(); i++) {
			
			DecisionVariable decVar = useCase1.variablesList.get(i);
			String nameVar = decVar.getName();
			
			if (nameVar.contains("anomaly")) {
				assert(decVar.getMin().equals(minAnomaly));
				assert(decVar.getMax().equals(maxAnomaly));
				
				counterAnomaly += 1;
			}
		}
		
		// we need to subtract one as the number of satellites specified in
		// the variable nbSat is a boundary which is excluded
		assert(counterAnomaly.equals(nbSatMax-1));
	}

}
