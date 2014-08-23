/*
 * Enrico Rotundo - 2014 - http://www.math.unipd.it/~crafa/prog3/
 */
package resource.part;

/**
 * The Interface PartInterface.
 *
 * @author erotundo
 */
public interface ResourcePartInterface {
	/**
	 * Gets the part number.
	 *
	 * @return the the number of the part. eg. 2 if object is the second part of a Resource.
	 */
	public Integer getPartNumber();

	/**
	 * Sets the part number.
	 *
	 * @param partID the partID to set
	 */
	public void setPartNumber(Integer partID);
	
	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString();

}
