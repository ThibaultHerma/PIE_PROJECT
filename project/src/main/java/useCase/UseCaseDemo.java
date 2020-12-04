package useCase;
import java.util.ArrayList;

import org.orekit.bodies.GeodeticPoint;

import decisionVector.DecisionVector;
import decisionVector.DecisionVectorDemo;
import decisionVector.DecisionVariable;
import utils.JsonReader;
import utils.Parameters;

//TODO Document the class 
//TODO create the super class to be able to extend for each use case
//TODO create the function loadParam()
//TODO create the test

@SuppressWarnings("rawtypes")
public class UseCaseDemo {
	
	public static void main(String[] args) {
		
		//read the JSON in input
	    JsonReader jsonReader =new JsonReader();
	    jsonReader.read("input/useCaseDemo.json");
	    
	    //get the Zone to cover
	    System.out.println("\n---- GET THE ZONE TO COVER -----");
	    ArrayList<GeodeticPoint> inputPolygon=jsonReader.getZone();
	    System.out.println("Meshing Style: " +Parameters.meshingStyle);
	    
	    //initialize randomly the demo decision vector
	    System.out.println("\n---- INITIALIZE DECISION VECTOR -----");
	    ArrayList<DecisionVariable> variableList=jsonReader.getDecisionVariables();
	    DecisionVector decisionVectorDemo=new DecisionVectorDemo(variableList,inputPolygon);
	    //decisionVectorDemo.randomInit();
	    
	   
	    //compute the Objective Function 
	    System.out.println("\n---- COMPUTE OBJECTIVE FUNCTION -----");
	    decisionVectorDemo.objectiveFunction();
	    
	    
	    
		}
	}

