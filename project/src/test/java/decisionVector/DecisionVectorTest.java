package decisionVector;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;

import constellation.Constellation;
import constellation.Satellite;
import utils.Parameters;

class DecisionVectorDemoTest {

	@Test
	void testget() {
		//Arrange
		
		DecisionVariable<Double> testDouble=new DecisionVariable<Double>(Double.class,"testDouble",1.,1.1);
		DecisionVariable<Integer> testInt=new DecisionVariable<Integer>(Integer.class,"testInteger",1,2);
		ArrayList<DecisionVariable> variableList =new ArrayList<DecisionVariable>();
		variableList.add(testDouble); variableList.add(testInt); 
		
		ArrayList<GeodeticPoint> inputPolygon =new ArrayList<GeodeticPoint>();
		GeodeticPoint testPoint =new GeodeticPoint(1,2,3);
		inputPolygon.add(testPoint);
		
		//Act 
		DecisionVector decisionVector= new DecisionVectorDemo(variableList,inputPolygon);
		DecisionVariable<Double> testDoubleGet= decisionVector.get("testDouble");
		DecisionVariable<Integer>testIntegerGet= decisionVector.get("testInteger");
		
		//Assert
		assert(testDoubleGet==testDouble);
		assert(testIntegerGet==testInt);
	}
	
	@Test
	void testRandomInit() {
		
		//Arrange
		
		DecisionVariable<Double> testDouble=new DecisionVariable<Double>(Double.class,"testDouble",1.,1.1);
		DecisionVariable<Integer> testInt=new DecisionVariable<Integer>(Integer.class,"testInteger",1,2);
		ArrayList<DecisionVariable> variableList =new ArrayList<DecisionVariable>();
		variableList.add(testDouble); variableList.add(testInt); 
		
		ArrayList<GeodeticPoint> inputPolygon =new ArrayList<GeodeticPoint>();
		GeodeticPoint testPoint =new GeodeticPoint(1,2,3);
		inputPolygon.add(testPoint);
		
		//Act 
		DecisionVector decisionVector= new DecisionVectorDemo(variableList,inputPolygon);
		decisionVector.randomInit();
		Double testDoubleValue= (Double)decisionVector.get("testDouble").getValue();
		Integer testIntegerValue= (Integer)decisionVector.get("testInteger").getValue();
		
		//Assert
		assert(testDoubleValue<1.1);
		assert(testDoubleValue>1);
		assert(testIntegerValue==1);
	}
	@Test
	void testGetConstellationFromVector() {
		
	    //Arrange
		DecisionVariable<Double> inclination=new DecisionVariable<Double>(Double.class,"inclination",1.,1.1);
		DecisionVariable<Double> a=new DecisionVariable<Double>(Double.class,"a",2.,2.1);
		DecisionVariable<Double> eccentricity=new DecisionVariable<Double>(Double.class,"eccentricity",0.,0.1);
		DecisionVariable<Double> rightAscendingNode=new DecisionVariable<Double>(Double.class,"rightAscendingNode",4.,4.1);
		DecisionVariable<Double> periapsisArgument=new DecisionVariable<Double>(Double.class,"periapsisArgument",5.,5.1); 
		DecisionVariable<Integer> nbSat=new DecisionVariable<Integer>(Integer.class,"nbSat",2,3);
		
		ArrayList<DecisionVariable> variableList =new ArrayList<DecisionVariable>();
		variableList.add(inclination); 
		variableList.add(a); 
		variableList.add(eccentricity); 
		variableList.add(rightAscendingNode); 
		variableList.add(periapsisArgument);
		variableList.add(nbSat);
		
		ArrayList<GeodeticPoint> inputPolygon =new ArrayList<GeodeticPoint>();
		GeodeticPoint testPoint =new GeodeticPoint(1,2,3);
		inputPolygon.add(testPoint);
		
		//Act 
		DecisionVector decisionVector= new DecisionVectorDemo(variableList,inputPolygon);
		Constellation constellation=decisionVector.createConstellationFromVector();
		ArrayList<Satellite> satList=constellation.getSatellitesList();
		
		//Assert
		assert(satList.size()==2);
		
		for(Satellite sat: satList) {
			KeplerianOrbit orbit=(KeplerianOrbit) sat.getInitialOrbit();
					
			assert(orbit.getI()>1 && orbit.getI()<1.1);
			assert(orbit.getA()>2 && orbit.getA()<2.1); 
			assert(orbit.getE()>0 && orbit.getE()<0.1); 
		
			assert(orbit.getRightAscensionOfAscendingNode()>4 && orbit.getRightAscensionOfAscendingNode()<4.1); 
			assert(orbit.getPerigeeArgument()>5 && orbit.getPerigeeArgument()<5.1);
			
			assert( orbit.getMeanAnomaly()==0 || (orbit.getMeanAnomaly()>3.1415 && orbit.getMeanAnomaly()<3.1416)); 
			
		}
		
	   
		
		
		
		
	
		
	}

}
