package resource.part;

import java.io.Serializable;

import resource.ResourceInterface;

public interface ResourcePartInterface extends Serializable {

	public ResourceInterface getOwnerResource();

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
	
	public void setDownloadingStatus(final TransfertStatus downloadingStatus);
	
	public TransfertStatus getDownloadingStatus();
}
