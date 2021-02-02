package constellation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import utils.Parameters;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.utils.PVCoordinates;

import simulation.Simulation;

public class SatelliteTest {
	
	@Test

	void testGetPositionVelocity() {
		// Arrange
		Satellite sat1 = new Satellite(10000, 0, 2.00000, 0.00000, 0, 0.00000, Parameters.t0);

		// Act
		Frame frame = FramesFactory.getGCRF();
		Vector3D Position  = sat1.getPosition(Parameters.t0, frame);
		Vector3D Velocity  = sat1.getVelocity(Parameters.t0, frame);
		ArrayList<Vector3D> positions = new ArrayList<Vector3D>();
		positions.add(Position);
		ArrayList<Vector3D> velocities = new ArrayList<Vector3D>();
		velocities.add(Velocity);
		Satellite sat2 = new Satellite(positions, velocities, Parameters.t0);
		
		// Assert 
		assert (Math.round(sat1.getA()*100)/100 == Math.round(sat2.getA()*100)/100);
		assert (Math.round(sat1.getE()*100)/100 == Math.round(sat2.getE()*100)/100);
		assert (Math.round(sat1.getI()*100)/100 == Math.round(sat2.getI()*100)/100);
		assert (Math.round(sat1.getRaan()*100)/100 == Math.round(sat2.getRaan()*100)/100);
		assert (Math.round(sat1.getW()*100)/100 == Math.round(sat2.getW()*100)/100);
		assert (Math.round(sat1.getM()*100)/100 == Math.round(sat2.getM()*100)/100);	
		
		System.out.println(sat1.toString());

	}
	
	@Test
	
	void testGetGeodeticPoint() {
		
		
		
		// Act
		Satellite sat1 = new Satellite(10000, 0, 2.00000, 0.00000, 0, 0.00000, Parameters.t0);
		
		
		Frame frame = FramesFactory.getGCRF();
		
		GeodeticPoint LatLongAlt = sat1.getGeodeticPoint(Parameters.t0, Parameters.earthFrame, Parameters.earth);
		System.out.println(LatLongAlt.getLatitude());
		assert(LatLongAlt.getLatitude()==-1.3351593219850104);
		System.out.println(LatLongAlt.getAltitude());
		assert(LatLongAlt.getAltitude()==-6355584.864443043);
		System.out.println(LatLongAlt.getLongitude());
		assert(LatLongAlt.getLongitude()==1.3928785661937213);
		
		// Assert
		
		
	}


}
