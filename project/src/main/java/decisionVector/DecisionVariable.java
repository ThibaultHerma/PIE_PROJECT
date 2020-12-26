
package decisionVector;

import java.util.Random;

/**
 *<p>The class is a decision variable for the optimization problem. It is characterized by a name,
 *   a min and a max value which define the domain of the variable. Since variables can be Integer 
 *   (ex: nb of sat) or Double (ex:inclination of the plan), the type of the Decision Variable is
 *   parameterized as <code>T</code>.</p>
 *  
 *<p>These are the guidelines to follow to use this class :</p>
 *  
 *<p> - Instantiate a new Decision Variable class by using the default constructor. The parameters of this
 *   constructor are the type of the variable, the name,the min and the max value. WARNING: min and max 
 *   value has to be of the type  <code>T</code>. </p>
 *   
 *   <p>Example of instantiation: <code>DecisionVariable Integer nbSat=new DecisionVariable(Integer.class,"nbSat",
 *   min,max)</code></p>
 *   
 * <p> - To call the methods getMin or getMax,  you have to cast the result to the correct type. To know the type,
 *  you can use the methods isDouble, or isInteger.</p>
 *  
 *  <p>Example: <code>if (nbSat.isInteger()){ Integer min= (Integer) nbSat.getMin()}</code></p>
 *  <p>TODO write randomInit test. </p>
 *  Thread safety : The class is conditionally thread safe.
 * @author Theo Nguyen
 */

public class DecisionVariable<T> {
	private final String name;
	private final T minValue;
	private final T maxValue;
	private final Class<T> type;
	private T value;

	/**
	 * Constructor of the class.
	 * 
	 * @param type:Class(T) the type of the decision variable
	 * @param name:String the name if the class
	 * @param min:T the max value of the variable
	 * @param max:T the min value of the variable
	 */
	public DecisionVariable(Class<T> type, String name, T min, T max) {

		this.name = name;
		this.minValue = min;
		this.maxValue = max;
		this.type = type;

		// check that the domain of the variable is feasible
		if (isInteger()) {
			assert ((Integer) min < (Integer) max);
		}
		if (isDouble()) {
			assert ((Double) min < (Double) max);
		}

	}

	/**
	 * Initialize randomly the variable via a uniform distribution on its domain.
	 */
	@SuppressWarnings("unchecked")
	public void randomInit() {
		Random rand = new Random();
		if (isInteger()) {
			Integer k = rand.nextInt((Integer) maxValue - (Integer) minValue);
			Integer randomValue = (Integer) minValue + k;
			this.value = (T) randomValue;
		}
		if (isDouble()) {
			Double r = rand.nextDouble() * ((Double) maxValue - (Double) minValue);
			Double randomValue = (Double) minValue + r;
			this.value = (T) randomValue;
		}

		System.out.println("Decision variable " + this.name + " initialized randomly with value: " + this.value);
	}

	/**
	 * Check the type of the variable
	 * 
	 * @return Boolean - True if the variable is a Double , else false
	 */
	public Boolean isDouble() {
		return (this.type == Double.class);
	}

	/**
	 * Check the type of the variable
	 * 
	 * @return Boolean - True if the variable is an Integer , else false
	 */
	public Boolean isInteger() {
		return (this.type == Integer.class);
	}

	/** Getter and Setter */

	/**
	 * Get the min value of the variable. WARNING : the return value has to be
	 * casted to the correct class.
	 * 
	 * @return T - the min value .
	 */
	public T getMin() {
		return (this.minValue);
	}

	/**
	 * Get the max value of the variable WARNING : the return value has to be casted
	 * to the correct class.
	 * 
	 * @return T - the max value .
	 */
	public T getMax() {
		return (this.maxValue);
	}

	/**
	 * Set the value of variable
	 * 
	 * @param value:T the value to set
	 */
	public void setValue(T value) {
		this.value = value;
	}

	/**
	 * Get the current value of the variable WARNING : the return value has to be
	 * casted to the correct class.
	 * 
	 * @return T - the current value .
	 */
	public T getValue() {
		return (this.value);
	}

	/**
	 * Get the name of the variable Threads safety : the function is reentrant
	 * because the access to the storage is done by atomic operations since the
	 * function use a read-only operation.
	 * 
	 * @return String - the name of the variable.
	 */
	public String getName() {
		return this.name;
	}

}



