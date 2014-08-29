package client;

import gui.ClientFrame;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Vector;

import resource.Resource;
import resource.part.ResourcePart;
import server.ServerInterface;

public final class Client implements ClientInterface {

	private static final long serialVersionUID = 6917781270556644082L;

	private Vector<Resource> resources = new Vector<Resource>();

	private final Vector<ResourcePart> downloadingParts = new Vector<ResourcePart>();
	private final ClientFrame guiClientFrame;

	private String name = "";
	/* download capacity */
	private Integer downloadCapacitInteger = 0;
	private Boolean connectionUpBoolean = new Boolean(false);
	private String serverName;
	private static final String HOST = "localhost";

	public Client(final String paramClientName, final String paramServerName, final Integer paramDownloadCapacity, final Vector<Resource> paramResources) throws RemoteException {
		name = paramClientName;
		serverName = paramServerName;
		downloadCapacitInteger = paramDownloadCapacity;
		resources = paramResources;
		guiClientFrame = new ClientFrame(name, this); // last one
	}

	@Override
	public Integer connect() {
		final Integer functionResultInteger = -1;
		System.out.println("Start: connect() connectionUpBoolean = " + connectionUpBoolean);
		try {
			final ServerInterface serverRemoteInterface = (ServerInterface) Naming.lookup("rmi://" + HOST + "/Server/" + serverName);
			if (connectionUpBoolean == false) { // connection
				serverRemoteInterface.clientConnect(this);
				connectionUpBoolean = true;
				guiClientFrame.setConnectionButtonText("Disconnect");
			} else { // disconnection
				serverRemoteInterface.clientDisconnect(this);
				connectionUpBoolean = false;
				guiClientFrame.setConnectionButtonText("Connect");
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		System.out.println("End: connect() functionResultInteger = " + functionResultInteger);
		guiClientFrame.repaint();
		return functionResultInteger;
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

	@Override
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
