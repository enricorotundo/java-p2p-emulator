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

	private ServerFrame guiServerFrame = null;

	private Vector<ClientInterface> myConnectedClients = new Vector<ClientInterface>();
	private Vector<ServerInterface> servers = new Vector<ServerInterface>();

	public Server(final String paramServerName) throws RemoteException {
		serverNameString = paramServerName;

		guiServerFrame = new ServerFrame(paramServerName, this);
		guiServerFrame.appendLogEntry("Buliding server at " + getServerUrl());
		// for (final ClientInterface clientInterface : myConnectedClients) {
		// myFrame.appendLogEntry(clientInterface.getClientName());
		// }
	}

	@Override
	public Integer clientConnect(final ClientInterface paramClient) throws RemoteException {
		if (myConnectedClients.contains(paramClient) == true) {
			System.out.println("Client " + paramClient.getClientName() + " already connected!");
			return 0;
		} else {
			myConnectedClients.add(paramClient);
			for (int i = 0; i < myConnectedClients.size(); i++) {
				System.out.println(myConnectedClients.elementAt(i).getClientName());
			}
			System.out.println("Client " + paramClient.getClientName() + " connected.");
			guiServerFrame.appendLogEntry(paramClient.getClientName() + " connected.");
			return 1;
		}
	}

	@Override
	public Integer clientDisconnect(final ClientInterface paramClient) throws RemoteException {
		Integer functionResultInteger = -1;
		for (int i = 0; i < myConnectedClients.size(); i++) {
			System.out.println(myConnectedClients.elementAt(i).getClientName());
		}
		for (int i = 0; i < myConnectedClients.size(); i++) {
			if (paramClient.getClientName() == myConnectedClients.elementAt(i).getClientName()) {
				myConnectedClients.removeElementAt(i);
				System.out.println("Client " + paramClient.getClientName() + " disconnected.");
				functionResultInteger = 1;
			}
		}
		System.out.println("clientDisconnect(..), retruned value: " + functionResultInteger);
		return functionResultInteger;
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
