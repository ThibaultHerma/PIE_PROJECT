package utils;

import org.orekit.bodies.BodyShape;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.FramesFactory;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.IERSConventions;

/**
 * Class which stores the main parameters of the project, such as constants.
 * These are the guidelines to change the class: -The attributes must be static
 * to have a common storage for all the simulation -As far as possible, the
 * attributes must be final to ensure that they will be used in read only and
 * therefore ensure thread safety. Warning: Since the data loaded from the JSON
 * have to be instantiated, they can't be final.
 * 
 * @author Louis Rivoire
 *
 */
public class Parameters {

	/** ----Simulation parameters---- */

	/**
	 * The IERS convention which should be used in all the project.
	 */
	public static final IERSConventions projectIERSConventions = IERSConventions.IERS_2010;

	/**
	 * The Earth equatorial radius which should be used in all the project.
	 */
	public static final double projectEarthEquatorialRadius = org.orekit.utils.Constants.WGS84_EARTH_EQUATORIAL_RADIUS;

	/**
	 * The Earth equatorial radius which should be used in all the project.
	 */
	public static final double projectEarthFlattening = org.orekit.utils.Constants.WGS84_EARTH_FLATTENING;

	/**
	 * The Earth mu which should be used in all the project.
	 */
	public static final double projectEarthMu = org.orekit.utils.Constants.WGS84_EARTH_MU;
	
	/**
	 * The Earth ITRF frame which should be used in all the project.
	 */
	public static final org.orekit.frames.Frame earthFrame = FramesFactory.getITRF(Parameters.projectIERSConventions, true);

	/**
	 * The Earth body shape frame which should be used in all the project.
	 */
	public static final BodyShape earth = new OneAxisEllipsoid(Parameters.projectEarthEquatorialRadius,
			Parameters.projectEarthFlattening, earthFrame);



	/**
	 * The start time of the simulation. By default, the constructor set the date to
	 * J2000_EPOCH, which is J2000.0 Reference epoch: 2000-01-01T12:00:00
	 * Terrestrial Time (not UTC) (see Orekit doc)
	 */
	public static final AbsoluteDate t0 = new AbsoluteDate();

	/**
	 * The duration of the simulation in chosen time scale (Terrestrial time for
	 * now)
	 */
	public static final Double simulationDuration = 10000000.0;

	/**
	 * The satellites half FOV, value in radian.
	 */
	public static final double halfFOV = 0.18;

	/**
	 * The elevation at which the point begins to be visible by a mesh point, value
	 * in radian.
	 */
	public static final double elevation = java.lang.Math.PI * 0.5 - halfFOV;

	/**
	 * The resolution of the standard mesh. The unit is the radian. The standard
	 * resolution is set to 20km (at the Equator).
	 */
	public static double standardMeshResolution = 20000 / projectEarthEquatorialRadius;

	/**
	 * ---- Simulation parameters which can also be loaded from the JSON file ----
	 */

	/**
	 * The style of meshing which has to be used to convert a polygon into a list of
	 * meshing points
	 */
	public static String meshingStyle = "lat_lon_standard_meshing";

	/** ----Configuration parameters---- */

	/**
	 * The use case chosen. WARNING : The use case nb is here temporarily for
	 * convenient reasons. It will be in the arguments of the main in the releases.
	 */

	public static final int useCaseNb = 1;

	/**
	 * The path of the input JSON files
	 */
	public static final String inputPath = "input/useCase";

	/**
	 * The path of the output JSON path
	 */
	public static final String outputPath = "output/";

	/**
	 * The path of the data file needed by orekit
	 */
	public static final String orekitDataPath = "data/orekit-data-master";

}
