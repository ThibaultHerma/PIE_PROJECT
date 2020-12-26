package useCase;

import java.util.ArrayList;



import constellation.Constellation;
import decisionVector.DecisionVector;
import decisionVector.DecisionVector1;


/**
 * 
 *<p><b>This class extends the UseCase for the first Use Case </b></p>
 *
 *<p> In this case, we consider that all the satellites are on a same orbital plane (meaning that 
 * a,e,i,raan,periapsis argument are the same for every satellite).</p>
 * The decision variables are  the following: nb_sat,a,e,i,raan,periapsis argument.
 * The cost  function is the following : Max revisit  time of a zone.
 * 
 * <p>The class is the main class of the program. It loads the input, instantiates a decision vector,
 *  optimizes the type of constellation given, and export the best constellation found.  </p>
 * 
 * <p>These are the guidelines to follow to use this class :<br>
 * - Instantiate a UseCase corresponding to the use case number that you want to solve.<br>
 * - Call the method loadParams. <br>
 * - Call the method optimizeConstellation<br>
 * - Eventually export the constellation with the method exportConstellation()</p>
 * 
 * @author Theo Nguyen
 */
//TODO create the test

public class UseCase1 extends UseCase{
	
	/**
	 * Optimize a constellation with several satellites on the same orbital plane.
	 * @return Constellation - The constellation optimized. 
	 */
	@Override
	public  Constellation optimizeConstellation() {
		
		
	    /** initialize randomly the demo decision vector */
	    System.out.println("\n---- INITIALIZE DECISION VECTOR -----");
	    
	    
	    DecisionVector decisionVector1=new DecisionVector1(this.variablesList,this.inputPolygon);
	    //decisionVectorDemo.get("nbSat").setValue(2);
	    //decisionVectorDemo.randomInit();
	    
	    
	    /** TODO create an optimization instance to optimize the vector.*/
	    
	    
	   /** -----------BLOC TO PUT IN THE OPTIMIZATION CLASS---------------
	    //compute the Objective Function 
	    System.out.println("\n---- COMPUTE OBJECTIVE FUNCTION -----");
	    //get the values of the decision vector
	    ArrayList<Object>values=decisionVector1.getValues();
	    //compute objective function (the objective function is thread safe, therefore we have 
	    //to pass the values as an argument of the function
	    System.out.println("Max revisit time: " +decisionVector1.costFunction(values));
	   ---------------------------------------------------------------------*/
	    
	    
	    //get and return the constellation from the decision vector
		//return (decisionVector1.createConstellationFromVector(values));
		
	    return null;
	    

		}
	}

