package controller.client;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import view.ClientFrame;

import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import controller.server.Server;
import controller.server.ServerInterface;
import model.client.ClientResources;
import model.share.Resource;

public class Client extends UnicastRemoteObject implements ClientInterface, ActionListener  {
	
	/**************** TEMPO DI DONWLOAD COSTANTE (PER PARTE) **************/
	public static final long UPLOAD_TIME = 1500;
	/**********************************************************************/
	
	private static final long serialVersionUID = -3445312807782067423L;
	private final String clientName;
	private final String serverName;
	private AtomicBoolean connectionStatusUp = new AtomicBoolean(false); //false=down, true=up
	private AtomicInteger currentDownloadsNumber = new AtomicInteger(0);
	private final Integer maxDownloadCapacity;
	private final ClientResources resourceModel; // MODEL
	private final ClientFrame gui; // VIEW
	private final ConnectionChecker connectionChecker;
	
	public Client(final String clientName, final String serverName, int maxDownloadCapacity, final ClientResources argResources) throws RemoteException {
		this.clientName = clientName;
		this.serverName = serverName;
		this.maxDownloadCapacity = maxDownloadCapacity;
		this.resourceModel = argResources;
		gui = new ClientFrame(clientName + "@" + serverName, resourceModel, this);
	
		// dico al MODEL chi e' il suo Observer
		this.resourceModel.addObserver(gui);
		// mi connetto subito al server 
		connectToServer();
		// avvio il thread che controlla lo status della connessione tra me Client e il mio Server
		connectionChecker = new ConnectionChecker(connectionStatusUp, serverName, gui);
		connectionChecker.start();
		
		// quando chiudo il client mi disconnetto dal server
		final ClientInterface thisClientInterface = this;
		Runtime.getRuntime().addShutdownHook(new Thread() {
		@Override
		public void run() {
				try {
					synchronized (connectionStatusUp) {
						// mi disconnetto solo se ero connesso
						if (connectionStatusUp.get() == true) {
							final ServerInterface remoteServerInterface = (ServerInterface) Naming.lookup(Server.URL_STRING + serverName);
							remoteServerInterface.clientDisconnect(thisClientInterface);							
						}
					}
				} catch (MalformedURLException | RemoteException | NotBoundException e) {
					e.printStackTrace();
				}
			}
		});
		
		// mostra gui
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		        gui.setVisible(true);
		    }
		});
	}

	@Override
	public Boolean download(final String callerName) throws RemoteException {
		try {
			gui.appendLogEntry(callerName + " is downloading from me.");
			Thread.sleep(Client.UPLOAD_TIME);
			return true;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
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
	 * Usato da controller.ClientChecker o altri per testare se il Client e' ancora up
	 */
	@Override
	public Boolean test() throws RemoteException {
		return true;
	}

	// lista risorse possedute dal client
	@Override
	public Vector<String[]> getResourceList() throws RemoteException {
		Vector<String[]> resourceList = new Vector<String[]>();
		for (Resource singleResource : resourceModel.getResources()) {
			resourceList.add(singleResource.toArrayStrings());
		}
		return resourceList;
	}
	
	private final void connectToServer() throws RemoteException {
		final ClientInterface thisClientInterface = this;
		new Thread() {
			@Override
			public void run() {
				try {
					// cerco il server al quale devo connettermi
					final ServerInterface remoteServerInterface = (ServerInterface) Naming.lookup(Server.URL_STRING + serverName);
					synchronized (connectionStatusUp) {
						if (connectionStatusUp.get() == false) {
							// start connection
							if (remoteServerInterface.clientConnect(thisClientInterface) == 1) {
								gui.setConnectionButtonText("Disconnect");
								connectionStatusUp.set(true);
								// risveglio il thread che controlla la connessione Client-Server
								synchronized (connectionChecker) {
									connectionChecker.notifyAll();
								}
							} else { // connection failed
								gui.appendLogEntry("Problems connecting to " + serverName);
							}
						} else {
							// start disconnection
							if (remoteServerInterface.clientDisconnect(thisClientInterface) == 0) {
								gui.setConnectionButtonText("Connect");
								connectionStatusUp.set(false);
							} else { // disconnection failed
								gui.appendLogEntry("Problems disconnecting to " + serverName);
							}
						}
					}
				} catch (MalformedURLException | NotBoundException | RemoteException e) {
					JOptionPane.showMessageDialog(gui, "Server " + serverName + " unreachable.", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}.start();
	}
	
	@Override
	public Boolean checkResourcePossession(final String resourceToSearchFor, final String caller) throws RemoteException {
		Boolean result = false;
		if(!caller.equals(clientName)) {
			try {
				synchronized (resourceModel) {
					result = resourceModel.containsResource(resourceToSearchFor);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			gui.appendLogEntry(caller + " contacted me for " + resourceToSearchFor);			
		}
		return result;
	}
	
	private Vector<ClientInterface> getResourceOwners(final String paramSearchedResourceString) throws RemoteException {
		ServerInterface remoteServerInterface = null;
		Vector<ClientInterface> owners = null;
		
		try {
			remoteServerInterface = (ServerInterface) Naming.lookup(Server.URL_STRING + serverName);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
		
		if (remoteServerInterface != null) {
			try {
				owners = remoteServerInterface.getResourceOwners(paramSearchedResourceString, clientName);
			} catch (RemoteException e) {
				e.printStackTrace();
			}			
		} else {
			owners = new Vector<ClientInterface>();
		}
		return owners;
	}
	
	private final void performSearch(final String searchedResourceName) throws RemoteException  {
		new Thread() {
			@Override
			public void run() {
				synchronized (connectionStatusUp) {
						// check for text field empty
						if (searchedResourceName.isEmpty()) {
							JOptionPane.showMessageDialog(gui, "Please enter a file name.", "File name empty", JOptionPane.WARNING_MESSAGE);
						} else {
								// e' in concorrenza con connectToServer()
								// check if client is connected
								if (connectionStatusUp.get() == true) {
									// client is connected and check if it already has the searched resource
									// if this client DOESNT own searched resource
									try {
										if (currentDownloadsNumber.get() <= 0) {
											if (!checkResourcePossession(searchedResourceName, clientName)) {
												gui.appendLogEntry("I havent " + searchedResourceName + ", asking " + serverName + " for owners.");
												Vector<ClientInterface> owners = null;
												owners = getResourceOwners(searchedResourceName);
												// if there are at least one resource owner
												if (!owners.isEmpty()) {
													gui.appendLogEntry("There are " + owners.size() + " owners of " + searchedResourceName);
																										
													// stampo i possessori della risorsa
													for (ClientInterface clientInterface : owners) {
														try {
															gui.appendLogEntry(clientInterface.getClientName() + " owns " + searchedResourceName);
														} catch (RemoteException e) {
															e.printStackTrace();
														}
													}
													
													// aggiungo la risorsa al MODEL
													resourceModel.addDownloadingResource(searchedResourceName);
													
													// risveglio il thread DownloadScheduler in wait sul MODEL
													synchronized (resourceModel) {														
														resourceModel.notifyAll();
													}
													
													// avvio Thread per il download
													new DownloadScheduler(resourceModel, owners, new String[]{searchedResourceName.substring(0,1), searchedResourceName.substring(2,3)}, maxDownloadCapacity, currentDownloadsNumber).start();
													
												} else {
													JOptionPane.showMessageDialog(gui, "Resource " + searchedResourceName + " not found in the network, please try searching another resource", "Please try searching another resource.", JOptionPane.INFORMATION_MESSAGE);
												}
											} else {
												JOptionPane.showMessageDialog(gui, "You cannon't download a owned resource, please try searching another one.", "You already own searched resource.", JOptionPane.INFORMATION_MESSAGE);
											}
										} else {
											JOptionPane.showMessageDialog(gui, "Only one resource download at one is permitted! Please wait.", "Please wait.", JOptionPane.INFORMATION_MESSAGE);
										}
									} catch (HeadlessException | RemoteException e) {
										e.printStackTrace();
									}
								} else {
									JOptionPane.showMessageDialog(gui, "Please connect first.", "Please connect first", JOptionPane.ERROR_MESSAGE);
								}
						}
					}	
				}
		}.start();
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if ("search".equals(e.getActionCommand())) {
					performSearch(gui.getSearchedText());
			} else {
				if ("connection".equals(e.getActionCommand())) {
						connectToServer();
					
				}
			}
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		
	}
	
	@Override
	public Integer getmMaxDownloadCapacity() throws RemoteException {
		return maxDownloadCapacity;
	}
	
}
