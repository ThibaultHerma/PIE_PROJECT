package constellation;
/**
 * Class used for representing a plane
 * @author ThibaultH
 *
 */
public class Plane {

	/** String - idPlane - Id of the plane corresponding to "inclination_rightAscensionOfAscendingNode" */
	private String idPlane ;
	
	/** Hashmap<Satellite> - mapSatellites - Hashmap with the satellites of the plane */
	private HashMap<Satellite> mapSatellites;
	
	/** float - inclination - Inclination of the plane (Precision given up to 3 digits)*/
	private float inclination;
	
	/** float - rightAscNode - Right ascension of the ascending node of the plane (Precision given up to 3 digits) */
	private float rightAscNode;

	/**
	 * Create a plane
	 * @param idPlane idPlane of the plane
	 * @param mapSatellites mapSatellites of the plane
	 * @param inclination inclination of the plane
	 * @param rightAscNode rightAscNode of the plane
	 */
	public Plane(String idPlane, HashMap<Satellite>  mapSatellites, float inclination, float rightAscNode){
		this.idPlane = idPlane;
		this.mapSatellites = mapSatellites;
		this.inclination = inclination;
		this.rightAscNode = rightAscNode;
			
	}
	
	
}
