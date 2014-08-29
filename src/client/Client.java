package client;

import gui.ClientFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;

import resource.Resource;
import resource.part.ResourcePart;
import server.ServerInterface;

public final class Client implements ClientInterface, ActionListener {

	private static final long serialVersionUID = 6917781270556644082L;
	private Vector<Resource> resources = new Vector<Resource>();
	private final Vector<ResourcePart> downloadingParts = new Vector<ResourcePart>();
	private final ClientFrame guiClientFrame;
	private String name = "";
	private Integer downloadCapacitInteger = 0;
	private Boolean connectionUpBoolean = new Boolean(false);
	private String serverName;
	private static final String HOST = "localhost";

	public Client(final String paramClientName, final String paramServerName, final Integer paramDownloadCapacity, final Vector<Resource> paramResources) throws RemoteException {
		name = paramClientName;
		serverName = paramServerName;
		downloadCapacitInteger = paramDownloadCapacity;
		resources = paramResources;
		guiClientFrame = new ClientFrame(name);
		guiClientFrame.getConnectionButton().addActionListener(this);
		guiClientFrame.getFileSearchButton().addActionListener(this);
		// update gui
		guiClientFrame.setResourceList(paramResources);
		guiClientFrame.setDownloadQueueList(downloadingParts);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		guiClientFrame.getFileSearchTextField().requestFocus();
		if ("search".equals(e.getActionCommand())) {
			// performSearch(); // va implemntate nel client non nell view
		}

		if ("connection".equals(e.getActionCommand())) {
			connectToServer();
		}
	}

	@Override
	public void connectToServer() {
		// TODO Auto-generated method stub
		try {
			final ServerInterface remoteServerInterface = (ServerInterface) Naming.lookup("rmi://" + HOST + "/Server/" + serverName);

			if (remoteServerInterface.clientConnect(this) == 1) {
				guiClientFrame.appendLogEntry("Connected to " + serverName);
			} else { // connection failed
				guiClientFrame.appendLogEntry("Problems connecting to " + serverName);
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getClientName() {
		return name;
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
		// if (connectionUpBoolean) {
		// // TODO
		// // if
		// for (final ResourcePart part : paramResquestedResource.getParts()) {
		// downloadingParts.add(part);
		// }
		//
		// // part.setDownloadingStatus(TransfertStatus.Downloading);
		//
		// guiClientFrame.getDownloadQueueList().updateUI();
		// } else
		// System.out.println("Connection down.");

		return paramResquestedResource;// stub
	}

	@Override
	public String toString() {
		return name;
	}
}
