package controller.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import model.server.ConnectedServers;

public class ServerChecker extends Thread {
	
	private final Object serversMonitor;
	private final ConnectedServers connectedServers; // MODEL condiviso con Server


	public ServerChecker(final Object serversMonitor, final ConnectedServers connectedServers) {
		setDaemon(true);
		this.serversMonitor = serversMonitor;
		this.connectedServers = connectedServers;
	}

	@Override
	public void run() {
		while (true) {
			try {
				/*
				 * questo Thread deve fare busy wait perche' se un altro server
				 * si chiude inaspettatamente non c'e' modo di comunicare a tutti
				 * gli atri server l'accaduto
				 */
				sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			synchronized (serversMonitor) {
				try {
					final String[] list = Naming.list(Server.URL_STRING);
					connectedServers.clearServers();
					for (final String string : list) {
						final ServerInterface serverToInsert = (ServerInterface) Naming.lookup(string);
						connectedServers.addServer(serverToInsert);
					}
				} catch (RemoteException | MalformedURLException | NotBoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}