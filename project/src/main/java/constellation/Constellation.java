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
	private arrayList<Satellite> satList;
	
	/**
	 * Create an constellation
	 * @param id id of the constellation
	 * @param nOrbitalPlans number of orbital plans of the constellation
	 * @param nSat number of satellites of the constellation
	 * @param planList list of orbital plans of the constellation
	 * @param satList list of satellites of the constellation
	 */
	public Constellation(int id, int nOrbitalPlanes, int nSat, arrayList<Planes> planesList, arrayList<Satellite> satList){
		this.id = id;
		this.nOrbitalPlanes = nOrbitalPlanes;
		this.nSat = nSat;
		this.planesList = planesList;
		this.satList = satList;		
	}
	

	
	/**
	 * add a plane
	 * @param idPlane id of the plane
	 * @param inclinaison inclination of the plane
	 * @param omega omega of the plane
	 * @param rightAscNode right ascension of the ascending node of the plane 
	 * @param satellitesList list of satellites of the plan
	 * 
	 */
	//un plan peut être vide  ??? 
	public void addPlane(int idPlane, double inclinaison, double omega, double rightAscNode, arrayList<Satellites> satellitesList) {
		nOrbitalPlanes+=1;
		Plane w = new Plane(idPlane, inclination, omega, rightAscNode, satellitesList );
		planesList.add(w);				
	}
	
	public void addSatellite(double a, double eccentricity, double inclinaison, double omega, double rightAscNode, double periapsisArgument double anomaly, double t0) {
		Satellite w = new Satellite (a, eccentricity, inclinaison, omega, rightAscNode, periapsisArgument, anomaly, t0)
//		nOrbitalPlanes+=1;
//		Plane w = new Plane(idPlane, inclination, omega, rightAscNode, satellitesList );
//		planesList.add(w);				
	}
	
	
	+removePlane()+ addSatellite(a,e,omega,i,w,M,t0): void
	+removeSatellite()
	+ getSatellitesList()
	
	
	

}
