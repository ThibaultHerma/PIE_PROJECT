package utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class to read a JSON file in input. It extracts the name of the use case,
 * some optimization parameters, the constraints, and a zone to watch.
 * 
 * @author PIE_CONSTELLATION
 */

public class JsonReader {

	/** to store all the data from the parsed file **/
	private JSONObject inputData;

	/**
	 * Read a JSON file and store it in inputData.
	 * 
	 * @param jsonFile : the path of the file to read.
	 */
	public void read(String jsonFile) {

		// JSON parser object to parse read file
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader(jsonFile)) {
			// Read JSON file
			Object obj = jsonParser.parse(reader);
			this.inputData = (JSONObject) obj;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print an error message in case the JSON doesn't have the correct syntax.
	 * 
	 * @param name : name of the parameter not found.
	 */
	private void printError(String name) {
		System.out.println(
				"ERROR : unable to read the field " + name + " in the input JSON file. Please check that it exists,"
						+ "that it is not empty, and that it has the correct syntax ");
	}

	/**
	 * Convert a HashMap with objects as value to HashMap with Double as value. This
	 * allows the JSON input to have Long and Double values which are then casted in
	 * Double with this function.
	 * 
	 * @param map : the original hashMap with objects
	 * @return the converted HashMap with Double
	 */
	protected HashMap<String, Double> convertToDouble(HashMap<String, Object> map) {

		HashMap<String, Double> mapDouble = new HashMap<String, Double>();
		try {
			for (String key : map.keySet()) {

				Double value;
				if (map.get(key) instanceof Long) {
					value = ((Long) map.get(key)).doubleValue();
				} else {
					value = (Double) map.get(key);
				}
				mapDouble.put(key, value);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
			System.out.println(" Input should have Long or Double values");
		}
		return mapDouble;

	}

	/**
	 * Get the use case ID of the problem.
	 * 
	 * @return use case ID
	 */
	public int getUseCaseId() {
		long useCaseId = -1;
		try {
			useCaseId = (long) inputData.get("useCaseId");

		} catch (NullPointerException e) {
			printError("useCaseId");
		}
		return (int) useCaseId;

	}

	/**
	 * get the optimization parameters of the problem. (A HashMap with the name of
	 * the algorithm and the stopParameters).
	 * 
	 * @return optimization parameters.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getOptimisationParameters() {

		HashMap<String, Object> optimisationParameters = new HashMap<String, Object>();
		try {
			optimisationParameters = (HashMap<String, Object>) inputData.get("optimisationParameters");

		} catch (NullPointerException e) {
			printError("optimisationParameters");
		}
		return optimisationParameters;
	}

	/**
	 * Get the name of the optimization algorithm to use.
	 * 
	 * @return the name of the algorithm.
	 */
	public String getAlgoName() {
		String algoName = null;
		try {
			algoName = (String) getOptimisationParameters().get("algoName");

		} catch (NullPointerException e) {
			printError("algoName");
		}
		return algoName;
	}

	/**
	 * Get the stop parameters of the optimization algorithm. The stop parameters are in
	 * a HashMap with the name of the parameter as key and max value as value.
	 * 
	 * @return stop parameters
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Double> getStopParameters() {
		HashMap<String, Object> stopParameters = new HashMap<String, Object>();// Stop parameters with eventually
																				// different types

		try {
			stopParameters = (HashMap<String, Object>) getOptimisationParameters().get("stopParameters");
		} catch (NullPointerException e) {
			printError("stopParameters");
		}
		return convertToDouble(stopParameters);
	}

	/**
	 * get the constraints of the problem, a HashMap with the parameter as key and a
	 * HashMap with min and max value as value. ex:"nbSat":{"min":1.0,"max":20.0}.
	 * 
	 * @return the constraints.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, HashMap<String, Double>> getConstraints() {

		HashMap<String, HashMap<String, Object>> constraints = new HashMap<String, HashMap<String, Object>>();
		HashMap<String, HashMap<String, Double>> constraintsDouble = new HashMap<String, HashMap<String, Double>>();
		try {

			constraints = (HashMap<String, HashMap<String, Object>>) inputData.get("constraints");

		} catch (NullPointerException e) {
			printError("constraints");

		}

		// cast all values to double
		for (String key : constraints.keySet()) {
			constraintsDouble.put(key, convertToDouble(constraints.get(key)));
		}
		return constraintsDouble;

	}
 
	/**
	 * Get the Zone to cover in the problem. The Zone is a HashMap with min and max
	 * longitude and latitude. ex: "zone":{
	 * "latMin":43.603950,"latMax":43.619355,"lonMin":1.444510,"lonMax":1.486368}
	 * 
	 * @return the zone to cover.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Double> getZone() {
		HashMap<String, Object> zone = new HashMap<String, Object>();
		try {
			zone = (HashMap<String, Object>) inputData.get("zone");
		} catch (NullPointerException e) {
			printError("zone");
		}
		return convertToDouble(zone);
	}

}
