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
import resource.ResourceInterface;
import resource.part.ResourcePart;
import server.Server;
import server.ServerInterface;

public final class Client extends UnicastRemoteObject implements ClientInterface, ActionListener {

	private static final long serialVersionUID = 6917781270556644082L;
	private final Vector<ResourceInterface> resources;
	private final Vector<ResourcePart> downloadingParts = new Vector<ResourcePart>();
	private final Vector<ClientInterface> uploadingClients = new Vector<ClientInterface>();
	private final Vector<ClientInterface> downloadingClients = new Vector<ClientInterface>();
	private final ClientFrame guiClientFrame;
	private final String clientName;
	private final Integer downloadCapacityInteger;
	private final String serverName;

	public Client(final String paramClientName, final String paramServerName, final Integer paramDownloadCapacity, final Vector<ResourceInterface> paramResources) throws RemoteException {
		clientName = paramClientName;
		serverName = paramServerName;
		downloadCapacityInteger = paramDownloadCapacity;
		resources = paramResources;
		guiClientFrame = new ClientFrame(clientName + "@" + serverName);
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
			performSearch();
		} else {
			if ("connection".equals(e.getActionCommand())) {
				connectToServer();
			}
		}
	}

	// @Override
	// public boolean clientCompare(final Object other) throws RemoteException {
	// if (other == null)
	// return false;
	// if (other == this)
	// return true;
	// if (!(other instanceof Client))
	// return false;
	// final Client otherMyClass = (Client) other;
	// if (otherMyClass.clientName.equals(this.clientName)) {
	// return true;
	// }
	// return false;
	// }

	/**
	 * Connect the client to the p2p system.
	 */
	private final void connectToServer() {
		try {
			final ServerInterface remoteServerInterface = (ServerInterface) Naming.lookup(Server.URL_STRING + serverName);

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

	/*
	 * the paramPartNumber resource of paramResource, if paramClient isn't
	 * already downloading parts from this client
	 */
	@Override
	public ResourcePart downloadPart(final ClientInterface paramClient, final ResourceInterface paramResource, final Integer paramPartNumber) throws RemoteException {
		if (!uploadingClients.contains(paramClient)) {
			uploadingClients.add(paramClient);
			for (final ResourceInterface res : resources) {
				if (res.toString().equals(paramResource.toString())) {
					return res.getParts().elementAt(paramPartNumber - 1);
				}
			}
		} else {
			System.out.println(paramClient + " is already downloading resources from me.");
		}
		uploadingClients.remove(paramClient);
		return null;
	}

	@Override
	public String getClientName() throws RemoteException {
		return clientName;
	}

	@Override
	public String getConnectedServer() throws RemoteException {
		return serverName;
	}

	@Override
	public Vector<ResourceInterface> getResources() {
		return resources;
	}

	private final void performSearch() {
		guiClientFrame.getFileSearchTextField().requestFocus();
		if (guiClientFrame.getFileSearchTextField().getValue() == null) {
			JOptionPane.showMessageDialog(guiClientFrame, "Please enter a file name.", "File name empty", JOptionPane.WARNING_MESSAGE);
		} else {
			if (guiClientFrame.getConnectionButton().getText().toString().equals("Disconnect")) {
				guiClientFrame.appendLogEntry("Searching for: " + guiClientFrame.getFileSearchTextField().getValue());

				Boolean alreadyPresent = false;
				for (final ResourceInterface resource : resources) {
					if (resource.toString().equals(guiClientFrame.getFileSearchTextField().getValue().toString())) {
						alreadyPresent = true;
					}
				}

				if (!alreadyPresent) {
					ServerInterface remoteServerInterface = null;
					final Vector<ClientInterface> owners = new Vector<ClientInterface>();
					try {
						remoteServerInterface = (ServerInterface) Naming.lookup(Server.URL_STRING + serverName);
						for (final ClientInterface cli : remoteServerInterface.resourceOwners(guiClientFrame.getFileSearchTextField().getValue().toString())) {
							guiClientFrame.appendLogEntry(cli.getClientName() + "@" + cli.getConnectedServer() + " owns " + guiClientFrame.getFileSearchTextField().getValue().toString());
							owners.add(cli);
						}
					} catch (MalformedURLException | RemoteException | NotBoundException e) {
						e.printStackTrace();
					}

					if (owners.size() > 0) {
						// TODO download
						// final Integer partNumberInteger =
						// Integer.parseInt(String.valueOf(guiClientFrame.getConnectionButton().getText().toString()));
						// ResourceInterface resourceInterface = null;
						// try {
						// resourceInterface = new
						// Resource(String.valueOf(guiClientFrame.getConnectionButton().getText().toString()));
						// } catch (NumberFormatException | RemoteException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
						// for (int i = 0; i < partNumberInteger; i++) {
						// for (final ClientInterface clientInterface : owners)
						// {
						// if (!downloadingClients.contains(clientInterface)) {
						// downloadingClients.add(clientInterface);
						// try {
						// final ResourcePart downloadedPart =
						// clientInterface.downloadPart(this, resourceInterface,
						// partNumberInteger);
						// } catch (final RemoteException e) {
						// e.printStackTrace();
						// }
						// } else {
						// // stai gia scaric da client
						// }
						// }
						// }

					} else {
						JOptionPane.showMessageDialog(guiClientFrame, "Resource " + guiClientFrame.getFileSearchTextField().getValue() + " not found in the network, please try searching another resource", "Please try searching another resource.", JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(guiClientFrame, "You cannon't download a owned resource, please try searching another one.", "You already own searched resource.", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(guiClientFrame, "Please connect first.", "Please connect first", JOptionPane.ERROR_MESSAGE);
			}
		}

	}

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
}
