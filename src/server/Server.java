package server;

import gui.ServerFrame;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import resource.Resource;
import resource.ResourceInterface;
import client.ClientInterface;

public final class Server extends UnicastRemoteObject implements ServerInterface {

	private class ServerChecker extends Thread {
		public ServerChecker() { setDaemon(true); }

		private void checkOtherServers() throws Exception {
			// synchronized (monitor) {
			final String[] list = Naming.list("rmi://" + HOST + "/Server/");
			connectedServers.clear();
			for (final String string : list) {
				final ServerInterface srvInterface = (ServerInterface) Naming.lookup(string);
				connectedServers.add(srvInterface);
			}
			// update gui
			guiServerFrame.setConnectedServersList(connectedServers);
			// }
		}

		@Override
		public void run() {
			while (true) {
				// synchronized (monitor) {
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
				// }
			}
		}
	}
	private static final long serialVersionUID = -2240153419231304793L;
	private static final String HOST = "localhost";
	private String serverNameString = "";
	private ServerFrame guiServerFrame = null;
	private Vector<ClientInterface> connectedClients = new Vector<ClientInterface>();
	private Vector<ServerInterface> connectedServers = new Vector<ServerInterface>();
	private ServerChecker srvChecker = new ServerChecker();

	public Server(final String paramServerName) throws RemoteException {
		serverNameString = paramServerName;
		guiServerFrame = new ServerFrame(paramServerName);
		// update gui
		guiServerFrame.setConnectedClientsList(connectedClients);
		guiServerFrame.setConnectedServersList(connectedServers);
		srvChecker.start();

	}

	@Override
	// if connection success returns 1, if something wrong returns -1
	public Integer clientConnect(final ClientInterface paramClient) throws RemoteException {
		Integer functionResultInteger = -1;
		Boolean alreadyPresentInteger = false;
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
		return functionResultInteger;
	}

	@Override
	// 0 if disconnection is done, -1 if something wrong
	public Integer clientDisconnect(final ClientInterface paramClient) throws RemoteException {
		Integer functionResultInteger = -1;

		for (int i = 0; i < connectedClients.size(); i++) {
			// check if client is connected
			if (connectedClients.elementAt(i).getClientName().equals(paramClient.toString())) {
				connectedClients.remove(i);
				functionResultInteger = 0;
			}
		}
		// update gui
		guiServerFrame.setConnectedClientsList(connectedClients);
		return functionResultInteger;
	}

	@Override
	public Vector<ServerInterface> getAllServers() {
		return connectedServers;
	}

	@Override
	public Vector<ClientInterface> getClients() throws RemoteException {
		return connectedClients;
	}

	@Override
	public Vector<Resource> getClientsResources() throws RemoteException {
		final Vector<Resource> resources = new Vector<Resource>();
		for (final ClientInterface cli : connectedClients) {
			for (final Resource resource : cli.getResources()) {
				resources.add(resource);
			}
		}
		return resources;
	}

	@Override
	public Vector<ClientInterface> getRequest(final ResourceInterface paramResource) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
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
	public String toString() {
		return serverNameString;
	}
}
