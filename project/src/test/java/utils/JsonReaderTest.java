package utils;

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
		System.out.println(useCaseId);
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
		System.out.println(algoName);
		//assert
		assert(algoName.equals("genetic")); 
	}

	@Test
	void testGetConstraints() {
 
		//Arrange 
		JsonReader jsonReader=new JsonReader();
		//Act 
		jsonReader.read("input/testReader.json");
		HashMap<String,HashMap<String,Double>> constraints = jsonReader.getConstraints();
		System.out.println(constraints.get("inclination").get("max")); 
		//assert
		assert(constraints.get("inclination").get("min")==0.0);
		assert(constraints.get("inclination").get("max")==90.0);
		assert(constraints.get("nbSat").get("min")==1.0); 
		assert(constraints.get("nbSat").get("max")==20.0);
 
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
		HashMap<String,Double> stopParameters=jsonReader.getStopParameters();
		System.out.println(stopParameters.get("nbIteration"));
		//assert
		assert(stopParameters.get("nbIteration")==1000.0);
		assert(stopParameters.get("maxRunTime(s)")==3600.0);

	}
	 

	@Test
	void testConvertToDouble() {
 
		//Arrange 
		JsonReader jsonReader=new JsonReader(); 
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("testDouble", 0.0);
		map.put("testInt",(long) 1); 
		
		
		//Act 
		HashMap<String,Double> mapDouble=jsonReader.convertToDouble(map);
		System.out.println(mapDouble.get("testInt").getClass().getName());
		//assert
		assert(mapDouble.get("testDouble") instanceof Double) ;
		assert(mapDouble.get("testInt") instanceof Double);

	}


}


