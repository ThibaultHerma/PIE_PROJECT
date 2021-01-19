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
	 * Check if the minimum and maximum values of the variables (for now, only the
	 * minimum value of the semi-major axis and the minimum value of the
	 * inclination) are compatible with the zone to be observed. If they are not
	 * compatible, they should be modified as the algorithm loses time to find
	 * solutions when it is not necessary.
	 */
	public void checkOptimisationBoundariesVariables() {

		/*
		 * We look for the maximum absolute value of the latitude for all the points in
		 * the input polygon. We do not look for the minimum as if the maximum is
		 * reached, the minimum is also reached.
		 */
		double maxLatitude = Math.abs(inputPolygon.get(0).getLatitude());

		for (int k = 1; k < inputPolygon.size(); k++) {
			if (Math.abs(inputPolygon.get(k).getLatitude()) > maxLatitude) {
				maxLatitude = Math.abs(inputPolygon.get(k).getLatitude());
			}
		}

		/*
		 * We will check whether the parameters of inclination, semi-major axis and
		 * Field Of View are compatible with the minimum and maximum latitude or not. We
		 * check that the combination of the minimum semi-major axis and minimum
		 * inclination are compatible with the maximum latitude. We also check that the
		 * combination of the minimum semi-major axis and maximum inclination are
		 * compatible with the maximum latitude. Compatible means here that the zone at
		 * the given latitude is visible.
		 */

		DecisionVariable variableSemiMajorAxis = null;
		DecisionVariable variableInclination = null;
		for (int k = 0; k < variablesList.size(); k++) {
			if (variablesList.get(k).getName().equals("a")) {
				variableSemiMajorAxis = variablesList.get(k);
			} else if (variablesList.get(k).getName().equals("inclination")) {
				variableInclination = variablesList.get(k);
			}
		}
		if (variableSemiMajorAxis == null) {
			System.out.println("WARNING : Decision variable of semi-major axis not found.");
		}
		if (variableInclination == null) {
			System.out.println("WARNING : Decision variable of inclination not found.");
		}

		double minSemiMajorAxis = (Double) variableSemiMajorAxis.getMin();
		double minInclination = (Double) variableInclination.getMin();
		double maxInclination = (Double) variableInclination.getMax();

		/*
		 * We check whether the satellite can see the maximum longitude with the minimum
		 * semi-major axis and the minimum and maximum inclination or not.
		 */

		/*
		 * beta is the angle between : - the intersection of the surface of the Earth
		 * and the axis [center of satellite - center of the Earth] - the center of the
		 * Earth - the intersection of the surface of the Earth and the FOV of the
		 * satellite
		 * 
		 * It is somehow the FOV of the satellite seen from the center of the Earth.
		 */

		double beta = (minSemiMajorAxis - Parameters.projectEarthEquatorialRadius);
		beta = beta * Math.tan(Parameters.halfFOV);
		beta = beta / Parameters.projectEarthEquatorialRadius;
		beta = Math.atan(beta);

		if (maxInclination - beta > maxLatitude) {
			System.out.println("When the satellite has the combination of the minimum semi-major axis "
					+ "and maximum inclination, it is not able to see the maximum latitude of the points "
					+ "of the zone to be observed (the satellite is 'too high in latitude'). ");
			System.out.println("This means that the range of the input parameters could be "
					+ "improved by reducing the maximum inclination or increasing the minimum semi-major axis.");
		}
		if (minInclination + beta < maxLatitude) {
			System.out.println("When the satellite has the combination of the minimum semi-major axis "
					+ "and minimum inclination, it is not able to see the maximum latitude of the points "
					+ "of the zone to be observed (the satellite is 'too low in latitude'). ");
			System.out.println("This means that the range of the input parameters could be "
					+ "improved by increasing the minimum inclination or increasing the minimum semi-major axis..");
		}
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

		// check whether the boundaries of the variables could be optimised or not
		useCase.checkOptimisationBoundariesVariables();

		// duration of the program : TOP
		long startTime = System.nanoTime();

		Constellation bestConstellation = useCase.optimizeConstellation();

		// duration of the program : END
		long endTime = System.nanoTime();
		long totalTime = endTime - startTime;

		System.out.println("run time : " + totalTime / Math.pow(10, 9));
		System.out.println("best constellation : " + bestConstellation);

		useCase.exportConstellation(bestConstellation);

	}

}
