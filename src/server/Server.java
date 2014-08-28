package server;

import gui.ServerFrame;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;

import resource.Resource;
import resource.ResourceInterface;
import client.ClientInterface;



public final class Server extends UnicastRemoteObject implements ServerInterface {

	private static final String HOST = "localhost";
	private String serverNameString = "";

	private ServerFrame myFrame = null;

	private Vector<ClientInterface> myConnectedClients = new Vector<ClientInterface>();
	Vector<ServerInterface> servers = new Vector<ServerInterface>();

	public Server(final String paramServerName) throws RemoteException {
		myFrame = new ServerFrame(paramServerName);
		myFrame.appendLogEntry("Buliding server...");
		serverNameString = paramServerName;

		for (final ClientInterface clientInterface : myConnectedClients) {
			myFrame.appendLogEntry(clientInterface.getClientName());
		}

	}

	@Override
	public Integer clientConnect(final ClientInterface paramClient) throws RemoteException {
		if (myConnectedClients.contains(paramClient)) {
			System.out.println("Client " + paramClient.getClientName() + " already connected!");
			return 0;
		} else {
			myConnectedClients.add(paramClient);
			return 1;
		}
	}

	@Override
	public void clientDisconnect(final ClientInterface paramClient) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public Vector<ServerInterface> getAllServers() {
		return servers;
	}

	@Override
	public Vector<ClientInterface> getClients() throws RemoteException {
		return myConnectedClients;
	}

	@Override
	public Vector<Resource> getClientsResources() throws RemoteException {
		final Vector<Resource> resources = new Vector<Resource>();
		for (final ClientInterface cli : myConnectedClients) {
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
}
