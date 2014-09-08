package controller.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicBoolean;

import view.ClientFrame;
import controller.server.Server;
import controller.server.ServerInterface;

class ConnectionChecker extends Thread {
	
	private AtomicBoolean connectionStatusUp;
	private String serverName;
	private final ClientFrame gui;
	
	public ConnectionChecker(final AtomicBoolean connectionStatusUp, final String serverName, final ClientFrame gui) {
		setDaemon(true);
		this.connectionStatusUp = connectionStatusUp;
		this.serverName = serverName;
		this.gui = gui;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				sleep(100);
				synchronized (connectionStatusUp) {
					
					// controllo lo stato della connessione solo se ero/sono connesso
					if (connectionStatusUp.get() == true) {
						ServerInterface remoteServerInterface = (ServerInterface) Naming.lookup(Server.URL_STRING + serverName);	
						remoteServerInterface.getServerUrl();
					}						
				}
			} catch (MalformedURLException | RemoteException | InterruptedException e) {
				e.printStackTrace();
			} catch (final NotBoundException e) {
				
				// qui il server a cui ero connesso non esiste piu'
				synchronized (connectionStatusUp) {
					connectionStatusUp.set(false);
				}
				if (gui != null) {
					gui.setConnectionButtonText("Connect");						
					gui.appendLogEntry("Disconnected from " + serverName + " because seems offline.");
				}
				
				synchronized (this) {
					try {
						/*
						 * viene risvegliato in connectToServer() cioe quando
						 * il client si riconnette al server serverName
						 */
						this.wait();
					} catch (final InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
