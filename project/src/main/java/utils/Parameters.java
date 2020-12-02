package utils;

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


}
