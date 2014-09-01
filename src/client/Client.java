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

	class ConnectionChecker extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					sleep(100);
					System.out.println(Naming.lookup(Server.URL_STRING + serverName));

				} catch (MalformedURLException | RemoteException | InterruptedException e) {
					e.printStackTrace();
				} catch (final NotBoundException e) {
					guiClientFrame.getConnectionButton().setText("Connect");
					guiClientFrame.appendLogEntry("Disconnected from " + serverName + " because seems offline.");
					synchronized (serverChecker) {
						try {
							serverChecker.wait();
						} catch (final InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}

	class Downloader extends Thread {
		private ClientInterface clientInterface;
		private String nameString;

		public Downloader(final ClientInterface paramClientInterface, final String paramName) {
			clientInterface = paramClientInterface;
			nameString = paramName;
		}

		@Override
		public void run() {
			while (true) {
				try {
					synchronized (downloadingParts) {
						if (downloadingParts.size() <= 0) {
							System.out.println("No parts to download, see you later! by " + nameString);
							downloadingParts.wait();
						}
						// qui ce qualcosa da scaricare
						ResourcePart partToDownload = downloadingParts.remove(0);
						partToDownload = downloadPart(clientInterface, partToDownload.getOwnerResource(), partToDownload.getPartNumber());
						sleep(Client.DOWNLOAD_TIME);
						System.out.println(" " + nameString);
					}
				} catch (final InterruptedException | RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**************** TEMPO DI DONWLOAD COSTANTE (PER PARTE) **************/
	private static long DOWNLOAD_TIME = 1000;
	/**********************************************************************/

	private static final long serialVersionUID = 6917781270556644082L;
	private final Vector<ResourceInterface> resources;
	private final Vector<ResourcePart> downloadingParts = new Vector<ResourcePart>();
	private final Vector<ClientInterface> uploadingClients = new Vector<ClientInterface>();
	private final ClientFrame guiClientFrame;
	private final String clientName;
	private final Integer maxDownloadCapacity;
	private Integer currentDownloads = 0;
	private final String serverName;
	private final ConnectionChecker serverChecker = new ConnectionChecker();

	public Client(final String paramClientName, final String paramServerName, final Integer paramDownloadCapacity, final Vector<ResourceInterface> paramResources) throws RemoteException {
		clientName = paramClientName;
		serverName = paramServerName;
		maxDownloadCapacity = paramDownloadCapacity;
		resources = paramResources;
		guiClientFrame = new ClientFrame(clientName + "@" + serverName);
		guiClientFrame.getConnectionButton().addActionListener(this);
		guiClientFrame.getFileSearchButton().addActionListener(this);
		connectToServer();
		serverChecker.start();
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
	 * @return true only if client has a resource called paramResNameString
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
		final ClientInterface thisClientInterface = this;
		new Thread() {
			@Override
			public void run() {
				try {
					final ServerInterface remoteServerInterface = (ServerInterface) Naming.lookup(Server.URL_STRING + serverName);

					synchronized (guiClientFrame.getConnectionButton()) {
						if (guiClientFrame.getConnectionButton().getText().toString().equals("Connect")) {
							// start connection
							if (remoteServerInterface.clientConnect(thisClientInterface) == 1) {
								guiClientFrame.appendLogEntry("Connected to " + serverName);
								guiClientFrame.getConnectionButton().setText("Disconnect");
								synchronized (serverChecker) {
									serverChecker.notifyAll();
								}
							} else { // connection failed
								guiClientFrame.appendLogEntry("Problems connecting to " + serverName);
							}
						} else {
							// start disconnection
							if (remoteServerInterface.clientDisconnect(thisClientInterface) == 0) {
								guiClientFrame.appendLogEntry("Disconnected from " + serverName);
								guiClientFrame.getConnectionButton().setText("Connect");
							} else { // disconnection failed
								guiClientFrame.appendLogEntry("Problems disconnecting to " + serverName);
							}
						}
					}
				} catch (MalformedURLException | NotBoundException | RemoteException e) {
					JOptionPane.showMessageDialog(guiClientFrame, "Server " + serverName + " unreachable.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}.start();
	}

	/*
	 * the paramPartNumber resource of paramResource, if paramClient isn't
	 * already downloading parts from this client
	 */
	@Override
	public ResourcePart downloadPart(final ClientInterface paramClient, final ResourceInterface paramResource, final Integer paramPartNumber) throws RemoteException {
		// remote checks if client is already downloading
		ResourcePart part = null;
		synchronized (uploadingClients) {
			while (uploadingClients.contains(paramClient)) {

			}
			uploadingClients.add(paramClient);
			synchronized (resources) {
				for (final ResourceInterface res : resources) {
					if (res.toString().equals(paramResource.toString())) {
						part = res.getParts().elementAt(paramPartNumber - 1);
					}
				}
			}
			uploadingClients.remove(paramClient);
		}
		return part;
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

		// QUI VA UN THREAD ?? !!!!!!!!!!!!!!!!!!!!!!

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
				// resource
				final Boolean checkResultBoolean = checkResourcePossession(searchedResString);
				// if this client DOESNT own searched resource
				if (!checkResultBoolean) { // if client hasnt the Resource, gets
					// who got it!

					final Vector<ClientInterface> owners = getResourceOwners(searchedResString);
					// if there are at least one resource owner
					if (owners.size() > 0) {
						guiClientFrame.appendLogEntry("Start downloading " + searchedResString);

						// qui ho: owners, searchedResString

						final ResourceInterface resourceToDownload = new Resource(searchedResString);

						synchronized (downloadingParts) {
							downloadingParts.addAll(resourceToDownload.getParts());
							downloadingParts.notifyAll();
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
}
