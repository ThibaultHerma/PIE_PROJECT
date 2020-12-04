package constellation;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Class used for representing a plane
 * @author ThibaultH
 *
 */
public class Plane {

	/** String - idPlane - Id of the plane corresponding to "inclination_rightAscensionOfAscendingNode" */
	private String idPlane ;
	
	/** ArrayList<Satellite> - listSatellites - List with the satellites of the plane */
	private ArrayList<Satellite> listSatellites;
	
	/** double - inclination - Inclination of the Plane (Precision given up to 3 digits)*/
	private double inclination;
	
	/** double - rightAscNode - Right ascension of the ascending node of the Plane (Precision given up to 3 digits) */
	private double rightAscNode;

	
	/**
	 * Create an empty plane of given inclination and right ascension of the ascending node
	 * @param inclination inclination of the Plane
	 * @param rightAscNode rightAscNode of the Plane
	 */
	public Plane(double inclination, double rightAscNode){
		this.idPlane = String.valueOf(inclination)+'_'+String.valueOf(rightAscNode);
		this.listSatellites = new ArrayList<Satellite>();
		this.inclination = inclination;
		this.rightAscNode = rightAscNode;			
	}
	
	/**
	 * Create a plane
	 * @param inclination inclination of the Plane
	 * @param rightAscNode rightAscNode of the Plane
	 * @param listSatellites ArrayList of satellites
	 */
	public Plane( double inclination, double rightAscNode, ArrayList<Satellite>  listSatellites){
		this.idPlane = String.valueOf(inclination)+'_'+String.valueOf(rightAscNode);
		this.listSatellites = listSatellites;
		this.inclination = inclination;
		this.rightAscNode = rightAscNode;
			
	}
	
	/**
	 * Return idPlane of the Plane
	 */
	public String getIdPlane() {
		return idPlane;		
	}
	
	/**
	 * Return the inclination of the Plane
	 */
	public double getInclinaison() {
		return inclination;		
	}
	
	/**
	 * Return the right ascension of the ascending node of the Plane
	 */
	public double getRightAscNode() {
		return rightAscNode;		
	}
	
	/**
	 * Return listSatellites of the Plane
	 */
	public ArrayList<Satellite> getListSatellites() {
		return listSatellites;		
	}
	
	/**
	 * Add a satellite to a Plane
	 * @param s Satellite to be added
	 */
	public void addSatellite(Satellite s){
		listSatellites.add(s);			
	}
	
	
}
