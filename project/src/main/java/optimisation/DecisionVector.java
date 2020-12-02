package optimisation;
import java.util.HashMap;

/**
 * The class is a decision vector for the optimization problem. It contains the variables 
 * to optimize and also their variation domain. The class is also able to compute the fitness (or objective) function
 * of the vector. To do so, it creates a constellation and a simulation from the decision vector and 
 * it calls the correct cost function in the simulation.  
 * Because the cost function and the vector depend of the use case, the method createConstellationFromVector() and 
 * costFunction() are implemented in children classes
 * WARNING : The cost function has to be thread Safe to allow multithread computing from the optimization library
 * @author Theo N
 * 
 * 
 */
public  class DecisionVector {
	
	/* The optimization variables on which the optimization program will play to optimize the cost function
	 * The Hashmap stores the current values of the variables*/
	  private HashMap<String,Double> optimizationVariable;
	  
    /* The domain of the optimization variables. It has the following syntax:
     *  HashMap with min and max value as value. "nbSat":{"min":1.0,"max":20.0} */
	  private HashMap<String, HashMap<String, Double>> optimizationDomain;
	  
	  /*
	   * Constructor of the class
	   * @Param optimizationDomain : the domain of the optimization variables.
	   */
	  public DecisionVector(HashMap<String, HashMap<String, Double>> optimizationDomain) {
		
		 
		  
	  }
	  
	  
	
	   

}
