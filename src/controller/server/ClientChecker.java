/**
 * 
 */
package controller.server;

import java.rmi.RemoteException;

import controller.client.ClientInterface;
import model.server.ConnectedClients;

public class ClientChecker extends Thread {
	
	private final Object clientsMonitor;
	private final ConnectedClients connectedClients; // MODEL condiviso con Server
	
	public ClientChecker(final Object clientsMonitor, final ConnectedClients connectedClients) {
		setDaemon(true);
		this.clientsMonitor = clientsMonitor;
		this.connectedClients = connectedClients;
	}

	@Override
	public void run() {
		while (true) {
			synchronized (clientsMonitor) {
				try {	
					for (final ClientInterface client : connectedClients.getConnectedClients()) {
						try {
							// throws a RemoteException if client is unreacheable
							client.test();
						} catch (final RemoteException e) {
							connectedClients.removeClient(client);
						}
					}
					/*
					 * risvegliato da clientConnect e clientDisconnect in Client
					 */
					clientsMonitor.wait();	
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}