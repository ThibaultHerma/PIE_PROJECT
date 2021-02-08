package utils;

import constellation.Constellation;
import constellation.Satellite;
import zone.Zone;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.time.AbsoluteDate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.IOException;

/**
 * 
 * Class to generate JSON objects, by using the position of satellites
 * of a given constellation at a specified date.
 * 
 * @author Louis Rivoire
 *
 */
public class Animator extends Thread {
	
	/**
	 * The constellation which will be used to build the JSON objects.
	 */
	private Constellation constellation;
	
	/**
	 * The zone to be observed by the constellation.
	 */
	private Zone zone;

	/**
	 * The next variable corresponds to the time step
	 * between every interpolation of the position of the satellites.
	 * We choose to use the maxcheck duration as it corresponds
	 * more or less to the minimum crossing time of the FOV of a satellite
	 * above a zone to observe.
	 */
	private double timeStepSimulation = Parameters.maxcheck;
	
	/**
	 * The next variable has to be an integer, it corresponds
	 * to the number of milliseconds the method sleeps before sending a JSON file.
	 */
	private long timeStepHMI = 100;
	
	/**
	 * The sender server which is used to communicate with the HMI.
	 */
	private SenderServer senderServer;
	
	/**
	 * Constructor of the Animator
	 * 
	 * @param constellation the constellation which will be displayed in the HMI.
	 * @param zone the zone to be observed by the constellation.
	 */
	public Animator(Constellation constellation, Zone zone) {
		this.constellation = constellation;
		this.zone = zone;
	}
	
	/**
	 * Method to initialize the sender server.
	 * Has to be called before running the Animator. 
	 */
	public void start() {
		try {
			senderServer = new SenderServer();
		}
		catch (SocketException e) {System.out.println(e);}
		catch (UnknownHostException e) {System.out.println(e);}
	}
	
	/**
	 * Main method to build the JSON objects which contain
	 * the positions of the satellites at a given date
	 * and the zone they have to observe.
	 * Caution : the sender server has to be instantiated
	 * before running that method.
	 */
	public void run() {
		AbsoluteDate t0 = Parameters.t0;
		AbsoluteDate currentDate = t0;
		AbsoluteDate maxDate = new AbsoluteDate(t0, Parameters.simulationDuration);
		
		while (currentDate.isBefore(maxDate)) {
			
			ArrayList<Vector3D> listPositions = this.getPositionsAtTime(currentDate);
			
			JSONObject obj = createJSONObject(listPositions, currentDate);
			
			// we try to send the JSON object
			try {
				senderServer.sendJSON(obj);
			}
			catch(IOException e) {System.out.println(e);}
			
			// we sleep for some time so as to wait for the HMI
			try {
				Thread.sleep(timeStepHMI);
			}
			catch(InterruptedException e) {System.out.println(e);}
			
			
			currentDate = new AbsoluteDate(currentDate, timeStepSimulation);
		}
		
		senderServer.close();
		
	}
	
	/**
	 * Method to close the sender server. 
	 */
	public void close() {
		senderServer.close();
	}
	
	/**
	 * Method to extract the position of the satellites
	 * of the constellation at a given date.
	 * Unknown behavior if the requested date is not within the correct boundaries
	 * (which are those used for the propagation of the satellites in the
	 * class Simulation).
	 * 
	 * @param date the date at which the position of the satellite
	 * 				has to be known.
	 * @return listPositions the list of the positions of the satellites at
	 * 				the date date.
	 */
	private ArrayList<Vector3D> getPositionsAtTime(AbsoluteDate date) {
		ArrayList<Vector3D> listPositions = new ArrayList<Vector3D>();
		
		ArrayList<Satellite> listSatellites = constellation.getSatellitesList();
		
		for (int sat = 0; sat < constellation.getNSat(); sat++) {
			listPositions.add(listSatellites.get(sat).getVelocity(date, Parameters.earthFrame));
		}
		
		return listPositions;
	}
	
	/**
	 * Method to create a JSON object containing the geodetic
	 * points of the zone, the positions of the satellites at a given date
	 * and the given date.
	 * 
	 * @param listPositions the list of positions of the satellites at a specified date date.
	 * @param date a given date, at which the positions of the satellites are given.
	 * @return obj a JSON object containing information about the position of
	 * 				the satellites at a given date, the points to be observed and the date.
	 */
	private JSONObject createJSONObject(ArrayList<Vector3D> listPositions, AbsoluteDate date) {
		
		ArrayList<GeodeticPoint> listMeshingPoints = zone.getListMeshingPoints();
		
		JSONObject obj = new JSONObject();
		obj.put("date", date.toString());

        JSONArray listGeodeticPointsZone = new JSONArray();
        // we add all the points belonging to the mesh
		for (int point = 0; point < listMeshingPoints.size(); point++) {
			listGeodeticPointsZone.add(listMeshingPoints.get(point).toString());
		}
		obj.put("listGeodeticPointsZone", listGeodeticPointsZone);
		
		JSONArray listVector3DSatellites = new JSONArray();
		// we add the position of every satellite
		for (int sat = 0; sat < listPositions.size(); sat++) {
			listVector3DSatellites.add(listPositions.get(sat).toString());
		}
		obj.put("listVector3DSatellites", listVector3DSatellites);
		
		return obj;		
	}

}
