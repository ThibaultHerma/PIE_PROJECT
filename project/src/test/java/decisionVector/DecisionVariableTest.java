package decisionVector;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import utils.JsonReader;

class DecisionVariableTest  {

	@Test
	void testGetMin() {
		//Arrange 
		DecisionVariable<Double> inclination=new DecisionVariable<Double>(Double.class,"inclination",0.0,90.0);
		//Act 
	    Double min= (Double) inclination.getMin();
		//assert
		assert(min==0.0);
	}
	
	@Test
	void testGetMax() {
		//Arrange 
		DecisionVariable<Integer> nbSat=new DecisionVariable<Integer>(Integer.class,"nbSat",1,20);
		//Act 
	    Integer max= (Integer) nbSat.getMax();
		//assert
		assert(max==20);
	}
	
	@Test
	void testIsInteger() {
		//Arrange 
		DecisionVariable<Integer> nbSat=new DecisionVariable<Integer>(Integer.class,"nbSat",1,20);
		DecisionVariable<Double> inclination=new DecisionVariable<Double>(Double.class,"inclination",0.0,90.0);
		//Act 
	    Boolean testInteger=  nbSat.isInteger();
	    Boolean testDouble= inclination.isInteger();
		//assert
		assert(testInteger);
		assert(!testDouble);
		
	}
	
	@Test
	void testIsDouble() {
		//Arrange 
		DecisionVariable<Integer> nbSat=new DecisionVariable<Integer>(Integer.class,"nbSat",1,20);
		DecisionVariable<Double> inclination=new DecisionVariable<Double>(Double.class,"inclination",0.0,90.0);
		//Act 
	    Boolean testInteger=  nbSat.isDouble();
	    Boolean testDouble= inclination.isDouble();
		//assert
		assert(!testInteger);
		assert(testDouble);
		
	}

 
}
