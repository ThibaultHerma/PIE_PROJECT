package test.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import utils.JsonReader;


class JsonReaderTest {


	@Test
	void testGetUseCaseId() {

		//Arrange 
		JsonReader jsonReader=new JsonReader();
		//Act 
		jsonReader.read("input/testReader.json");
		int useCaseId=jsonReader.getUseCaseId();
		//assert
		assert(useCaseId==1);

	}

	@Test
	void testGetAlgoName() {

		//Arrange 
		JsonReader jsonReader=new JsonReader();
		//Act 
		jsonReader.read("input/testReader.json");
		String algoName=jsonReader.getAlgoName();
		//assert
		assert(algoName=="genetic");
	}

	@Test
	void testGetConstraints() {

		//Arrange 
		JsonReader jsonReader=new JsonReader();
		//Act 
		jsonReader.read("input/testReader.json");
		HashMap<String,ArrayList<Double>> constraints = jsonReader.getConstraints();
		//assert
		assert(constraints.get("incination").get(0)==0.0);
		assert(constraints.get("nbSat").get(0)==1.0);
		assert(constraints.get("incination").get(1)==90.0);
		assert(constraints.get("nbSat").get(1)==20.0);

	}

	@Test
	void testGetZone() {

		//Arrange 
		JsonReader jsonReader=new JsonReader();
		//Act 
		jsonReader.read("input/testReader.json");
		HashMap<String,Double> zone=jsonReader.getZone();
		//Assert
		assert(zone.get("latMin")==43.603950);
		assert(zone.get("latMax")==43.619355);
		assert(zone.get("lonMin")==1.444510);
		assert(zone.get("lonMax")==1.486368);
	}

	@Test
	void testGetStopParameters() {

		//Arrange 
		JsonReader jsonReader=new JsonReader();
		//Act 
		jsonReader.read("input/testReader.json");
		HashMap<String,Integer> stopParameters=jsonReader.getStopParameters();
		//assert
		assert(stopParameters.get("nbIteration")==1000);
		assert(stopParameters.get("maxrunTime(s)")==3600);

	}

}


