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
						client.getClientName();
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
				final String[] list = Naming.list("rmi://" + HOST + "/Server/");
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
		Boolean alreadyPresentInteger = false;
		synchronized (clientsMonitor) {
			for (int i = 0; i < connectedClients.size(); i++)
				// check if client is already connected
				if (connectedClients.elementAt(i).getClientName().equals(paramClient.toString()))
					alreadyPresentInteger = true;

			if (alreadyPresentInteger == false) {
				connectedClients.add(paramClient);
				guiServerFrame.appendLogEntry(paramClient.getClientName() + " connected.");
				functionResultInteger = 1;
			}
			// update gui
			guiServerFrame.setConnectedClientsList(connectedClients);
		}
		return functionResultInteger;
	}

	@Override
	// 0 if disconnection is done, -1 if something wrong
	public Integer clientDisconnect(final ClientInterface paramClient) throws RemoteException {
		Integer functionResultInteger = -1;
		synchronized (clientsMonitor) {
			for (int i = 0; i < connectedClients.size(); i++) {
				// check if client is connected
				if (connectedClients.elementAt(i).getClientName().equals(paramClient.getClientName())) {
					connectedClients.remove(i);
					functionResultInteger = 0;
					guiServerFrame.appendLogEntry(paramClient.getClientName() + " disconnected.");
				}
			}
			// update gui
			guiServerFrame.setConnectedClientsList(connectedClients);
		}
		return functionResultInteger;
	}

	@Override
	public void disconnect() throws NotBoundException, MalformedURLException, RemoteException {
		// synchronized (sync) {
		Naming.unbind("rmi://" + HOST + "/Server/" + serverNameString);
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
		return "rmi://" + HOST + "/Server/" + serverNameString;
	}

	@Override
	public Vector<ClientInterface> resourceOwners(final String paramResourceName) throws RemoteException {
		final ResourceInterface paramResource = new Resource(paramResourceName);
		final Vector<ClientInterface> searchedResourceOweners = new Vector<ClientInterface>();
		synchronized (serversMonitor) {
			for (final ServerInterface serverInterface : connectedServers) {
				for (final ClientInterface cli : serverInterface.getClients()) { // sync
					// client
					// side
					for (final ResourceInterface resource : cli.getResources()) {
						if (resource.toString().equals(paramResource.toString())) {
							searchedResourceOweners.add(cli);
						}
					}
				}
			}

		}
		return searchedResourceOweners;
	}

	@Override
	public String toString() {
		return serverNameString;
	}
}
