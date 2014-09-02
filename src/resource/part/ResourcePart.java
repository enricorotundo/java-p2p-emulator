package resource.part;

import java.rmi.RemoteException;

import resource.Resource;
import resource.ResourceInterface;

public final class ResourcePart implements ResourcePartInterface { // Josh
	// Bloch's:
	// "design for inheritance or prohibit it"

	public static ResourcePartInterface createResourcePartInterface(final Integer paramPartNumber,
			final Resource paramOwnerResource) {
		ResourcePartInterface newInterface = null;
		try {
			newInterface = new ResourcePart(paramPartNumber, paramOwnerResource);
		} catch (final RemoteException e) {
			e.printStackTrace();
		}
		return newInterface;
	}
	private static final long serialVersionUID = 6463128579315535109L;
	private Integer partNumber = 0;
	private ResourceInterface ownerResource;
	private TransfertStatus downloadingStatus;

	public ResourcePart(final Integer paramPartNumber,
			final Resource paramOwnerResource) throws RemoteException {
		partNumber = paramPartNumber;
		setOwnerResource(paramOwnerResource);
		setDownloadingStatus(TransfertStatus.NotStarted);
	}

	@Override
	public TransfertStatus getDownloadingStatus() {
		return downloadingStatus;
	}

	@Override
	public ResourceInterface getOwnerResource() {
		return ownerResource;
	}

	@Override
	public Integer getPartNumber() {
		return partNumber;
	}
	
	@Override
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
