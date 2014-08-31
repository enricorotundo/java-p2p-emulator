package server;

import gui.ServerFrame;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import resource.Resource;
import resource.ResourceInterface;
import client.ClientInterface;

public final class Server extends UnicastRemoteObject implements ServerInterface {

	private class ClientChecker extends Thread {
		public ClientChecker() {
			setDaemon(true);
		}

		private void clientsUpCheck() throws Exception {
			synchronized (clientsMonitor) {

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
			}
		}

		@Override
		public void run() {
			while (true) {
				synchronized (clientsMonitor) {
					try {
						clientsUpCheck();
					} catch (final Exception e) {
						e.printStackTrace();
					}
					try {
						sleep(10);
					} catch (final InterruptedException e) {
						System.out.println("Interrupted ClientChecker thread.");
					}
				}
			}
		}
	}

	private class ServerChecker extends Thread {
		public ServerChecker() {
			setDaemon(true);
		}

		private void checkOtherServers() throws Exception {
			synchronized (serversMonitor) {
				final String[] list = Naming.list(Server.URL_STRING);
				connectedServers.clear();
				for (final String string : list) {
					final ServerInterface srvInterface = (ServerInterface) Naming.lookup(string);
					connectedServers.add(srvInterface);
				}
				// update gui
				guiServerFrame.setConnectedServersList(connectedServers);
			}
		}

		@Override
		public void run() {
			while (true) {
				synchronized (serversMonitor) {
					try {
						checkOtherServers();
					} catch (final Exception e) {
						e.printStackTrace();
					}
					try {
						sleep(10);
					} catch (final InterruptedException e) {
						System.out.println("Interrupted ServerChecker thread.");
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
		guiServerFrame.setConnectedClientsList(connectedClients);
		guiServerFrame.setConnectedServersList(connectedServers);
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
		}
		return functionResultInteger;
	}

	@Override
	public void disconnect() throws NotBoundException, MalformedURLException, RemoteException {
		// synchronized (sync) {
		Naming.unbind(Server.URL_STRING + serverNameString);
		// }
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
		final ResourceInterface paramResource = new Resource(paramResourceName);
		final Vector<ClientInterface> searchedResourceOweners = new Vector<ClientInterface>();
		synchronized (serversMonitor) {
			for (final ServerInterface serverInterface : connectedServers) {
				for (final ClientInterface cli : serverInterface.getClients()) { //
					// sync client side
					guiServerFrame.appendLogEntry("Looking for " + paramResource.toString() + " in " + cli.getClientName() + "@" + serverInterface.getServerNameString());
					for (final ResourceInterface resource : cli.getResources()) {
						if (resource.equals(paramResource)) {
							guiServerFrame.appendLogEntry(cli.getClientName() + "@" + serverInterface.getServerNameString() + " has " + resource.toString());
							searchedResourceOweners.add(cli);
						}
					}
				}
			}
		}
		return searchedResourceOweners;
	}

	// @Override
	// public boolean serverCompare(final Object other) throws RemoteException {
	// if (other == null)
	// return false;
	// if (other == this)
	// return true;
	// if (!(other instanceof Server))
	// return false;
	// final Server otherMyClass = (Server) other;
	// if (otherMyClass.serverNameString.equals(this.serverNameString)) {
	// return true;
	// }
	// return false;
	// }
}
