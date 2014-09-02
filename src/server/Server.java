package server;

import gui.ServerFrame;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import resource.ResourceInterface;
import client.ClientInterface;

public final class Server extends UnicastRemoteObject implements ServerInterface {

	private class ClientChecker extends Thread {
		public ClientChecker() {
			setDaemon(true);
		}

		@Override
		public void run() {
			while (true) {
				synchronized (clientsMonitor) {
					try {
						for (final ClientInterface client : connectedClients) {
							try {
								client.getConnectedServer();
							} catch (final Exception e) {
								connectedClients.remove(client);
								guiServerFrame.appendLogEntry("Client disconnected.");
								// update gui
								guiServerFrame.setConnectedClientsList(connectedClients);
							}
						}
						clientsMonitor.wait();
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private class ServerChecker extends Thread {
		public ServerChecker() {
			setDaemon(true);
		}

		@Override
		public void run() {
			while (true) {
				synchronized (serversMonitor) {
					try {
						final String[] list = Naming.list(Server.URL_STRING);
						connectedServers.clear();
						for (final String string : list) {
							final ServerInterface srvInterface = (ServerInterface) Naming.lookup(string);
							connectedServers.add(srvInterface);
						}
						// update gui
						guiServerFrame.setConnectedServersList(connectedServers);
						sleep(10);
					} catch (final InterruptedException | RemoteException | MalformedURLException | NotBoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private static final long serialVersionUID = -2240153419231304793L;
	private static final String HOST = "localhost";
	public static final String URL_STRING = "rmi://" + HOST + "/Server/";
	private final String serverNameString;
	private final ServerFrame guiServerFrame;
	private final Vector<ClientInterface> connectedClients = new Vector<ClientInterface>();
	private final Vector<ServerInterface> connectedServers = new Vector<ServerInterface>();
	private final ServerChecker srvChecker = new ServerChecker();
	private final ClientChecker clisChecker = new ClientChecker();
	private final Object clientsMonitor = new Object();
	private final Object serversMonitor = new Object();

	public Server(final String paramServerName) throws RemoteException {
		serverNameString = paramServerName;
		guiServerFrame = new ServerFrame(paramServerName);
		// update gui
		synchronized (clientsMonitor) {
			guiServerFrame.setConnectedClientsList(connectedClients);
		}
		synchronized (serversMonitor) {
			guiServerFrame.setConnectedServersList(connectedServers);
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					disconnect();
				} catch (MalformedURLException | RemoteException | NotBoundException e) {
					e.printStackTrace();
				}
			}
		});
		srvChecker.start();
		clisChecker.start();
	}

	@Override
	// if connection success returns 1, if something wrong returns -1
	public Integer clientConnect(final ClientInterface paramClient) throws RemoteException {
		Integer functionResultInteger = -1;
		synchronized (clientsMonitor) {
			// check if client is already connected
			if (!connectedClients.contains(paramClient)) {
				connectedClients.add(paramClient);
				guiServerFrame.appendLogEntry(paramClient.getClientName() + " connected.");
				functionResultInteger = 1;
				// update gui
				guiServerFrame.setConnectedClientsList(connectedClients);
			}
			clientsMonitor.notifyAll();
		}
		return functionResultInteger;
	}

	@Override
	// 0 if disconnection is done, -1 if something wrong
	public Integer clientDisconnect(final ClientInterface paramClient) throws RemoteException {
		Integer functionResultInteger = -1;
		synchronized (clientsMonitor) {
			// check if client is connected
			if (connectedClients.contains(paramClient)) {
				connectedClients.remove(paramClient);
				functionResultInteger = 0;
				guiServerFrame.appendLogEntry(paramClient.getClientName() + " disconnected.");
				// update gui
				guiServerFrame.setConnectedClientsList(connectedClients);
			}
			clientsMonitor.notifyAll();
		}
		return functionResultInteger;
	}

	@Override
	public void disconnect() throws NotBoundException, MalformedURLException, RemoteException {
		Naming.unbind(Server.URL_STRING + serverNameString);
	}

	@Override
	public Vector<ClientInterface> getClients() throws RemoteException {
		synchronized (connectedClients) {
			return connectedClients;
		}
	}

	@Override
	public String getServerNameString() {
		return serverNameString;
	}

	@Override
	public String getServerUrl() throws RemoteException {
		return Server.URL_STRING + serverNameString;
	}

	@Override
	public Vector<ClientInterface> resourceOwners(final String paramResourceName) throws RemoteException {
		final Vector<ClientInterface> searchedResourceOweners = new Vector<ClientInterface>();
		synchronized (serversMonitor) {
			for (final ServerInterface serverInterface : connectedServers) {
				for (final ClientInterface cli : serverInterface.getClients()) { //
					// sync client side
					guiServerFrame.appendLogEntry("Looking for " + paramResourceName + " in " + cli.getClientName() + "@" + serverInterface.getServerNameString());
					for (final ResourceInterface resource : cli.getResources()) {
						if (resource.resourceCompare(paramResourceName)) {
							guiServerFrame.appendLogEntry(cli.getClientName() + "@" + serverInterface.getServerNameString() + " has " + resource.toString());
							searchedResourceOweners.add(cli);
						}
					}
				}
			}
		}
		return searchedResourceOweners;
	}

}
