package decisionVector;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;

import constellation.Constellation;
import constellation.Satellite;
import utils.Parameters;


class DecisionVectorDemoTest {
	
	ArrayList<GeodeticPoint> inputPolygon=new ArrayList<GeodeticPoint>();
	ArrayList<DecisionVariable> variableList= new ArrayList<DecisionVariable>();
	
	DecisionVariable<Double> inclination = new DecisionVariable<Double>(Double.class, "inclination", 1., 1.1);
	DecisionVariable<Double> a = new DecisionVariable<Double>(Double.class, "a", 2., 2.1);
	DecisionVariable<Double> eccentricity = new DecisionVariable<Double>(Double.class, "eccentricity", 0., 0.1);
	DecisionVariable<Double> rightAscendingNode = new DecisionVariable<Double>(Double.class, "rightAscendingNode",4., 4.1);
	DecisionVariable<Double> periapsisArgument = new DecisionVariable<Double>(Double.class, "periapsisArgument", 5.,5.1);
	DecisionVariable<Integer> nbSat = new DecisionVariable<Integer>(Integer.class, "nbSat", 2, 3);
	
	DecisionVariable<Double> testDouble = new DecisionVariable<Double>(Double.class, "testDouble", 1., 1.1);
	DecisionVariable<Integer> testInt = new DecisionVariable<Integer>(Integer.class, "testInteger", 1, 2);
	
	GeodeticPoint testPoint = new GeodeticPoint(-0.005, 44, 143);
	GeodeticPoint testPoint2 = new GeodeticPoint(0.005, 44.005, 144);
	
	

	@Test
	void testgetName() {

		// Arrange
		this.variableList.add(testDouble);
		this.variableList.add(testInt);
		
		this.inputPolygon.add(testPoint);
		this.inputPolygon.add(testPoint2);


		// Act
		DecisionVector decisionVector = new DecisionVectorDemo(this.variableList, this.inputPolygon);
		DecisionVariable<Double> testDoubleGet = decisionVector.get("testDouble");
		DecisionVariable<Integer> testIntegerGet = decisionVector.get("testInteger");

		// Assert
		assert (testDoubleGet == testDouble);
		assert (testIntegerGet == testInt);
	}

	@Test
	void testgetFromIndex() {

		// Arrange

		this.variableList.add(testDouble);
		this.variableList.add(testInt);
		
		this.inputPolygon.add(testPoint);
		this.inputPolygon.add(testPoint2);


		// Act
		DecisionVector decisionVector = new DecisionVectorDemo(variableList, inputPolygon);
		DecisionVariable<Double> testDoubleGet = decisionVector.get(0);
		DecisionVariable<Integer> testIntegerGet = decisionVector.get(1);

		// Assert
		assert (testDoubleGet == testDouble);
		assert (testIntegerGet == testInt);
	}

	@Test
	void testRandomInit() {

		// Arrange
		variableList.add(testDouble);
		variableList.add(testInt);
		
		this.inputPolygon.add(testPoint);
		this.inputPolygon.add(testPoint2);

		// Act
		DecisionVector decisionVector = new DecisionVectorDemo(variableList, inputPolygon);
		decisionVector.randomInit();
		Double testDoubleValue = (Double) decisionVector.get("testDouble").getValue();
		Integer testIntegerValue = (Integer) decisionVector.get("testInteger").getValue();

		// Assert
		assert (testDoubleValue < 1.1);
		assert (testDoubleValue > 1);
		assert (testIntegerValue == 1);
	}

	@Test
	void testGetConstellationFromVector() {

		// Arrange
		ArrayList<Object> listValues = new ArrayList<Object>();
		variableList.add(this.inclination);
		listValues.add(1.05);
		variableList.add(this.a);
		listValues.add(2.05);
		variableList.add(this.eccentricity);
		listValues.add(0.05);
		variableList.add(this.rightAscendingNode);
		listValues.add(4.05);
		variableList.add(this.periapsisArgument);
		listValues.add(5.05);
		variableList.add(this.nbSat);
		listValues.add(2);
		
		this.inputPolygon.add(testPoint);
		this.inputPolygon.add(testPoint2);

		// Act
		DecisionVector decisionVector = new DecisionVectorDemo(variableList, inputPolygon);

		Constellation constellation = decisionVector.createConstellationFromVector(listValues);

		ArrayList<Satellite> satList = constellation.getSatellitesList();

		// Assert
		assert (satList.size() == 2);

		for (Satellite sat : satList) {
			KeplerianOrbit orbit = (KeplerianOrbit) sat.getInitialOrbit();

			assert (orbit.getI() == 1.05);
			assert (orbit.getA() == 2.05);
			assert (orbit.getE() == 0.05);

			assert (orbit.getRightAscensionOfAscendingNode() == 4.05);
			assert (orbit.getPerigeeArgument() == 5.05);

			assert (orbit.getMeanAnomaly() == 0
					|| (orbit.getMeanAnomaly() > 3.1415 && orbit.getMeanAnomaly() < 3.1416));

		}

	}

    /**
     * Test the thread safety of the cost function. From 2 constellations close to the sentinel2 mission, 
     * create  nbThread*2 Threads and runs it simultaneously. Then, it checks that all every thread computed the same cost 
     * for each constellation.
     * @throws InterruptedException
     */
	@Test
	public void testThreadSafety() throws InterruptedException {

		// Arrange
		
		//values for the the decision variables of the 1st constellation
		final ArrayList<Object> listValues1 = new ArrayList<Object>();
		//values for the the decision variables of the 2nd constellation
		final ArrayList<Object> listValues2 = new ArrayList<Object>();
		
		
		variableList.add(inclination);
		listValues1.add(1.719);listValues2.add(1.7195);
		variableList.add(a);
		listValues1.add(7157000.);listValues2.add(7157005.);
		variableList.add(eccentricity);
		listValues1.add(0.);listValues2.add(0.1);
		variableList.add(rightAscendingNode);
		listValues1.add(0.);listValues2.add(0.1);
		variableList.add(periapsisArgument);
		listValues1.add(0.);listValues2.add(0.1);
		variableList.add(nbSat);
		listValues1.add(1);listValues2.add(2);
		
		//zone
		this.inputPolygon.add(testPoint);
		this.inputPolygon.add(testPoint2);

		//costs of the first constellation for each thread. 
		final HashMap<String,Double> mapCost1 = new HashMap<String,Double>();
		//costs of the first constellation for each thread. 
		final HashMap<String,Double> mapCost2 = new HashMap<String,Double>();
		
		final DecisionVector decisionVector = new DecisionVectorDemo(variableList, inputPolygon);
		
		//load Orekit Data
		File orekitData = new File(Parameters.orekitDataPath);
		DataProvidersManager manager = DataProvidersManager.getInstance();
		manager.addProvider(new DirectoryCrawler(orekitData));
		
		// list of all the threads
		ArrayList<Thread> listThread=new ArrayList<Thread>();
		
		
		//To decrease test duration, change the number of thread tested (the number is x2, for one thread by constellation
		Integer threadNb=3;
		
		
		//configure every thread
		for (Integer i=0;i<threadNb;i++) {
			final String index=i.toString();
			
			//Thread for the first constellation
			Thread first = new Thread(new Runnable() {
				@Override
				public void run() {
					mapCost1.put(index,decisionVector.costFunction(listValues1));
					
				}
			});
			
			//Thread for the second constellation
			Thread second = new Thread(new Runnable() {
				@Override
				public void run() {
					mapCost2.put(index,decisionVector.costFunction(listValues2));
					
				}
			});
			
			listThread.add(first);
			listThread.add(second);
		}
		
		//Start all threads simultaneously
		for (Thread thread:listThread) {
			thread.start();
		}
		for (Thread thread:listThread) {
			thread.join();
		}
		
		//Check that all the costs are the same for each constellation.
		Double cost1=mapCost1.get("0");
		Double cost2=mapCost2.get("0");
		
	
		for (String key : mapCost1.keySet()) {
			assertEquals(cost1, mapCost1.get(key));
		}
		for (String key : mapCost2.keySet()) {
			assertEquals(cost2, mapCost2.get(key));
		}
		System.out.println("Cost1: "+mapCost1);
		System.out.println("Cost2: "+mapCost2);
	}
	
}
