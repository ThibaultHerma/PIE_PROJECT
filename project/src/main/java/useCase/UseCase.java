package useCase;

import java.io.File;
import java.util.ArrayList;

import org.orekit.bodies.GeodeticPoint;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;

import constellation.Constellation;
import decisionVector.DecisionVariable;

import utils.JsonReader;
import utils.Parameters;

/**
 * <p>
 * The class is the main class of the program. It loads the input, instantiates
 * a decision vector, optimizes the type of constellation given, and export the
 * best constellation found.
 * </p>
 * 
 * <p>
 * The optimization depend of the use case since the following characteristics
 * can change:<br>
 * - The type of constellation <br>
 * - The decision variables of the decision vector<br>
 * - The optimization algorithm<br>
 * Therefore, the function optimizeConstellation is implemented in subclasses.
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
//TODO create tests
@SuppressWarnings("rawtypes")
public abstract class UseCase {

	/** the zone to cover */
	protected ArrayList<GeodeticPoint> inputPolygon;

	/**
	 * A list of decision variables from the Json. Please note that depending of the
	 * use case, it will not necessarily be the decision vector.The function
	 * optimizeConstellation will create the appropriate decision vector from this
	 * list.
	 */
	protected ArrayList<DecisionVariable> variablesList;

	/**
	 * Load Orekit data, read the JSON input file, load the Zone and the list of
	 * variables.
	 * 
	 * @param inputFile:String The path of the JSON file with the input parameters
	 */
	public void loadParams(String inputFile) {

		/* load Orekit data */
		File orekitData = new File(Parameters.orekitDataPath);
		DataProvidersManager manager = DataProvidersManager.getInstance();
		manager.addProvider(new DirectoryCrawler(orekitData));

		/** read the JSON in input */
		JsonReader jsonReader = new JsonReader();
		jsonReader.read(inputFile);

		/** get the Zone to cover */
		System.out.println("\n---- GET THE ZONE TO COVER -----");
		this.inputPolygon = jsonReader.getZone();
		System.out.println("Meshing Style: " + Parameters.meshingStyle);

		/** get the decision variables specifications in the JSON **/
		this.variablesList = jsonReader.getDecisionVariables();

	}

	/**
	 * Export the constellation in a JSON file.
	 * 
	 * @param constellation:Constellation The constellation to export
	 */
	public void exportConstellation(Constellation constellation) {
		// TODO write the function

	}

	/**
	 * Optimize the type of constellation given in input. Since it depends of the
	 * use case (type of constellation,type of optimizer,type of decision vector),
	 * the function is implemented in subclasses.
	 * 
	 * @return Constellation - The constellation optimized.
	 */
	public abstract Constellation optimizeConstellation();

	/**
	 * Main function of the program. Depending of the Use Case specified in the
	 * arguments (not yet implemented), it implements the correct use case and
	 * optimize the constellation
	 * 
	 * @param args useCaseId
	 * @throws Exception If the use case doesn't exist.
	 */
	public static void main(String[] args) throws Exception {
		// TODO load the use case number in the argument of the main
		int useCaseNb = Parameters.useCaseNb;

		UseCase useCase;
		if (useCaseNb == -2) {
			useCase = new UseCaseSentinel2();
			useCase.loadParams(Parameters.inputPath + "DemoSentinel2.json");
		} else if (useCaseNb == -1) {
			useCase = new UseCaseGalileo();
			useCase.loadParams(Parameters.inputPath + "DemoGalileo.json");
		} else if (useCaseNb == 0) {
			useCase = new UseCaseDemo();
			useCase.loadParams(Parameters.inputPath + "Demo.json");
		} else if (useCaseNb == 1) {
			useCase = new UseCase1();
			// TODO create the JSON for use case 1
			useCase.loadParams(Parameters.inputPath + "1.json");
		} else {
			throw new Exception("\"The use case specified (" + useCaseNb + ") doesn't exist");
		}
		
		//duration of the program : TOP
		long startTime = System.nanoTime();
		
		Constellation bestConstellation = useCase.optimizeConstellation();
		
		//duration of the program : END
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		
		System.out.println("run time : "+totalTime/Math.pow(10, 9)); 
		System.out.println("best constellation : "+ bestConstellation);

		useCase.exportConstellation(bestConstellation);

	}

}


