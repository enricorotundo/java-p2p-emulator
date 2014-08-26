package client;

import gui.ClientFrame;

import java.util.Vector;

import resource.Resource;
import resource.part.ResourcePart;

public final class Client implements ClientInterface {

	private static Vector<Resource> loadRandomResources() {
		return Resource.createRandomResourceVector();
	}

	private Vector<Resource> myResources = new Vector<Resource>();
	private Vector<ResourcePart> myDownloadingParts = new Vector<ResourcePart>();
	private ClientFrame myGuiClientFrame;
	private String myName="";
	private Integer myDownloadCapacitInteger = 0; // il client puo scaricare
	// fino a myDownloadCapaciy
	// PARTI DI RISORSE
	// contemporaneamente


	private Boolean connectionUpBoolean = false;

	public Client(final String paramClientName, final Integer paramDownloadCapacity) {
		setMyName(paramClientName);
		setMyDownloadCapaciy(paramDownloadCapacity);
		setMyResources(Resource.createRandomResourceVector());

		setMyGuiClientFrame(createClientFrame()); // should be the last one call (GUI could request a full object set)
	}

	@Override
	public Integer connect() {
		if (getConnectionStatus()) {
			//disconnection
			connectionUpBoolean = false;
			return Integer.valueOf(0);
			//TODO: tornare -1 se qualcosa va storno
		} else {
			//connection
			connectionUpBoolean = true;
			return Integer.valueOf(1);
			//TODO: tornare -1 se qualcosa va storno
		}
	}

	private ClientFrame createClientFrame() {
		final ClientFrame clientFrame = new ClientFrame(getMyName(), this);
		clientFrame.appendLogEntry("Creating client... ");
		clientFrame.appendLogEntry("Starting creating resources...");
		loadRandomResources();
		setMyResources(loadRandomResources());
		clientFrame.appendLogEntry("Resources created.");
		return clientFrame;
	}

	@Override
	public Boolean getConnectionStatus() {
		//TODO: gestire eccezz ecc..
		return connectionUpBoolean;
	}

	@Override
	public Integer getMyDownloadCapaciy() {
		return myDownloadCapacitInteger;
	}

	@Override
	public Vector<ResourcePart> getMyDownloadingParts() {
		return myDownloadingParts;
	}

	@Override
	public ClientFrame getMyGuiClientFrame() {
		return myGuiClientFrame;
	}

	@Override
	public final String getMyName() {
		return myName;
	}

	@Override
	public Vector<Resource> getMyResources() {
		return myResources;
	}

	@Override
	public Resource requestResource(final Resource paramResquestedResource) {
		if (getConnectionStatus()) {
			//TODO
			//if
			for (final ResourcePart part : paramResquestedResource.getParts()) {
				getMyDownloadingParts().add(part);
			}

			//			part.setDownloadingStatus(TransfertStatus.Downloading);

			myGuiClientFrame.getDownloadQueueList().updateUI();
		} else {
			System.out.println("Connection down.");
		}

		return paramResquestedResource;//stub
	}

	@Override
	public void setMyDownloadCapaciy(final Integer myDownloadCapaciy) {
		this.myDownloadCapacitInteger = myDownloadCapaciy;
	}

	@Override
	public void setMyDownloadingParts(final Vector<ResourcePart> myDownloadingParts) {
		this.myDownloadingParts = myDownloadingParts;
	}

	@Override
	public void setMyGuiClientFrame(final ClientFrame myGuiClientFrame) {
		this.myGuiClientFrame = myGuiClientFrame;
	}

	@Override
	public final void setMyName(final String myName) {
		this.myName = myName;
	}

	@Override
	public void setMyResources(final Vector<Resource> myResources) {
		this.myResources = myResources;
	}
}
