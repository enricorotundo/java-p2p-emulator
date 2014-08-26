package resource.part;

public interface ResourcePartInterface {
	/**
	 * Gets the part number.
	 *
	 * @return the the number of the part. eg. 2 if object is the second part of
	 *         a Resource.
	 */
	public Integer getPartNumber();

	public void setPartNumber(Integer partID);

	@Override
	public String toString();

}
