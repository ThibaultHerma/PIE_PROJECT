package constellation;

import java.util.ArrayList;
import java.util.HashMap;

import org.orekit.time.AbsoluteDate;
/**
 * Class used for representing a constellation
 * Thread safety: the class is conditionally thread safe: it is safe to use the class only if each 
 * thread access  to its own different instance of the class.
 * @author ThibaultH
 *
 */
public class Constellation {
	

	
	/** Number of orbital plans of the constellation */
	private int nOrbitalPlanes;
	
	/** Number of satellites of the constellation */
	private int nSat;
	
	/** List of orbital plans of the constellation */
	private HashMap<String,Plane> mapPlanes;

	
	/** List of satellites of the constellation */
	private ArrayList<Satellite> listSatellites;
	
	/**
	 * Create a constellation
	 * @param mapPlanes list of orbital plans of the constellation
	 * @param listSatellites list of satellites of the constellation
	 */
	public Constellation(HashMap<String,Plane> mapPlanes, ArrayList<Satellite> listSatellites){
	
		this.nOrbitalPlanes = mapPlanes.size();
		this.nSat = listSatellites.size();
		this.mapPlanes = mapPlanes;
		this.listSatellites = listSatellites;		
	}
	/**
	 * Create an empty constellation
	 */
	public Constellation(){
		this.nOrbitalPlanes = 0;
		this.nSat = 0;
		this.mapPlanes = new HashMap<String,Plane>();
		this.listSatellites = new ArrayList<Satellite>();		
	}
	
	
	/**
	 * return the number of Orbital Planes of the constellation
	 */
	public int getNOrbitalPlanes(){
		return (nOrbitalPlanes);
	}
	
	/**
	 * return the number of satellites of the constellation
	 */
	public int getNSat(){
		return (nSat);
	}
	
	/**
	 * return the list of the planes of the constellation
	 */
	public HashMap<String,Plane> getMapPlanes(){
		return (mapPlanes);
	}
    
	/**
	 * return the list of the satellites of the constellation
	 */
	public ArrayList<Satellite> getSatellitesList(){
		return (listSatellites);
	}
	
// useful ?? TODO implement in Satellite : getIdPlane(), getA() ...
//	/**
//	 * return the plane of planesList the Satellite "satellite" is part of 
//	 * @param satellite Satellite  belonging to the plane we want to reach
//	 */
//	public Plane getPlaneOfSatellite(Satellite satellite){		
//		Plane p = this.getPlane(satellite.getIdPlane());
//		return p;
//	}
	
///////////////////////////////////////////////can a plane be empty ??	
	/**
	 * add an empty plane if the Plane does not already exist
	 * @param inclinaison inclination of the plane
	 * @param rightAscNode right ascension of the ascending node of the plane 
	 * 
	 */
	//Can a plane be empty ??
	public void addPlane(double inclination, double rightAscNode) {
		if (mapPlanes.get(String.valueOf(inclination)+'_'+String.valueOf(rightAscNode)) == null ) { //the plane of does not already exist
			nOrbitalPlanes += 1; //update nOrbitalPlanes
			Plane p = new Plane(inclination, rightAscNode);
			mapPlanes.put(p.getIdPlane(),p);	
		}			
	}
	
	/**
	 * add a plane
	 * @param inclinaison inclination of the plane
	 * @param rightAscNode right ascension of the ascending node of the plane 
	 * @param satellitesList list of satellites of the plan
	 * 
	 */
	public void addPlane(double inclination, double rightAscNode, Satellite s) {
		if (mapPlanes.get(String.valueOf(inclination)+'_'+String.valueOf(rightAscNode)) == null ) { //the plane of does not already exist
			nOrbitalPlanes += 1; //update nOrbitalPlanes
			nSat += 1; //update nSat
			Plane p = new Plane(inclination, rightAscNode);
			p.addSatellite(s);
			mapPlanes.put(p.getIdPlane(),p);	
		}			
	}
	
	/**
	 * add a satellite : adds a new plane if the satellite does not belong to an existing plane
	 * @param a
	 * @param eccentricity
	 * @param inclinaison 
	 * @param rightAscNode right ascension of the ascending node 
	 * @param omega
	 * @param anomaly
	 * @param t0
	 * 
	 */
	public void addSatellite(double a, double eccentricity, double inclination,double rightAscNode, double omega, double anomaly, AbsoluteDate t0) {		

		//Checking if the added satellite belongs to an already existing plane
		String idPlane = String.valueOf(inclination)+'_'+String.valueOf(rightAscNode);
		Satellite newSat = new Satellite ( a, eccentricity, inclination, rightAscNode, omega, anomaly, t0);
		if (nOrbitalPlanes !=0) {
			Plane p = mapPlanes.get(idPlane);
			if (p==null) {//there is no such plane

				
				this.addPlane(inclination, rightAscNode, newSat);


			}else { // the satellite belongs to the Plane p

				/* check if a satellite with the same parameters already exist =====> TODO implement a function that guarantees 2 satellites are not too close from each other*/
				//				int var=0;
				//				for (Satellite s in p.getListSatellites()) { 
				//					if (s.getA() == a 
				//							&& s.getE() == eccentricity 
				//							&& s.getI() == inclination 
				//							&& s.getRaan() == rightAscNode 
				//							&& s.getW() == omega 
				//							&& s.getM() == anomaly 
				//							&& s.getT0() == t0)     { //the newSat already exists within Plane p
				//						var=1;
				//					}
				//					if (var==0) { //newSat can be added to Plane p						
				
				p.addSatellite(newSat);
				//					}
			}
		}
		
		// add the satellite to the list of sat
		listSatellites.add(newSat);
		nSat+=1; //update nSat			
	}
	
}
	
		
		
	
	
	


