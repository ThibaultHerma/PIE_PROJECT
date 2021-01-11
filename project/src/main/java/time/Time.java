package time;

import org.orekit.time.DateComponents;

/**
 * This class represents a time broken up as year, month, day, hour, minute and second. It also contains the
 * julian time component associated with the time described.
 * 
 * Two constructors are available for this class:
 *  - A Time instance can be defined with a year, a month, a day, an hour, a minute and a second.
 *  - A Time instance can be defined by its julian of type double.
 * 
 * @author Loic Mace
 * 
 */

public class Time {

	private double julian2000;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private double second;
	
	
	/**
	 * Create a Time instance from a Julian with a reference set to 1st January 2000, 12:00
	 * @param double julian
	 */
	public Time(double julian) {
		this.julian2000 = julian;
		DateComponents date = new DateComponents(0);
		if (julian - Math.floor(julian) < 0.5) {
			date = new DateComponents((int) julian);
		} else {
			date = new DateComponents((int) (julian+1));
		}
		this.year = date.getYear();
		this.month = date.getMonth();
		this.day = date.getDay();
		julian = 24*(julian - date.getJ2000Day());
		this.hour = (int) Math.floor(julian+12);
		julian = 60*(julian - Math.floor(julian));
		this.minute = (int) Math.floor(julian);
		this.second = 60*(julian - Math.floor(julian));	
	}
	
	
	/**
	 * Create a Time instance from a Gregorian date
	 * @param int year
	 * @param int month
	 * @param int day
	 * @param int hour
	 * @param int minute
	 * @param double second
	 */
	public Time(int year, int month, int day, int hour, int minute, double second) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		DateComponents date = new DateComponents(year, month, day);
		double j2000 = date.getJ2000Day();
		if (hour > 11) {
			this.julian2000 = j2000 + ((double) hour - 12)/24 + ((double) minute)/1440 + ((double) second)/86400;
		} else {
			this.julian2000 = j2000 - (12 - (double) hour)/24 + ((double) minute)/1440 + ((double) second)/86400;
		}
	}
	
	/**
	 * 
	 * @return the Julian of the Time instance
	 */
	public double getJulian() {
		return julian2000;
	}
	
	/**
	 * 
	 * @return the Gregorian parameters of the Time instance
	 */
	public String getGregorian() {
		return (year + " - " + month + " - " + day + ", " + hour + "h " + minute + "m " + second + "s");
	}
	
	
}


