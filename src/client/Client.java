package client;

import gui.ClientFrame;

import java.util.Vector;

import resource.Resource;
import resource.part.ResourcePart;

public final class Client implements ClientInterface {

	private static Vector<Resource> loadRandomResources() {
		return Resource.createRandomResourceVector();
	}

	private Vector<Resource> resources = new Vector<Resource>();

	private final Vector<ResourcePart> downloadingParts = new Vector<ResourcePart>();

	private final ClientFrame guiClientFrame;
	private String name = "";

	private Integer downloadCapacitInteger = 0; // il client puo scaricare
	// fino a myDownloadCapaciy
	// PARTI DI RISORSE
	// contemporaneamente
	private Boolean connectionUpBoolean = new Boolean(false);
	public Client(final String paramClientName, final Integer paramDownloadCapacity) {
		name = paramClientName;
		downloadCapacitInteger = paramDownloadCapacity;
		resources = Resource.createRandomResourceVector();

		guiClientFrame = createClientFrame();
	}


	@Override
	public Integer connect() {
		if (connectionUpBoolean) {
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
		final ClientFrame clientFrame = new ClientFrame(name, this);

		clientFrame.appendLogEntry("Creating client... ");
		clientFrame.appendLogEntry("Starting creating resources...");
		loadRandomResources();
		resources = loadRandomResources();
		clientFrame.appendLogEntry("Resources created.");
		return clientFrame;
	}

	@Override
	public String getClientName() {
		return name;
	}

	public Boolean getConnectionUpBoolean() {
		return connectionUpBoolean;
	}

	public Vector<ResourcePart> getDownloadingParts() {
		return downloadingParts;
	}

	public Vector<Resource> getResources() {
		return resources;
	}

	@Override
	public Resource requestResource(final Resource paramResquestedResource) {
		if (connectionUpBoolean) {
			// TODO
			// if
			for (final ResourcePart part : paramResquestedResource.getParts()) {
				downloadingParts.add(part);
			}

			// part.setDownloadingStatus(TransfertStatus.Downloading);

			guiClientFrame.getDownloadQueueList().updateUI();
		} else
			System.out.println("Connection down.");

		return paramResquestedResource;// stub
	}

}
