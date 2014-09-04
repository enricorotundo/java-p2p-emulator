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
import java.util.concurrent.atomic.AtomicInteger;

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
	public static final long DOWNLOAD_TIME = 1500;
	/**********************************************************************/

	private static final long serialVersionUID = 6917781270556644082L;
	private final String clientName;
	private final String serverName;
	private AtomicInteger currentDownloads = new AtomicInteger(0);
	private final Integer maxDownloadCapacity;
	private final Downloader myDownloader; 
	
	/**** RISORSE CONDIVISE DA SINCRONIZZARE *****/
	private final Vector<ClientInterface> clientsBusyWithMe = new Vector<ClientInterface>();
	private final ConnectionChecker connectionToServerChecker = new ConnectionChecker();
	private final Vector<ResourceInterface> resources;
	private final Vector<ResourceInterface> downloadingResources = new Vector<ResourceInterface>();
	private final ClientFrame guiClientFrame;
	/*********************************************/

	class ConnectionChecker extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					sleep(100);
					synchronized (guiClientFrame.getConnectionButton()) {
						// controllo la connessione solo se sono connesso
						if (guiClientFrame.getConnectionButton().getText().equals("Disconnect")) {
							Naming.lookup(Server.URL_STRING + serverName);	
						}						
					}
				} catch (MalformedURLException | RemoteException | InterruptedException e) {
					e.printStackTrace();
				} catch (final NotBoundException e) {
					synchronized (guiClientFrame.getConnectionButton()) {
						guiClientFrame.getConnectionButton().setText("Connect");						
					}
					// l'insert e' gia synchronized
					guiClientFrame.appendLogEntry("Disconnected from " + serverName + " because seems offline.");
					synchronized (connectionToServerChecker) {
						try {
							/*
							 * viene risvegliato in connectToServer() cioe quando
							 * il client si riconnette al server serverName
							 */
							connectionToServerChecker.wait();
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
		synchronized (downloadingResources) {
			for (ResourceInterface res : downloadingResources) {
				parts.addAll(res.getParts());
			}			
		}
		return parts;
	}

	@Override
	public Integer getMinIndex(final ResourceInterface paramResourceToDownload) throws RemoteException {
		long startTime = System.nanoTime();
		final Vector<Integer> vettIntegers = new Vector<Integer>();
		// getcount e' atomica
		vettIntegers.add(maxDownloadCapacity - getCount()); // numero slot download liberi
		// e' gia sincronizzata
		vettIntegers.add(getPartiMancanti().size()); // numero di parti mancanti da scaricare
		// getResourceOwners non usa risorse condivise, paramResourceToDownload e' final, getClientsBusyWithMe() e' sincronizzata
		vettIntegers.add(getResourceOwners(paramResourceToDownload.toString(), this.getClientName()).size() - getClientsBusyWithMe().size()); // num client disponibili (che non ci stiano gia inviando parti)			
		Integer result = Collections.min(vettIntegers);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("D'=" + vettIntegers.elementAt(0) + " K'=" + vettIntegers.elementAt(1) + " N'=" + vettIntegers.elementAt(2) + " min(D',K',N')=" + result.toString() + " completed in: " + (duration / 1000000000.0) + " seconds.");
		return result;
	}

	public Client(final String paramClientName, final String paramServerName, final Integer paramDownloadCapacity, final Vector<ResourceInterface> paramResources) throws RemoteException {
		clientName = paramClientName;
		serverName = paramServerName;
		maxDownloadCapacity = paramDownloadCapacity;
		// non serve sincronizzare perche' gli altri thread che la usano devono ancora partire
		resources = paramResources;			
		guiClientFrame = new ClientFrame(clientName + "@" + serverName);
		guiClientFrame.getConnectionButton().addActionListener(this);
		guiClientFrame.getFileSearchButton().addActionListener(this);
		connectToServer();
		connectionToServerChecker.start();
		myDownloader = new Downloader(this, "DownloaderThread1", downloadingResources);
		myDownloader.start();
		// update gui
		synchronized (resources) {
			guiClientFrame.setResourceList(resources);			
		}
		synchronized (downloadingResources) {			
			guiClientFrame.setDownloadQueueList(downloadingResources);
		}
		
		// quando chiudo il client mi disconnetto dal server
		final ClientInterface thisClientInterface = this;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					synchronized (guiClientFrame.getConnectionButton()) {
						// mi disconnetto solo se ero connesso
						if (guiClientFrame.getConnectionButton().getText().equals("Disconnect")) {
							final ServerInterface remoteServerInterface = (ServerInterface) Naming.lookup(Server.URL_STRING + serverName);
							remoteServerInterface.clientDisconnect(thisClientInterface);							
						}
					}
				} catch (MalformedURLException | RemoteException | NotBoundException e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * @param paramResNameString
	 * @return true only if client has a resource called paramResNameString
	 */
	private Boolean checkResourcePossession(final String paramResNameString) {
		long startTime = System.nanoTime();
		Boolean result = false;
		guiClientFrame.appendLogEntry("Searching for: " + paramResNameString);
		try {
			synchronized (resources) {
				result = resources.contains(new Resource(paramResNameString));
			}
		} catch (NumberFormatException | RemoteException e) {
			e.printStackTrace();
		}
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		System.out.println("checkResourcePossession(" + paramResNameString + ") completed in: " + (duration / 1000000000.0) + " seconds.");
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
								synchronized (connectionToServerChecker) {
									connectionToServerChecker.notifyAll();
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

	@Override
	public Vector<ClientInterface> getResourceOwners(final String paramSearchedResourceString, final String callerName) throws RemoteException {
		ServerInterface remoteServerInterface = null;
		guiClientFrame.appendLogEntry(callerName + ":prima di recuperare tutti i client che hanno " + paramSearchedResourceString);
		final Vector<ClientInterface> owners = new Vector<ClientInterface>();
		try {
			remoteServerInterface = (ServerInterface) Naming.lookup(Server.URL_STRING + serverName);
			if (remoteServerInterface != null) {
				for (final ClientInterface cli : remoteServerInterface.resourceOwners(paramSearchedResourceString)) {
					guiClientFrame.appendLogEntry(cli.getClientName() + "@" + cli.getConnectedServer() + " owns " + paramSearchedResourceString);
					owners.add(cli);
				}				
			}
			guiClientFrame.appendLogEntry(callerName + ":ho recuperato tutti client che possiedono " + paramSearchedResourceString);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		return owners;
	}

	private final void performSearch() throws NumberFormatException, RemoteException {

		new Thread() {
			@Override
			public void run() {
				synchronized (guiClientFrame.getFileSearchTextField()) {
						guiClientFrame.getFileSearchTextField().requestFocus();
						// check for text field empty
						if (guiClientFrame.getFileSearchTextField().getValue() == null || guiClientFrame.getFileSearchTextField().getText() == "") {
							JOptionPane.showMessageDialog(guiClientFrame, "Please enter a file name.", "File name empty", JOptionPane.WARNING_MESSAGE);
						} else {
							final String connectionButtonState;
							// e' in concorrenza con connectToServer()
							synchronized (guiClientFrame.getConnectionButton()) {
								connectionButtonState = guiClientFrame.getConnectionButton().getText().toString();								
								// check if client is connected
								if (connectionButtonState.equals("Disconnect")) {
									final String searchedResString = guiClientFrame.getFileSearchTextField().getValue().toString();
									// client is connected and check if it already has the searched resource
									final Boolean checkResultBoolean = checkResourcePossession(searchedResString);
									// if this client DOESNT own searched resource
									if (!checkResultBoolean) { // if client hasnt the Resource, gets who got it!
										guiClientFrame.appendLogEntry("ok, i havent " + searchedResString + ", asking " + serverName + " for owners.");
										Vector<ClientInterface> owners = null;
										try {
											owners = getResourceOwners(searchedResString, this.getName());
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
	public void incrementCount() {
		currentDownloads.incrementAndGet();
	}
	
	@Override
	public void decrementCount() {
		currentDownloads.decrementAndGet();
	}
	  
	@Override
	public int getCount() {
	    return currentDownloads.get();
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
	public Integer getMaxDownloadCapacity() throws RemoteException {
		return maxDownloadCapacity;
	}
	
	@Override
	public Vector<ClientInterface> getClientsBusyWithMe() {
		synchronized (clientsBusyWithMe) {
			return clientsBusyWithMe;			
		}
	}
}
