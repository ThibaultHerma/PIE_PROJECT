package utils;

import org.orekit.time.AbsoluteDate;
import org.orekit.utils.IERSConventions;

/**
 * Class which stores the main parameters of the project,
 * such as constants.
 * 
 * @author Louis Rivoire
 *
 */
public class Parameters {
	
	/**
	 * The IERS convention which should be used in all the project.
	 */
	public static IERSConventions projectIERSConventions = IERSConventions.IERS_2010;

	/**
	 * The Earth equatorial radius which should be used in all the project.
	 */
	public static double projectEarthEquatorialRadius = org.orekit.utils.Constants.WGS84_EARTH_EQUATORIAL_RADIUS;

	/**
	 * The Earth equatorial radius which should be used in all the project.
	 */
	public static double projectEarthFlattening = org.orekit.utils.Constants.WGS84_EARTH_FLATTENING; 
    
	/**
	 * The start time of the simulation. By default, the constructor set the date 
	 * to J2000_EPOCH, which is  J2000.0 Reference epoch: 
	 * 2000-01-01T12:00:00 Terrestrial Time (not UTC) (see Orekit doc)
	 */
	public static AbsoluteDate t0 = new AbsoluteDate();
	
	/**
	 * The duration of the simulation in chosen time scale
	 * (Terrestrial time for now)
	 */
	public static Double simulationDuration= 10000000.0;
	
	/**
	 * The style of meshing which has to be used to convert a polygon into a list of meshing points
	 */
	public static String meshingStyle="lat_lon_standard_meshing";

}
