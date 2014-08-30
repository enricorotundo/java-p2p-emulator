package client;

import gui.ClientFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import javax.swing.JOptionPane;

import resource.Resource;
import resource.part.ResourcePart;
import server.ServerInterface;

public final class Client extends UnicastRemoteObject implements ClientInterface, ActionListener {

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
		connectToServer();
		// update gui
		guiClientFrame.setResourceList(paramResources);
		guiClientFrame.setDownloadQueueList(downloadingParts);
	}

	@Override
	public final void actionPerformed(final ActionEvent e) {
		guiClientFrame.getFileSearchTextField().requestFocus();
		if ("search".equals(e.getActionCommand())) {
			// performSearch(); // va implemntate nel client non nell view
		}

		if ("connection".equals(e.getActionCommand())) {
			connectToServer();
		}
	}

	/**
	 * Connect the client to the p2p system.
	 */
	private final void connectToServer() {
		try {
			final ServerInterface remoteServerInterface = (ServerInterface) Naming.lookup("rmi://" + HOST + "/Server/" + serverName);

			if (guiClientFrame.getConnectionButton().getText().toString().equals("Connect")) {
				// start connection
				if (remoteServerInterface.clientConnect(this) == 1) {
					guiClientFrame.appendLogEntry("Connected to " + serverName);
					guiClientFrame.getConnectionButton().setText("Disconnect");
				} else { // connection failed
					guiClientFrame.appendLogEntry("Problems connecting to " + serverName);
				}
			} else {
				// start disconnection
				if (remoteServerInterface.clientDisconnect(this) == 0) {
					guiClientFrame.appendLogEntry("Disconnected from " + serverName);
					guiClientFrame.getConnectionButton().setText("Connect");
				} else { // disconnection failed
					guiClientFrame.appendLogEntry("Problems disconnecting to " + serverName);
				}
			}
		} catch (MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		} catch (final RemoteException e) {
			JOptionPane.showMessageDialog(guiClientFrame, "Server " + serverName + " unreachable.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public String getClientName() throws RemoteException {
		return name;
	}

	// public Vector<ResourcePart> getDownloadingParts() {
	// return downloadingParts;
	// }

	@Override
	public Vector<Resource> getResources() {
		return resources;
	}

	private final void performSearch() {
		// fileSearchTextField.requestFocus();
		// if (fileSearchTextField.getValue() == null) {
		// JOptionPane.showMessageDialog(this, "Please enter a file name.",
		// "File name empty", JOptionPane.WARNING_MESSAGE);
		// } else {
		// if (client.getConnectionUpBoolean()) {
		// appendLogEntry("Searching for: " + fileSearchTextField.getValue());
		//
		// final Resource searchedResource = new
		// Resource(fileSearchTextField.getValue().toString());
		//
		// final Resource returnedResource =
		// client.requestResource(searchedResource);
		//
		// // TODO check if != null
		// // TODO
		// // TODO avviare la ricerca e l eventuale download
		// } else {
		// JOptionPane.showMessageDialog(this, "Connect first.",
		// "File name empty", JOptionPane.WARNING_MESSAGE);
		// }
		// }
	}

	/**
	 * @return true if and only if the connection is up.
	 */
	private final Resource requestResource(final Resource paramResquestedResource) {
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
