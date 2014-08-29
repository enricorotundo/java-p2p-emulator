package resource.part;

import resource.Resource;

public final class ResourcePart implements ResourcePartInterface { // Josh
	// Bloch's:
	// "design for inheritance or prohibit it"

	private static final long serialVersionUID = 6463128579315535109L;
	private Integer partNumber = 0;
	private Resource ownerResource;
	private TransfertStatus downloadingStatus;

	public ResourcePart(final Integer paramPartNumber,
			final Resource paramOwnerResource) {
		partNumber = paramPartNumber;
		setOwnerResource(paramOwnerResource);
		setDownloadingStatus(TransfertStatus.NotStarted);
	}

	public TransfertStatus getDownloadingStatus() {
		return downloadingStatus;
	}

	public Resource getOwnerResource() {
		return ownerResource;
	}

	@Override
	public Integer getPartNumber() {
		return partNumber;
	}

	public void setDownloadingStatus(final TransfertStatus downloadingStatus) {
		this.downloadingStatus = downloadingStatus;
	}

	public void setOwnerResource(final Resource ownerResource) {
		this.ownerResource = ownerResource;
	}

	@Override
	public void setPartNumber(final Integer partNumber) {
		this.partNumber = partNumber;
	}

	@Override
	public String toString() {
		return getOwnerResource().toString() + " [" + getPartNumber() + "/"
				+ getOwnerResource().toString().charAt(2) + "] ("
				+ getDownloadingStatus() + ")";
	}

}
