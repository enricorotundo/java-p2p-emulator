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
import resource.part.ResourcePartInterface;
import server.Server;
import server.ServerInterface;

public final class Client extends UnicastRemoteObject implements ClientInterface, ActionListener {

	/**************** TEMPO DI DONWLOAD COSTANTE **************/
	private static long DOWNLOAD_TIME = 1000;
	/**********************************************************/


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
			try {
				performSearch();
			} catch (NumberFormatException | RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			if ("connection".equals(e.getActionCommand())) {
				connectToServer();
			}
		}
	}

	/**
	 * @param paramResNameString
	 * @return true only if client has a resource called
	 *         paramConnectionButtonState
	 */
	private Boolean checkResourcePossession(final String paramResNameString) {
		Boolean result = false;
		guiClientFrame.appendLogEntry("Searching for: " + paramResNameString);
		try {
			if (resources.contains(new Resource(paramResNameString))) {
				result = true;
			}
		} catch (NumberFormatException | RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}

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

	/**
	 * @param paramSearchedResourceString
	 * @return a Vector<ClientInterface> containing ClientsInterfaces who owns
	 *         paramSearchedResString
	 */
	private Vector<ClientInterface> getResourceOwners(final String paramSearchedResourceString) {
		ServerInterface remoteServerInterface = null;
		final Vector<ClientInterface> owners = new Vector<ClientInterface>();
		try {
			remoteServerInterface = (ServerInterface) Naming.lookup(Server.URL_STRING + serverName);
			for (final ClientInterface cli : remoteServerInterface.resourceOwners(paramSearchedResourceString)) {
				guiClientFrame.appendLogEntry(cli.getClientName() + "@" + cli.getConnectedServer() + " owns " + paramSearchedResourceString);
				owners.add(cli);
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		return owners;
	}

	@Override
	public Vector<ResourceInterface> getResources() {
		return resources;
	}

	private final void performSearch() throws NumberFormatException, RemoteException {
		guiClientFrame.getFileSearchTextField().requestFocus();
		// check for text field empty
		if (guiClientFrame.getFileSearchTextField().getValue() == null) {
			JOptionPane.showMessageDialog(guiClientFrame, "Please enter a file name.", "File name empty", JOptionPane.WARNING_MESSAGE);
		} else {
			final String connectionButtonState = guiClientFrame.getConnectionButton().getText().toString();
			// check if client is connected
			if (connectionButtonState.equals("Disconnect")) {
				final String searchedResString = guiClientFrame.getFileSearchTextField().getValue().toString();
				// client is connected and check if it already has the searched
				final Boolean checkResultBoolean = checkResourcePossession(searchedResString);
				// if this client DOESNT own searched resource
				if (!checkResultBoolean) { // if client hasnt the Resource, gets
					// who got it!
					final Vector<ClientInterface> owners = getResourceOwners(searchedResString);
					// if there are at least one resource owner
					if (owners.size() > 0) {
						guiClientFrame.appendLogEntry("Start downloading " + searchedResString);

						// TODO download

						// qui ho: owners, searchedResString

						final ResourceInterface resourceToDownload = new Resource(searchedResString);

						final Vector<ResourcePartInterface> partsToDownload = new Vector<ResourcePartInterface>(resourceToDownload.getParts());
						final ClientInterface thisClientInterface = this;

						final Integer concurrencyLevel = 2; // min(D', K', N')

						for (int i = 0; i < concurrencyLevel; i++) {
							// for (int i = 0; i < downloadCapacityInteger; i++)
							// {
							// concurrent PARTS download
							new Thread() {
								@Override
								public void run() {

									for (final ClientInterface singleOwner : owners) {
										// check if i'm dowloading from him
										synchronized (downloadingClients) {
											if (!downloadingClients.contains(singleOwner)) {
												// ok i can download from him,
												// but which part? the first
												// one!
												final ResourcePartInterface singlePartToDownload = partsToDownload.get(partsToDownload.indexOf(partsToDownload.firstElement()));
												try {
													// start downloading part
													// through RMI
													singleOwner.downloadPart(thisClientInterface, resourceToDownload, singlePartToDownload.getPartNumber());
													// wait fake transfer time
													sleep(Client.DOWNLOAD_TIME);
												} catch (final RemoteException | InterruptedException e) {
													e.printStackTrace();
												}
											}
										}
									}

								}
							}.start();
						}

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
