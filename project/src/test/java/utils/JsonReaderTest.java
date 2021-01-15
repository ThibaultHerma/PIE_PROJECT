package utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.orekit.bodies.GeodeticPoint;

import decisionVector.DecisionVariable;
import utils.JsonReader;
import zone.Zone;


class JsonReaderTest {


	@Test
	void testGetUseCaseId() {

		//Arrange 
		JsonReader jsonReader=new JsonReader();
		//Act 
		jsonReader.read(Parameters.inputPath+"TestReader.json");
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
		jsonReader.read(Parameters.inputPath+"TestReader.json");
		String algoName=jsonReader.getAlgoName();
		System.out.println(algoName);
		//assert
		assert(algoName.equals("genetic")); 
	} 

	@Test
	void testGetDecisionVariables() {

		//Arrange 
		JsonReader jsonReader=new JsonReader();
		//Act 
		jsonReader.read(Parameters.inputPath+"TestReader.json");

		@SuppressWarnings("rawtypes")
		ArrayList<DecisionVariable> decisionVariables = jsonReader.getDecisionVariables();

		//assert
		assert((Double)decisionVariables.get(0).getMin()==0.0); 
		assert((Double)decisionVariables.get(0).getMax()==90.0);

		assert((Integer)decisionVariables.get(1).getMin()==1); 
		assert((Integer)decisionVariables.get(1).getMax()==20);


	}

	@Test
	void testGetZone() {

		//Arrange 
		JsonReader jsonReader=new JsonReader();
		//Act 
		jsonReader.read(Parameters.inputPath+"TestReader.json");
		ArrayList<GeodeticPoint> inputPolygon =jsonReader.getZone();

		//Assert
		
		assert(inputPolygon.get(0).getLatitude()==40./180*Math.PI);
		assert(inputPolygon.get(0).getLongitude()==5./180*Math.PI);
		assert(inputPolygon.get(0).getAltitude()==143);
		assert(inputPolygon.get(1).getLatitude()==30./180*Math.PI);
		assert(inputPolygon.get(1).getLongitude()==2./180*Math.PI);
		assert(inputPolygon.get(1).getAltitude()==Parameters.projectEarthEquatorialRadius);
		assert(Parameters.meshingStyle.equals("lat_lon_standard_meshing"));

	} 

	@Test
	void testGetStopParameters() {

		//Arrange  
		JsonReader jsonReader=new JsonReader(); 
		//Act 
		jsonReader.read(Parameters.inputPath+"TestReader.json");
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


