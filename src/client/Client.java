package client;

import gui.ClientFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.xml.crypto.Data;

import resource.Resource;
import resource.ResourceInterface;
import resource.part.ResourcePartInterface;
import resource.part.TransfertStatus;
import server.Server;
import server.ServerInterface;
import client.Downloader;

public final class Client extends UnicastRemoteObject implements ClientInterface, ActionListener {

	/**************** TEMPO DI DONWLOAD COSTANTE (PER PARTE) **************/
	public static final long DOWNLOAD_TIME = 500;
	/**********************************************************************/

	private static final long serialVersionUID = 6917781270556644082L;
	private final Vector<ResourceInterface> resources;
	private final Vector<ResourceInterface> downloadingResources = new Vector<ResourceInterface>();
	private final ClientFrame guiClientFrame;
	private final String clientName;
	private final Integer maxDownloadCapacity;
	private Integer currentDownloads = 0;
	private final String serverName;
	private final ConnectionChecker serverChecker = new ConnectionChecker();
	private final Vector<ClientInterface> occupiedClients = new Vector<ClientInterface>();

	class ConnectionChecker extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					sleep(100);
					Naming.lookup(Server.URL_STRING + serverName);
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

	/**
	 * @return un Vector<ResourcePartInterface> di parti che sono in coda di scaricamento
	 */
	private Vector<ResourcePartInterface> getPartiMancanti() {
		Vector<ResourcePartInterface> parts = new Vector<ResourcePartInterface>();
		for (ResourceInterface res : downloadingResources) {
			parts.addAll(res.getParts());
		}
		return parts;
	}

	@Override
	public Integer getMinIndex(final ResourceInterface paramResourceToDownload) throws RemoteException {
		final Vector<Integer> vettIntegers = new Vector<Integer>();
		vettIntegers.add(maxDownloadCapacity - currentDownloads); // numero slot download liberi
		vettIntegers.add(getPartiMancanti().size()); // numero di parti mancanti da scaricare
		vettIntegers.add(getResourceOwners(paramResourceToDownload.toString()).size() - occupiedClients.size()); // num client disponibili (che non ci stiano gia inviando parti)
		System.out.println("D'=" + vettIntegers.elementAt(0) + " K'=" + vettIntegers.elementAt(1) + " N'=" + vettIntegers.elementAt(2) + " min(D',K',N')=" + Collections.min(vettIntegers).toString());
		return Collections.min(vettIntegers);
	}

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
		final Downloader myDownloader = new Downloader(this, "DownloaderThread1", downloadingResources);
		myDownloader.start();
		// update gui
		guiClientFrame.setResourceList(paramResources);
		guiClientFrame.setDownloadQueueList(downloadingResources);
	}



	/**
	 * @param paramResNameString
	 * @return true only if client has a resource called paramResNameString
	 */
	private Boolean checkResourcePossession(final String paramResNameString) {
		Boolean result = false;
		guiClientFrame.appendLogEntry("Searching for: " + paramResNameString);
		try {
			synchronized (resources) {
				if (resources.contains(new Resource(paramResNameString))) {
					result = true;
				}
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

	/**
	 * @param paramSearchedResourceString
	 * @return a Vector<ClientInterface> containing ClientsInterfaces who owns
	 *         paramSearchedResString
	 */
	@Override
	public Vector<ClientInterface> getResourceOwners(final String paramSearchedResourceString) throws RemoteException {
		ServerInterface remoteServerInterface = null;
		final Vector<ClientInterface> owners = new Vector<ClientInterface>();
		try {
			remoteServerInterface = (ServerInterface) Naming.lookup(Server.URL_STRING + serverName);
			for (final ClientInterface cli : remoteServerInterface.resourceOwners(paramSearchedResourceString)) {
//				guiClientFrame.appendLogEntry(cli.getClientName() + "@" + cli.getConnectedServer() + " owns " + paramSearchedResourceString);
				owners.add(cli);
			}
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		return owners;
	}

	private final void performSearch() throws NumberFormatException, RemoteException {

		new Thread() {
			@Override
			public void run() {
				guiClientFrame.getFileSearchTextField().requestFocus();
				// check for text field empty
				if (guiClientFrame.getFileSearchTextField().getValue() == null) {
					JOptionPane.showMessageDialog(guiClientFrame, "Please enter a file name.", "File name empty", JOptionPane.WARNING_MESSAGE);
				} else {
					final String connectionButtonState = guiClientFrame.getConnectionButton().getText().toString();
					// check if client is connected
					if (connectionButtonState.equals("Disconnect")) {
						final String searchedResString = guiClientFrame.getFileSearchTextField().getValue().toString();
						// client is connected and check if it already has the searched resource
						final Boolean checkResultBoolean = checkResourcePossession(searchedResString);
						// if this client DOESNT own searched resource
						if (!checkResultBoolean) { // if client hasnt the Resource, gets who got it!
							guiClientFrame.appendLogEntry("ok, i havent " + searchedResString + ", asking servers for owners.");
							Vector<ClientInterface> owners = null;
							try {
								owners = getResourceOwners(searchedResString);
							} catch (RemoteException e1) {
								e1.printStackTrace();
							}
							// if there are at least one resource owner
							if (!owners.isEmpty()) {
								guiClientFrame.appendLogEntry("There are " + owners.size() + " owners of " + searchedResString);
								// qui ho: owners, searchedResString
								ResourceInterface resourceToDownload = null;
								try {
									resourceToDownload = new Resource(searchedResString);
									synchronized (downloadingResources) {
										downloadingResources.add(resourceToDownload);
										guiClientFrame.appendLogEntry(resourceToDownload + " added to download list.");
										downloadingResources.notifyAll();
									}
								} catch (NumberFormatException | RemoteException e) {
									e.printStackTrace();
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
		}.start();
	}
	
	@Override
	public void actionPerformed(final ActionEvent e) {
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
	 * @return the guiClientFrame
	 */
	@Override
	public ClientFrame getGuiClientFrame() {
		return guiClientFrame;
	}
	
	@Override
	public void download() throws RemoteException {
		try {
			guiClientFrame.appendLogEntry("One client is downloading...");
			Thread.sleep(Client.DOWNLOAD_TIME);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
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
	
	@Override
	public Integer getCurrentDownloads() throws RemoteException {
		synchronized (currentDownloads) {			
			return currentDownloads;
		}
	}

	@Override
	public void setCurrentDownloads(Integer currentDownloads) throws RemoteException {
		synchronized (currentDownloads) {
			this.currentDownloads = currentDownloads;			
		}
	}

	@Override
	public void incrementCurrentDownloadsCounter() throws RemoteException {
		guiClientFrame.appendLogEntry("incrmento " + currentDownloads + " -> " + (currentDownloads + 1));
		synchronized (currentDownloads) {
			currentDownloads++;
		}
	}

	@Override
	public void decrementCurrentDownloadsCounter() throws RemoteException {
		guiClientFrame.appendLogEntry("Decrmento " + currentDownloads + " -> " + (currentDownloads  - 1));
		synchronized (currentDownloads) {
			currentDownloads--;
		}
	}
	
	@Override
	public Integer getMaxDownloadCapacity() throws RemoteException {
		return maxDownloadCapacity;
	}
}
