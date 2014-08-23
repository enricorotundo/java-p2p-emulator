/*
 * Enrico Rotundo - 2014 - http://www.math.unipd.it/~crafa/prog3/
 */
package resource.part;

import resource.Resource;

// TODO: Auto-generated Javadoc
/**
 * The Class Part.
 *
 * @author erotundo
 */
public final class ResourcePart implements ResourcePartInterface { // Josh
																	// Bloch's:
																	// "design for inheritance or prohibit it"

	/** The part number. */
	private Integer partNumber = 0;

	/** The owner resource. */
	private Resource ownerResource;

	/** The downloading status. */
	private TransfertStatus downloadingStatus;

	/**
	 * Instantiates a new part.
	 *
	 * @param paramPartNumber
	 *            the param part number
	 * @param paramOwnerResource
	 *            the param owner resource
	 */
	public ResourcePart(final Integer paramPartNumber,
			final Resource paramOwnerResource) {
		partNumber = paramPartNumber;
		setOwnerResource(paramOwnerResource);
		setDownloadingStatus(TransfertStatus.NotStarted);
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
	 * Gets the owner resource.
	 *
	 * @return the ownerResource
	 */
	public Resource getOwnerResource() {
		return ownerResource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see resource.part.PartInterface#getPartNumber()
	 */
	@Override
	public Integer getPartNumber() {
		return partNumber;
	}

	/**
	 * Sets the downloading status.
	 *
	 * @param downloadingStatus
	 *            the downloadingStatus to set
	 */
	public void setDownloadingStatus(final TransfertStatus downloadingStatus) {
		this.downloadingStatus = downloadingStatus;
	}

	/**
	 * Sets the owner resource.
	 *
	 * @param ownerResource
	 *            the ownerResource to set
	 */
	public void setOwnerResource(final Resource ownerResource) {
		this.ownerResource = ownerResource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see resource.part.PartInterface#setPartNumber(java.lang.Integer)
	 */
	@Override
	public void setPartNumber(final Integer partNumber) {
		this.partNumber = partNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getOwnerResource().toString() + " [" + getPartNumber() + "/"
				+ getOwnerResource().toString().charAt(2) + "] ("
				+ getDownloadingStatus() + ")";
	}

}
