package controller.client;

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

import controller.server.Server;
import controller.server.ServerInterface;
import model.client.ClientResources;
import model.share.Resource;

public class Client extends UnicastRemoteObject  implements ClientInterface, ActionListener  {
	
	/**************** TEMPO DI DONWLOAD COSTANTE (PER PARTE) **************/
	public static final long UPLOAD_TIME = 4000;
	/**********************************************************************/
	
	private static final long serialVersionUID = -3445312807782067423L;
	private final String clientName;
	private final String serverName;
	private AtomicBoolean connectionStatusUp = new AtomicBoolean(false); //false=down, true=up
	private AtomicInteger currentDownloads = new AtomicInteger(0);
	private final Integer maxDownloadCapacity;
	private final ClientResources resources; // MODEL
	private final ClientFrame gui; // VIEW
	private final ConnectionChecker connectionChecker;
	
	public Client(final String clientName, final String serverName, int maxDownloadCapacity, final ClientResources argResources) throws RemoteException {
		this.clientName = clientName;
		this.serverName = serverName;
		this.maxDownloadCapacity = maxDownloadCapacity;
		this.resources = argResources;
		gui = new ClientFrame(clientName + "@" + serverName, resources, this);
		// dico al MODEL chi e' il suo Observer
		this.resources.addObserver(gui);
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
	}

	@Override
	public Boolean download(String callerName) throws RemoteException {
		try {
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

	@Override
	public Boolean test() throws RemoteException {
		return true;
	}

	@Override
	public Vector<String[]> getResourceList() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private final void connectToServer() {
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
	public void actionPerformed(ActionEvent e) {
		if ("search".equals(e.getActionCommand())) {
			// performSearch
		} else {
			if ("connection".equals(e.getActionCommand())) {
				connectToServer();
			}
		}
		
	}
	
}
