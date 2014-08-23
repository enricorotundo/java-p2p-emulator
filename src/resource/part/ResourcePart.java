/*
 * Enrico Rotundo - 2014 - http://www.math.unipd.it/~crafa/prog3/
 */
package resource.part;

import resource.Resource;
import resource.part.TransfertStatus;

// TODO: Auto-generated Javadoc
/**
 * The Class Part.
 *
 * @author erotundo
 */
public final class ResourcePart implements ResourcePartInterface { // Josh Bloch's: "design for inheritance or prohibit it"
	
	/** The part number. */
	private Integer partNumber=0;
	
	/** The owner resource. */
	private Resource ownerResource;
	
	/** The downloading status. */
	private TransfertStatus downloadingStatus;

	
	/**
	 * Instantiates a new part.
	 *
	 * @param paramPartNumber the param part number
	 * @param paramOwnerResource the param owner resource
	 */
	public ResourcePart(Integer paramPartNumber, Resource paramOwnerResource) {
		partNumber = paramPartNumber;
		setOwnerResource(paramOwnerResource);
		setDownloadingStatus(TransfertStatus.NotStarted);
	}

	/* (non-Javadoc)
	 * @see resource.part.PartInterface#getPartNumber()
	 */
	public Integer getPartNumber() {
		return partNumber;
	}

	/* (non-Javadoc)
	 * @see resource.part.PartInterface#setPartNumber(java.lang.Integer)
	 */
	public void setPartNumber(Integer partNumber) {
		this.partNumber = partNumber;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getOwnerResource().toString() + " [" + getPartNumber() + "/" + getOwnerResource().toString().charAt(2) + "] (" + getDownloadingStatus() + ")";
	}

	/**
	 * Gets the owner resource.
	 *
	 * @return the ownerResource
	 */
	public Resource getOwnerResource() {
		return ownerResource;
	}

	/**
	 * Sets the owner resource.
	 *
	 * @param ownerResource the ownerResource to set
	 */
	public void setOwnerResource(Resource ownerResource) {
		this.ownerResource = ownerResource;
	}

	/**
	 * Gets the downloading status.
	 *
	 * @return the downloadingStatus
	 */
	public TransfertStatus getDownloadingStatus() {
		return downloadingStatus;
	}

	/**
	 * Sets the downloading status.
	 *
	 * @param downloadingStatus the downloadingStatus to set
	 */
	public void setDownloadingStatus(TransfertStatus downloadingStatus) {
		this.downloadingStatus = downloadingStatus;
	}

}
