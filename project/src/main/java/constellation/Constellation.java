package constellation;
/**
 * Class used for representing a constellation
 * @author ThibaultH
 *
 */
public class Constellation {
	

	/** Id of the constellation */
	private final int id ;
	
	/** Number of orbital plans of the constellation */
	private int nOrbitalPlanes;
	
	/** Number of satellites of the constellation */
	private int nSat;
	
	/** List of orbital plans of the constellation */
	private arrayList<Plan> planesList;
	
	/** List of satellites of the constellation */
	private arrayList<Satellite> satellitesList;
	
	/**
	 * Create an constellation
	 * @param id id of the constellation
	 * @param nOrbitalPlans number of orbital plans of the constellation
	 * @param nSat number of satellites of the constellation
	 * @param planList list of orbital plans of the constellation
	 * @param satList list of satellites of the constellation
	 */
	public Constellation(int id, int nOrbitalPlanes, int nSat, Plane planesList, Satellite satList){
		this.id = id;
		this.nOrbitalPlanes = nOrbitalPlanes;
		this.nSat = nSat;
		this.planesList = planesList;
		this.satellitesList = satList;		
	}
	
	/**
	 * return the id of the constellation
	 */
	public int getId(){
		return (id);
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
	public Plane getPlanesList(){
		return (planesList);
	}
	
	/**
	 * return the list of the satellites of the constellation
	 */
	public Satellite getSatellitesList(){
		return (satellitesList);
	}

	
	/**
	 * add a plane
	 * @param idPlane id of the plane
	 * @param inclinaison inclination of the plane
	 * @param rightAscNode right ascension of the ascending node of the plane 
	 * @param satellitesList list of satellites of the plan
	 * 
	 */
	//un plan peut etre vide  ??? 
	public void addPlane(int idPlane, double inclination, double rightAscNode, Satellite satellitesList) {
		nOrbitalPlanes += 1; //update nOrbitalPlanes
		nSat += satelliteList.size(); //update nSat
		Plane w = new Plane(idPlane, inclination, rightAscNode, satellitesList );
		planesList.add(w);				
	}
	
	/**
	 * add a satellite : adds a new plane if the satellite does not belong to an existing plane
	 * @param 
	 * @param inclinaison inclination of the plane
	 * @param rightAscNode right ascension of the ascending node of the plane 
	 * @param a
	 * @param eccentricity
	 * @param omega
	 * @param periapsisArgument
	 * @param anomaly
	 * @param t0
	 * 
	 */
	public void addSatellite(double a, double eccentricity, double inclination, double omega, double rightAscNode, double periapsisArgument, double anomaly, double t0) {

		//Checking if the added satellite belongs to an already existing plane
		int idPlane = -1;
		if (nOrbitalPlanes !=0) {
			for (int i = 0 ; i<nOrbitalPlanes ; i++) {
				if (planesList.get(i).getInclination()==inclination && planesList.get(i).getRightAscNode()==rightAscNode) {
					idPlane = i;
				}
			}
		}
		if (idPlane == -1) { //there is no such plane
			int idNewPlane = this.idNewPlane();
			Satellite w = new Satellite (idNewPlane, a, eccentricity, inclination, omega, rightAscNode, periapsisArgument, anomaly, t0);
			this.addPlane(idNewPlane, inclination, rightAscNode, w);
			nOrbitalPlanes+=1; // update nOrbitalPlanes
			
		} else { // the satellite belongs to the idPlane-th plane of planesList 
			Satellite w = new Satellite (planesList.get.get(idPlane).getId(), a, eccentricity, inclination, omega, rightAscNode, periapsisArgument, anomaly, t0);
			planesList.get(idPlane).addSatellite(w);
		}
		nSat+=1; //update nSat			
	}
	
	/**
	 * return an unused id for a new plan 
	 */
//	public int idNewPlane(){
//		if (nOrbitalPlanes != 0) {
//			return(planesList.get(nOrbitalPlanes-1).getId() + 1 ) // by default, returns the "id of the last existing plane in planesList + 1 "
//		}
//		return (0);
//	}
	
//	/**
//	 * return an unused id for a new plan 
//	 */
//	public void removePlane(){
//		if (nOrbitalPlanes != 0) {
//			return(planesList.get(nOrbitalPlanes-1).getId() + 1 ) // by default, returns the "id of the last existing plane in planesList + 1 "
//		}
//		return (0);
//	}
	
	/**
	 * remove the plane of id "idPlane" from planesList
	 * @param idPlane idPlane of the plane to remove from the list
	 */
	public void removePlane(String idPlane){
		if (nOrbitalPlanes != 0) {
			if ( planesList.get(idPlane) != null) { //the Plane of id idPlane is in planesList
				planesList.remove(idPlane);
				nOrbitalPlanes-=1;
				for (String key : satellitesList.keySet()) {
					if (satellitesList.get(key).getIdPlane()==idPlane) { // does the satellite belong to the plane of id idPlane
						satellitesList.remove(key);
						nSat-=1;
					}
				}
		}
		
	}
		
	/**
	 * remove the plane of id "idPlane" from planesList
	 * @param idPlane idPlane of the plane to remove from the list
	 */
	public void removeSatellite(String idSatellite){
		if (nSat != 0) {
			if (satellitesList.get(idSatellite) != null) { //the satellite of id idSatellite is in satellitesList
				Satellite s = satellitesList.get(idSatellite);
				Plane p = planesList.get(s.getIdPlane());
				if (p.getSatellitesList().size()>1) {
					p.get(s.getIdPlane()).removeSatellite();
					
				}else {
					
				}
				satellitesList.remove(idSatellite);
				nSat-=1;

				}
		}
			
	}	
	+removeSatellite()
	+ getSatellitesList()
	

		
		
		
		
	}
	
	
	

}
