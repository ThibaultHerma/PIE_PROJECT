package constellation;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import org.orekit.time.AbsoluteDate;

import decisionVector.DecisionVariable;
import utils.Parameters;

class ConstellationTest {

	
	@Test
	void testAddSatellite() {
		//Arrange 
		Constellation constel=new Constellation();
		
		//Act 
	    constel.addSatellite(10000, 0, 2.00000, 0.00000, 0, 0.00000, Parameters.t0 );
	    constel.addSatellite(10000, 0, 2,       0.00000, 0, 0.00000, Parameters.t0 );
	    constel.addSatellite(10000, 0, 3.00000, 0.00000, 0, 0.00000, Parameters.t0 );
	    constel.addSatellite(10000, 0, 2.00000, 0.50000, 0, 0.00000, Parameters.t0 );
	    constel.addSatellite(10000, 0.1, 2.00000, 0.50000, 0, 1.00000, Parameters.t0 );
	    /*constel.addSatellite(10000, 0.1, 2.00000, 0.50000, 0, 1.00000, Parameters.t0 );*/
	    System.out.println(constel.getMapPlanes());
	    Plane p1 = constel.getMapPlanes().get("2.0_0.0");
	    Plane p2 = constel.getMapPlanes().get("3.0_0.0");
	    Plane p3 = constel.getMapPlanes().get("2.0_0.5");
	    
	    
		//assert
	    System.out.println(constel.getNOrbitalPlanes());
	    System.out.println(constel.getNSat());
	    System.out.println(p1.getListSatellites().size());
	    System.out.println(p2.getListSatellites().size());
	    System.out.println(p3.getListSatellites().size());
		assert(constel.getNOrbitalPlanes()==3);
		assert(constel.getNSat()==5);
		assert(p1.getListSatellites().size()==2);
		assert(p2.getListSatellites().size()==1);
		assert(p3.getListSatellites().size()==2);
	}

}
